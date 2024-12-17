package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender emailSender;
    @Value("${app.confirmation.base-url}")
    private String authLink;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public Mono<Void> sendConfirmationEmail(PendingCodes pendingCode) {
        return Mono.defer(() -> {
            if (!pendingCode.isUsed() && pendingCode.getEmail() != null && !pendingCode.getEmail().isBlank()) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(pendingCode.getEmail());
                message.setSubject("Confirm your email");
                message.setText("Please confirm your email by clicking the link below: \n" +
                                authLink +"?code=" + pendingCode.getCode());

                try {
                    emailSender.send(message);
                    log.info("Confirmation email sent to {}", pendingCode.getEmail());
                } catch (Exception ex) {
                    log.error("Failed to send email to {}: {}", pendingCode.getEmail(), ex.getMessage());
                }
            } else {
                log.warn("Skipping email: Email is null or already used for userId {}", pendingCode.getUserId());
            }
            return Mono.empty();
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

}
