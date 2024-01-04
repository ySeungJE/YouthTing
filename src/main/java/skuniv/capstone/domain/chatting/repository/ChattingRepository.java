package skuniv.capstone.domain.chatting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skuniv.capstone.domain.chatting.Chatting;

import java.util.List;

public interface ChattingRepository extends JpaRepository<Chatting, Long> {
    List<Chatting> findByRoomNum(Long roomId);
}
