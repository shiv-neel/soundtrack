package com.example.spotify_analytics;

public class UserPost {

    private Long posterId;

    private Long id;
    private String primaryData;
    private String secondaryData;
    private String imageUri;
    private String description;
    private String username;

    private int numLikes;
    private int numComments;

    private int datePosted;

    public UserPost(Long id, Long posterId, String username, String primaryData, String secondaryData,
                    String imageUri, String description, int numLikes, int numComments) {
        this.id = id;
        this.posterId = posterId;
        this.username = username;
        this.primaryData = primaryData;
        this.imageUri = imageUri;
        this.secondaryData = secondaryData;
        this.description = description;
        this.numLikes = numLikes;
        this.numComments = numComments;
    }

    public Long getId() {
        return id;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getPrimaryData() {
        return primaryData;
    }

    public String getSecondaryData() {
        return secondaryData;
    }

    public String getDescription() {
        return description;
    }

    public String getUsername() {
        return username;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public int getNumComments() {
        return numComments;
    }
}
