package com.SoundTrack.Backend.repository;

import com.SoundTrack.Backend.model.PostLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeEntityRepository extends JpaRepository<PostLikeEntity, Long> {
    Optional<PostLikeEntity> findById(long id);

    Optional<PostLikeEntity> findByPostLikedId(long id);
}
