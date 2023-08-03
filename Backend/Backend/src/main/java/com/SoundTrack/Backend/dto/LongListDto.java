package com.SoundTrack.Backend.dto;

import com.SoundTrack.Backend.model.PostEntity;
import lombok.Data;

import java.util.List;

@Data
public class LongListDto {
    private List<Long> ids;
}
