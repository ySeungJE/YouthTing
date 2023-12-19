package skuniv.capstone.web.request.room.dto;

import lombok.Data;
import skuniv.capstone.domain.user.User;

@Data
public class ChattingMemberDto {
        private String storeProfileName;
        private String name;

        public ChattingMemberDto(User u) {
            this.storeProfileName = u.getStoreProfileName();
            this.name = u.getName();
    }
}
