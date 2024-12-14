package kz.marcy.endtermproject.Service;

import jakarta.annotation.PostConstruct;
import kz.marcy.endtermproject.Entity.Roles;
import kz.marcy.endtermproject.Entity.Users;
import kz.marcy.endtermproject.Repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService extends AbstractSuperService<Users> {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final RolesService rolesService;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder, RolesService rolesService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.rolesService = rolesService;
    }

    public Mono<Users> findByLogin(String login) {
        return userRepo.findByLogin(login);
    }

    public Mono<Users> saveUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public Mono<Users> updateUser(Users user) {
        return userRepo.findById(user.getId())
                .flatMap(existingUser -> {
                    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                    }
                    existingUser.setUsername(user.getUsername());
                    return userRepo.save(existingUser);
                });
    }

    public Flux<Users> findAll() {
        return userRepo.findAll();
    }

    public Mono<Boolean> validateUserByLoginAndPassword(String login, String password) {
        return userRepo.findByLogin(login)
                .flatMap(user -> {
                    if (passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.just(true);
                    } else {
                        return Mono.just(false);
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("Error validating user: " + e.getMessage());
                    return Mono.just(false);
                })
                .defaultIfEmpty(false);
    }

    public Mono<String> getUserRole(String login) {
        return userRepo.findByLogin(login)
                .map(Users::getRoles)
                .map(Roles::getCode);
    }


    @PostConstruct
    public void init() {
        Users user = new Users();
        user.setLogin("admin");
        user.setPassword("admin");
        user.setUsername("admin");
        user.setRoles(rolesService.findByCode("ROLE_ADMIN").block());
        userRepo.findByLogin(user.getLogin())
                .switchIfEmpty(saveUser(user))
                .subscribe();
    }
}
