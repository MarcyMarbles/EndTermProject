package kz.marcy.endtermproject.WebSocketHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kz.marcy.endtermproject.Entity.News;
import kz.marcy.endtermproject.Entity.Transient.Message;
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
public class NewsWebSocketHandler implements WebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(NewsWebSocketHandler.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Sinks.Many<String> newsSink = Sinks.many().multicast().onBackpressureBuffer();

    public NewsWebSocketHandler() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    public void publishNews(News news, String type) {
        List<String> receivers = news.getAuthor().getFriends();
        Message message = new Message(type, news, receivers);
        try {
            String json = objectMapper.writeValueAsString(message);
            newsSink.tryEmitNext(json);
        } catch (JsonProcessingException e) {
            log.error("Error serializing news: {}", e.getMessage());
        }
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(
                newsSink.asFlux()
                        .map(session::textMessage)
        ).and(
                session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .doOnNext(msg -> System.out.println("Received from client: " + msg))
                        .then()
        );
    }
}
