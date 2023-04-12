package skuniv.capstone.web.exhandler;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserJoinErrorResult {
    private String error;
    private String message;
}
