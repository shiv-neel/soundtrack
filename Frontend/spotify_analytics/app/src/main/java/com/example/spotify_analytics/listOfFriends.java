package com.example.spotify_analytics;

public class listOfFriends {
    private String name,username;

    public listOfFriends(String name, String username) {
        this.name = name;
        this.username = username;
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
