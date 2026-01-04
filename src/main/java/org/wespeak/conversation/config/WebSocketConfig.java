package org.wespeak.conversation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.wespeak.conversation.websocket.SignalingWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  private final SignalingWebSocketHandler signalingHandler;

  public WebSocketConfig(SignalingWebSocketHandler signalingHandler) {
    this.signalingHandler = signalingHandler;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(signalingHandler, "/ws/signaling").setAllowedOrigins("*");
  }
}
