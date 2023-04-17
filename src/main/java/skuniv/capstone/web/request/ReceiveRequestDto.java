package skuniv.capstone.web.request;

import lombok.Data;
import skuniv.capstone.domain.request.Meeting;
import skuniv.capstone.domain.request.RequestStatus;
import skuniv.capstone.domain.request.SoloOrGroup;
import skuniv.capstone.domain.user.MBTI;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.userrequest.UserRequest;

@Data
public class ReceiveRequestDto {
    private Long userRequestId;
    private String title;
    private RequestStatus status;
    private String storeProFileName;
    private String email;
    private String univName;
    public ReceiveRequestDto(UserRequest userRequest, User user) {
        userRequestId = userRequest.getId();
        title = userRequest.getRequest().getName();
        status = userRequest.getRequest().getRequestStatus();
        storeProFileName = user.getStoreProfileName();
        email = user.getEmail();
        univName = user.getUniv().getUnivName();
    }
}
