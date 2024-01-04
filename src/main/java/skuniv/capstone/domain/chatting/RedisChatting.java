package skuniv.capstone.domain.chatting;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("chatting")
@ToString
@AllArgsConstructor
public class RedisChatting {
    @Id
    public Long id;
    @Indexed
    public Long roomNum;
    public String content;
    public String time;
    public Long userId;
    public String sender;
    public String storeProfileName;

    public RedisChatting(Chatting chatting) {
        id = chatting.getId();
        roomNum = chatting.getRoomNum();
        content = chatting.getContent();
        time = chatting.getTime();
        userId = chatting.getUserId();
        sender = chatting.getSender();
        storeProfileName = chatting.getStoreProfileName();
    }

    public RedisChatting() { // 기본 생성자 명시 안하면 noSuchMethodException <init> 이라는 예외 뜸

    }
}