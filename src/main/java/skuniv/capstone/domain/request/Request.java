package skuniv.capstone.domain.request;

import jakarta.persistence.*;
import lombok.*;
import skuniv.capstone.domain.userrequest.UserRequest;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.InheritanceType.*;

@Getter
@Entity
@NoArgsConstructor
@Inheritance(strategy = SINGLE_TABLE) // 다음부터는 이 구조 말고 JOINED 전략을 써야겠다, 다음부턴 이렇게 절대 안한다...
                                                        // 이유 1. abstract로 해놓으면 Builder를 쓸 수가 없음, 아마 AllArgsConstructor 를 못 썼던거 같다
@DiscriminatorColumn(name = "dtype")                        // 이유 2. 이게 제일 ㅈ같은데 도통 써먹지를 못하겠음 DB에서 Request 형으로 꺼내는데, 그 후 형변환을 못하겠으니까 자식 클래스에 정의해놓은 기능들을 쓸 수가 없어
@AllArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;
    private String name;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;
    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    @OneToOne(mappedBy = "request", cascade = ALL)
    private UserRequest userRequest;

    public Request(Long id, String name, RequestStatus requestStatus, RequestType requestType) {
        this.id = id;
        this.name = name;
        this.requestStatus = requestStatus;
        this.requestType = requestType;
    }
    //== 비즈니스 로직 ==//
    public String changeStatus(RequestStatus status) {
        this.requestStatus = status;
        return "Request 의 상태가 SUCCESS 로 변경되었습니다";
    }

    public void updateUserRequest(UserRequest userRequest) {
        this.userRequest = userRequest;
    }
}
