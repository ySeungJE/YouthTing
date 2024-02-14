package skuniv.capstone.domain.chatting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
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
    private Long userId;
    private Long roomNum;
    private String sender;
    private String storeProfileName;
    private String storeProfileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id") // 이게 있어야 cascade 전파 가능
    @JsonIgnore
    private Room room;

    //== 생성 메서드 ==//
    public static Chatting createChatting(String content, String sender, Long userId , Room room, String time) { // 얘도 생성과 매핑을 동시에
        Chatting chatting = Chatting.builder()
                .content(content)
                .time(time)
                .sender(sender)
                .userId(userId)
                .build();
        chatting.room = room;
        return chatting;
    }

    //== 비즈니스 로직 ==//

    /**
     * content 설정
     */
    public void updateContent(String content) {
        this.content = content;
    }
    public void updateRoom(Room room) {
        this.room = room;
    }
    public void fillOthers(String sender, Long userId,String storeProfileName, Long roomNum) {
        this.sender=sender;
        this.userId=userId;
        this.roomNum=roomNum;
        this.storeProfileName = storeProfileName;
        this.storeProfileUrl = "http://localhost:8080/user/profile/"+storeProfileName;
    }
}