package kz.marcy.endtermproject.WebSocketHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kz.marcy.endtermproject.Entity.Comments;
import kz.marcy.endtermproject.Entity.News;
import kz.marcy.endtermproject.Entity.Transient.Message;
import kz.marcy.endtermproject.Repository.UserRepo;
import kz.marcy.endtermproject.Service.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.List;

@Controller
public class CommentWebSocketHandler implements WebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(CommentWebSocketHandler.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Sinks.Many<String> commentSink = Sinks.many().multicast().onBackpressureBuffer();

    public CommentWebSocketHandler() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    public void publishCommentary(Comments comments, String type) {
        Message message = new Message(type, comments, List.of());
        try {
            String json = objectMapper.writeValueAsString(message);
            commentSink.tryEmitNext(json);
        } catch (JsonProcessingException e) {
            log.error("Error serializing news: {}", e.getMessage());
        }
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(
                commentSink.asFlux()
                        .map(session::textMessage)
        ).and(
                session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .doOnNext(msg -> System.out.println("Received from client: " + msg))
                        .then()
        );
    }
}
