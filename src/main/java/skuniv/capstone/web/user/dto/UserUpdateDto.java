package skuniv.capstone.web.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;
import skuniv.capstone.domain.user.MBTI;

@Data
@AllArgsConstructor
public class UserUpdateDto {
    @NotEmpty
    private String password;
    @NotEmpty
    private Integer height;
    @NonNull
    private MBTI mbti;
    @NotEmpty
    private String intro;
    @NonNull
    private MultipartFile attachFile;
}
