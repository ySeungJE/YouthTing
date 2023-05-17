package skuniv.capstone.domain.chatting;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper;
//    private final RoomRepository roomRepository;
    private final ChattingService chattingService;
    private Map<Long, Set<WebSocketSession>> roomSessions = new HashMap<>();

    @Override // 프론트에서 send한 걸 일단 얘가 받았잖아. 일단 받았으면 대박인거ㅋㅋㅋㅋㅋ
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String payload = message.getPayload();
        log.info("payload {}", payload);

        Chatting chatting = mapper.readValue(payload, Chatting.class);
        if (roomSessions.isEmpty()) {
            roomSessions.put(chatting.getRoomNum(), new HashSet<>());
            roomSessions.get(chatting.getRoomNum()).add(session);
        } else {
            roomSessions.get(chatting.getRoomNum()).add(session);
        }

        log.info("chatting {}", chatting.toString());

        chattingService.handleAction(session, chatting, roomSessions);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        System.out.println("클라이언트 접속해제");
    }
}