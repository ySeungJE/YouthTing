package skuniv.capstone.domain.request;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@AllArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    //== 비즈니스 로직 ==//
    public String changeStatus(RequestStatus status) {
        this.requestStatus = status;
        return "Request 의 상태가 SUCCESS 로 변경되었습니다";
    }

}
