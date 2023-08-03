package com.example.spotify_analytics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FollowingListRecyclerViewAdapter extends RecyclerView.Adapter<FollowingListRecyclerViewAdapter.FollowingListViewHolder>{

    private ArrayList<listOfFollowers> values;
    private Context context;

    public FollowingListRecyclerViewAdapter(ArrayList<listOfFollowers> values, Context context){
        this.values = values;
        this.context = context;
    }


    @NonNull
    @Override
    public FollowingListRecyclerViewAdapter.FollowingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.following_list_recycler_view, parent, false);
        FollowingListRecyclerViewAdapter.FollowingListViewHolder followingListViewHolder = new FollowingListRecyclerViewAdapter.FollowingListViewHolder(view);
        return followingListViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull FollowingListRecyclerViewAdapter.FollowingListViewHolder holder, int position) {
        listOfFollowers list = values.get(position);
        holder.textViewName.setText(list.getName());
        holder.textViewUsername.setText(list.getUsername());

    }

    @Override
    public int getItemCount() {
        return this.values.size();
    }

    public static class FollowingListViewHolder extends RecyclerView.ViewHolder{
        TextView textViewName, textViewUsername;

        public FollowingListViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.following_list_name);
            textViewUsername = itemView.findViewById(R.id.following_list_account);

        }
    }
}
