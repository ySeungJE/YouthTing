package skuniv.capstone.domain.request;

import jakarta.persistence.Entity;
import lombok.*;
import skuniv.capstone.domain.group.Group;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend extends Request{

    @Builder
    public Friend(Long id, String name, RequestStatus requestStatus,RequestType requestType) {
        super(id, name, requestStatus,requestType);
    }

    //== 생성자 메서드 ==//
    public static Friend createFriend(String name, RequestStatus requestStatus, RequestType requestType) {
        return Friend.builder()
                .name(name+"님의 친구요청")
                .requestStatus(requestStatus)
                .requestType(requestType)
                .build();
    }
}
