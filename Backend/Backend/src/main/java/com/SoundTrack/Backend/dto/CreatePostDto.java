package com.SoundTrack.Backend.dto;

import lombok.Data;

@Data
public class CreatePostDto {
    private String primaryData;
    private String secondaryData;
    private String imageUri;
    private String description;
}
