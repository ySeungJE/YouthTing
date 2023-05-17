package skuniv.capstone.domain.chatting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import skuniv.capstone.domain.chatting.Chatting;
import skuniv.capstone.domain.room.Room;
import skuniv.capstone.domain.room.repository.RoomRepository;
import skuniv.capstone.domain.room.service.RoomService;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChattingService {
    private final ObjectMapper mapper;
//    private Map<Long, Set<WebSocketSession>> roomSessions = new HashMap<>();
    private final RoomRepository roomRepository;
    private Map<Long, Set<WebSocketSession>> roomSessions = new HashMap<>();
    public <T> void sendMessage(WebSocketSession session, T message) {
        try{
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void handleAction(WebSocketSession session, Chatting message, Map<Long, Set<WebSocketSession>> roomSessions) {
        System.out.println("ChattingService.handleAction");

        this.roomSessions = roomSessions;

        Room room = roomRepository.findById(message.getRoomNum()).orElse(null);
        room.addChatting(message);

        // chatting 이 room 에 들어가는 거까지 ok, 이제 웹소켓 송신하는거 이어서 구현하자

//        if (roomSessions.isEmpty()) {
//            roomSessions.put(message.getRoomNum(), new HashSet<>());
//            roomSessions.get(message.getRoomNum()).add(session);
//        } else {
//            roomSessions.get(message.getRoomNum()).add(session);
//        }
        sendMessage(message, message.getRoomNum());
//        if (message.getType().equals(ChatDTO.MessageType.ENTER)) { // 나같은 경우에는 파라미터에 미팅멤버 list 를 추가해서, 한명씩 돌리면서 이 ENTER 작업을 해줘야지
//            // sessions 에 넘어온 session 을 담고,
//            sessions.add(session, message.getRoom().getId());
//
//            // message 에는 입장하였다는 메시지를 띄운다
//            message.setMessage(message.getSender() + " 님이 입장하셨습니다");
//            sendMessage(message);
//        } else if (message.getType().equals(ChatDTO.MessageType.TALK)) {
//            message.setMessage(message.getMessage());
//            sendMessage(message, message.getRoom().getId());
//        }
    }

    public <T> void sendMessage(T message, Long roomId) {
        System.out.println("roomSessions.get(roomId) = " + roomSessions.get(roomId));
        roomSessions.values(); //   HttpSessionHandshakeInterceptor 를 사용해서 attribute 에 roomId 가져오면 됨. 그걸 내일 하자
        roomSessions.get(roomId).parallelStream().forEach(session -> sendMessage(session, message));
    }
}
