package skuniv.capstone.domain.group.repository;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import skuniv.capstone.domain.user.MBTI;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupSearch {
    @Positive
    private Integer groupAgeMax;
    @Positive
    private Integer groupAgeMin;
    @Nullable
    private String univName;
}
