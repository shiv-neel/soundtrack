package com.SoundTrack.Backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "curator_requests")
public class CuratorRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "new_curator_id")
    private UserEntity newCurator;

}
