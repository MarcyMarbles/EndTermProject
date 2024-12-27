package kz.marcy.endtermproject.Configuration;

import kz.marcy.endtermproject.WebSocketHandlers.CommentWebSocketHandler;
import kz.marcy.endtermproject.WebSocketHandlers.NewsWebSocketHandler;
import kz.marcy.endtermproject.WebSocketHandlers.UserWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Map;

@Slf4j
@Configuration
public class WebSocketConfig {
    @Bean
    public HandlerMapping webSocketMapping(UserWebSocketHandler userWebSocketHandler,
                                           NewsWebSocketHandler newsWebSocketHandler,
                                           CommentWebSocketHandler commentWebSocketHandler) {
        Map<String, Object> handlerMap = Map.of(
                "/ws/users", userWebSocketHandler,
                "/ws/news", newsWebSocketHandler,
                "/ws/comments", commentWebSocketHandler
        );
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(10);
        mapping.setUrlMap(handlerMap);


        log.info("WebSocket routes configured: {}", handlerMap.keySet());

        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

}
