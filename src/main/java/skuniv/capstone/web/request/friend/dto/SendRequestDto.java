package skuniv.capstone.web.request.friend.dto;

import lombok.Data;
import skuniv.capstone.domain.request.RequestStatus;
import skuniv.capstone.domain.userrequest.UserRequest;

@Data
public class SendRequestDto {
    private Long requestId;
    private String title;
    private String storeProFileName;
    private RequestStatus status;

    public SendRequestDto(UserRequest userRequest) {
        requestId = userRequest.getId();
        title = userRequest.getReceiveUser().getName();
        storeProFileName = userRequest.getSendUser().getStoreProfileName();
        status = userRequest.getRequest().getRequestStatus();
    }
}
