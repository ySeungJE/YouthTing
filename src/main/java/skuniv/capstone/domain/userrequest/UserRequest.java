package skuniv.capstone.domain.userrequest;

import jakarta.persistence.*;
import lombok.*;
import skuniv.capstone.domain.request.Request;
import skuniv.capstone.domain.user.User;

import static jakarta.persistence.FetchType.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class UserRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_request_id")
    private Long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "send_user_id")
    private User sendUser;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "receive_user_id")
    private User receiveUser;
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "request_id")
    private Request request;

    //== 생성 메서드 ==//
    public static void createUserRequest(User sendUser, User receiveUser, Request request) {
        UserRequest.builder()
                .sendUser(sendUser)
                .receiveUser(receiveUser)
                .request(request)
                .build();
    }

    //== 비즈니스 로직 ==//
    public void inviteProcess() {
        this.sendUser.getSendRequestList().add(this);
        this.receiveUser.getReceiveRequestList().add(this);
    }
}
