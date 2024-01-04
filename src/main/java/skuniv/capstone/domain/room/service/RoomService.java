package skuniv.capstone.domain.room.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skuniv.capstone.domain.chatting.Chatting;
import skuniv.capstone.domain.chatting.RedisChatting;
import skuniv.capstone.domain.chatting.WebSocketChatHandler;
import skuniv.capstone.domain.chatting.repository.ChattingRedisRepository;
import skuniv.capstone.domain.chatting.repository.ChattingRepository;
import skuniv.capstone.domain.request.*;
import skuniv.capstone.domain.room.Room;
import skuniv.capstone.domain.room.repository.RoomRepository;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.user.repository.UserRepository;
import skuniv.capstone.domain.userrequest.UserRequest;
import skuniv.capstone.web.request.group.controller.GroupController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static skuniv.capstone.domain.chatting.WebSocketChatHandler.*;
import static skuniv.capstone.domain.request.RequestStatus.*;
import static skuniv.capstone.domain.request.RequestType.*;
import static skuniv.capstone.domain.request.SoloOrGroup.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final ChattingRepository chattingRepository;
    private final ChattingRedisRepository chattingRedisRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final WebSocketChatHandler webSocketChatHandler;
    public void requestGroupRoom(User me, User someone) {
        me.getReceiversGroup().add(someone.getGroup().getId());
        Meeting meeting = Meeting.createMeeting(me.getName()+"의 그룹",someone.getName()+"의 그룹", WAIT, MEETING, GROUP);
        UserRequest userRequest = UserRequest.createUserRequest(me, someone, meeting);
        meeting.updateUserRequest(userRequest);
        userRequest.requestProcess();
    }
    public void requestSoloRoom(User me, User someone) {
        me.getReceiversSolo().add(someone.getId());
        Meeting meeting = Meeting.createMeeting(me.getName(), someone.getName(),WAIT, MEETING, SOLO);
        UserRequest userRequest = UserRequest.createUserRequest(me, someone, meeting);
        meeting.updateUserRequest(userRequest);
        userRequest.requestProcess();
    }
    public void successMeeting(UserRequest userRequest, Meeting meeting) {
        userRequest.getRequest().changeStatus(SUCCESS);
        if (meeting.getSoloOrGroup() == SOLO) {
            Room room = Room.createSoloRoom(userRequest.getSendUser().getName(), userRequest.getReceiveUser().getName(), userRequest, SOLO);
            room.enterUser(userRequest.getSendUser(), userRequest.getReceiveUser());
            log.info("{},{}의 미팅이 성사되었습니다", userRequest.getSendUser().getName(),userRequest.getReceiveUser().getName());
        } else {
            Room room = Room.createGroupRoom(userRequest.getSendUser().getName(), userRequest.getReceiveUser().getName(), userRequest, GROUP);
            room.enterGroup(userRequest.getSendUser().getGroup().getUserList()
                    ,userRequest.getReceiveUser().getGroup().getUserList());
            log.info("{}님의 그룹,{}님의 그룹의 미팅이 성사되었습니다", userRequest.getSendUser().getName(),userRequest.getReceiveUser().getName());
        }
    }
    public Chatting createChatting(Long userId, Room room,String sender ,String content, String time) {
        return Chatting.createChatting(content, sender,userId,room,time);
    }

    public List<RedisChatting> getRoomChatting(Long roomId) { // redis에 해당 방 채팅정보가 없으면 DB에서 load 해주는 코드
        List<RedisChatting> chattings = chattingRedisRepository.findByRoomNum(roomId);
        if (chattings.isEmpty()) {
            List<Chatting> chattingsDB = chattingRepository.findByRoomNum(roomId);
            for (Chatting chatting : chattingsDB) {
                RedisChatting redisChatting = new RedisChatting(chatting);
                chattingRedisRepository.save(redisChatting);
                chattings.add(redisChatting);
            }
        }
        return chattings;
    }
    public Room findRoomById(Long id) {
        return roomRepository.findById(id).orElse(null);
    }

    public void exitRoom(User sessionUser) {
        Room room = sessionUser.getRoom();
        User partner = (room.getUserRequest().getSendUser() == sessionUser)
                ? room.getUserRequest().getReceiveUser():room.getUserRequest().getSendUser();
        log.info("상대이름 : {}", partner.getName());
        if(room.getSoloOrGroup()==SOLO) sessionUser.getReceiversSolo().remove(partner.getId());
        else if(room.getSoloOrGroup()==GROUP) sessionUser.getReceiversGroup().remove(partner.getGroup().getId());
        if (room.getUserList().size() == 1) {
            sessionUser.exitRoom();
//            log.info(" 전 : {}", webSocketChatHandler.getRoomSessions());
            roomRepository.delete(room);
            webSocketChatHandler.removeSession(room.getId()); // room이 없어지면 웹소켓 세션도 없어지게 함
//            log.info(" 후 : {}", webSocketChatHandler.getRoomSessions());
        } else {
            sessionUser.exitRoom();
        }
    }
}
