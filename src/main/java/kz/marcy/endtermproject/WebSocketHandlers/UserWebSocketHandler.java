package kz.marcy.endtermproject.WebSocketHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kz.marcy.endtermproject.Entity.Transient.Message;
import kz.marcy.endtermproject.Entity.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;


@Controller
public class UserWebSocketHandler implements WebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(UserWebSocketHandler.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Sinks.Many<String> userSink = Sinks.many().multicast().onBackpressureBuffer();

    public UserWebSocketHandler() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    public void publishUser(Users user, String type) {
        Message message = new Message(type, user, null);
        try {
            String json = objectMapper.writeValueAsString(message);
            userSink.tryEmitNext(json);
        } catch (JsonProcessingException e) {
            log.error("Error serializing user: {}", e.getMessage());
        }
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(
                userSink.asFlux()
                        .map(session::textMessage)
        ).and(
                session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .doOnNext(msg -> System.out.println("Received from client: " + msg))
                        .then()
        );
    }

}
