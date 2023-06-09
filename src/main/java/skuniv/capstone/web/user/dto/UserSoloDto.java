package skuniv.capstone.web.user.dto;

import lombok.Data;
import skuniv.capstone.domain.user.MBTI;
import skuniv.capstone.domain.user.User;

@Data
public class UserSoloDto {
    private String name;
    private Integer height;
    private String intro;
    private MBTI mbti;
    private String univName;
    private String email;
    private String storeProfileName;

    public UserSoloDto(User user) {
        name = user.getName();
        height = user.getHeight();
        intro = user.getIntro();
        mbti = user.getMbti();
        univName = user.getUniv().getUnivName();
        email = user.getEmail();
        storeProfileName = user.getStoreProfileName();
    }
}
