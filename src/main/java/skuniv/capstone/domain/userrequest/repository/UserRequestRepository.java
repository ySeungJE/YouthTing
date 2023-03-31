package skuniv.capstone.domain.userrequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import skuniv.capstone.domain.userrequest.UserRequest;
@Transactional
public interface UserRequestRepository extends JpaRepository<UserRequest, Long> {
}
