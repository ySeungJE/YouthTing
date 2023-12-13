package skuniv.capstone.web.request.group.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import skuniv.capstone.domain.group.Group;
import skuniv.capstone.domain.group.repository.GroupSearch;
import skuniv.capstone.domain.group.service.GroupService;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.user.repository.UserSearch;
import skuniv.capstone.domain.user.service.UserService;
import skuniv.capstone.domain.userrequest.UserRequest;
import skuniv.capstone.domain.userrequest.sevice.RequestService;
import skuniv.capstone.web.request.ReceiveRequestDto;
import skuniv.capstone.web.request.SendRequestDto;
import skuniv.capstone.web.request.UserDto;
import skuniv.capstone.web.request.group.dto.GroupDto;
import skuniv.capstone.web.request.group.dto.GroupMemberDto;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static skuniv.capstone.domain.request.RequestType.*;

@Slf4j
@Controller
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {
    private final UserService userService;
    private final GroupService groupService;
    private final RequestService requestService;

    @GetMapping("/invite")
    public String inviteGroup(HttpServletRequest request, Model model) {
        User user = userService.getSessionUser(request); // session 주는건 되네 ㅋㅋㅋ

        List<UserDto> collect = user.getFriendShipList().stream()
                .map(l -> new UserDto(l.getMe()))// 이거 진짜 왜이랰ㅋㅋㅋㅋㅋㅋㅋㅋㅋ getMe 를 해야 정상적으로 출력되는 현상... 어떻게 해도 고쳐지지가 않음
                .collect(toList());

        model.addAttribute("friendList", collect);
        return "/group/invite";
    }

    @PostMapping
    public String createGroup(HttpServletRequest request) {
        User me = userService.getSessionUser(request);
        Group created = groupService.saveGroup(Group.createGroup(me)); //객체 save와 toMany 배열 사용은 같은 트랜잭션에서 안되는건가...? @Builder.default 를 사용해야만 null 이 안뜸
        groupService.connectMaster(me,created);
        return me.getName() + "님의 그룹이 생성되었습니다";
    }

    @ResponseBody
    @PostMapping("/request/{email}")
    public String requestGroup(@PathVariable String email, HttpServletRequest request) {
        log.info("초대할 이메일은 : {}", email);

        User me = userService.getSessionUser(request); // user 에 friendEmailList 를 추가해. 이메일이 있냐? 이것만 보면 됨
        User friend = userService.findByEmail(email);

        if (me.getGroup() == null) {
            Group created = groupService.saveGroup(Group.createGroup(me)); //객체 save와 toMany 배열 사용은 같은 트랜잭션에서 안되는건가...? @Builder.default 를 사용해야만 null 이 안뜸
            groupService.connectMaster(me,created);
        }
        if (!me.getFriendsEmail().contains(email)) {
            log.info("친구추가가 된 회원만 초대할 수 있습니다.");
            throw new IllegalStateException();
        }
        if (me.getGroup().getUserList().contains(friend)) {
            log.info("이미 내 그룹에 포함된 친구입니다.");
            throw new IllegalStateException();
        } else if (friend.getGroup()!=null) {
            log.info("이미 다른 그룹에 포함된 친구입니다.");
            throw new IllegalStateException();
        }

        userService.requestGroup(me, friend, me.getGroup());

        return friend.getName();
    }
    @GetMapping("/receive")
    public String receiveList(HttpServletRequest request, Model model) {
        User me = userService.getSessionUser(request);
        List<UserRequest> list = me.getReceiveRequestList();

        List<ReceiveRequestDto> collect = list.stream()
                .filter(u -> u.getRequest().getRequestType() == INVITE)
                .map(u -> new ReceiveRequestDto(u))
                .collect(toList());

        model.addAttribute("requestList", collect);

        return "/group/receive";
    }
    @GetMapping("/send")
    public String sendList(HttpServletRequest request, Model model) {
        User me = userService.getSessionUser(request);
        List<UserRequest> list = me.getSendRequestList();

        List<SendRequestDto> collect = list.stream()
                .filter(u -> u.getRequest().getRequestType() == INVITE)
                .map(SendRequestDto::new)
                .collect(toList());

        model.addAttribute("requestList", collect);
        return "/group/send";
    }
    @PostMapping("/success/{requestId}") // request 상태가 SUCCESS 로 변경되고 마스터의 그룹에 게스트가 추가됨
    public String successInvite(@PathVariable Long requestId) {
        requestService.successInvite(requestId);

        return "redirect:/";
    }
    @PostMapping("/fail/{userRequestId}")
    public String groupFail(@PathVariable Long userRequestId, HttpServletRequest request) {
        UserRequest userRequest = requestService.findUserRequest(userRequestId);
        requestService.meetingFail(userRequest);
        return "redirect:" + request.getHeader("Referer");
    }
    @GetMapping("/member")
    public String groupMember(HttpServletRequest request, Model model) {
        User user = userService.getSessionUser(request);

        if(user.getGroup()==null)
            return "redirect:/group/invite";

        List<User> userList = user.getGroup().getUserList();

        List<GroupMemberDto> groupMembers = userList.stream()
                .map(u -> new GroupMemberDto(u))
                .toList();
        model.addAttribute("members",groupMembers);

        return "/group/member";
    }
    @GetMapping("/member/{email}")
    public String otherGroupMember(@PathVariable String email) {
        User user = userService.findByEmail(email);
        List<User> groupUser = user.getGroup().getUserList();

        groupUser.stream()
                .map(u -> new GroupMemberDto(u))
                .toList();

        return "/group/member";
    }

    @GetMapping("/out")
    public String groupOut(HttpServletRequest request) {
        User sessionUser = userService.getSessionUser(request);
        groupService.groupOut(sessionUser);
        return "redirect:/";
    }

    @GetMapping("/start")
    public String startGroupTing(HttpServletRequest request) {
        User sessionUser = userService.getSessionUser(request);
        if (sessionUser.getIdle()) {
            log.info("이미 개인 미팅에 참여중입니다. 퇴장 후 다시 시도하십시오");
            throw new IllegalStateException();
        }

        if (sessionUser.getGroup().getIdle() == false) {
            log.info("이미 그룹 미팅이 진행중입니다");
            return "/meeting/groupTingStart";
        } else {
            return "redirect:/group/list";
        }
    }

    @PostMapping("/start")
    public String joinGroupting(HttpServletRequest request, Model model) {
        User sessionUser = userService.getSessionUser(request);

        if (sessionUser.getAuthorized() != true) {
            model.addAttribute("authorized", false);
            return "/meeting/groupTingStart";
        }

        groupService.startGroupting(sessionUser);
        log.info("{}님의 그룹이 미팅에 참여했습니다", sessionUser.getName());
        return "redirect:/group/list";
    }
    @PostMapping("/stop")
    public String exitGroup(HttpServletRequest request) {
        User sessionUser = userService.getSessionUser(request);
        groupService.stopGroupting(sessionUser);
        log.info("{}님의 그룹이 미팅에서 퇴장하였습니다", sessionUser.getName());
        return "redirect:/";
    }
    @GetMapping("/list")
    public String groupList(@Valid @ModelAttribute GroupSearch groupSearch, HttpServletRequest request, Model model) {
        User sessionUser = userService.getSessionUser(request);

        groupService.checkStartTime();  // 미팅참여 후 3일 지난 그룹은 자동으로 퇴장

        // 인원수 맞춰야 하고, 성별 달라야 하고 근처 학교여야 해. 그리고 그룹을 누르면 멤버 정보도 확인 가능해야함
        List<GroupDto> groupList = groupService.findALl(sessionUser, groupSearch).stream()
                .map(g -> new GroupDto(g, sessionUser))
                .collect(toList());

        model.addAttribute("groupList", groupList);

        return "/group/groupTingList";
    }
}
