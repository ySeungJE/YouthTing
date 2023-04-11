package skuniv.capstone.domain.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skuniv.capstone.domain.request.Request;


public interface RequestRepository extends JpaRepository<Request, Long> {

}
