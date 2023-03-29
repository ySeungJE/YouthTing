package skuniv.capstone.web.login.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import skuniv.capstone.domain.login.service.LoginService;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.web.login.dto.LoginForm;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {
    public static final String LOGIN_USER = "loginUser";
    private final LoginService loginService;

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form,
                        HttpServletRequest request) throws IOException {

        User loginUser = loginService.login(form.getEmail(), form.getPassword());

        if (loginUser == null) {
            throw new IllegalArgumentException();
        }

        HttpSession session = request.getSession();

        session.setAttribute(LOGIN_USER, loginUser);
        return "["+loginUser.getName()+"]님 환영합니다";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "로그아웃 완료";
    }

    @GetMapping("/notLoginUser")
    public void IllegalAccess() throws IllegalAccessException {
        throw new IllegalAccessException(); // 이런거 겹칠수 있으니 걍 내가 에러 커스텀하는게 더 낫겠다
    }

}
