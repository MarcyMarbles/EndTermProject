package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Service.JwtUtils;
import kz.marcy.endtermproject.Service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public AuthController(JwtUtils jwtUtils, UserService userService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody LoginRequest loginRequest) {
        return userService.validateUserByLoginAndPassword(loginRequest.getLogin(), loginRequest.getPassword())
                .flatMap(isValid -> {
                    if (!isValid) {
                        log.warn("Invalid login attempt for user: {}", loginRequest.getLogin());
                        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new AuthResponse("Invalid login credentials", null)));
                    }
                    return userService.getUserRole(loginRequest.getLogin())
                            .flatMap(role -> {
                                String token = jwtUtils.generateToken(loginRequest.getLogin(), role);
                                log.info("User {} logged in successfully with role {}", loginRequest.getLogin(), role);
                                return Mono.just(ResponseEntity.ok(new AuthResponse(token, role)));
                            });
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse("User not found or invalid credentials", null))));
    }


}

@Data
@AllArgsConstructor
@NoArgsConstructor
class LoginRequest {
    private String login;
    private String password;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class AuthResponse {
    private String token;
    private String role;
}
