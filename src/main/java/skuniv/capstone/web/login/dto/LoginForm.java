package skuniv.capstone.web.login.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor // json 받아오려면 이거 필요하던가?
public class LoginForm {
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}
