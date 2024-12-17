package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Service.JwtUtils;
import kz.marcy.endtermproject.Service.PendingCodes;
import kz.marcy.endtermproject.Service.PendingService;
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
    private final PendingService pendingService;

    public AuthController(JwtUtils jwtUtils, UserService userService, PendingService pendingService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.pendingService = pendingService;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody LoginRequest loginRequest) {
        return userService.validateUserByLoginAndPassword(loginRequest.getLogin(), loginRequest.getPassword())
                .flatMap(isValid -> {
                    if (!isValid) {
                        log.warn("Invalid login attempt for user: {}", loginRequest.getLogin());
                        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new AuthResponse("Invalid login credentials", null, null)));
                    }

                    // Fetch role and userId after validation
                    return userService.getUserRole(loginRequest.getLogin())
                            .flatMap(role -> userService.getUserID(loginRequest.getLogin())
                                    .flatMap(userId -> {
                                        String token = jwtUtils.generateToken(loginRequest.getLogin(), role, userId);
                                        log.info("User {} logged in successfully with role {} and id {}",
                                                loginRequest.getLogin(), role, userId);
                                        return Mono.just(ResponseEntity.ok(new AuthResponse(token, role, userId)));
                                    })
                            );
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse("User not found or invalid credentials", null, null))));
    }


    /*@PostMapping("/confirm")
    public Mono<ResponseEntity<String>> confirmation(@RequestParam String token, @RequestParam String email) {
        return userService.findUserByEmail(email)
                .flatMap(login -> {
                    if (jwtUtils.validateToken(token, login.getLogin())) {
                        return userService.confirmUser(email)
                                .map(isConfirmed -> {
                                    if (isConfirmed) {
                                        log.info("User {} confirmed his email", login);
                                        return ResponseEntity.ok("Email confirmed successfully");
                                    } else {
                                        log.warn("User {} tried to confirm his email, but he is already confirmed", login);
                                        return ResponseEntity.badRequest().body("User is already confirmed");
                                    }
                                });
                    } else {
                        log.warn("User {} tried to confirm his email with invalid token", login);
                        return Mono.just(ResponseEntity.badRequest().body("Invalid token"));
                    }
                })
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().body("User not found")));
    }*/

    @PostMapping("/confirm")
    public Mono<ResponseEntity<String>> confirmation(@RequestParam String code) {
        return pendingService.confirmUser(code)
                .map(isConfirmed -> {
                    if (isConfirmed) {
                        log.info("User confirmed his email");
                        return ResponseEntity.ok("Email confirmed successfully");
                    } else {
                        log.warn("User tried to confirm his email, but he is already confirmed");
                        return ResponseEntity.badRequest().body("User is already confirmed");
                    }
                })
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().body("Invalid code")));
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
    private String id;
}
