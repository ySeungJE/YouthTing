package skuniv.capstone.domain.chatting;

import jakarta.persistence.*;
import lombok.*;
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
    private LocalDateTime time;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}
