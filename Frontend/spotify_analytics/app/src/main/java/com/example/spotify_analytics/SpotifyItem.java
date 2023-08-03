package com.example.spotify_analytics;

public class SpotifyItem {
    private String primaryData;
    private String secondaryData;
    private String imageUri;

    public SpotifyItem(String primaryData, String imageUri, String secondaryData) {
        this.primaryData = primaryData;
        this.imageUri = imageUri;
        this.secondaryData = secondaryData;
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
}
