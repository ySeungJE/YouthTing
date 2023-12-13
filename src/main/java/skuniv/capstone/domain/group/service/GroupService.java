package skuniv.capstone.domain.group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skuniv.capstone.domain.group.Group;
import skuniv.capstone.domain.group.repository.GroupQueryRepository;
import skuniv.capstone.domain.group.repository.GroupRepository;
import skuniv.capstone.domain.group.repository.GroupSearch;
import skuniv.capstone.domain.user.User;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupQueryRepository queryRepository;
    @Transactional
    public Group saveGroup(Group group) {
        return groupRepository.save(group);
    }

    public Group findById(Long id) {
        return groupRepository.findById(id).orElse(null);
    }
    @Transactional // 어차피 생성과 마스터 연결은 동시에 해야 하니까 같이 해결
    public void connectMaster(User user, Group group) {
        group.connect(user);
    }
    @Transactional
    public void startGroupting(User sessionUser) {
        sessionUser.getGroup().startGroupting();
    }
    @Transactional
    public void stopGroupting(User sessionUser) {
        sessionUser.getGroup().stopGroupting();
    }

    @Transactional
    public void checkStartTime() {
        List<Group> groups = groupRepository.findByIdleOrderByStartTimeAsc(true);
        for (Group group : groups)
            if (Duration.between(Instant.ofEpochSecond(group.getStartTime()), Instant.now()).getSeconds() > 259200) {
                group.stopGroupting();
            } else break; // StartTime 기준 오름차순을 했으므로, 한번 3일 안쪽으로 들어오면 그 후 유저들은 모두 3일 안쪽인 것
    }
    @Transactional
    public List<Group> findALl(User user, GroupSearch groupSearch) {
        return queryRepository.findAll(user.getGender(),
                user.getGroup().getUserList().size(),
                user.getUniv().getUnivAddress(), groupSearch);
    }

    @Transactional
    public void groupOut(User sessionUser) {
        if (sessionUser.getGroup().getUserList().size() == 1) {
            sessionUser.getGroup().memberOut(sessionUser);
            groupRepository.delete(sessionUser.getGroup());
            sessionUser.groupOut();
        } else {
            sessionUser.getGroup().memberOut(sessionUser);
            sessionUser.groupOut();
        }
    }
}
