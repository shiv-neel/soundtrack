package com.SoundTrack.Backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "comments")
public class PostCommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long commenterUserID;

    private String commentText;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private long postId;

}
