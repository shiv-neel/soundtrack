package com._AS_4.SoundTrackBackend.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    private User sender;
    private Timestamp dateCreated;

    public FriendRequest(){

    }
    public FriendRequest(User sender){
        this.sender = sender;
        dateCreated = new Timestamp(System.currentTimeMillis());
    }

    public User getSender() {
        return sender;
    }
}