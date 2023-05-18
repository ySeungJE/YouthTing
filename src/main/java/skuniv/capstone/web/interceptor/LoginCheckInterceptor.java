package skuniv.capstone.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import skuniv.capstone.web.login.controller.LoginController;

import static skuniv.capstone.web.login.controller.LoginController.*;


@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        HttpSession session = request.getSession();

        if (session == null || session.getAttribute(LOGIN_USER) == null) {
            log.info("미인증 사용자 요청");
            //로그인으로 redirect
            response.sendRedirect("/login?redirectURL="+requestURI);
            return false;
        }
        return true;
    }
}
