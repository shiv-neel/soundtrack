package com.SoundTrack.Backend.dto;

import lombok.Data;

@Data
public class FriendshipDto {

    private long friendshipId;
    private String currentUsername;
    private String friendUsername;

    public FriendshipDto(long friendshipId, String currentUsername, String friendUsername){
        this.friendshipId = friendshipId;
        this.currentUsername = currentUsername;
        this.friendUsername = friendUsername;
    }

}
