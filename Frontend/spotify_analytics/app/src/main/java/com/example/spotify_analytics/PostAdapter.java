package com.example.spotify_analytics;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {
    private List<UserPost> posts;

    private Context context;

    public PostAdapter(Context context, List<UserPost> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.single_post_display, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        UserPost post = posts.get(position);
        holder.bindData(post);
        holder.itemView.setOnClickListener(v -> {
            Intent createUploadPostIntent = new Intent(context, ViewPostActivity.class);
            createUploadPostIntent.putExtra("post_id", post.getId());
            context.startActivity(createUploadPostIntent);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}