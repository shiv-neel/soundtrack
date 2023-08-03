package com.example.spotify_analytics;

public class UserLists {

    private String name,username;

    public UserLists(String name, String username){
        this.setName(name);
        this.setUsername(username);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
