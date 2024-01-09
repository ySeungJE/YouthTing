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
import skuniv.capstone.domain.request.SoloOrGroup;
import skuniv.capstone.domain.room.service.RoomService;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.user.service.UserService;
import skuniv.capstone.domain.userrequest.UserRequest;
import skuniv.capstone.domain.userrequest.sevice.RequestService;
import skuniv.capstone.web.request.ReceiveRequestDto;
import skuniv.capstone.web.request.SendRequestDto;
import skuniv.capstone.web.request.UserDto;
import skuniv.capstone.web.request.room.dto.ChattingMemberDto;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static skuniv.capstone.domain.request.RequestType.MEETING;
import static skuniv.capstone.domain.request.SoloOrGroup.*;

@Slf4j
@Controller
@RequestMapping("/meeting")
@RequiredArgsConstructor
public class RoomController {

    private final UserService userService;
    private final RoomService roomService;
    private final RequestService requestService;

    @PostMapping("/requestSoloTing/{email}")
    public String requestSoloTing(@PathVariable String email, HttpServletRequest request) {
        User me = userService.getSessionUser(request);
        User someone = userService.findByEmail(email);
        log.info("someone={}", email);
        if (me.getIdle() == true && someone.getIdle() == true) {
            log.info("{}님에게 미팅을 신청했습니다", someone.getName());
            roomService.requestSoloRoom(me, someone);
        } else {
            log.info("에러 발생 : 상대에게 미팅을 신청할 수 없습니다");
            throw new IllegalStateException();
        }
        return "redirect:" + request.getHeader("Referer");
    }
    @PostMapping("/requestGroupTing/{email}")
    public String requestGroupTing(@PathVariable String email, HttpServletRequest request) {
        User me = userService.getSessionUser(request);
        User someone = userService.findByEmail(email);
        log.info("someone={}", email);
        if (me.getGroup().getIdle() == true && someone.getGroup().getIdle() == true) {
            log.info("{}님의 그룹에게 미팅을 신청했습니다", someone.getName());
            roomService.requestGroupRoom(me, someone);
        } else {
            log.info("에러 발생 : 상대에게 미팅을 신청할 수 없습니다");
            throw new IllegalStateException();
        }
        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/soloTingReceive")
    public String soloTingReceiveList(HttpServletRequest request, Model model, @RequestParam(required = false) String error) {
        User me = userService.getSessionUser(request);
        List<UserRequest> list = me.getReceiveRequestList();

        List<ReceiveRequestDto> collect = list.stream()
                .filter(u -> u.getRequest().getRequestType() == MEETING
                        && ((Meeting)Hibernate.unproxy((u.getRequest()))).getSoloOrGroup()==SOLO)
                .map(u -> new ReceiveRequestDto(u))
                .collect(toList());

        model.addAttribute("errorCode", error);
        model.addAttribute("requestList", collect);
        return "meeting/soloTingReceive";
    }

    @GetMapping("/soloTingSend")
    public String soloTingSendList(HttpServletRequest request, Model model) {
        User me = userService.getSessionUser(request);
        List<UserRequest> list = me.getSendRequestList();

        List<SendRequestDto> collect = list.stream()
                .filter(u -> u.getRequest().getRequestType() == MEETING
                        && ((Meeting)Hibernate.unproxy((u.getRequest()))).getSoloOrGroup()==SOLO)
                .map(u -> new SendRequestDto(u))
                .collect(toList());

        model.addAttribute("requestList", collect);
        return "meeting/soloTingSend";
    }
    @GetMapping("/groupTingReceive")
    public String groupTingReceiveList(HttpServletRequest request, Model model, @RequestParam(required = false) String error) {
        User me = userService.getSessionUser(request);
        List<UserRequest> list = me.getReceiveRequestList();

        List<ReceiveRequestDto> collect = list.stream()
                .filter(u -> u.getRequest().getRequestType() == MEETING
                        && ((Meeting)Hibernate.unproxy((u.getRequest()))).getSoloOrGroup()==GROUP)
                .map(u -> new ReceiveRequestDto(u))
                .collect(toList());

        model.addAttribute("errorCode", error);
        model.addAttribute("requestList", collect);
        return "meeting/groupTingReceive";
    }

    @GetMapping("/groupTingSend")
    public String groupTingSendList(HttpServletRequest request, Model model) {
        User me = userService.getSessionUser(request);
        List<UserRequest> list = me.getSendRequestList();

        List<SendRequestDto> collect = list.stream()
                .filter(u -> u.getRequest().getRequestType() == MEETING
                        && ((Meeting)Hibernate.unproxy((u.getRequest()))).getSoloOrGroup()==GROUP)
                .map(u -> new SendRequestDto(u))
                .collect(toList());

        model.addAttribute("requestList", collect);
        return "meeting/groupTingSend";
    }

    @PostMapping("/success/{requestId}") // 룸 객체가 만들어지고 유저와 양방향 매핑됨
    public String successMeeting(@PathVariable Long requestId, HttpServletRequest request) {
        UserRequest userRequest = requestService.findUserRequest(requestId);
        User sendUser = userService.findById(userRequest.getSendUser().getId());
        User receiveUser = userService.findById(userRequest.getReceiveUser().getId());

        if (sendUser.getRoom() != null || receiveUser.getRoom() != null) {
            StringBuffer referer = new StringBuffer(request.getHeader("Referer"));
            if(referer.indexOf("?")!=-1) referer.delete(referer.indexOf("?"), referer.length());
            return "redirect:" + referer + "?error=RoomAlreadyExist";
        }

        Meeting meeting = (Meeting) Hibernate.unproxy(requestService.findRequest(userRequest.getRequest().getId())); // proxy 를 해제하는 것으로 형 변환을 할 수 있다. 근데 접때는 대체 어떻게 그냥 형변환한거지
        roomService.successMeeting(userRequest, meeting);
        return "redirect:/meeting/chatting";
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
            return "redirect:/?error=noRoom";
        }


//        List<Chatting> chattingList = sessionUser.getRoom().getChattingList(); // 이게 지금 다 lazy 초기화하는 거지
        model.addAttribute("roomName", sessionUser.getRoom().getName());
        model.addAttribute("chatList", roomService.getRoomChatting(sessionUser.getRoom().getId()));
        model.addAttribute("myId", sessionUser.getId());
        model.addAttribute("sender", sessionUser.getName());
        return "chatting";
    }

    @GetMapping("/member")
    public String meetingMember(HttpServletRequest request, Model model) {
        User sessionUser = userService.getSessionUser(request);
        List<User> userList = sessionUser.getRoom().getUserList();

        List<ChattingMemberDto> memberList = userList.stream().map(u -> new ChattingMemberDto(u)).toList();

        model.addAttribute("memberList", memberList);
        return "meeting/member";
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
        return "meeting/showMap";
    }
}
