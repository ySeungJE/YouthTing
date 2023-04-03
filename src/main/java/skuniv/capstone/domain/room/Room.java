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
}