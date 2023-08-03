package com.SoundTrack.Backend.dto;

import com.SoundTrack.Backend.model.Role;
import lombok.Data;

import java.util.List;

@Data
public class UserObjectDto {
    private long id;
    private String username;
    private String email;
    private String fullName;
    private String spotifyUsername;
    private String profilePicture;

    private List<Role> roles;
    private List<Long> userPostIds;
    private List<FriendRequestDto> pendingFriendRequests;
}
