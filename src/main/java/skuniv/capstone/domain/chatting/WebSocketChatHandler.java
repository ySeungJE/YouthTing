package skuniv.capstone.domain.chatting;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import skuniv.capstone.domain.chatting.service.ChattingService;
import skuniv.capstone.domain.room.Room;
import skuniv.capstone.domain.room.repository.RoomRepository;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.user.repository.UserRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static skuniv.capstone.web.login.controller.LoginController.LOGIN_USER;


@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper;
    private final UserRepository userRepository;
    private final ChattingService chattingService;
    private Map<Long, Set<WebSocketSession>> roomSessions = new HashMap<>();

    @Override // 프론트에서 ws.send 로 메세지 보내면 호출되는 함수.
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String payload = message.getPayload();

        Chatting chatting = mapper.readValue(payload, Chatting.class);
        User user = (User)session.getAttributes().get("user");

        // 프론트에서 온 message 기반으로 객체 만들어주고, user 정보 삽입해서 db에 저장
        chatting.fillOthers(user.getName(), user.getId(), user.getStoreProfileName() ,getRoomId(user));

        // 웹소켓 실시간 통신을 위해서 호출
        chattingService.handleAction(session, chatting, roomSessions);
    }

    // 웹소켓 서버에 클라이언트가 접속하면 호출되는 메소드
    @Override // 웹소켓에 연결되면 호출되는 함수. WebSocketSession 에 넣어놨던 user 정보를 가져와서 해당 roomId에 WebSocketSession을 추가(입장)
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        Long roomId = getRoomId((User) session.getAttributes().get("user"));

        if (roomSessions.containsKey(roomId)==false) {
            roomSessions.put(roomId, new HashSet<>());
            roomSessions.get(roomId).add(session);
        } else {
            roomSessions.get(roomId).add(session);
        }

        super.afterConnectionEstablished(session);
        log.info("클라이언트 접속됨");
        log.info("roomSessions[{}] = {}", roomId, roomSessions.get(roomId));
    }

    @Override // 웹소켓 연결이 끊기면 호출되는 함수. 채팅룸에서 WebSocketSession를 제거함으로써 유저를 퇴장시킴
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long roomId = getRoomId((User) session.getAttributes().get("user"));

        roomSessions.get(roomId).remove(session);
        super.afterConnectionClosed(session, status);
        log.info("클라이언트 접속해제");
        log.info("roomSessions[{}] = {}", roomId, roomSessions.get(roomId));
    }

    @Transactional(readOnly = true)
    public Long getRoomId(User sessionUser) {
        User dbUser  = userRepository.findById(sessionUser.getId()).orElse(null);
        return dbUser.getRoom().getId();
    }

}