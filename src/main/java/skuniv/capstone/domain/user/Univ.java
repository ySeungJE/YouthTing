package skuniv.capstone.domain.user;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Univ {
    private String UnivAddress;
    private String UnivName;
}
