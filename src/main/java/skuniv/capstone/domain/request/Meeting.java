package skuniv.capstone.domain.request;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.AnyKeyJavaClass;
import skuniv.capstone.domain.group.Group;
import skuniv.capstone.domain.room.Room;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Meeting extends Request{
    @OneToOne
    @JoinColumn(name = "room_id")
    private Room room;
    @Enumerated(EnumType.STRING)
    private SoloOrGroup soloOrGroup;

    @Builder // 이거 때문에 값이 안들어오는 거였네
    public Meeting(Long id, String name, RequestStatus requestStatus,RequestType requestType, Room room, SoloOrGroup soloOrGroup) {
        super(id, name, requestStatus,requestType);
        this.room = room;
        this.soloOrGroup=soloOrGroup;
    }

    //== 생성자 메서드 ==//
    public static Meeting createMeeting(String name, RequestStatus requestStatus, Room room, SoloOrGroup soloOrGroup) {
        return Meeting.builder()
                .name(name)
                .requestStatus(requestStatus)
                .room(room)
                .soloOrGroup(soloOrGroup)
                .build();
    }
}
