package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Entity.Transient.PageWrapper;
import kz.marcy.endtermproject.Entity.Users;
import kz.marcy.endtermproject.Repository.PendingRepo;
import kz.marcy.endtermproject.Service.PendingCodes;
import kz.marcy.endtermproject.Service.PendingService;
import kz.marcy.endtermproject.Service.RolesService;
import kz.marcy.endtermproject.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    private final RolesService rolesService;

    private final PendingService pendingService;

    public UserController(UserService userService, RolesService rolesService, PendingService pendingService) {
        this.userService = userService;
        this.rolesService = rolesService;
        this.pendingService = pendingService;
    }

    @GetMapping("/users")
    public Flux<Users> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return userService.findAll(PageWrapper.of(page, size));
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> addUser(@RequestBody Users user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            return Mono.just(ResponseEntity.badRequest().body("Invalid user input"));
        }

        return rolesService.findByCode("ROLE_USER")
                .flatMap(role -> {
                    if (role == null) {
                        return Mono.just(ResponseEntity.badRequest().body("Role 'ROLE_USER' not found. Contact admin."));
                    }
                    user.setRoles(role);
                    user.setPending(true);
                    return pendingService.createPendingCode(user)
                            .flatMap(_ -> userService.saveUser(user)
                                    .then(Mono.just(ResponseEntity.ok("User registered successfully. Please confirm your email"))));

                })
                .defaultIfEmpty(ResponseEntity.badRequest().body("Role not found"));
    }

    @PostMapping("/updateUser")
    public Mono<ResponseEntity<String>> updateUser(@RequestBody Users user) {
        if (user == null || user.getId() == null) {
            return Mono.just(ResponseEntity.badRequest().body("Invalid user input"));
        }

        return userService.updateUser(user)
                .then(Mono.just(ResponseEntity.ok("User updated successfully")));
    }

    @DeleteMapping("/deleteUser/{id}")
    public Mono<ResponseEntity<String>> deleteUser(@PathVariable String id) {
        return userService.deleteUser(id)
                .then(Mono.just(ResponseEntity.ok("User deleted successfully")))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error deleting user: " + e.getMessage())));
    }


}
