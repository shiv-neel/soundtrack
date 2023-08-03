package com.SoundTrack.Backend.dto;

import lombok.Data;

@Data
public class PostAddCommentDto {
    private Long postId;
    private String comment;
}
