package skuniv.capstone.web.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import skuniv.capstone.domain.file.FIleStore;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.user.repository.UserSearch;
import skuniv.capstone.domain.user.service.UserService;
import skuniv.capstone.domain.userrequest.UserRequest;
import skuniv.capstone.web.user.dto.SoloUserDto;
import skuniv.capstone.web.user.dto.UserJoinDto;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FIleStore fileStore;
    @PostMapping("/add")
    public String join(@Valid @RequestBody UserJoinDto userJoinDto) throws IOException {
//        String storeProfileName = fileStore.storeFile(userJoinDto.getAttachFile());
        User created = User.createUser(userJoinDto, "exemple.png"); // 임시로 아무 이름이나 보냄
        userService.join(created);
        return created.getName() + "님 가입하셨습니다";
    }
    @GetMapping("/soloList")
    public List<SoloUserDto> soloList(@Valid @RequestBody UserSearch userSearch) {
        List<User> aLl = userService.findALl(userSearch);
        return aLl.stream()
                .map(SoloUserDto::new)
                .collect(toList());
    }
}
