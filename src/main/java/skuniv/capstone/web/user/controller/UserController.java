package skuniv.capstone.web.user.controller;

import jakarta.annotation.PostConstruct;
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
import skuniv.capstone.springEmail.EmailService;
import skuniv.capstone.web.user.dto.MyPageDto;
import skuniv.capstone.web.user.dto.UserSoloDto;
import skuniv.capstone.web.user.dto.UserJoinDto;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FIleStore fileStore;
    private final EmailService emailService;
    private static Map<String, String> emailMapper = new HashMap<>();
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

        String userEmail = userJoinDto.getEmail();
        String univEmail = emailMapper.get(userJoinDto.getUnivName());

        log.info("{} {}", userEmail, univEmail);

        System.out.println("userEmail.contains(univEmail) = " + userEmail.contains(univEmail));
        if (userEmail.contains(univEmail)!=true) {
            bindingResult.rejectValue("email","wrongEmailForm", userJoinDto.getUnivName()+"의 웹메일 형식과 다릅니다");
            return "user/joinForm";
        }

        String storeProfileName = fileStore.storeFile(userJoinDto.getProfilePicture(),userJoinDto.getName());
        System.out.println("storeProfileName = " + storeProfileName);

        try {
            userService.join(User.createUser(userJoinDto,storeProfileName));
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("email","emailDuplicated", "중복된 이메일입니다");
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

        return "meeting/soloTingList";
    }

    @GetMapping("/{email}")
    public String findOne(@PathVariable String email, Model model) {
        log.info("{}", email);
        User user = userService.findByEmail(email);
        model.addAttribute("soloUser", new UserSoloDto(user));
        return "user/soloUser";
    }
    @GetMapping("/profileClick/{storeProFileName}")
    public String findByStoreProFileName(@PathVariable String storeProFileName, Model model) {
        log.info("{}", storeProFileName);
        User user = userService.findByStoreProfileName(storeProFileName);
        model.addAttribute("soloUser", new UserSoloDto(user));
        return "user/soloUser";
    }
    @GetMapping("/myPage")
    public String myData(HttpServletRequest request, Model model) {
        User sessionUser = userService.getSessionUser(request);

        model.addAttribute("myData", new MyPageDto(sessionUser));
        return "user/myPage";
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

        if (sessionUser.getIdle() == true) {
            return "redirect:/user/list";
        } else {
            return "meeting/soloTingStart";
        }
    }

    @PostMapping("/start")
    public String joinSoloTing(HttpServletRequest request, Model model) {
        User sessionUser = userService.getSessionUser(request);

        if (sessionUser.getAuthorized() != true) {
            model.addAttribute("authorized", false);
            return "meeting/soloTingStart";
        }

        userService.startSoloting(sessionUser);

        log.info("{}님이 미팅에 참여하였습니다", sessionUser.getName());

        return "redirect:/user/list";
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
    @PostMapping("/emailConfirm")
    public String emailConfirm(HttpServletRequest request, Model model) throws Exception {
        User sessionUser = userService.getSessionUser(request);
        String confirm = emailService.sendSimpleMessage(sessionUser.getEmail());
        model.addAttribute("uniqueCode", sessionUser.getUniqueCode());
        model.addAttribute("authorized", sessionUser.getAuthorized());
        return "user/confirmForm";
    }
    @GetMapping("confirm")
    public String userConfirmForm(HttpServletRequest request, Model model) {
        User sessionUser = userService.getSessionUser(request);
        model.addAttribute("uniqueCode", sessionUser.getUniqueCode());
        model.addAttribute("authorized", sessionUser.getAuthorized());
        return "user/confirmForm";
    }

    @PostMapping("confirm")
    public String userConfirm(@RequestParam String code, HttpServletRequest request, Model model) {
        User sessionUser = userService.getSessionUser(request);
        Boolean confirmed = userService.userConfirm(code, sessionUser);
        model.addAttribute("confirmed", confirmed);
        return "redirect:/user/confirm";
    }

    @PostConstruct
    public void init() {
        emailMapper.put("서경대학교","@skuniv.ac.kr");
        emailMapper.put("국민대학교","@naver.com");
        emailMapper.put("성신여자대학교", "@gmail.com");
    }
}
