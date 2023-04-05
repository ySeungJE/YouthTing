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
    public Invite(Long id, String name, RequestStatus requestStatus,RequestType requestType, Group group) {
        super(id, name, requestStatus, requestType);
        this.group = group;
    }
    //== 생성자 메서드 ==//
    public static Invite createInvite(String name, RequestStatus requestStatus,RequestType requestType, Group group) {
        return Invite.builder()
                .name(name+"님의 그룹 초대")
                .requestStatus(requestStatus)
                .requestType(requestType)
                .group(group)
                .build();
    }
}
