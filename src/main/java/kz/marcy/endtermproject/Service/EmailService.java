package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.Users;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private final JwtUtils jwtUtils;

    public EmailService(JavaMailSender emailSender, JwtUtils jwtUtils) {
        this.emailSender = emailSender;
        this.jwtUtils = jwtUtils;
    }

    public Mono<Void> sendConfirmationEmail(Users to) {
        return Mono.defer(() -> {
            if (to.isPending() && to.getEmail() != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to.getEmail());
                message.setSubject("Confirm your email");
                message.setText("Please confirm your email by clicking the link below: \n" +
                                "http://localhost:5173/confirm?" +
                                "token=" + jwtUtils.generateToken(to.getLogin(), to.getRoles().getCode(), to.getId()) +
                                "&email=" + to.getEmail());
                emailSender.send(message);
            }
            return Mono.empty();
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
