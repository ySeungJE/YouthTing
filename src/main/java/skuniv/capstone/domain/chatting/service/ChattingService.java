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
    private final RoomRepository roomRepository;
    private Map<Long, Set<WebSocketSession>> roomSessions = new HashMap<>();

    // 실제로 메세지를 전달하는 함수
    public <T> void sendMessage(WebSocketSession session, T message) {
        try{
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void handleAction(WebSocketSession session, Chatting message, Map<Long, Set<WebSocketSession>> roomSessions) {
        // 채팅룸 입장인원 정보 전달
        this.roomSessions = roomSessions;

        // 해당 room db 에 채팅 객체 추가
        Room room = roomRepository.findById(message.getRoomNum()).orElse(null);
        room.addChatting(message);

        // 현재 입장중인 사람들에게 실시간 메세지 전송. 퇴장했던 사람도 입장 시 room에서 채팅 정보를 끌고 오니 내용 확인 가능
        sendMessage(message, message.getRoomNum());
    }

    // 채팅룸 속 모든 유저에게 sendMessage 날리는 함수
    public <T> void sendMessage(T message, Long roomId) {
        System.out.println("roomSessions.get(roomId) = " + roomSessions.get(roomId));
        roomSessions.get(roomId).parallelStream().forEach(session -> sendMessage(session, message));
    }
}
