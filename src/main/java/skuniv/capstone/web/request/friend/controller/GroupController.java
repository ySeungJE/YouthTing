package skuniv.capstone.web.request.friend.controller;

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
import skuniv.capstone.web.request.friend.dto.ReceiveRequestDto;
import skuniv.capstone.web.request.friend.dto.SendRequestDto;
import skuniv.capstone.web.request.friend.dto.UserDto;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static skuniv.capstone.domain.request.RequestType.*;

@Slf4j
@RestController
@RequestMapping("group")
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
                .map(u -> new UserDto(u))// 이거 진짜 왜이랰ㅋㅋㅋㅋㅋㅋㅋㅋㅋ getMe 를 해야 정상적으로 출력되는 현상... 어떻게 해도 고쳐지지가 않음
                .collect(toList());
        return new GroupMember(new UserDto(user.getGroup().getMaster()), guest);
    }

    //== Controller 내부 정의 DTO ==//
    @Data // 컬렉션으로 바로 내보내면 json 배열 타입으로 나가버리기 때문에(유연성이 확 떨어짐) 한번 감싸줘야 함
    @AllArgsConstructor
    static class GroupMember<T> {
        private UserDto host;
        private T guest;

    }

}
