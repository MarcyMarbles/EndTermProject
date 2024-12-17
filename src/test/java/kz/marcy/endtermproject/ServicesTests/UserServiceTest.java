package kz.marcy.endtermproject.ServicesTests;


import kz.marcy.endtermproject.Entity.Roles;
import kz.marcy.endtermproject.Entity.Users;
import kz.marcy.endtermproject.Entity.Transient.Message;
import kz.marcy.endtermproject.Repository.UserRepo;
import kz.marcy.endtermproject.Service.PendingCodes;
import kz.marcy.endtermproject.Service.UserService;
import kz.marcy.endtermproject.WebSocketHandlers.UserWebSocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserWebSocketHandler userWebSocketHandler;

    @InjectMocks
    private UserService userService;

    private Users testUser;
    private Roles testRole;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testRole = new Roles();
        testRole.setName("USER");
        testRole.setCode("ROLE_USER");

        testUser = new Users();
        testUser.setId("123");
        testUser.setUsername("testUser");
        testUser.setPassword("plainPassword");
        testUser.setRoles(testRole);
        testUser.setPending(false);
    }

    @Test
    public void testSaveUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepo.save(any(Users.class))).thenReturn(Mono.just(testUser));

        StepVerifier.create(userService.saveUser(testUser))
                .expectNext(testUser)
                .verifyComplete();

        verify(passwordEncoder).encode("plainPassword");
        verify(userRepo).save(any(Users.class));
        verify(userWebSocketHandler).publishUser(testUser, Message.Type.CREATE);
    }

    @Test
    public void testUpdateUser() {
        when(userRepo.findById("123")).thenReturn(Mono.just(testUser));
        when(userRepo.save(any(Users.class))).thenReturn(Mono.just(testUser));

        StepVerifier.create(userService.updateUser(testUser))
                .expectNext(testUser)
                .verifyComplete();

        verify(userRepo).findById("123");
        verify(userRepo).save(testUser);
        verify(userWebSocketHandler).publishUser(testUser, Message.Type.UPDATE);
    }

    @Test
    public void testDeleteUser() {
        // Настройка мока для findByIdAndDeletedAtIsNull
        when(userRepo.findByIdAndDeletedAtIsNull("123")).thenReturn(Mono.just(testUser));

        // Настройка мока для save (заменяет сохранение в softDelete)
        when(userRepo.save(any(Users.class))).thenReturn(Mono.just(testUser));

        // Выполнение метода и проверка результата
        StepVerifier.create(userService.deleteUser("123"))
                .verifyComplete();

        // Проверка вызовов методов
        verify(userRepo).findByIdAndDeletedAtIsNull("123");
        verify(userRepo).save(any(Users.class));
        verify(userWebSocketHandler).publishUser(testUser, Message.Type.DELETE);
    }

    @Test
    public void testValidateUserByLoginAndPassword() {
        when(userRepo.findByLoginAndDeletedAtIsNull("testUser"))
                .thenReturn(Mono.just(testUser));
        when(passwordEncoder.matches("plainPassword", "plainPassword")).thenReturn(true);

        StepVerifier.create(userService.validateUserByLoginAndPassword("testUser", "plainPassword"))
                .expectNext(true)
                .verifyComplete();

        verify(userRepo).findByLoginAndDeletedAtIsNull("testUser");
        verify(passwordEncoder).matches("plainPassword", "plainPassword");
    }

    @Test
    public void testGetUserRole() {
        when(userRepo.findByLoginAndDeletedAtIsNull("testUser"))
                .thenReturn(Mono.just(testUser));

        StepVerifier.create(userService.getUserRole("testUser"))
                .expectNext("ROLE_USER")
                .verifyComplete();

        verify(userRepo).findByLoginAndDeletedAtIsNull("testUser");
    }

    @Test
    public void testFindUserByEmail() {
        when(userRepo.findByEmailAndDeletedAtIsNull("test@example.com"))
                .thenReturn(Mono.just(testUser));

        StepVerifier.create(userService.findUserByEmail("test@example.com"))
                .expectNext(testUser)
                .verifyComplete();

        verify(userRepo).findByEmailAndDeletedAtIsNull("test@example.com");
    }

    @Test
    public void testGetUserID() {
        when(userRepo.findByLoginAndDeletedAtIsNull("testUser"))
                .thenReturn(Mono.just(testUser));

        StepVerifier.create(userService.getUserID("testUser"))
                .expectNext("123")
                .verifyComplete();

        verify(userRepo).findByLoginAndDeletedAtIsNull("testUser");
    }

    @Test
    public void testConfirmUser() {
        testUser.setPending(true);

        PendingCodes pendingCode = new PendingCodes();
        pendingCode.setCode("someCode");
        pendingCode.setUserId("123");
        pendingCode.setEmail("test@example.com");
        pendingCode.setUsed(false);
        pendingCode.setDueDate(Instant.now());

        when(userRepo.findByIdAndDeletedAtIsNull("123")).thenReturn(Mono.just(testUser));
        when(userRepo.save(any(Users.class))).thenReturn(Mono.just(testUser));

        StepVerifier.create(userService.confirmUser(pendingCode))
                .expectNext(false)
                .verifyComplete();

        verify(userRepo).findByIdAndDeletedAtIsNull("123");
        verify(userRepo).save(testUser);
    }
}


