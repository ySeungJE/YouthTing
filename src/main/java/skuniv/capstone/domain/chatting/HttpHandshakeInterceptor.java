package skuniv.capstone.domain.chatting;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import skuniv.capstone.domain.user.User;

import java.util.Map;

import static skuniv.capstone.web.login.controller.LoginController.*;

public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    @Override // 웹소켓 연결 전에 호출되는 메소드. WebSocketSession 안에 user의 정보를 입력한다
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map attributes) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            User user = (User)servletRequest.getServletRequest().getSession().getAttribute(LOGIN_USER);
            attributes.put("user", user);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
    }
}