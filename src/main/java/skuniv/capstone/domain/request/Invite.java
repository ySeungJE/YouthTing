package skuniv.capstone.domain.request;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;
import skuniv.capstone.domain.group.Group;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invite extends Request{
    @OneToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Builder
    protected Invite(Long id, String name, RequestStatus requestStatus, Group group) {
    }

    //== 생성자 메서드 ==//
    public static Invite createInvite(String name, RequestStatus requestStatus, Group group) {
        return Invite.builder()
                .name(name)
                .requestStatus(requestStatus)
                .group(group)
                .build();
    }
}
