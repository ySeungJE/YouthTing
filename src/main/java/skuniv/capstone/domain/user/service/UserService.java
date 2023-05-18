package skuniv.capstone.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skuniv.capstone.domain.group.Group;
import skuniv.capstone.domain.request.Friend;
import skuniv.capstone.domain.request.Invite;
import skuniv.capstone.domain.request.RequestStatus;
import skuniv.capstone.domain.request.RequestType;
import skuniv.capstone.domain.user.Gender;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.user.repository.UserQueryRepository;
import skuniv.capstone.domain.user.repository.UserRepository;
import skuniv.capstone.domain.user.repository.UserSearch;
import skuniv.capstone.domain.userrequest.UserRequest;
import skuniv.capstone.web.user.dto.MyPageDto;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static skuniv.capstone.web.login.controller.LoginController.LOGIN_USER;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserQueryRepository queryRepository;
    @Transactional
    public User join(User user) {
        return userRepository.save(user);
    }
    public List<User> findALl(Gender gender, UserSearch userSearch) { // mbti, 키를 설정하여 동적 검색
        return queryRepository.findAll(gender,userSearch);
    }
    @Transactional
    public String requestFriend(User me, User friend) {

        Friend build  = Friend.createFriend(me.getName(), RequestStatus.WAIT, RequestType.FRIEND);

        UserRequest userRequest = UserRequest.createUserRequest(me, friend, build);

        userRequest.requestProcess();

        return me.getName()+"님이 "+friend.getName()+"님에게 친구 요청을 보냈습니다";
    }
    @Transactional
    public String requestGroup(User me, User friend, Group group) {

        Invite build  = Invite.createInvite(me.getName(), RequestStatus.WAIT, RequestType.INVITE, group);

        UserRequest userRequest = UserRequest.createUserRequest(me, friend, build);

        userRequest.requestProcess();

        return me.getName()+"님이 "+friend.getName()+"님을 그룹에 초대했습니다";
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    public User getSessionUser(HttpServletRequest request) {
        User session = (User) request.getSession().getAttribute(LOGIN_USER);

        User user = findById(session.getId());
        return user;
    }

    @Transactional
    public void updateUser(MyPageDto myPageDto, String storeProfileName) {
        User user = findByEmail(myPageDto.getEmail());
        user.update(myPageDto,storeProfileName);
    }
    @Transactional
    public void profileUpdate(User created, String sample) {
        created.profileUpdate(sample);
    }
    @Transactional
    public void startSoloting(User sessionUser) {
        sessionUser.startSoloting();
    }
    @Transactional
    public void stopSoloting(User sessionUser) {
        sessionUser.stopSoloting();
    }
    @Transactional
    public void checkStartTime() {
        List<User> userList = userRepository.findByIdleOrderByStartTimeAsc(true);
        for (User user : userList) {
            if (Duration.between(Instant.ofEpochSecond(user.getStartTime()), Instant.now()).getSeconds() > 259200) {
                user.stopSoloting();
            } else break; // StartTime 기준 오름차순을 했으므로, 한번 3일 안쪽으로 들어오면 그 후 유저들은 모두 3일 안쪽인 것
        }
    }

//    public boolean findDuplicated(String email) {
//
//    }
}
