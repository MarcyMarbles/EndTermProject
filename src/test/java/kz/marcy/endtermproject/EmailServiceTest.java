package kz.marcy.endtermproject;

import kz.marcy.endtermproject.Service.EmailService;
import kz.marcy.endtermproject.Service.PendingCodes;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Test
    void testSendConfirmationEmail() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        EmailService service = new EmailService(mailSender);

        PendingCodes pendingCode = new PendingCodes();
        pendingCode.setEmail("test@example.com");
        pendingCode.setCode("12345");

        StepVerifier.create(service.sendConfirmationEmail(pendingCode))
                .verifyComplete();

        verify(mailSender, times(1)).send(Mockito.any(SimpleMailMessage.class));
    }
}