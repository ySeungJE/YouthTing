package skuniv.capstone.web.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;
import skuniv.capstone.domain.user.Gender;
import skuniv.capstone.domain.user.MBTI;
import skuniv.capstone.domain.user.Univ;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJoinDto {
    @Email(message = "이메일 형식에 맞지 않습니다")
    @NotEmpty // NotEmpty 는 NonNull 보다 순위가 늦어.
    private String email;
    @NotEmpty
    private String password;
    @NotEmpty
    private String name;
    @NonNull // 얘는 null만 허락을 안하고 "" (공백 문자열)이거는 허용하는데 이것도 String형 변수에서나 허용하지 다른 변수는 "" 이거 자체를 가질 수가 없기 때문에 null 값을 넣어버림
    private Gender gender;
    @NonNull
    private Integer age;
    @NotEmpty
    private String univAddress;
    @NotEmpty
    private String univName;
    @NonNull
    private Integer height;
    @NonNull
    private MBTI mbti;
    @NotEmpty
    private String intro;

}
