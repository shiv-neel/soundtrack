package com.SoundTrack.Backend.repository;

import com.SoundTrack.Backend.model.CuratorRequest;
import com.SoundTrack.Backend.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuratorRequestRepository extends JpaRepository<CuratorRequest, Long> {
    boolean existsById(int id);

}
