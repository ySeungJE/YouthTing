package skuniv.capstone.domain.chatting.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import skuniv.capstone.domain.chatting.Chatting;
import skuniv.capstone.domain.chatting.RedisChatting;

import java.util.List;

@Repository
public interface ChattingRedisRepository extends CrudRepository<RedisChatting, Long> {
    List<RedisChatting> findByRoomNum(Long roomNum);
}
