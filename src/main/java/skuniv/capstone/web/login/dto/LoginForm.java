package skuniv.capstone.web.login.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor // json 받아오려면 이거 필요하던가?
@Schema(title = "회원가입 DTO")
public class LoginForm {
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}
