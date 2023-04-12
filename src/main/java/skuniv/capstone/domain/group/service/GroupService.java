package skuniv.capstone.domain.group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skuniv.capstone.domain.group.Group;
import skuniv.capstone.domain.group.repository.GroupQueryRepository;
import skuniv.capstone.domain.group.repository.GroupRepository;
import skuniv.capstone.domain.user.User;

import java.time.Duration;
import java.time.LocalDateTime;
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
        List<Group> groupList = groupRepository.findByIdle(true);
        for (int i=groupList.size()-1; i>=0; i--) {
            if (Duration.between(groupList.get(i).getStartTime(), LocalDateTime.now()).getSeconds() > 259200) {
                g.stopGroupting();
            }
        }
        groupList.forEach(g -> {
            if (Duration.between(g.getStartTime(), LocalDateTime.now()).getSeconds() > 259200) {
                g.stopGroupting();
            }
        });
    }
    @Transactional
    public List<Group> findALl(User user) {
        return queryRepository.findAll(user.getGender(),
                user.getGroup().getUserList().size(),
                user.getUniv().getUnivAddress());
    }
}
