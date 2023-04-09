package skuniv.capstone.web.request;

import lombok.Data;
import skuniv.capstone.domain.request.RequestStatus;
import skuniv.capstone.domain.userrequest.UserRequest;

@Data
public class ReceiveRequestDto {
    private Long userRequestId;
    private String title;
    private String storeProFileName;
    private RequestStatus status;

    public ReceiveRequestDto(UserRequest userRequest) {
        userRequestId = userRequest.getId();
        title = userRequest.getRequest().getName();
        storeProFileName = userRequest.getSendUser().getStoreProfileName();
        status = userRequest.getRequest().getRequestStatus();
    }
}
