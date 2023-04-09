package skuniv.capstone.domain.room;


import jakarta.persistence.*;
import lombok.*;
import skuniv.capstone.domain.chatting.Chatting;
import skuniv.capstone.domain.user.User;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String name;
    @OneToMany(mappedBy = "room")
    private List<User> userList = new ArrayList<>();
    @OneToMany(mappedBy = "room")
    private List<Chatting> chattingList = new ArrayList<>();

    //== 생성 메소드 ==//
    public static Room createRoom(String myName, String friendName) {
        return Room.builder()
                .name(myName + "님의 그룹," + friendName + "님의 그룹의 채팅룸").build();
    }

    //== 비즈니스 로직 ==//
    /**
     * 그룹원들을 룸에 입장
     * @param userList
     */
    public void enterUsers(List<User> userList) {
        this.userList.addAll(userList);
    }
}
