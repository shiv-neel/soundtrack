package com.SoundTrack.Backend.repository;

import com.SoundTrack.Backend.model.PostCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCommentEntityRepository extends JpaRepository<PostCommentEntity, Long> {
    Optional<PostCommentEntity> findById(long id);
}
