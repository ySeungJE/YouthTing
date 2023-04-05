package skuniv.capstone.domain.userrequest;

import jakarta.persistence.*;
import lombok.*;
import skuniv.capstone.domain.friendship.Friendship;
import skuniv.capstone.domain.request.Request;
import skuniv.capstone.domain.request.RequestStatus;
import skuniv.capstone.domain.user.User;

import static jakarta.persistence.CascadeType.*;
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
    @OneToOne(fetch = LAZY,cascade = ALL)
    @JoinColumn(name = "request_id")
    private Request request;

    //== 생성 메서드 ==//
    public static UserRequest createUserRequest(User sendUser, User receiveUser, Request request) {
        return UserRequest.builder()
                .sendUser(sendUser)
                .receiveUser(receiveUser)
                .request(request)
                .build();
    }

    //== 비즈니스 로직 ==//

    /**
     * 송신자와 수신자의 requestList 에 각각 추가
     */
    public void requestProcess() {
        this.sendUser.getSendRequestList().add(this);
        this.receiveUser.getReceiveRequestList().add(this);
    }

    /**
     * 상대가 친구요청을 받았을 때의 로직
     */
    public String successFriend() {
        String s = this.request.changeStatus(RequestStatus.SUCCESS);
        String s2 = Friendship.createFriendship(this.receiveUser, this.sendUser);// 생성과 연결을 동시에
        return s+"\n"+s2;
    }

    /**
     * 상대가 그룹 초대를 받았을 때의 로직
     * @return
     */
    public String successInvite() {
        this.receiveUser.setGroup(this.sendUser.getGroup());
        return sendUser.getName() + "님의 그룹에 " + receiveUser.getName() + "님이 초대되었습니다";
    }
}
