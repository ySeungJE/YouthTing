package skuniv.capstone.domain.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skuniv.capstone.domain.room.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
