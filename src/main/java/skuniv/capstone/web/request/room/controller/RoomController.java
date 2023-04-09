package skuniv.capstone.web.request.room.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import skuniv.capstone.domain.group.Group;
import skuniv.capstone.domain.group.service.GroupService;
import skuniv.capstone.domain.room.service.RoomService;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.user.service.UserService;
import skuniv.capstone.domain.userrequest.UserRequest;
import skuniv.capstone.web.request.ReceiveRequestDto;
import skuniv.capstone.web.request.SendRequestDto;
import skuniv.capstone.web.request.UserDto;
import skuniv.capstone.web.request.group.controller.GroupController;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static skuniv.capstone.domain.request.RequestType.INVITE;
import static skuniv.capstone.domain.request.RequestType.MEETING;

@Slf4j
@RestController
@RequestMapping("/meeting")
@RequiredArgsConstructor
public class RoomController {

    private final UserService userService;
    private final GroupService groupService;
    private final RoomService roomService;
    @PostMapping("/request/{email}")
    public void requestGroup(@PathVariable String email, HttpServletRequest request) {
        User me = userService.getSessionUser(request); // user 에 friendEmailList 를 추가해. 이메일이 있냐? 이것만 보면 됨
        User someone = userService.findByEmail(email);

        if (me.getGroup().getIdle() == true && someone.getGroup().getIdle() == true) {
            log.info("{}님의 그룹에게 미팅을 신청했습니다", someone.getName());
            roomService.requestGroupRoom(me, someone);
        } else if (me.getIdle() == true && someone.getIdle() == true) {
            log.info("{}님에게 미팅을 신청했습니다}");
            roomService.requestSoloRoom(me, someone);
        } else {
            log.info("에러 발생 : 채팅룸에 입장할 수 없습니다");
            throw new IllegalStateException();
        }
    }
    @GetMapping("/receive")
    public List<ReceiveRequestDto> receiveList(HttpServletRequest request) {
        User me = userService.getSessionUser(request);
        List<UserRequest> list = me.getReceiveRequestList();

        return list.stream()
                .filter(u-> u.getRequest().getRequestType()==MEETING )
                .map(u -> new ReceiveRequestDto(u))
                .collect(toList());
    }
    @GetMapping("/send")
    public List<SendRequestDto> sendList(HttpServletRequest request) {
        User me = userService.getSessionUser(request);
        List<UserRequest> list = me.getSendRequestList();

        return list.stream()
                .filter(u-> u.getRequest().getRequestType()==MEETING )
                .map(u -> new SendRequestDto(u))
                .collect(toList());
    }
    @PostMapping("/success/{requestId}") // request 상태가 SUCCESS 로 변경되고 마스터의 그룹에 게스트가 추가됨
    public void successMeeting(@PathVariable Long requestId) {
        roomService.successMeeting(requestId);
    }
//    @GetMapping("/member")
//    public GroupController.GroupMember groupMember(HttpServletRequest request) {
//        User user = userService.getSessionUser(request);
//        List<User> groupUser = user.getGroup().getUserList();
//
//        List<UserDto> guest = groupUser.stream()
//                .filter(u -> u.getGroup().getMaster()!=u)
//                .map(u -> new UserDto(u))
//                .collect(toList());
//        return new GroupController.GroupMember(new UserDto(user.getGroup().getMaster()), guest);
//    }
}
