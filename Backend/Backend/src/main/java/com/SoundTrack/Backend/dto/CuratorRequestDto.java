package com.SoundTrack.Backend.dto;

import lombok.Data;

@Data
public class CuratorRequestDto {
    private long requestId;
    private String requesterFullName;
    private String requesterUsername;

    public CuratorRequestDto(long requestId, String requesterUserFullName, String requesterUsername){
        this.requestId = requestId;
        this.requesterFullName = requesterUserFullName;
        this.requesterUsername = requesterUsername;
    }
}
