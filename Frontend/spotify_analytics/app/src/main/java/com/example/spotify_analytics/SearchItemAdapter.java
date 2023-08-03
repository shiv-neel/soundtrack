package com.example.spotify_analytics;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemViewHolder> {
    private List<SpotifyItem> items;

    private Context context;

    public SearchItemAdapter(Context context, List<SpotifyItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public SearchItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.spotify_search_response_display, parent, false);
        return new SearchItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchItemViewHolder holder, int position) {
        SpotifyItem item = items.get(position);
        holder.bindData(item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createUploadPostIntent = new Intent(context, CreateUploadPostActivity.class);
                createUploadPostIntent.putExtra("primary_data", item.getPrimaryData());
                createUploadPostIntent.putExtra("secondary_data", item.getSecondaryData());
                createUploadPostIntent.putExtra("image_uri", item.getImageUri());
                context.startActivity(createUploadPostIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}