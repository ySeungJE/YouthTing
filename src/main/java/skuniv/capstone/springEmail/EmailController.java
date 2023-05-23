package skuniv.capstone.springEmail;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EmailController {
//    private final EmailService emailService;
//    @PostMapping("/emailConfirm")
//    public String emailConfirm(HttpServletRequest request) throws Exception {
//
//        String confirm = emailService.sendSimpleMessage(email);
//
//        return confirm;
//    }
}
