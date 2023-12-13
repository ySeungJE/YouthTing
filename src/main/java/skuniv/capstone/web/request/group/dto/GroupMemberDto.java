package skuniv.capstone.web.request.group.dto;

import lombok.Data;
import skuniv.capstone.domain.user.User;

@Data
public class GroupMemberDto {
    private String storeProfileName;
    private String name;
    private String univName;
    private String email;
    private Boolean master;

    public GroupMemberDto(User u) {
        this.storeProfileName = u.getStoreProfileName();
        this.name = u.getName();
        this.univName = u.getUniv().getUnivName();
        this.email = u.getEmail();
        this.master = u.getGroup().getMaster()==u;
    }
}
