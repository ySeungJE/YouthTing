package skuniv.capstone.web.request.room.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import skuniv.capstone.domain.chatting.Chatting;
import skuniv.capstone.domain.request.Meeting;
import skuniv.capstone.domain.request.RequestStatus;
import skuniv.capstone.domain.room.Room;
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
@Controller
@RequestMapping("/meeting")
@RequiredArgsConstructor
public class RoomController {

    private final UserService userService;
    private final RoomService roomService;
    private final RequestService requestService;

    @PostMapping("/request/{email}")
    public String requestGroup(@PathVariable String email, HttpServletRequest request) {
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
            log.info("에러 발생 : 상대에게 미팅을 신청할 수 없습니다");
            throw new IllegalStateException();
        }
        return "redirect:/user/list";
    }

    @GetMapping("/receive")
    public String receiveList(HttpServletRequest request, Model model) {
        User me = userService.getSessionUser(request);
        List<UserRequest> list = me.getReceiveRequestList();

        List<ReceiveRequestDto> collect = list.stream()
                .filter(u -> u.getRequest().getRequestType() == MEETING)
                .map(u -> new ReceiveRequestDto(u))
                .collect(toList());

        model.addAttribute("requestList", collect);
        return "/meeting/receive";
    }

    @GetMapping("/send")
    public String sendList(HttpServletRequest request, Model model) {
        User me = userService.getSessionUser(request);
        List<UserRequest> list = me.getSendRequestList();

        List<SendRequestDto> collect = list.stream()
                .filter(u -> u.getRequest().getRequestType() == MEETING)
                .map(u -> new SendRequestDto(u))
                .collect(toList());

        model.addAttribute("requestList", collect);
        return "/meeting/send";
    }

    @PostMapping("/success/{requestId}") // 룸 객체가 만들어지고 유저와 양방향 매핑됨
    public String successMeeting(@PathVariable Long requestId) {
        UserRequest userRequest = requestService.findUserRequest(requestId);
        Meeting meeting = (Meeting) Hibernate.unproxy(requestService.findRequest(userRequest.getRequest().getId())); // proxy 를 해제하는 것으로 형 변환을 할 수 있다. 근데 접때는 대체 어떻게 그냥 형변환한거지
        roomService.successMeeting(userRequest, meeting);
        return "redirect:/meeting/receive";
    }

    @GetMapping("/member")
    public List<UserDto> groupMember(HttpServletRequest request) {
        User user = userService.getSessionUser(request);
        List<User> groupUser = user.getRoom().getUserList();

        return groupUser.stream()
                .map(u -> new UserDto(u))
                .collect(toList());
    }

    @PostMapping("/fail/{userRequestId}")
    public String meetingFail(@PathVariable Long userRequestId, HttpServletRequest request) {
        UserRequest userRequest = requestService.findUserRequest(userRequestId);
        requestService.meetingFail(userRequest);
        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/chatting")
    public String goChat(HttpServletRequest request, Model model) {
        User sessionUser = userService.getSessionUser(request);

        if (sessionUser.getRoom() == null) {
            return "redirect:/user/start";
        }

        List<Chatting> chattingList = sessionUser.getRoom().getChattingList(); // 이게 지금 다 lazy 초기화하는 거지
        model.addAttribute("roomName", sessionUser.getRoom().getName());
        model.addAttribute("chatList", chattingList);
        model.addAttribute("myId", sessionUser.getId());
        model.addAttribute("sender", sessionUser.getName());
        return "chatting";
    }

    @PostMapping("/exit")
    public String exitRoom(HttpServletRequest request) {
        User sessionUser = userService.getSessionUser(request);
        roomService.exitRoom(sessionUser);
        return "redirect:/";
    }

    @GetMapping("/showMap")
    public String showMap(HttpServletRequest request) {
//        User sessionUser = userService.getSessionUser(request);
//        roomService.exitRoom(sessionUser);
        return "/meeting/showMap";
    }
}
