package com.SoundTrack.Backend.repository;

import com.SoundTrack.Backend.model.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostEntityRepository extends JpaRepository<PostEntity, Long> {
    Optional<PostEntity> findById(Long id);
    boolean existsById(long id);
}
