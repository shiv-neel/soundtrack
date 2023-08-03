package com.SoundTrack.Backend.repository;

import com.SoundTrack.Backend.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    //query method to find users by username
    Optional<UserEntity> findByUsername(String username);

    Optional<Long> findIdByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
