package skuniv.capstone.domain.chatting;

import jakarta.persistence.*;
import lombok.*;
import skuniv.capstone.domain.friendship.Friendship;
import skuniv.capstone.domain.room.Room;
import skuniv.capstone.domain.user.User;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class Chatting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatting_id")
    private Long id;
    private String content;
    private String time;
    private String sender;
    //    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
    @ManyToOne
    @JoinColumn(name = "room_id") // 그냥 이거는.. 음 room 에서 chatting 배열이 꼭 필요하므로 좀 불편하지만 해야 하는..? 느낌으로 인식해야 할듯
    private Room room;

    //== 생성 메서드 ==//
    public static Chatting createChatting(String content, User user, Room room) { // 얘도 생성과 매핑을 동시에
        Chatting chatting = Chatting.builder()
                .content(content)
                .sender(user.getName())
                .time(LocalDateTime.now())
                .build();
        chatting.room = room;
        room.getChattingList().add(chatting);
        return chatting;
    }

    //== 비즈니스 로직 ==//

    /**
     * content 설정
     */
    public void updateContent(String content) {
        this.content = content;
    }
}