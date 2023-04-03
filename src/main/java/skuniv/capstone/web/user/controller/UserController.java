package skuniv.capstone.web.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    public String join(@RequestPart MultipartFile proFilePicture,
                       @Valid @RequestPart UserJoinDto userJoinDto) throws IOException {
        String storeProfileName = fileStore.storeFile(proFilePicture);
        User created = User.createUser(userJoinDto, storeProfileName); // 임시로 아무 이름이나 보냄
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
    @GetMapping("/{email}")
    public SoloUserDto findOne(@PathVariable String email) {
        User user = userService.findByEmail(email);
        return new SoloUserDto(user);
    }
    @GetMapping("/myPage")
    public SoloUserDto myData(HttpServletRequest request) {
        User sessionUser = userService.getSessionUser(request);
        return new SoloUserDto(sessionUser);
    }
}
