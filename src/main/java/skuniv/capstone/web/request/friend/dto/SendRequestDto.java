package skuniv.capstone.web.request.friend.dto;

import lombok.Data;
import skuniv.capstone.domain.request.RequestStatus;
import skuniv.capstone.domain.userrequest.UserRequest;

@Data
public class SendRequestDto {
    private Long send;
    private Long receive;
    private String title;
    private String storeProFileName;
    private RequestStatus status;

    public SendRequestDto(UserRequest userRequest) {
        send = userRequest.getReceiveUser().getId();
        receive = userRequest.getSendUser().getId();
        title = userRequest.getReceiveUser().getName();
        storeProFileName = userRequest.getSendUser().getStoreProfileName();
        status = userRequest.getRequest().getRequestStatus();
    }
}
