package skuniv.capstone.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skuniv.capstone.domain.group.Group;
import skuniv.capstone.domain.group.repository.GroupRepository;
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
import skuniv.capstone.web.user.dto.UserJoinDto;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static skuniv.capstone.domain.user.Gender.*;
import static skuniv.capstone.domain.user.MBTI.*;
import static skuniv.capstone.web.login.controller.LoginController.LOGIN_USER;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserQueryRepository queryRepository;
    private final GroupRepository groupRepository;
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
        group.addInvite(build);

        log.info("저장된 invite는 : {}", groupRepository.findById(group.getId()).orElse(null).getInviteList());

        userRequest.requestProcess();

        return me.getName()+"님이 "+friend.getName()+"님을 그룹에 초대했습니다";
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    public User findByStoreProfileName(String storeProfileName) {
        return userRepository.findByStoreProfileName(storeProfileName).orElse(null);
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

    @Transactional
    public Boolean userConfirm(String code, User sessionUser) {
        Boolean result = code.equals(sessionUser.getUniqueCode());

        if (result==true) {
            sessionUser.upgradeAuthority();
        }
        return result;
    }

    @Transactional
//    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        User join = join(User.createUser(new UserJoinDto("2017301050@skuniv.ac.kr", "123", "user_man1", MAN, 26, "서울 성북구 서경로 124", "서경대학교",
                178, ISTP, "안녕하세요"), "user_man1Init.jpg"));
        User join1 = join(User.createUser(new UserJoinDto("testMan1@naver.com", "123", "user_man2", MAN, 25, "서울 성북구 서경로 124", "국민대학교",
                180, ENFJ, "안녕하세요.\n" +
                "저는 큰 키에 서글서글한 웃음이 매력적인 사람입니다. 성격은 솔직, 적극, 감성적, 재미있다는 단어들로 표현할 수 있습니다. 누구의 무슨 이야기라도 잘 듣는 사람이고, 정이 많습니다. 평생을 친구처럼 다정한 부모님을 보고 성장하였습니다. 이제는 삶의 길이가 비슷한 사람과 알콩달콩 인생을 살아가 보려고 합니다. 더 자세한 이야기는 만나서 하는 게 어떨까요? 곧 뵐게요^^"), "user_man2Init.jpg"));
        User join2 = join(User.createUser(new UserJoinDto("testMan2@naver.com", "123", "user_man3", MAN, 21, "서울 성북구 서경로 124", "국민대학교",
                183, ISTJ, "안녕하세요.\n" +
                "저는 큰 키에 서글서글한 웃음이 매력적인 사람입니다. 성격은 솔직, 적극, 감성적, 재미있다는 단어들로 표현할 수 있습니다. 누구의 무슨 이야기라도 잘 듣는 사람이고, 정이 많습니다. 평생을 친구처럼 다정한 부모님을 보고 성장하였습니다. 이제는 삶의 길이가 비슷한 사람과 알콩달콩 인생을 살아가 보려고 합니다. 더 자세한 이야기는 만나서 하는 게 어떨까요? 곧 뵐게요^^"), "user_man3Init.jpg"));
        User join3 = join(User.createUser(new UserJoinDto("testMan3@naver.com", "123", "user_man4", MAN, 22, "서울 성북구 서경로 124", "국민대학교",
                173, INFJ, "안녕하세요.\n" +
                "저는 큰 키에 서글서글한 웃음이 매력적인 사람입니다. 성격은 솔직, 적극, 감성적, 재미있다는 단어들로 표현할 수 있습니다. 누구의 무슨 이야기라도 잘 듣는 사람이고, 정이 많습니다. 평생을 친구처럼 다정한 부모님을 보고 성장하였습니다. 이제는 삶의 길이가 비슷한 사람과 알콩달콩 인생을 살아가 보려고 합니다. 더 자세한 이야기는 만나서 하는 게 어떨까요? 곧 뵐게요^^"), "user_man4Init.jpg"));
        User join4 = join(User.createUser(new UserJoinDto("testWoman1@gmail.com", "123", "user_woman1", WOMAN, 23, "서울 성북구 보문로34다길 2", "성신여자대학교",
                165, ENTP, "저는 외모를 중요시합니다. 제 맘에 드는 외모요^^ 그리고 험한 세상 풍파를 저와 같이 헤쳐나갈 강단이 있는 사람, 부모님을 사랑하는 사람, 타인을 곤경에 빠뜨리지 않는 성격의 소유자와 만나고 싶습니다. 외유내강형으로서 대화가 잘 통하는 사람이라면 다 좋습니다."), "user_woman1Init.jpg"));
        User join5 = join(User.createUser(new UserJoinDto("testWoman2@gmail.com", "123", "user_woman2", WOMAN, 24, "서울 성북구 보문로34다길 2", "성신여자대학교",
                161, ISFP, "안녕하세요. 서로 믿음을 가지고 각자의 시간은 존중해 주며 친구같이 잘 지낼 수 있는 좋은 인연 만났으면 좋겠어요. 집을 좋아하나 영화나 맛있는 거 먹으러 나가거나 산책하기 좋아합니다. 검소하고 술 많이 하지않고 너무 외향적이거나 감성적이지 않은 분 만났으면 좋겠습니다^^"), "user_woman2Init.jpg"));
        User join6 = join(User.createUser(new UserJoinDto("testWoman3@gmail.com", "123", "user_woman3", WOMAN, 21, "서울 성북구 보문로34다길 2", "성신여자대학교",
                168, ESFP, "저는 다른사람의 말을 경청하고 사람들이 처한 다른 환경에 대한 이해 하려고 하며, 항상 긍정적이고 웃음이 많아 다른 사람들과 같이 잘 어울립니다.\n" +
                "주말에는 여행을 가는것을 즐기며 틈틈히 시간 날때마다 독서를 하는게 취미이고, 최근에는 캠핑에 관심이 있어 캠핑에 대해 공부 하고 있습니다.\n" +
                "현재에 안주하지 않고 꿈을 위해 하루하루 자기계발과 영어공부를 하며 미래를 준비하고 있는 남성입니다."), "user_woman3Init.jpg"));
        User join7 = join(User.createUser(new UserJoinDto("testWoman4@gmail.com", "123", "user_woman4", WOMAN, 20, "서울 성북구 보문로34다길 2", "성신여자대학교",
                158, ESTJ, "안녕하세요.\n" +
                "상대방을 배려할 줄 아는 연인 만나고자 찾아왔습니다.\n" +
                "잘 부탁드립니다."), "user_woman4Init.jpg"));
        join.upgradeAuthority();
        join1.upgradeAuthority();
        join2.upgradeAuthority();
        join3.upgradeAuthority();
        join4.upgradeAuthority();
        join5.upgradeAuthority();
        join6.upgradeAuthority();
        join7.upgradeAuthority();
    }

    public void exitRoom(User sessionUser) {
    }
//    public boolean findDuplicated(String email) {
//
//    }
}
