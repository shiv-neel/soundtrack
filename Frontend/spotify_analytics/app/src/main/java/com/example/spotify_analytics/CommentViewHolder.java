package com.example.spotify_analytics;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    TextView usernameTextView;
    TextView commentTextView;

    TextView dateTextView;

    public CommentViewHolder(View commentView) {
        super(commentView);
        usernameTextView = commentView.findViewById(R.id.comment_username);
        commentTextView = commentView.findViewById(R.id.comment_data);
        dateTextView = commentView.findViewById(R.id.comment_date);

    }

    public void bindData(Comment comment) {
        if (comment.getCommenterUserID() == null) {
            usernameTextView.setText("Anonymous");
        } else if (comment.getCommenterUserID().equals(1L)) {
            usernameTextView.setText("ibdalton");
        }
        else if (comment.getCommenterUserID().equals(4L)) {
            usernameTextView.setText("shiv1622");
        }
        else {
            usernameTextView.setText("jondoe123");
        }
        commentTextView.setText(comment.getCommentText());
        dateTextView.setText(comment.getDateString());
    }
}