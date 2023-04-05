package skuniv.capstone.domain.group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skuniv.capstone.domain.group.Group;
import skuniv.capstone.domain.group.repository.GroupRepository;
import skuniv.capstone.domain.user.User;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {
    private final GroupRepository groupRepository;
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

}
