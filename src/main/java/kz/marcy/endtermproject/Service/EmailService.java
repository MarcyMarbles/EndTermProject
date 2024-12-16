package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.Users;
import kz.marcy.endtermproject.Service.JwtUtils;
import kz.marcy.endtermproject.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private final JwtUtils jwtUtils;

    @Autowired
    public EmailService(JavaMailSender emailSender, UserService userService, JwtUtils jwtUtils) {
        this.emailSender = emailSender;
        this.jwtUtils = jwtUtils;
    }

    public Mono<Void> sendConfirmationEmail(Users to) {
        return Mono.fromRunnable(() -> {
                    if (to.isPending() && to.getEmail() != null) {
                        SimpleMailMessage message = new SimpleMailMessage();
                        message.setTo(to.getEmail());
                        message.setSubject("Confirm your email");
                        message.setText("Please confirm your email by clicking the link below: \n" +
                                        "http://localhost:8080/confirm?email="
                                        + to.getEmail()
                                        + "&token=" + jwtUtils.generateToken(to.getLogin(), to.getRoles().getCode(), to.getId()));
                        emailSender.send(message);
                    }
                }).subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
