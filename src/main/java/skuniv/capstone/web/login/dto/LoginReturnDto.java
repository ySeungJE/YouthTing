package skuniv.capstone.web.login.dto;

import lombok.Data;
import skuniv.capstone.domain.user.MBTI;
import skuniv.capstone.domain.user.User;

@Data
public class LoginReturnDto {
    private String name;
    private String password;
    private Integer height;
    private MBTI mbti;
    private String univName;
    private String email;
    private String storeProfileName;

    public LoginReturnDto(User user) {
        password = user.getPassword();
        name = user.getName();
        height = user.getHeight();
        mbti = user.getMbti();
        univName = user.getUniv().getUnivName();
        email = user.getEmail();
        storeProfileName = user.getStoreProfileName();
    }
}