package skuniv.capstone.web.request.group.dto;

import lombok.Data;
import skuniv.capstone.domain.group.Group;

@Data
public class GroupDto {
    private String groupName;
    private Integer groupAge;
    private String masterEmail;
    private Integer headCount;
    private String univName;
    private String masterProfile;
    public GroupDto(Group group) {
        groupName = group.getName();
        groupAge = group.getGroupAge();
        masterEmail = group.getMaster().getEmail();
        headCount = group.getUserList().size();
        univName = group.getMaster().getUniv().getUnivName();
        masterProfile = group.getMaster().getStoreProfileName();
    }
}
