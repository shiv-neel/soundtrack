package com.SoundTrack.Backend.repository;

import java.util.Optional;

import com.SoundTrack.Backend.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestRepository extends JpaRepository<UserEntity, Long>{
    boolean existsById(int id);
}
