package skuniv.capstone.web.request.room.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChattingSendDto {
    @NotEmpty
    String content;
}
