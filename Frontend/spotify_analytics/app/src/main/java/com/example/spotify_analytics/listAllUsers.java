package com.example.spotify_analytics;

public class listAllUsers {
    private String name,username;

    public listAllUsers(String name, String username) {
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
}
