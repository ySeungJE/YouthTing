package skuniv.capstone.domain.room.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skuniv.capstone.domain.request.*;
import skuniv.capstone.domain.room.Room;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.userrequest.UserRequest;
import skuniv.capstone.domain.userrequest.repository.UserRequestRepository;

import static skuniv.capstone.domain.request.RequestStatus.SUCCESS;
import static skuniv.capstone.domain.request.RequestStatus.WAIT;
import static skuniv.capstone.domain.request.RequestType.*;
import static skuniv.capstone.domain.request.SoloOrGroup.*;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {
    private final UserRequestRepository userRequestRepository;
    public void requestGroupRoom(User me, User someone) {
        Meeting meeting = Meeting.createMeeting(me.getName(), WAIT, MEETING, GROUP);
        UserRequest userRequest = UserRequest.createUserRequest(me, someone, meeting);
        userRequest.requestProcess();
    }
    public void requestSoloRoom(User me, User someone) {
        Meeting meeting = Meeting.createMeeting(me.getName(), WAIT, MEETING, SOLO);
        UserRequest userRequest = UserRequest.createUserRequest(me, someone, meeting);
        userRequest.requestProcess();
    }
    public void successMeeting(Long requestId) {
        UserRequest userRequest = userRequestRepository.findById(requestId).orElse(null);
        userRequest.getRequest().changeStatus(SUCCESS);
        Room room = Room.createRoom(userRequest.getSendUser().getName(), userRequest.getReceiveUser().getName());
//        ((Meeting) userRequest.getRequest()).updateRoom(room); // JPA 에서 형 변환은 안되는걸로.
        room.enterUsers(userRequest.getSendUser().getGroup().getUserList());
        room.enterUsers(userRequest.getReceiveUser().getGroup().getUserList());
    }
}
