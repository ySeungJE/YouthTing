package skuniv.capstone.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import skuniv.capstone.domain.chatting.Chatting;
import skuniv.capstone.domain.room.Room;
import skuniv.capstone.domain.room.repository.RoomRepository;
import skuniv.capstone.domain.room.service.RoomService;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper;
    private final RoomRepository roomRepository;
    private final ChatService service;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload {}", payload);

//        TextMessage textMessage = new TextMessage("Welcome Chatting Server");
//        session.sendMessage(textMessage);

        Chatting chatMessage = mapper.readValue(payload, Chatting.class);
        log.info("session {}", chatMessage.toString());

        Room room = roomRepository.findById(chatMessage.getRoom().getId()).orElse(null);
        log.info("room {}", room.toString());

        room.handleAction(session, chatMessage, service);
    }
}
