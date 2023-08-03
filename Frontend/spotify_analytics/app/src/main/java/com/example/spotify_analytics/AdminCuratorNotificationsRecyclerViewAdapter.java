package com.example.spotify_analytics;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.spotify_analytics.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminCuratorNotificationsRecyclerViewAdapter extends RecyclerView.Adapter<AdminCuratorNotificationsRecyclerViewAdapter.NotificationsViewHolder>{
    private ArrayList<UserLists> values;
    private Context context;

    public AdminCuratorNotificationsRecyclerViewAdapter(ArrayList<UserLists> values, Context context){
        this.values = values;
        this.context = context;
    }

    @NonNull
    @Override
    public AdminCuratorNotificationsRecyclerViewAdapter.NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_row_item, parent, false);
        return new AdminCuratorNotificationsRecyclerViewAdapter.NotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminCuratorNotificationsRecyclerViewAdapter.NotificationsViewHolder holder, int position) {
        UserLists modal = values.get(position);
        holder.friendRequestName.setText(modal.getName());
        holder.friendRequestAccount.setText(modal.getUsername());
    }

    @Override
    public int getItemCount() { return this.values.size(); }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder {
        private TextView friendRequestName, friendRequestAccount;
        private Button buttonAccept, buttonDecline;

        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            friendRequestName = itemView.findViewById(R.id.friend_request_name);
            friendRequestAccount = itemView.findViewById(R.id.friend_request_account);
//            itemView.setOnClickListener();
            buttonAccept = itemView.findViewById(R.id.accept_button);
            buttonDecline = itemView.findViewById(R.id.decline_button);


            buttonAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RequestQueue queue = AppController.getInstance().getRequestQueue();
                    String acceptFriendRequestUrl = "https://coms-309-026.class.las.iastate.edu/api/curatorRequests/acceptCuratorRequest/" + friendRequestAccount.getText().toString();
                    JSONObject temp = new JSONObject();
                    try {
                        temp.put("username", friendRequestAccount.getText().toString());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, acceptFriendRequestUrl, temp,
                            response -> {
                                Log.d(FriendsNotificationsRecylerViewAdapter.NotificationsViewHolder.class.getSimpleName(), response.toString());
                            }, error -> {
                        VolleyLog.d(acceptFriendRequestUrl, "Error: " + error.getMessage());
                    })
                    {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("Content-Type", "application/json");
                            headers.put("Authorization", getAccessToken());
                            return headers;
                        }};
                    queue.add(jsonObjectRequest);
                }

            });

            buttonDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RequestQueue queue = AppController.getInstance().getRequestQueue();
                    String declineFriendRequestUrl = "https://coms-309-026.class.las.iastate.edu/api/curatorRequests/declineCuratorRequest/" + friendRequestAccount.getText().toString();
                    JSONObject temp = new JSONObject();
                    try {
                        temp.put("username", friendRequestAccount.getText().toString());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, declineFriendRequestUrl, temp,
                            response -> {
                                Log.d(FriendsNotificationsRecylerViewAdapter.NotificationsViewHolder.class.getSimpleName(), response.toString());
                            }, error -> {
                        VolleyLog.d(declineFriendRequestUrl, "Error: " + error.getMessage());
                    })
                    {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("Content-Type", "application/json");
                            headers.put("Authorization", getAccessToken());
                            return headers;
                        }};
                    queue.add(jsonObjectRequest);
                }
            });
        }
    }
    public String getAccessToken() {
        SharedPreferences seePrefs = context.getSharedPreferences("ACCESS_TOKEN", Context.MODE_PRIVATE);
        return seePrefs.getString("accessToken", "");
    }
}
