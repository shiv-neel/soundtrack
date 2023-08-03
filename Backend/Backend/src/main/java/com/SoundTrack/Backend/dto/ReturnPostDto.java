package com.SoundTrack.Backend.dto;

import com.SoundTrack.Backend.model.PostCommentEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReturnPostDto {
    private long postId;
    private LocalDateTime postCreationTime;
    private int numLikes;
    private int numComments;
    private String originalPosterUsername;
    private long originalPosterId;
    //data passed by post creation DTO
    private String primaryData;
    private String secondaryData;
    private String imageUri;
    private String description;

    private List<PostCommentEntity> comments;
}
