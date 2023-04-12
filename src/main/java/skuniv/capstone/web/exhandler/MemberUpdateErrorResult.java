package skuniv.capstone.web.exhandler;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberUpdateErrorResult {
    private String code;
    private String message;
//    private MemberUpdateDto memberUpdateDto;
}
