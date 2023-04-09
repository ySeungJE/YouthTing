package skuniv.capstone.web.request.friend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.user.service.UserService;
import skuniv.capstone.domain.userrequest.UserRequest;
import skuniv.capstone.domain.userrequest.sevice.RequestService;
import skuniv.capstone.web.request.UserDto;
import skuniv.capstone.web.request.ReceiveRequestDto;
import skuniv.capstone.web.request.SendRequestDto;

import java.util.List;

import static java.util.stream.Collectors.*;
import static skuniv.capstone.domain.request.RequestType.FRIEND;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/friend")
public class FriendController {
    private final UserService userService;
    private final RequestService requestService;
    @PostMapping("/request/{email}") // 이건 뭐냐.. 왜 /request/{email} 로 하면 primary key 겹친다는 오류가 뜸?ㅋㅋㅋㄹㅇㅋㅋ
    public String requestFriend(@PathVariable String email, HttpServletRequest request) {
        User me = userService.getSessionUser(request);
        User friend = userService.findByEmail(email);

        if (me.getFriendsEmail().contains(email)==true) {
            log.info("이미 친구가 되어있는 회원입니다.");
            throw new IllegalStateException();
        }
        return userService.requestFriend(me, friend);
    }

    @GetMapping("/receive")
    public List<ReceiveRequestDto> receiveList(HttpServletRequest request) {
        User me = userService.getSessionUser(request);
        List<UserRequest> list = me.getReceiveRequestList();

        return list.stream()
                .filter(u-> u.getRequest().getRequestType()==FRIEND )
                .map(userRequest -> new ReceiveRequestDto(userRequest))
                .collect(toList());
    }
    @GetMapping("/send")
    public List<SendRequestDto> sendList(HttpServletRequest request) {
        User me = userService.getSessionUser(request);
        List<UserRequest> list = me.getSendRequestList();

        return list.stream()
                .filter(u-> u.getRequest().getRequestType()==FRIEND)
                .map(u -> new SendRequestDto(u))
                .collect(toList());
    }
    @PostMapping("/success/{requestId}") // request 상태가 SUCCESS 로 변경되고 양측이 친구로 추가됨
    public String successFriend(@PathVariable Long requestId) {
        return requestService.successFriend(requestId);  // 이래서 시발 귀찮게 String으로 확인 안하는 구나 log 가 훨씬 낫다
    }
    @GetMapping("/list")
    public List<UserDto> friendList(HttpServletRequest request) {
        User user = userService.getSessionUser(request); // session 주는건 되네 ㅋㅋㅋ

        return user.getFriendShipList().stream()
                .map(l -> new UserDto(l.getMe()))// 이거 진짜 왜이랰ㅋㅋㅋㅋㅋㅋㅋㅋㅋ getMe 를 해야 정상적으로 출력되는 현상... 어떻게 해도 고쳐지지가 않음
                .collect(toList());
    }



}
