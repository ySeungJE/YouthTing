package skuniv.capstone.web.exhandler.advice;


import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import skuniv.capstone.web.exhandler.BoardCreateErrorResult;

@Slf4j
@org.springframework.web.bind.annotation.RestControllerAdvice(basePackages = {"oopsla.lmp.web.board"})
public class BoardExceptionAdvice {
    @ExceptionHandler
    public ResponseEntity<BoardCreateErrorResult> validationFailHandler(BindException e) {
        BoardCreateErrorResult boardCreateErrorResult = new BoardCreateErrorResult("board-validation-fail","제목이나 내용이 빈 칸일 수 없습니다");
        return new ResponseEntity(boardCreateErrorResult, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<BoardCreateErrorResult> validationFailHandler(ConstraintViolationException e) {
        BoardCreateErrorResult boardCreateErrorResult = new BoardCreateErrorResult("board-validation-fail","제목이나 내용이 빈 칸일 수 없습니다");
        return new ResponseEntity(boardCreateErrorResult, HttpStatus.BAD_REQUEST);
    }

}
