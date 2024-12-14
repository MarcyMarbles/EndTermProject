package kz.marcy.endtermproject.Service;

import jakarta.annotation.PostConstruct;
import kz.marcy.endtermproject.Entity.Transient.Message;
import kz.marcy.endtermproject.Entity.Transient.PageWrapper;
import kz.marcy.endtermproject.Entity.Roles;
import kz.marcy.endtermproject.Entity.Users;
import kz.marcy.endtermproject.Handlers.UserWebSocketHandler;
import kz.marcy.endtermproject.Repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class UserService extends AbstractSuperService<Users> {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final RolesService rolesService;
    private final UserWebSocketHandler userWebSocketHandler;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder, RolesService rolesService, UserWebSocketHandler userWebSocketHandler) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.rolesService = rolesService;
        this.userWebSocketHandler = userWebSocketHandler;
    }

    public Mono<Users> findByLogin(String login) {
        return userRepo.findByLogin(login);
    }

    public Mono<Users> saveUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user).doOnSuccess(users -> userWebSocketHandler.publishUser(users, Message.Type.CREATE));
    }

    public Mono<Users> updateUser(Users user) {
        return userRepo.findById(user.getId())
                .flatMap(existingUser -> {
                    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                    }
                    existingUser.setUsername(user.getUsername());
                    existingUser.setUpdatedAt(Instant.now());
                    return userRepo.save(existingUser);
                }).doOnSuccess(users -> userWebSocketHandler.publishUser(users, Message.Type.UPDATE));
    }

    @Override
    public void softDelete(Users entity) {
        super.softDelete(entity);
        userWebSocketHandler.publishUser(entity, Message.Type.DELETE);
    }

    @Override
    public void softDelete(Iterable<Users> entities) {
        super.softDelete(entities);
        entities.forEach(users -> {
            userWebSocketHandler.publishUser(users, Message.Type.DELETE);
        });
    }

    public Flux<Users> findAll(PageWrapper page) {
        return userRepo.findAll()
                .skip((long) page.getPage() * page.getSize())
                .take(page.getSize());
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
                    log.error("Error validating user: {}", e.getMessage());
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
        rolesService.findByCode("ROLE_ADMIN")
                .flatMap(role -> {
                    user.setRoles(role);
                    return userRepo.findByLogin(user.getLogin())
                            .switchIfEmpty(saveUser(user));
                }).subscribe();

    }
}
