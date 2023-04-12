package skuniv.capstone.web.exhandler;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class BoardCreateErrorResult {
    private String code;
    private String message;
}