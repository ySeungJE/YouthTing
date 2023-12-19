package skuniv.capstone.domain.room;


import jakarta.persistence.*;
import lombok.*;
import skuniv.capstone.domain.chatting.Chatting;
import skuniv.capstone.domain.group.Group;
import skuniv.capstone.domain.request.SoloOrGroup;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.userrequest.UserRequest;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;
import static skuniv.capstone.domain.request.SoloOrGroup.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id") // 이거를 바꾸면...ㅋㅋㅋㅋㅋㅋ ㅅㅂ 데이터베이스에 반영을 해줬어야지
    private Long id;
    private String name;
    @OneToMany(mappedBy = "room")
    @Builder.Default
    private List<User> userList = new ArrayList<>();
    @OneToMany(mappedBy = "room", cascade = ALL, fetch = FetchType.EAGER) // 이걸 안하니까 안됐던거 같은데?
    @Builder.Default
    private List<Chatting> chattingList = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private SoloOrGroup soloOrGroup;
    @OneToOne(cascade = ALL) // 양방향 매핑
    @JoinColumn(name = "user_request_id")
    private UserRequest userRequest;

    //== 연관관계 편의 메서드 ==//
    public void addChatting(Chatting chatting) {
        chatting.updateRoom(this);
        this.chattingList.add(chatting);
    }

    //== 생성 메소드 ==//
    public static Room createGroupRoom(String myName, String friendName, UserRequest userRequest, SoloOrGroup soloOrGroup) {
        return Room.builder()
                .name("[ "+myName + "의 그룹, " + friendName + "의 그룹 ] 의 채팅룸")
                .userRequest(userRequest)
                .soloOrGroup(soloOrGroup)
                .build();
    }
    public static Room createSoloRoom(String myName, String friendName, UserRequest userRequest, SoloOrGroup soloOrGroup) {
        return Room.builder()
                .name("[ "+myName + ", " + friendName + " ] 의 채팅룸")
                .userRequest(userRequest)
                .soloOrGroup(soloOrGroup)
                .build();
    }

    //== 비즈니스 로직 ==//
    /**
     * 그룹원들을 룸에 입장
     */
    public void enterGroup(List<User> group1,List<User> group2) {
        group1.forEach(u->u.setRoom(this));
        group2.forEach(u->u.setRoom(this));
        soloOrGroup = GROUP;
    }

    /**
     * 개인 미팅 룸에 입장
     */
    public void enterUser(User user1, User user2) {
        user1.setRoom(this);
        user2.setRoom(this);
        soloOrGroup = SOLO;
    }
}
