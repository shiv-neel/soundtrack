package com.SoundTrack.Backend.dto;

import lombok.Data;

@Data
public class FollowerRelationshipDto {

    private long followerId;
    private String currentCuratorUsername;
    private String followerUsername;

    public FollowerRelationshipDto(long followingId, String currentCuratorUsername, String followerUsername){
        this.followerId = followingId;
        this.currentCuratorUsername = currentCuratorUsername;
        this.followerUsername = followerUsername;
    }
}
