package com.SoundTrack.Backend.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "followings")
public class FollowingRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "current_user_id")
    private UserEntity currentUser;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "curator_id")
    private UserEntity curatorUser;

    public FollowingRelationship(){
    }

}
