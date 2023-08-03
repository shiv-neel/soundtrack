package com.example.spotify_analytics;

import android.annotation.SuppressLint;

import java.util.Date;

public class Comment {
    private Long commentId;
    private Long commenterUserID;
    private Long postId;
    private String commentText;
    private Date date;



    public Comment(Long commentId, Long commenterUserID, Long postId, String commentText, Date date) {
        this.commentId = commentId;
        this.commenterUserID = commenterUserID;
        this.postId = postId;
        this.commentText = commentText;
        this.date = date;
    }

    public Long getCommenterUserID() {
        return commenterUserID;
    }


    public String getCommentText() {
        return commentText;
    }


    @SuppressLint("DefaultLocale")
    public String getDateString() {
        int month = date.getMonth();
        int day = date.getDate();
        int hour = (date.getHours()) % 12;
        int minute = date.getMinutes();
        return "" + String.format("%02d", month) + "/" + String.format("%02d", day) +
                " " + String.format("%02d", hour) + ":" + String.format("%02d", minute) +
                " " + (date.getHours() > 12 ? "pm" : "am");
    }

    public String getUsername(){
        return "username";
    }

}
