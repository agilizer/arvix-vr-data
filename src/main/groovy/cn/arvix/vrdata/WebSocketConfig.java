package cn.arvix.vrdata;


import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Configuration
@EnableWebSocket
@EnableScheduling
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(), "/websocket/endpoint")
            .setAllowedOrigins("*")
            .withSockJS().setHttpMessageCacheSize(2000);
    }

    @Bean
    public WebSocketTransactionsHandler myHandler() {
        return new WebSocketTransactionsHandler();
    }

    public static class WebSocketTransactionsHandler extends TextWebSocketHandler {
        private final WebSocketSessions sessions = new WebSocketSessions();

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            sessions.add(session);
        }

        @Override
        public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
            sessions.sendMessage(message);
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            sessions.remove(session);
        }

        public void broadcastMessage(TextMessage message) throws IOException {
            sessions.sendMessage(message);
        }
    }

    public static class WebSocketSessions {
        Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

        public void add(WebSocketSession session) {
            sessions.put(session.getId(), session);
        }

        public void remove(WebSocketSession session) {
            sessions.remove(session.getId());
        }

        public void sendMessage(TextMessage message) throws IOException {
            for (String s : sessions.keySet()) {
                sessions.get(s).sendMessage(message);
            }
        }
    }




}
