package skuniv.capstone.domain.user;

import jakarta.persistence.*;
import lombok.*;
import skuniv.capstone.domain.userrequest.UserRequest;
import skuniv.capstone.domain.friendship.FriendShip;
import skuniv.capstone.domain.group.Group;
import skuniv.capstone.domain.room.Room;

import java.util.ArrayList;
import java.util.List;

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
    private String name;
    private int height;
    private String intro;
    @Embedded
    private Univ school;
    @Enumerated(EnumType.STRING)
    private MBTI mbti;
    private String profileFile;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "room_id")
    private Room room;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "group_id")
    private Group group;
    private Boolean idle;
    @OneToMany(mappedBy = "friend")
    private List<FriendShip> friendShipList = new ArrayList<>();
    @OneToMany(mappedBy = "sendUser")
    private List<UserRequest> sendRequestList = new ArrayList<>();
    @OneToMany(mappedBy = "receiveUser")
    private List<UserRequest> receiveRequestList = new ArrayList<>();


}
