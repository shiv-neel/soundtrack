package com.SoundTrack.Backend.dto;

import lombok.Data;

@Data
public class FollowingRelationshipDto {

    private long followingId;
    private String currentUsername;
    private String curatorUsername;

    public FollowingRelationshipDto(long followingId, String currentUsername, String curatorUsername){
        this.followingId = followingId;
        this.currentUsername = currentUsername;
        this.curatorUsername = curatorUsername;
    }

}
