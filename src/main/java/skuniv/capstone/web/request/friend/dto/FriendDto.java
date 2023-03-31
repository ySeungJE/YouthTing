package skuniv.capstone.web.request.friend.dto;

import lombok.Data;
import skuniv.capstone.domain.user.User;

@Data
public class FriendDto {
    private String storeProfileName;
    private String name;
    private String univName;
    private String email;
    public FriendDto(User friend) {
        this.email = friend.getEmail();
        this.name = friend.getName();
        this.storeProfileName = friend.getStoreProfileName();
        this.univName = friend.getUniv().getUnivName();
    }
}
