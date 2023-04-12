package skuniv.capstone.web.exhandler.advice;


import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.web.exhandler.UserJoinErrorResult;

@Slf4j
@RestControllerAdvice(basePackages = {"skuniv.capstone.web.user"})
public class UserExceptionAdvice {

    //=============================== 유저 회원가입 예외 처리 ====================================//
    @ExceptionHandler // 지금 빈칸을 허용하지 않는다는 목적은 똑같은데, 문자열이랑 다른 타입의 변수들에서 나오는 에러가 달라지네. 이건 ㅈㄴ 비효율적이긴 하지. 차라리 내가 저 두개를 합쳐서 하나의 어노테이션으로 만들면?
    public ResponseEntity<UserJoinErrorResult> validationFailHandler(DataIntegrityViolationException e) { // DB 구조에 안맞아서 직접 보낸 에러
        UserJoinErrorResult userJoinErrorResult = new UserJoinErrorResult("DBNotAllowException","중복된 이메일, 프로필 사진 미첨부");
        return new ResponseEntity(userJoinErrorResult, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<UserJoinErrorResult> validationFailHandler(MethodArgumentNotValidException e) { // validation.constraints 에 속한 어노테이션에서 온 에러
        UserJoinErrorResult userJoinErrorResult = new UserJoinErrorResult("UserJoinDataException","필수입력 칸에 공백, 잘못된 이메일 형식");
        return new ResponseEntity(userJoinErrorResult, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<UserJoinErrorResult> validationFailHandler(HttpMessageNotReadableException e) { // lombok 에 속한 validation 어노테이션에서 온 에러
        UserJoinErrorResult userJoinErrorResult = new UserJoinErrorResult("DataNotSuitableException","숫자 칸에 문자입력, 잘못된 MBTI 입력, 필수입력 칸에 공백");
        return new ResponseEntity(userJoinErrorResult, HttpStatus.BAD_REQUEST);
    }

    //====================================== =========================================//





//    @ExceptionHandler
//    public ResponseEntity<UserJoinErrorResult> validationFailHandler(IllegalStateException e, HttpServletRequest request) {
//        Member rejectedMember = new Member(request.getParameter("email"),request.getParameter("password"),request.getParameter("name"));
//        UserJoinErrorResult userJoinErrorResult = new UserJoinErrorResult("member-login-fail","이미 존재하는 회원입니다", rejectedMember);
//        return new ResponseEntity(userJoinErrorResult, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler
//    public ResponseEntity<UserJoinErrorResult> validationFailHandler(ConstraintViolationException e, HttpServletRequest request) {
//        MemberUpdateDto rejectedMember = new MemberUpdateDto(request.getParameter("password"),request.getParameter("name"));
//        MemberUpdateErrorResult memberUpdateErrorResult = new MemberUpdateErrorResult("member-update-fail","회원정보 변경 검증 오류", rejectedMember);
//        return new ResponseEntity(memberUpdateErrorResult, HttpStatus.BAD_REQUEST);
//    }
}
