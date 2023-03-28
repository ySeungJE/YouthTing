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
    @OneToMany(mappedBy = "group")
    private List<User> userList = new ArrayList<>();
}
