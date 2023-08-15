package skuniv.capstone.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import skuniv.capstone.domain.user.User;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByIdleOrderByStartTimeAsc(Boolean idle);
    Optional<User> findByStoreProfileName(String storeProfileName);
}
