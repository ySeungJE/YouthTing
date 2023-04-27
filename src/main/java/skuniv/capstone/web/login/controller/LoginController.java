package skuniv.capstone.web.login.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import skuniv.capstone.domain.login.service.LoginService;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.web.login.dto.LoginForm;

import java.io.IOException;
import java.util.Enumeration;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
    public static final String LOGIN_USER = "loginUser";
    private final LoginService loginService;
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "user/loginForm";
    }
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form,
                        HttpServletRequest request) throws IOException {

        User loginUser = loginService.login(form.getEmail(), form.getPassword());

        if (loginUser == null) {
            throw new IllegalArgumentException();
        }

        HttpSession session = request.getSession();

        session.setAttribute(LOGIN_USER, loginUser);
        return "redirect:/";
    }
    @GetMapping("/sessionTest")
    public void sessionTest(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            System.out.println(session.getAttribute(attributeNames.nextElement()));
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    @GetMapping("/notLoginUser")
    public void IllegalAccess() throws IllegalAccessException {
        throw new IllegalAccessException(); // 이런거 겹칠수 있으니 걍 내가 에러 커스텀하는게 더 낫겠다
    }

}
