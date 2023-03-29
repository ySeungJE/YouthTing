package skuniv.capstone.domain.friendship;

import jakarta.persistence.*;
import lombok.*;
import skuniv.capstone.domain.user.User;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friendship_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User me;
    @OneToOne
    @JoinColumn(name = "friend_id")
    private User friend;

    //== 생성 메서드 ==//
    public static void createFriendship(User me, User friend) { // 생성과 연관관계 매핑을 동시에
        Friendship myFriendship = Friendship.builder()
                .me(me)
                .friend(friend)
                .build();
        Friendship friendFriendship = Friendship.builder()
                .me(friend)
                .friend(me)
                .build();
        myFriendship.me.getFriendShipList().add(myFriendship);
        friendFriendship.me.getFriendShipList().add(friendFriendship);
    }
}
