package com.SoundTrack.Backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "friendships")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "current_user_id")
    private UserEntity currentUser;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "friend_id")
    private UserEntity friendUser;

    public Friendship(){
    }
}

