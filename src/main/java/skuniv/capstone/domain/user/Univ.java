package skuniv.capstone.domain.user;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@Embeddable
public class Univ {
    private String univAddress;
    private String univName;
}
