package skuniv.capstone.domain.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skuniv.capstone.domain.group.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
