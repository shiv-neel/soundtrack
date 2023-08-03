package com.example.spotify_analytics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FollowersListRecyclerViewAdapter extends RecyclerView.Adapter<FollowersListRecyclerViewAdapter.FollowersListViewHolder>{
    private ArrayList<listOfFollowers> values;
    private Context context;
    public FollowersListRecyclerViewAdapter(ArrayList<listOfFollowers> values, Context context){
        this.values = values;
        this.context = context;
    }


    @NonNull

    public FollowersListRecyclerViewAdapter.FollowersListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.followers_list_recycler_view, parent, false);
        FollowersListRecyclerViewAdapter.FollowersListViewHolder followersListViewHolder = new FollowersListRecyclerViewAdapter.FollowersListViewHolder(view);
        return followersListViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull FollowersListRecyclerViewAdapter.FollowersListViewHolder holder, int position) {
        listOfFollowers list = values.get(position);
        holder.textViewName.setText(list.getName());
        holder.textViewUsername.setText(list.getUsername());

    }

    @Override
    public int getItemCount() {
        return this.values.size();
    }

    public static class FollowersListViewHolder extends RecyclerView.ViewHolder{
        TextView textViewName, textViewUsername;

        public FollowersListViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.following_list_name);
            textViewUsername = itemView.findViewById(R.id.following_list_account);

        }
    }
}
