package skuniv.capstone.domain.chatting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import skuniv.capstone.domain.chatting.Chatting;
import skuniv.capstone.domain.chatting.RedisChatting;
import skuniv.capstone.domain.chatting.repository.ChattingRedisRepository;
import skuniv.capstone.domain.chatting.repository.ChattingRepository;
import skuniv.capstone.domain.room.Room;
import skuniv.capstone.domain.room.repository.RoomRepository;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChattingService {
    private final ObjectMapper mapper;
    private final RoomRepository roomRepository;
    private final ChattingRepository chattingRepository;
    private final ChattingRedisRepository chattingRedisRepository;
    private final RedisTemplate<String, String> redisTemplate;
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

        chattingRepository.save(message);


        chattingRedisRepository.save(new RedisChatting(message.getId(),message.getRoomNum(), message.getContent(), message.getTime(), message.getUserId()
                                                    ,message.getSender(), message.getStoreProfileName()));

        log.info("저장된 레디스 data : {}", chattingRedisRepository.findAll());
        log.info("해당 방에 저장된 RedisChatting : {}", chattingRedisRepository.findByRoomNum(room.getId()));
//        chattingRedisRepository.deleteAll();
        //resid test
//        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
//        valueOperations.set(message.getSender(), message.getContent());

//        ListOperations<String, String> listOperations = redisTemplate.opsForList();
//        listOperations.leftPush(message.getSender(), message.getContent());

        // 현재 입장중인 사람들에게 실시간 메세지 전송. 퇴장했던 사람도 입장 시 room에서 채팅 정보를 끌고 오니 내용 확인 가능
        sendMessage(message, message.getRoomNum());
    }

    // 채팅룸 속 모든 유저에게 sendMessage 날리는 함수
    public <T> void sendMessage(T message, Long roomId) {
        log.info("roomSessions.get(roomId) = {}", roomSessions.get(roomId));
        roomSessions.get(roomId).parallelStream().forEach(session -> sendMessage(session, message));
    }
}
