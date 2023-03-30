package skuniv.capstone.domain.user;

import jakarta.persistence.*;
import lombok.*;
import skuniv.capstone.domain.friendship.Friendship;
import skuniv.capstone.domain.userrequest.UserRequest;
import skuniv.capstone.domain.group.Group;
import skuniv.capstone.domain.room.Room;
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
    private int height;
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
    public static User createUser(UserJoinDto userJoinDto, String storeProfileName) {
        return User.builder()
                .email(userJoinDto.getEmail())
                .password(userJoinDto.getPassword())
                .name(userJoinDto.getName())
                .gender(userJoinDto.getGender())
                .univ(userJoinDto.getUniv())
                .height(userJoinDto.getHeight())
                .mbti(userJoinDto.getMbti())
                .intro(userJoinDto.getIntro())
                .storeProfileName(storeProfileName)
                .build();
    }

    //== 비즈니스 로직 ==//
    public void setFriendShip(User me, User friend) { // friendship 은 사실상 생성되면 변하지 않으니.. 생성 메서드에 로직을 넣을까
        Friendship.createFriendship(me, friend);
    }
    public void requestFriend(UserRequest userRequest) { // UserRequest 객체를 만들어서 내 sendList 상대 receiveList 에 넣는게 목적
        userRequest.requestProcess();
    }

    public void connectGroup(User user, Group group) {
        group.connect(user);
    }
    public void inviteGroup(Group group, UserRequest userRequest) { // createRequest 한 객체를 service 계층에서 사용해야지, 여기에 해버리면 안될듯
        this.setGroup(group);
        userRequest.requestProcess();
    }
    public void joinGroup(User guest) {
        guest.setGroup(this.getGroup());
    }
}
