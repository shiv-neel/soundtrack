package com.SoundTrack.Backend.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ImageGeneratorJsDto {
    private String username;
    private String type;
    private ArrayList<String> items;
}
