package skuniv.capstone.domain.group;

import jakarta.persistence.*;
import lombok.*;
import skuniv.capstone.domain.user.User;

import java.util.ArrayList;
import java.util.List;

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
    @OneToMany(mappedBy = "group") // 얘는 user 가 양방향 매핑해줌
    private List<User> userList = new ArrayList<>();

    //== 생성 메서드 ==//
    public static Group createGroup(String name, User master) {
        return Group.builder()
                .name(name)
                .master(master)
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
