package skuniv.capstone.web.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import skuniv.capstone.domain.file.FIleStore;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.user.repository.UserSearch;
import skuniv.capstone.domain.user.service.UserService;
import skuniv.capstone.domain.userrequest.UserRequest;
import skuniv.capstone.web.user.dto.MyPageDto;
import skuniv.capstone.web.user.dto.UserSoloDto;
import skuniv.capstone.web.user.dto.UserJoinDto;

import javax.naming.Binding;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import static java.util.stream.Collectors.*;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FIleStore fileStore;
    @GetMapping("/add")
    public String joinForm(Model model) {           // 빈 껍데기 객체 가져가는 이유 배웠지? object 때문에
        model.addAttribute("userForm", new UserJoinDto());
        return "user/joinForm";
    }

//    @GetMapping("/test")
//    public String checkUserId(HttpServletRequest request) {
//        System.out.println(request.getSession().getAttributeNames());
//        return "redirect:/";
//    }

    @PostMapping("/add") // 여기서 이메일 중복되면 클라에 오류 메세지 띄워주는거부터 하자
    public String join(@Valid @ModelAttribute("userForm") UserJoinDto userJoinDto,
                       BindingResult bindingResult) throws IOException {

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "user/joinForm";
        }

        String storeProfileName = fileStore.storeFile(userJoinDto.getProfilePicture(),userJoinDto.getName());

        try {
            userService.join(User.createUser(userJoinDto,storeProfileName));
        } catch (DataIntegrityViolationException e) {
            bindingResult.reject("emailDuplicated", "이메일이 중복됩니다");
            return "user/joinForm";
        }

//            created.getSendRequestList().add(null); // 객체를 save함과 동시에 배열이 생성될 순 없는지를 확인하기 위한 문장, 일단 유저가 만들어지긴 할 거.
        // 지렸닼ㅋㅋㅋㅋㅋ 이게 안되는 거였네... 나름대로 해석하자면 같은 트랜잭션 내에서 바로 list 속성을 사용하려면, @Builder.default 가 필수라는 거
        return "redirect:/login";
    }
    @GetMapping("/list")
    public String soloList(@Valid @ModelAttribute UserSearch userSearch, HttpServletRequest request, Model model) {
        User sessionUser = userService.getSessionUser(request);


        userService.checkStartTime(); // 미팅참여 후 3일 지난 애들은 자동으로 퇴장

        List<UserSoloDto> collect = userService.findALl(sessionUser.getGender(), userSearch)
                .stream()
                .map(user -> new UserSoloDto(sessionUser, user))
                .collect(toList());

        model.addAttribute("userList", collect);

        return "/meeting/soloTingList";
    }

    @GetMapping("/{email}")
    public UserSoloDto findOne(@PathVariable String email) {
        User user = userService.findByEmail(email);
        return new UserSoloDto(user);
    }
    @GetMapping("/myPage")
    public String myData(HttpServletRequest request, Model model) {
        User sessionUser = userService.getSessionUser(request);

        model.addAttribute("myData", new MyPageDto(sessionUser));
        return "/user/myPage";
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

    @GetMapping("/start")
    public String checkIdleState(HttpServletRequest request) {
        User sessionUser = userService.getSessionUser(request);

        if (sessionUser.getGroup()!=null && sessionUser.getGroup().getIdle() == true) {
            log.info("이미 그룹 미팅에 참여중입니다. 퇴장 후 다시 시도하십시오");
            throw new IllegalStateException();
        }

        if (sessionUser.getIdle() == false) {
            return "/meeting/soloTingStart";
        } else {
            return "redirect:/user/list";
        }
    }

    @PostMapping("/start")
    public String joinSoloTing(HttpServletRequest request) {
        User sessionUser = userService.getSessionUser(request);

        userService.startSoloting(sessionUser);

        log.info("{}님이 미팅에 참여하였습니다", sessionUser.getName());
        return "redirect:/user/start";
    }
    @PostMapping("/stop")
    public String exitGroup(HttpServletRequest request) {
        User sessionUser = userService.getSessionUser(request);
        userService.stopSoloting(sessionUser);
        log.info("{}님이 개인 미팅에서 퇴장하였습니다", sessionUser.getName());
        return "redirect:/";
    }

    @ResponseBody
    @GetMapping("/profile/{profileName}")
    public Resource downloadImage(@PathVariable String profileName) throws MalformedURLException {
//        "file:/C:/Users/YoonSJ/Desktop/inflearn/file_upload/c7c2c3b4-e123-4688-81ea-37d0d38719a2.png
        return new UrlResource("file:" + fileStore.getFullPath(profileName));
    }
}
