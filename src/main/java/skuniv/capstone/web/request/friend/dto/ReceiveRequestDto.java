package skuniv.capstone.web.request.friend.dto;

import lombok.Data;
import skuniv.capstone.domain.userrequest.UserRequest;

@Data
public class ReceiveRequestDto {
    private Long userRequestId;
    private String title;
    private String storeProFileName;

    public ReceiveRequestDto(UserRequest userRequest) {
        userRequestId = userRequest.getId();
        title = userRequest.getRequest().getName();
        storeProFileName = userRequest.getSendUser().getStoreProfileName();
    }
}
