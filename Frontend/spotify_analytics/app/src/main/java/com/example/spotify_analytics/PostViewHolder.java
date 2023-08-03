package com.example.spotify_analytics;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class PostViewHolder extends RecyclerView.ViewHolder {
    TextView primaryData;
    TextView secondarydata;

    TextView postDescription;
    ImageView itemImageView;

    TextView usernameTextView;

    String imageUri;

    TextView numLikesTextView;
    TextView numCommentsTextView;

    public PostViewHolder(View postView) {
        super(postView);
        primaryData = postView.findViewById(R.id.single_post_primary_data);
        secondarydata = postView.findViewById(R.id.single_post_secondary_data);
        postDescription = postView.findViewById(R.id.single_post_description);
        itemImageView = postView.findViewById(R.id.single_post_item_image);
        usernameTextView = postView.findViewById(R.id.single_post_item_header_text);
        numLikesTextView = postView.findViewById(R.id.single_post_like_count);
        numCommentsTextView = postView.findViewById(R.id.single_post_comment_count);

    }

    public void bindData(UserPost post) {
        primaryData.setText(post.getPrimaryData());
        secondarydata.setText(post.getSecondaryData());
        postDescription.setText(post.getDescription());
        usernameTextView.setText(post.getUsername());
        Picasso.get().load(post.getImageUri()).into(itemImageView);
        numLikesTextView.setText(String.valueOf(post.getNumLikes()));
        numCommentsTextView.setText(String.valueOf(post.getNumComments()));
    }
}