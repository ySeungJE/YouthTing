package skuniv.capstone.domain.chatting;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import skuniv.capstone.domain.chatting.service.ChattingService;
import skuniv.capstone.domain.room.Room;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper;

    private final ChattingService service;

    @Override // 프론트에서 send한 걸 일단 얘가 받았잖아. 일단 받았으면 대박인거ㅋㅋㅋㅋㅋ
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload {}", payload);

//        TextMessage textMessage = new TextMessage("Welcome Chatting Server");
//        session.sendMessage(textMessage);

        Chatting chatMessage = mapper.readValue(payload, Chatting.class); // 그럼 매핑만 되게 html쪽 수정 하고
        log.info("session {}", chatMessage.toString());  

        Room room = service.findRoomById(chatMessage.getRoom().getId()); // room 은 포스트맨으로 미리 만들어놓고 다시 해보면 됨 오늘은 여기까지 쉬버
        log.info("room {}", room.toString());

        service.handleAction(session, chatMessage); // 여기부터 계속하면 되겠다
    }
}