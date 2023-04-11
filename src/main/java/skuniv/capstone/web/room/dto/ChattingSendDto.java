package skuniv.capstone.web.room.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChattingSendDto {
    @NotEmpty
    String content;
}
