package kz.marcy.endtermproject.Handlers;

import kz.marcy.endtermproject.Entity.Users;
import kz.marcy.endtermproject.Repository.UserRepo;
import kz.marcy.endtermproject.Service.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.net.URI;


@Controller
public class UserWebSocketHandler implements WebSocketHandler {

    @Autowired
    private UserRepo userRepository;

    private final JwtUtils jwtUtils;

    private final Sinks.Many<Users> userSink = Sinks.many().multicast().onBackpressureBuffer();

    public UserWebSocketHandler(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    public void publishUser(Users user) {
        userSink.tryEmitNext(user);
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(
                userSink.asFlux()
                        .map(user -> session.textMessage(user.toString()))
        ).and(
                session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .doOnNext(msg -> System.out.println("Received from client: " + msg))
                        .then()
        );
    }

}
