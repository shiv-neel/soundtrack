package com.SoundTrack.Backend.dto;

import lombok.Data;

@Data
public class RelationshipDto {

    String message;

    public RelationshipDto(String message){
        this.message = message;
    }
}
