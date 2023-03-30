package skuniv.capstone.web.request.friend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.user.service.UserService;
import skuniv.capstone.domain.userrequest.UserRequest;
import skuniv.capstone.web.request.friend.dto.ReceiveRequestDto;
import skuniv.capstone.web.request.friend.dto.SendRequestDto;

import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend")
public class FriendController {
    private final UserService userService;
    @PostMapping("/{myId}/{friendId}")
    public String requestFriend(@PathVariable Long myId, @PathVariable Long friendId) {
        return userService.requestFriend(myId, friendId);
    }

    @GetMapping("/receive/{myId}")
    public List<ReceiveRequestDto> receiveList(@PathVariable Long myId) {
        User me = userService.findById(myId);
        List<UserRequest> list = me.getReceiveRequestList();

        return list.stream()
                .map(userRequest -> new ReceiveRequestDto(userRequest))
                .collect(toList());
    }
    @GetMapping("/send/{myId}")
    public List<SendRequestDto> sendList(@PathVariable Long myId) {
        User me = userService.findById(myId);
        List<UserRequest> list = me.getSendRequestList();

        return list.stream()
                .map(userRequest -> new SendRequestDto(userRequest))
                .collect(toList());
    }

    @PostMapping("/success/{requestId}")
    public void successFriend(@PathVariable Long requestID) {

    }
}
