package com.rodrigo.drawing_contest.repositories;

import com.rodrigo.drawing_contest.models.room.Room;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoomRepository extends CrudRepository<Room, UUID> {}
