package skuniv.capstone.domain.friendship;

import jakarta.persistence.*;
import lombok.*;
import skuniv.capstone.domain.user.User;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class FriendShip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User me;
    @OneToOne
    @JoinColumn(name = "friend_id")
    private User friend;
}
