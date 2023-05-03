package skuniv.capstone.web.request.room.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.web.bind.annotation.*;
import skuniv.capstone.domain.request.Meeting;
import skuniv.capstone.domain.room.service.RoomService;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.user.service.UserService;
import skuniv.capstone.domain.userrequest.UserRequest;
import skuniv.capstone.domain.userrequest.sevice.RequestService;
import skuniv.capstone.web.request.ReceiveRequestDto;
import skuniv.capstone.web.request.SendRequestDto;
import skuniv.capstone.web.request.UserDto;
import skuniv.capstone.web.request.room.dto.ShowChattingDto;
import skuniv.capstone.web.request.room.dto.ChattingSendDto;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static skuniv.capstone.domain.request.RequestType.MEETING;

@Slf4j
@RestController
@RequestMapping("/meeting")
@RequiredArgsConstructor
public class RoomController {

    private final UserService userService;
    private final RoomService roomService;
    private final RequestService requestService;

    @PostMapping("/request/{email}")
    public void requestGroup(@PathVariable String email, HttpServletRequest request) {
        User me = userService.getSessionUser(request);
        User someone = userService.findByEmail(email);
        log.info("someone={}", email);
        if (me.getIdle() == true && someone.getIdle() == true) {
            log.info("{}님에게 미팅을 신청했습니다", someone.getName());
            roomService.requestSoloRoom(me, someone);
        } else if (me.getGroup().getIdle() == true && someone.getGroup().getIdle() == true) {
            log.info("{}님의 그룹에게 미팅을 신청했습니다", someone.getName());
            roomService.requestGroupRoom(me, someone);
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
                .filter(u -> u.getRequest().getRequestType() == MEETING)
                .map(u -> new ReceiveRequestDto(u))
                .collect(toList());
    }

    @GetMapping("/send")
    public List<SendRequestDto> sendList(HttpServletRequest request) {
        User me = userService.getSessionUser(request);
        List<UserRequest> list = me.getSendRequestList();

        return list.stream()
                .filter(u -> u.getRequest().getRequestType() == MEETING)
                .map(u -> new SendRequestDto(u))
                .collect(toList());
    }

    @PostMapping("/success/{requestId}") // 룸 객체가 만들어지고 유저와 양방향 매핑됨
    public void successMeeting(@PathVariable Long requestId) {
        UserRequest userRequest = requestService.findUserRequest(requestId);
        Meeting meeting = (Meeting) Hibernate.unproxy(requestService.findRequest(userRequest.getRequest().getId())); // proxy 를 해제하는 것으로 형 변환을 할 수 있다. 근데 접때는 대체 어떻게 그냥 형변환한거지
        roomService.successMeeting(userRequest, meeting);
    }

    @GetMapping("/member")
    public List<UserDto> groupMember(HttpServletRequest request) {
        User user = userService.getSessionUser(request);
        List<User> groupUser = user.getRoom().getUserList();

        return groupUser.stream()
                .map(u -> new UserDto(u))
                .collect(toList());
    }

    @PostMapping("/chatting") // 이거 왜 안됐었냐? 케스케이드를 안했잖아ㅋㅋㅋㅋㅋ persist 가 전파가 안되니까 chatting 이 save가 안된거지
    public void chat(@RequestBody ChattingSendDto chatting, HttpServletRequest request) {
        User me = userService.getSessionUser(request);
        roomService.createChatting(me,chatting.getContent());
    }

    @GetMapping("/chatting")
    public List<ShowChattingDto> allChatting(HttpServletRequest request) {
        User sessionUser = userService.getSessionUser(request);

        return sessionUser.getRoom().getChattingList()
                .stream()
                .map(c -> new ShowChattingDto(c.getContent(), c.getUser()))
                .collect(Collectors.toList());
    }
}
