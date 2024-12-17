package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.Users;
import kz.marcy.endtermproject.Repository.PendingRepo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class PendingService {
    private final PendingRepo pendingRepo;
    private final EmailService emailService;

    public PendingService(PendingRepo pendingRepo, EmailService emailService) {
        this.pendingRepo = pendingRepo;
        this.emailService = emailService;
    }

    public Mono<PendingCodes> createPendingCode(Users users) {
        PendingCodes pendingCode = new PendingCodes();
        pendingCode.setCode(users.getUsername() + System.currentTimeMillis());
        pendingCode.setUserId(users.getId());
        pendingCode.setEmail(users.getEmail());
        pendingCode.setUsed(false);
        pendingCode.setDueDate(Instant.now().plus(2, ChronoUnit.HOURS));
        return pendingRepo.save(pendingCode)
                .flatMap(savedCode -> emailService.sendConfirmationEmail(savedCode)
                        .thenReturn(savedCode));
    }

    public Mono<Boolean> confirmUser(String code){
        return pendingRepo.findByCodeAndUsedIsFalseAndDueDateAfter(code, Instant.now())
                .flatMap(pendingCode -> {
                    pendingCode.setUsed(true);
                    return pendingRepo.save(pendingCode)
                            .map(_ -> true);
                });
    }

}
