package skuniv.capstone.domain.group;

import jakarta.persistence.*;
import lombok.*;
import skuniv.capstone.domain.user.User;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;
    private String name;
    @OneToOne
    @JoinColumn(name = "master_id")
    private User master;
    @OneToMany(mappedBy = "group", cascade = ALL) // 얘는 user 가 양방향 매핑해줌
    @Builder.Default // 뭐야 시발 이래야 되네? 그럼 야 시발 User 에 그 수많은 List 들은 왜 그냥 만들어주고 얘는 안만들어주고 쥐랄임?
    private List<User> userList = new ArrayList<>();
    private Boolean idle;

    //== 생성 메서드 ==//
    public static Group createGroup(User master) {
        return Group.builder()
                .name(master.getName())
                .master(master)
                .idle(false)
                .build();
    }

    //== 비즈니스 로직 ==//

    /**
     * master 유저를 등록하고 상호 매핑
     */
    public void connect(User user) {
        this.master = user;
        user.setGroup(this);
    }
}
