package com.SoundTrack.Backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "friend_requests")
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "user_sender_id")
    private UserEntity sender;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_receiver_id")
    private UserEntity receiver;

    public FriendRequest(){
    }
}
