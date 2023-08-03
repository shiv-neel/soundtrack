package com.SoundTrack.Backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "followers")
public class FollowerRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "current_curator_id")
    private UserEntity currentCurator;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "follower_id")
    private UserEntity followerUser;

    public FollowerRelationship(){
    }

}
