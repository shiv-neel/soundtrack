package com.example.spotify_analytics;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class SearchItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView primaryData;
    TextView secondarydata;
    ImageView itemImageView;

    public SearchItemViewHolder(View itemView) {
        super(itemView);
        primaryData = itemView.findViewById(R.id.primary_item_response);
        secondarydata = itemView.findViewById(R.id.secondary_item_response);
        itemImageView = itemView.findViewById(R.id.image_response);
    }

    public void bindData(SpotifyItem item) {
        primaryData.setText(item.getPrimaryData());
        secondarydata.setText(item.getSecondaryData());
        Picasso.get().load(item.getImageUri()).into(itemImageView);
    }

    @Override
    public void onClick(View view) {
        Intent createUploadPostIntent = new Intent(view.getContext(), CreateUploadPostActivity.class);
        createUploadPostIntent.putExtra("primary_data", primaryData.getText());
        createUploadPostIntent.putExtra("secondary_data", secondarydata.getText());
        view.getContext().startActivity(createUploadPostIntent);
    }
}