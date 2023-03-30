package skuniv.capstone.domain.user.repository;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import skuniv.capstone.domain.user.MBTI;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearch {
    @Nullable
    private MBTI mbti;
    @Positive
    private Integer heightMin;
    @Positive
    private Integer heightMax;
}
