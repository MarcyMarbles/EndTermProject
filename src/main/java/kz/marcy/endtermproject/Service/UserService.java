package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.Transient.Message;
import kz.marcy.endtermproject.Entity.Transient.PageWrapper;
import kz.marcy.endtermproject.Entity.Roles;
import kz.marcy.endtermproject.Entity.Users;
import kz.marcy.endtermproject.WebSocketHandlers.UserWebSocketHandler;
import kz.marcy.endtermproject.Repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;

@Service
public class UserService extends AbstractSuperService<Users> {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserWebSocketHandler userWebSocketHandler;
    private final JwtUtils jwtUtils;
    private final PendingService pendingService;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder, UserWebSocketHandler userWebSocketHandler, JwtUtils jwtUtils, PendingService pendingService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.userWebSocketHandler = userWebSocketHandler;
        this.jwtUtils = jwtUtils;
        this.pendingService = pendingService;
    }

    public Mono<Users> saveUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user)
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(users -> userWebSocketHandler.publishUser(users, Message.Type.CREATE));
    }


    public Mono<Users> updateUser(Users user) {
        return userRepo.findById(user.getId())
                .flatMap(existingUser -> {
                    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                    }
                    if(user.getAvatar() != null) {
                        existingUser.setAvatar(user.getAvatar());
                    }
                    // Temp email is not changeable
                    if(user.getUsername() != null && !user.getUsername().isEmpty()) {
                        existingUser.setUsername(user.getUsername());
                    }
                    return userRepo.save(existingUser);
                }).doOnSuccess(users -> userWebSocketHandler.publishUser(users, Message.Type.UPDATE));
    }

    @Override
    public void saveEntity(Users entity) {
        userRepo.save(entity).subscribe();
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
        return userRepo.findByLoginAndDeletedAtIsNull(login)
                .flatMap(user -> {
                    if (passwordEncoder.matches(password, user.getPassword())) {
                        if (user.isPending()) {
                            return pendingService.findByUserId(user.getId())
                                    .flatMap(pendingCodes -> {
                                        if (pendingCodes.getDueDate().isBefore(Instant.now()) && !pendingCodes.isUsed()) {
                                            return deleteUser(user.getId()).thenReturn(false);
                                        }
                                        return Mono.just(false); // User is still pending
                                    });
                        }
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
        return userRepo.findByLoginAndDeletedAtIsNull(login)
                .map(Users::getRoles)
                .map(Roles::getCode);
    }

    public Mono<Void> deleteUser(String id) {
        return userRepo.findByIdAndDeletedAtIsNull(id)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found or already deleted")))
                .flatMap(user -> Mono.fromRunnable(() -> softDelete(user)))
                .then();
    }

    public Mono<Users> getUserByLogin(String login) {
        return userRepo.findByLoginAndDeletedAtIsNull(login);
    }

    public Mono<Users> findUserByEmail(String email) {
        return userRepo.findByEmailAndDeletedAtIsNull(email);
    }

    public Mono<Boolean> confirmUser(PendingCodes code) {
        return userRepo.findByIdAndDeletedAtIsNull(code.getUserId())
                .flatMap(user -> {
                    if (!user.isPending()) {
                        return Mono.just(false);
                    } else {
                        user.setPending(false);
                        return userRepo.save(user).map(Users::isPending);
                    }
                });
    }

    public Mono<Users> findUserByUsername(String username) {
        return userRepo.findByUsernameAndDeletedAtIsNull(username);
    }

    public Mono<Users> findByToken(String token) {
        return Mono.just(jwtUtils.extractLogin(token))
                .flatMap(this::findUserByUsername);
    }

}
