package skuniv.capstone.domain.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skuniv.capstone.domain.group.Group;
import skuniv.capstone.domain.user.User;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByIdle(boolean idle);
    List<Group> findByIdleByGroupDesc(); // 낼 생각해보자!!
}
