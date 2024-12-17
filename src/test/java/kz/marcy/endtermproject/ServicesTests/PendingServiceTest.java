package kz.marcy.endtermproject.ServicesTests;


import kz.marcy.endtermproject.Entity.Users;
import kz.marcy.endtermproject.Repository.PendingRepo;
import kz.marcy.endtermproject.Service.EmailService;
import kz.marcy.endtermproject.Service.PendingService;
import kz.marcy.endtermproject.Service.PendingCodes;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PendingServiceTest {

    @Mock
    private PendingRepo pendingRepo;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PendingService pendingService;

    @Test
    void testCreatePendingCode() {
        // Arrange
        Users user = new Users();
        user.setId("123");
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        PendingCodes pendingCode = new PendingCodes();
        pendingCode.setCode(user.getUsername() + System.currentTimeMillis());
        pendingCode.setUserId(user.getId());
        pendingCode.setEmail(user.getEmail());
        pendingCode.setUsed(false);
        pendingCode.setDueDate(Instant.now().plus(2, ChronoUnit.HOURS));

        when(pendingRepo.save(any(PendingCodes.class))).thenReturn(Mono.just(pendingCode));
        when(emailService.sendConfirmationEmail(any(PendingCodes.class))).thenReturn(Mono.empty());


        Mono<PendingCodes> result = pendingService.createPendingCode(user);


        StepVerifier.create(result)
                .expectNextMatches(savedCode ->
                        savedCode.getUserId().equals("123") &&
                                savedCode.getEmail().equals("test@example.com") &&
                                !savedCode.isUsed()
                )
                .verifyComplete();

        verify(pendingRepo, times(1)).save(any(PendingCodes.class));
        verify(emailService, times(1)).sendConfirmationEmail(any(PendingCodes.class));
    }

    @Test
    void testConfirmUserSuccess() {

        String code = "testCode";
        PendingCodes pendingCode = new PendingCodes();
        pendingCode.setCode(code);
        pendingCode.setUsed(false);
        pendingCode.setDueDate(Instant.now().plus(1, ChronoUnit.HOURS));

        when(pendingRepo.findByCodeAndUsedIsFalseAndDueDateAfter(eq(code), any(Instant.class)))
                .thenReturn(Mono.just(pendingCode));
        when(pendingRepo.save(any(PendingCodes.class))).thenReturn(Mono.just(pendingCode));


        Mono<Boolean> result = pendingService.confirmUser(code);


        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(pendingRepo, times(1)).findByCodeAndUsedIsFalseAndDueDateAfter(eq(code), any(Instant.class));
        verify(pendingRepo, times(1)).save(any(PendingCodes.class));
    }

    @Test
    void testConfirmUserNotFound() {

        String code = "invalidCode";

        when(pendingRepo.findByCodeAndUsedIsFalseAndDueDateAfter(eq(code), any(Instant.class)))
                .thenReturn(Mono.empty());


        Mono<Boolean> result = pendingService.confirmUser(code);


        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(pendingRepo, times(1)).findByCodeAndUsedIsFalseAndDueDateAfter(eq(code), any(Instant.class));
        verify(pendingRepo, times(0)).save(any(PendingCodes.class));
    }
}
