package skuniv.capstone.domain.chatting;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import skuniv.capstone.domain.chatting.service.ChattingService;
import skuniv.capstone.domain.room.Room;
import skuniv.capstone.domain.room.repository.RoomRepository;


@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper;
//    private final RoomRepository roomRepository;
    private final ChattingService chattingService;

    @Override // 프론트에서 send한 걸 일단 얘가 받았잖아. 일단 받았으면 대박인거ㅋㅋㅋㅋㅋ
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String payload = message.getPayload();
        log.info("payload {}", payload);

//        TextMessage textMessage = new TextMessage("Welcome Chatting Server");
//        session.sendMessage(textMessage);

        Chatting chatting = mapper.readValue(payload, Chatting.class); // 그럼 매핑만 되게 html쪽 수정 하고
//        Room room = roomRepository.findById(chatting.getRoomNum()).orElse(null);
//        room.addChatting(chatting); // 여기서 동일한 작업을 해도 lazy 초기화가 안되는 건... 웹소켓 내부이거나 override 메소드이거나 protected 여서...?
        log.info("chatting {}", chatting.toString());
//        log.info("room {}", room.toString());

        chattingService.handleAction(session, chatting); // 여기부터 계속하면 되겠다
    }
}