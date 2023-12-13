package skuniv.capstone.domain.group;

import jakarta.persistence.*;
import lombok.*;
import skuniv.capstone.domain.request.Invite;
import skuniv.capstone.domain.user.User;

import java.time.Instant;
import java.time.LocalDateTime;
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
    private Integer groupAge; // null 이었다가 미팅을 참여하면 그때 부여하자
    @OneToOne
    @JoinColumn(name = "master_id")
    private User master;
    @OneToMany(mappedBy = "group") // 얘는 user 가 양방향 매핑해줌
    @Builder.Default // 뭐야 시발 이래야 되네? 그럼 야 시발 User 에 그 수많은 List 들은 왜 그냥 만들어주고 얘는 안만들어주고 쥐랄임?
    private List<User> userList = new ArrayList<>();
    @OneToMany(mappedBy = "group", cascade = ALL) // 얘는 user 가 양방향 매핑해줌
    @Builder.Default // 여기에도 invite 정보가 있어야 cascade로 지울수가 있는가?
    private List<Invite> inviteList = new ArrayList<>();
    private Boolean idle;
    private Long startTime;

    //== 생성 메서드 ==//
    public static Group createGroup(User master) {
        return Group.builder()
                .name(master.getName()+"의 그룹")
                .master(master)
                .idle(false)
                .groupAge(0)
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

    /**
     * idle 상태 변경, 미팅 참여
     */
    public void startGroupting() {
        this.idle=true;
        groupAge(); // 이 때 나이 설정
        this.startTime = Instant.now().getEpochSecond();; // 그룹팅 참여한 시간 설정
    }

    /**
     * idle 상태 변경, 미팅 퇴장
     */
    public void stopGroupting() {
        this.idle=false;
    }

    /**
     * 그룹원들 나이 평균
     */
    public void groupAge() {
        this.groupAge=0;
        for (User user : userList) {
            groupAge+=user.getAge();
        }
        this.groupAge/=this.userList.size();
    }

    /**
     * 이 그룹에서 해당 유저를 퇴장
     * @param sessionUser
     */
    public void memberOut(User sessionUser) {
        this.userList.remove(sessionUser);
    }

    public void addInvite(Invite build) {
        this.inviteList.add(build);
    }
}
