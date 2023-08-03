package com._AS_4.SoundTrackBackend.model;


import com._AS_4.SoundTrackBackend.POJOs.Password;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private int id;
    @JsonProperty("username")
    private String username;
    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String password;
    @JsonProperty("accountName")
    private String accountName;

    @JsonProperty("profilePicture")
    private byte[] profilePicture;
    //private UserMetadata metadata;
    //timecreated

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendRequest> friendRequests;

    public User(){
        friendRequests = new ArrayList<>();
    }
    public User(String accountName, String username, String email, String unhashedPassword){
        this.accountName = accountName;
        this.username = username;
        this.email = email;
        password = Password.hashPassword(unhashedPassword);
        friendRequests = new ArrayList<>();
    }
    public boolean addFriendRequest(FriendRequest newRequest){
        //check if it exists already
        for (FriendRequest existing: friendRequests){
            if (existing.getSender().getUsername().equals(newRequest.getSender())){
                return false;
            }
        }
        //else add the requested to the friendRequests
        this.friendRequests.add(newRequest);
        return true;
    }


    public String getUsername(){
        return username;
    }

    public String getHashedPassword(){
        return password;
    }

    //STATIC USER METHODS:

}

