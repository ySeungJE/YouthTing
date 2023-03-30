package skuniv.capstone.domain.request;

import jakarta.persistence.Entity;
import lombok.*;
import skuniv.capstone.domain.group.Group;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend extends Request{

    @Builder
    public Friend(Long id, String name, RequestStatus requestStatus) {
        super(id, name, requestStatus);
    }

    //== 생성자 메서드 ==//
    public static Friend createFriend(String name, RequestStatus requestStatus) {
        return Friend.builder()
                .name(name)
                .requestStatus(requestStatus)
                .build();
    }
}
