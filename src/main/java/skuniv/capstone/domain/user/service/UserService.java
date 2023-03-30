package skuniv.capstone.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skuniv.capstone.domain.request.Friend;
import skuniv.capstone.domain.request.Request;
import skuniv.capstone.domain.request.RequestStatus;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.user.repository.UserQueryRepository;
import skuniv.capstone.domain.user.repository.UserRepository;
import skuniv.capstone.domain.user.repository.UserSearch;
import skuniv.capstone.domain.userrequest.UserRequest;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserQueryRepository queryRepository;
    @Transactional
    public User join(User user) {
        return userRepository.save(user);
    }
    public List<User> findALl(UserSearch userSearch) { // mbti, 키를 설정하여 동적 검색
        return queryRepository.findAll(userSearch);
    }
    @Transactional
    public String requestFriend(Long myId, Long friendID) {
        User me = userRepository.findById(myId).orElse(null); // 영속성 컨텍스트
        User friend = userRepository.findById(friendID).orElse(null); // 영속성 컨텍스트

        Friend build  = Friend.builder()
                .name(me.getName() + "님의 친구요청")
                .requestStatus(RequestStatus.WAIT)
                .build();
        UserRequest userRequest = UserRequest.builder()
                .sendUser(me)
                .receiveUser(friend)
                .request(build)
                .build();

//        me.addSendRequestList(userRequest);
//        friend.addReceiveRequestList(userRequest);
        userRequest.requestProcess();

        return me.getName()+"님이 "+friend.getName()+"님에게 친구 요청을 보냈습니다";
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

}
