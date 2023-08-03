package com.SoundTrack.Backend.dto;

import lombok.Data;

@Data
public class GetPostsByUserIdDto {
    private long userId;
    private int limit;
}
