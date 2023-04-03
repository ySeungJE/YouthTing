package skuniv.capstone.web.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import skuniv.capstone.domain.user.MBTI;
import skuniv.capstone.domain.user.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyPageDto {
    private String name;
    private String password;
    private Integer height;
    private String intro;
    private MBTI mbti;
    private String univName;
    private String email;
    private String storeProfileName;

    public MyPageDto(User user) {
        this.name = user.getName();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.univName = user.getUniv().getUnivName();
        this.mbti = user.getMbti();
        this.height = user.getHeight();
        this.intro = user.getIntro();
        this.storeProfileName =user.getStoreProfileName();
    }
}
