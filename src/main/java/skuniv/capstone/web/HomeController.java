package skuniv.capstone.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import skuniv.capstone.domain.login.service.LoginService;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.web.login.controller.LoginController;

@Slf4j
@Controller
public class HomeController {
    @GetMapping("/")
    public String homeLoginV3Spring(
            @SessionAttribute(name = LoginController.LOGIN_USER, required = false) User loginUser,
            @RequestParam(required = false) String error, Model model) {

        //세션에 회원 데이터가 없으면 home
        if (loginUser == null) {
            return "home";
        }

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("errorCode", error);
        model.addAttribute("user", loginUser);
        return "loginHome";
    }
}