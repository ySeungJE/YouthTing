package skuniv.capstone.web.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import skuniv.capstone.domain.user.Gender;
import skuniv.capstone.domain.user.MBTI;
import skuniv.capstone.domain.user.Univ;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJoinDto {
    @Email(message = "이메일 형식에 맞지 않습니다")
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
    @NotEmpty
    private String name;
    @NonNull
    private Gender gender;
    @NonNull
    private Univ univ;
    @NonNull
    private Integer height;
    @NonNull
    private MBTI mbti;
    @NotEmpty
    private String intro;
}
