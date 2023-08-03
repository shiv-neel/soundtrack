package com.SoundTrack.Backend.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class WrappedPostDto {
    private String type;
    private ArrayList<String> items;
}
