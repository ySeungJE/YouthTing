package skuniv.capstone.domain.room.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skuniv.capstone.domain.chatting.Chatting;
import skuniv.capstone.domain.request.*;
import skuniv.capstone.domain.room.Room;
import skuniv.capstone.domain.room.repository.RoomRepository;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.user.repository.UserRepository;
import skuniv.capstone.domain.user.service.UserService;
import skuniv.capstone.domain.userrequest.UserRequest;

import static skuniv.capstone.domain.request.RequestStatus.SUCCESS;
import static skuniv.capstone.domain.request.RequestStatus.WAIT;
import static skuniv.capstone.domain.request.RequestType.*;
import static skuniv.capstone.domain.request.SoloOrGroup.*;
import static skuniv.capstone.web.login.controller.LoginController.LOGIN_USER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    public void requestGroupRoom(User me, User someone) {
        me.getReceiversGroup().add(someone.getGroup().getId());
        Meeting meeting = Meeting.createMeeting(me.getName()+"의 그룹",someone.getName()+"의 그룹", WAIT, MEETING, GROUP);
        UserRequest userRequest = UserRequest.createUserRequest(me, someone, meeting);
        userRequest.requestProcess();
    }
    public void requestSoloRoom(User me, User someone) {
        me.getReceiversSolo().add(someone.getId());
        Meeting meeting = Meeting.createMeeting(me.getName(), someone.getName(),WAIT, MEETING, SOLO);
        UserRequest userRequest = UserRequest.createUserRequest(me, someone, meeting);
        userRequest.requestProcess();
    }
    public void successMeeting(UserRequest userRequest, Meeting meeting) {
        userRequest.getRequest().changeStatus(SUCCESS);
        if (meeting.getSoloOrGroup() == SOLO) {
            Room room = Room.createSoloRoom(userRequest.getSendUser().getName(), userRequest.getReceiveUser().getName());
            room.enterUser(userRequest.getSendUser(), userRequest.getReceiveUser());
            log.info("{},{}의 미팅이 성사되었습니다", userRequest.getSendUser().getName(),userRequest.getReceiveUser().getName());
        } else {
            Room room = Room.createGroupRoom(userRequest.getSendUser().getName(), userRequest.getReceiveUser().getName());
            room.enterGroup(userRequest.getSendUser().getGroup().getUserList()
                    ,userRequest.getReceiveUser().getGroup().getUserList());
            log.info("{}님의 그룹,{}님의 그룹의 미팅이 성사되었습니다", userRequest.getSendUser().getName(),userRequest.getReceiveUser().getName());
        }
    }
    public void createChatting(User me,String content, String time) {
        Chatting.createChatting(content, me, me.getRoom(),time);

    }
    public Room findRoomById(Long id) {
        return roomRepository.findById(id).orElse(null);
    }

}
