package skuniv.capstone.web.request.room.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import skuniv.capstone.domain.user.User;

@Data
@NoArgsConstructor
public class ShowChattingDto {
    String content;
    String userName;
    String userProfile;

    public ShowChattingDto(String content, User user) {
        this.content = content;
        this.userName = user.getName();
        this.userProfile = user.getStoreProfileName();
    }
}
