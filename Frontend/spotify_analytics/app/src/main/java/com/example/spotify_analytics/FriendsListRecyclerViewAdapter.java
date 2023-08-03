package com.example.spotify_analytics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FriendsListRecyclerViewAdapter extends RecyclerView.Adapter<FriendsListRecyclerViewAdapter.FriendsListViewHolder>{
    private ArrayList<listOfFriends> values;
    private Context context;

    public FriendsListRecyclerViewAdapter(ArrayList<listOfFriends> values, Context context){
        this.values = values;
        this.context = context;
    }


    @NonNull
    @Override
    public FriendsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_recycler_view, parent, false);
        FriendsListViewHolder friendsListViewHolder = new FriendsListViewHolder(view);
        return friendsListViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull FriendsListViewHolder holder, int position) {
        listOfFriends list = values.get(position);
        holder.textViewName.setText(list.getName());
        holder.textViewUsername.setText(list.getUsername());

    }

    @Override
    public int getItemCount() {
        return this.values.size();
    }

    public static class FriendsListViewHolder extends RecyclerView.ViewHolder{
        TextView textViewName;
        TextView textViewUsername;

        public FriendsListViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.friend_list_name);
            textViewUsername = itemView.findViewById(R.id.friend_list_account);

        }
    }

}
