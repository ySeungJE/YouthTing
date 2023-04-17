package skuniv.capstone.web.request;

import lombok.Data;
import skuniv.capstone.domain.request.RequestStatus;
import skuniv.capstone.domain.user.MBTI;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.userrequest.UserRequest;

@Data
public class SendRequestDto {
    private Long requestId;
    private String title;
    private String storeProFileName;
    private RequestStatus status;
    private String email;
    private String univName;

    public SendRequestDto(UserRequest userRequest, User user) {
        requestId = userRequest.getId();
        title = userRequest.getRequest().getName();
        storeProFileName = userRequest.getReceiveUser().getStoreProfileName();
        status = userRequest.getRequest().getRequestStatus();
        email = user.getEmail();
        univName = user.getUniv().getUnivName();
    }
}
