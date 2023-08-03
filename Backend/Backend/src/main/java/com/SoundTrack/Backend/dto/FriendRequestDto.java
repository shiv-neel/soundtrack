package com.SoundTrack.Backend.dto;

import lombok.Data;

@Data
public class FriendRequestDto {
    private long requestId;
    private long requesterUserId;
    private String requesterUsername;

    public FriendRequestDto(long requestId, long requesterUserId, String requesterUsername){
        this.requestId = requestId;
        this.requesterUserId = requesterUserId;
        this.requesterUsername = requesterUsername;
    }
}
