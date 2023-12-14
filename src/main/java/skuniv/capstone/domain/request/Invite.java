package skuniv.capstone.domain.request;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import skuniv.capstone.domain.group.Group;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invite extends Request{
    @ManyToOne // 그니까 참조 무결성 제약을 따지려면, 말 그대로 데이터베이스 구조 상 참조 구조를 보고 판단해야 함. 코드 상이 아니라
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
