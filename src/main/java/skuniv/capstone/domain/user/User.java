package skuniv.capstone.domain.user;

import jakarta.persistence.*;
import lombok.*;
import skuniv.capstone.domain.friendship.Friendship;
import skuniv.capstone.domain.userrequest.UserRequest;
import skuniv.capstone.domain.group.Group;
import skuniv.capstone.domain.room.Room;
import skuniv.capstone.web.user.dto.MyPageDto;
import skuniv.capstone.web.user.dto.UserJoinDto;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(unique = true)
    private String email;
    private String password;
    private String name;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Integer height;
    private Integer age;
    @Column(columnDefinition = "TEXT")
    private String intro;
    @Embedded
    private Univ univ;
    @Enumerated(EnumType.STRING)
    private MBTI mbti;
    private String storeProfileName;
    @ManyToOne(fetch = LAZY, cascade = ALL) // 양방향 매핑
    @JoinColumn(name = "room_id")
    private Room room;
    @ManyToOne(fetch = LAZY, cascade = ALL) // 양방향 매핑
    @JoinColumn(name = "group_id")
    private Group group;
    private Boolean idle;
    
    @ElementCollection
    private List<String> friendsEmail;
    @OneToMany(mappedBy = "friend", cascade = ALL) // 복함 매핑
    private List<Friendship> friendShipList = new ArrayList<>();
    @OneToMany(mappedBy = "sendUser", cascade = ALL) // 복합 매핑
    private List<UserRequest> sendRequestList = new ArrayList<>();
    @OneToMany(mappedBy = "receiveUser", cascade = ALL) // 복합 매핑
    private List<UserRequest> receiveRequestList = new ArrayList<>();

    //== 연관관계 편의 메서드 ==//
    public void setRoom(Room room) {
        this.room = room;
        room.getUserList().add(this);
    }
    public void setGroup(Group group) {
        this.group = group;
        group.getUserList().add(this);
    }
    public void addSendRequestList(UserRequest userRequest) {
        this.sendRequestList.add(userRequest);
    }
    public void addReceiveRequestList(UserRequest userRequest) {
        this.receiveRequestList.add(userRequest);
    }
    //== 생성 메서드 ==//
    public static User createUser(UserJoinDto userJoinDto) {
        return User.builder()
                .email(userJoinDto.getEmail())
                .password(userJoinDto.getPassword())
                .name(userJoinDto.getName())
                .gender(userJoinDto.getGender())
                .age(userJoinDto.getAge())
                .univ(userJoinDto.getUniv())
                .height(userJoinDto.getHeight())
                .mbti(userJoinDto.getMbti())
                .intro(userJoinDto.getIntro())
                .idle(false)
                .build();
    }


    //== 비즈니스 로직 ==//
//    public void setFriendShip(User me, User friend) { // friendship 은 사실상 생성되면 변하지 않으니.. 생성 메서드에 로직을 넣을까
//        Friendship.createFriendship(me, friend);
//    } // 수행 주체가 UserRequest 이니 그 쪽에 추가하는 게 좋겠음

    /**
     * 그룹 매핑 초기화 및 초대 요청
     * @param group
     * @param userRequest
     */
    public void inviteGroup(Group group, UserRequest userRequest) { // createRequest 한 객체를 service 계층에서 사용해야지, 여기에 해버리면 안될듯
        group.connect(this); // 매핑 초기화
        userRequest.requestProcess(); // UserRequest 를 양방향 매핑해줌
    }

    /**
     * 내 그룹에 게스트를 참여시킴
     * @param guest
     */
    public void joinGroup(User guest) {
        guest.setGroup(this.getGroup());
    }

    /**
     * User 정보 업데이트 ( password,height,mbti,intro )
     */
    public void update(MyPageDto myPageDto,String storeProfileName) {
        this.password = myPageDto.getPassword();
        this.height = myPageDto.getHeight();
        this.mbti = myPageDto.getMbti();
        this.intro = myPageDto.getIntro();
        if (storeProfileName != null) {
            this.storeProfileName = storeProfileName;
        }
    }
    /**
     * 프로필 사진 설정
     */
    public void profileUpdate(String storeProfileName) {
        this.storeProfileName = storeProfileName;
    }

    /**
     * idle 상태 변경, 개인 미팅 참여
     */
    public void startSoloting() {
        this.idle = true;
    }
    /**
     * idle 상태 변경, 개인 미팅 퇴장
     */
    public void stopSoloting() {
        this.idle = false;
    }

}
