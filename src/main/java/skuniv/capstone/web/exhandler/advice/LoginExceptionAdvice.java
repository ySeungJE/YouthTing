package skuniv.capstone.web.exhandler.advice;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import skuniv.capstone.web.exhandler.LoginErrorResult;

@Slf4j
@RestControllerAdvice(basePackages = {"skuniv.capstone.web.login"})
public class LoginExceptionAdvice {
    @ExceptionHandler
    public ResponseEntity<LoginErrorResult> validationFailHandler(MethodArgumentNotValidException e) {
        LoginErrorResult loginErrorResult = new LoginErrorResult("NotEmptyException","이메일과 비밀번호는 빈 칸일 수 없습니다");
        return new ResponseEntity(loginErrorResult, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<LoginErrorResult> validationFailHandler(IllegalArgumentException e) {
        LoginErrorResult loginErrorResult = new LoginErrorResult("LoginFailException","이메일 또는 비밀번호가 틀립니다");
        return new ResponseEntity(loginErrorResult, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<LoginErrorResult> validationFailHandler(IllegalAccessException e) {
        LoginErrorResult loginErrorResult = new LoginErrorResult("NotLoginException","로그인이 필요합니다");
        return new ResponseEntity(loginErrorResult, HttpStatus.BAD_REQUEST);
    }

}
