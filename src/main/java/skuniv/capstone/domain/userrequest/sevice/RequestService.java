package skuniv.capstone.domain.userrequest.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skuniv.capstone.domain.request.Request;
import skuniv.capstone.domain.request.repository.RequestRepository;
import skuniv.capstone.domain.userrequest.UserRequest;
import skuniv.capstone.domain.userrequest.repository.UserRequestRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {
    private final UserRequestRepository userRequestRepository;
    private final RequestRepository requestRepository;
    @Transactional
    public String successFriend(Long requestId) {
        UserRequest userRequest = userRequestRepository.findById(requestId).orElse(null);

        return userRequest.successFriend();
    }

    @Transactional
    public String successInvite(Long requestId) {
        UserRequest userRequest = userRequestRepository.findById(requestId).orElse(null);
        return userRequest.successInvite();
    }

    public UserRequest findUserRequest(Long requestId) {
        return userRequestRepository.findById(requestId).orElse(null);
    }

    public Request findRequest(Long id) {
        return requestRepository.findById(id).orElse(null);
    }

}
