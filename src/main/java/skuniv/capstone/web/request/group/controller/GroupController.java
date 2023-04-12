package skuniv.capstone.web.request.group.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import skuniv.capstone.domain.group.Group;
import skuniv.capstone.domain.group.service.GroupService;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.user.service.UserService;
import skuniv.capstone.domain.userrequest.UserRequest;
import skuniv.capstone.domain.userrequest.sevice.RequestService;
import skuniv.capstone.web.request.ReceiveRequestDto;
import skuniv.capstone.web.request.SendRequestDto;
import skuniv.capstone.web.request.UserDto;
import skuniv.capstone.web.request.group.dto.GroupDto;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static skuniv.capstone.domain.request.RequestType.*;

@Slf4j
@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {
    private final UserService userService;
    private final GroupService groupService;
    private final RequestService requestService;
    @PostMapping
    public String createGroup(HttpServletRequest request) {
        User me = userService.getSessionUser(request);
        Group created = groupService.saveGroup(Group.createGroup(me)); //객체 save와 toMany 배열 사용은 같은 트랜잭션에서 안되는건가...? @Builder.default 를 사용해야만 null 이 안뜸
        groupService.connectMaster(me,created);
        return me.getName() + "님의 그룹이 생성되었습니다";
    }
    @PostMapping("/request/{email}")
    public String requestGroup(@PathVariable String email, HttpServletRequest request) {
        User me = userService.getSessionUser(request); // user 에 friendEmailList 를 추가해. 이메일이 있냐? 이것만 보면 됨
        User friend = userService.findByEmail(email);

        if (me.getGroup() == null) {
            log.info("그룹을 생성해야 합니다");
            throw new IllegalStateException();
        }

        if (me.getFriendsEmail().contains(email)==false) {
            log.info("친구추가가 된 회원만 초대할 수 있습니다.");
            throw new IllegalStateException();
        }

        return userService.requestGroup(me, friend, me.getGroup());
    }
    @GetMapping("/receive")
    public List<ReceiveRequestDto> receiveList(HttpServletRequest request) {
        User me = userService.getSessionUser(request);
        List<UserRequest> list = me.getReceiveRequestList();

        return list.stream()
                .filter(u-> u.getRequest().getRequestType()==INVITE )
                .map(u -> new ReceiveRequestDto(u))
                .collect(toList());
    }
    @GetMapping("/send")
    public List<SendRequestDto> sendList(HttpServletRequest request) {
        User me = userService.getSessionUser(request);
        List<UserRequest> list = me.getSendRequestList();

        return list.stream()
                .filter(u-> u.getRequest().getRequestType()==INVITE )
                .map(u -> new SendRequestDto(u))
                .collect(toList());
    }
    @PostMapping("/success/{requestId}") // request 상태가 SUCCESS 로 변경되고 마스터의 그룹에 게스트가 추가됨
    public String successInvite(@PathVariable Long requestId) {
        return requestService.successInvite(requestId);
    }
    @GetMapping("/member")
    public GroupMember groupMember(HttpServletRequest request) {
        User user = userService.getSessionUser(request);
        List<User> groupUser = user.getGroup().getUserList();

        List<UserDto> guest = groupUser.stream()
                .filter(u -> u.getGroup().getMaster()!=u)
                .map(u -> new UserDto(u))
                .collect(toList());
        return new GroupMember(new UserDto(user.getGroup().getMaster()), guest);
    }
    @GetMapping("/member/{email}")
    public GroupMember otherGroupMember(@PathVariable String email) {
        User user = userService.findByEmail(email);
        List<User> groupUser = user.getGroup().getUserList();

        List<UserDto> guest = groupUser.stream()
                .filter(u -> u.getGroup().getMaster()!=u)
                .map(u -> new UserDto(u))
                .collect(toList());
        return new GroupMember(new UserDto(user.getGroup().getMaster()), guest);
    }
    @PostMapping("/start")
    public void joinGroupting(HttpServletRequest request) {
        User sessionUser = userService.getSessionUser(request);
        if (sessionUser.getIdle() == true) {
            log.info("이미 개인 미팅에 참여중입니다. 퇴장 후 다시 시도하십시오");
            throw new IllegalStateException();
        }
        groupService.startGroupting(sessionUser);
        log.info("{}님의 그룹이 미팅에 참여했습니다", sessionUser.getName());
    }
    @PostMapping("/stop")
    public void exitGroup(HttpServletRequest request) {
        User sessionUser = userService.getSessionUser(request);
        groupService.stopGroupting(sessionUser);
        log.info("{}님의 그룹이 미팅에서 퇴장하였습니다", sessionUser.getName());
    }
    @GetMapping("/list")
    public List<GroupDto> groupList(HttpServletRequest request) {
        User sessionUser = userService.getSessionUser(request);

        groupService.checkStartTime();  // 미팅참여 후 3일 지난 그룹은 자동으로 퇴장

        // 인원수 맞춰야 하고, 성별 달라야 하고 근처 학교여야 해. 그리고 그룹을 누르면 멤버 정보도 확인 가능해야함
        return groupService.findALl(sessionUser).stream()
                .map(g -> new GroupDto(g))
                .collect(Collectors.toList());
    }


    //== Controller 내부 정의 DTO ==//
    @Data // 컬렉션으로 바로 내보내면 json 배열 타입으로 나가버리기 때문에(유연성이 확 떨어짐) 한번 감싸줘야 함
    @AllArgsConstructor
    static class GroupMember<T> {
        private UserDto host;
        private T guest;

    }

}
