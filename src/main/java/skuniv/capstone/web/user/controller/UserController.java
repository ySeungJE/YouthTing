package skuniv.capstone.web.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import skuniv.capstone.domain.file.FIleStore;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.user.repository.UserSearch;
import skuniv.capstone.domain.user.service.UserService;
import skuniv.capstone.web.user.dto.MyPageDto;
import skuniv.capstone.web.user.dto.UserSoloDto;
import skuniv.capstone.web.user.dto.UserJoinDto;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FIleStore fileStore;
    @PostMapping("/add")
    public String join(@RequestPart MultipartFile proFilePicture,
                       @Valid @RequestPart UserJoinDto userJoinDto) throws IOException {
            User created = User.createUser(userJoinDto);
            userService.join(created);
            String storeProfileName = fileStore.storeFile(proFilePicture,created.getName());
            userService.profileUpdate(created, storeProfileName); // service 밖으로 나와버렸기 때문에 변경 감지는 안되고, 다만 lazy 초기화는 된다? "확인"
            return created.getName() + "님이 가입하셨습니다";
    }
    @GetMapping("/list")
    public List<UserSoloDto> soloList(@Valid @RequestBody UserSearch userSearch,
                                      HttpServletRequest request) {
        User sessionUser = userService.getSessionUser(request);
        List<User> aLl = userService.findALl(sessionUser.getGender(),userSearch);

        return aLl.stream()
                .map(UserSoloDto::new)
                .collect(toList());
    }
    @GetMapping("/{email}")
    public UserSoloDto findOne(@PathVariable String email) {
        User user = userService.findByEmail(email);
        return new UserSoloDto(user);
    }
    @GetMapping("/myPage")
    public MyPageDto myData(HttpServletRequest request) {
        User sessionUser = userService.getSessionUser(request);
        return new MyPageDto(sessionUser);
    }
    @PostMapping("update")  // myPage 를 update 페이지로 재활용할 건데 프로필사진 -> 업로드 칸으로
    public void updateUser(@RequestPart(required = false) MultipartFile proFilePicture,
                             @Valid @RequestPart MyPageDto myPageDto) throws IOException {
        String storeProfileName = null;
        if (proFilePicture!=null)
            storeProfileName = fileStore.storeFile(proFilePicture,myPageDto.getName());
        userService.updateUser(myPageDto,storeProfileName);
        log.info("{}님의 정보가 업데이트 되었습니다", myPageDto.getName());
    }
    @PostMapping("/start")
    public void joinSoloTing(HttpServletRequest request) {
        User sessionUser = userService.getSessionUser(request);
        if (sessionUser.getGroup().getIdle() == true) {
            log.info("이미 그룹 미팅에 참여중입니다. 퇴장 후 다시 시도하십시오");
            throw new IllegalStateException();
        }
        userService.startSoloting(sessionUser);
        log.info("{}님이 미팅에 참여하였습니다", sessionUser.getName());
    }
    @PostMapping("/stop")
    public void exitGroup(HttpServletRequest request) {
        User sessionUser = userService.getSessionUser(request);
        userService.stopSoloting(sessionUser);
        log.info("{}님이 개인 미팅에서 퇴장하였습니다", sessionUser.getName());
    }


}
