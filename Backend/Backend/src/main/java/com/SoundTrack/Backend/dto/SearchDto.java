package com.SoundTrack.Backend.dto;

import lombok.Data;

@Data
public class SearchDto {
    private String name;
    private String username;
    private String userType;

    private String profilePicture;

}
//return new SearchDto("ian", "username")