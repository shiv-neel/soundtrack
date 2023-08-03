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

public class AdminRecyclerViewAdapter extends RecyclerView.Adapter<AdminRecyclerViewAdapter.AdminViewHolder>{
    private ArrayList<listAllUsers> values;
    private Context context;

    public AdminRecyclerViewAdapter(ArrayList<listAllUsers> values, Context context){
        this.values = values;
        this.context = context;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_recycler_view, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminRecyclerViewAdapter.AdminViewHolder holder, int position) {
        listAllUsers list = values.get(position);
        holder.textViewName.setText(list.getName());
        holder.textViewUsername.setText(list.getUsername());
    }

    @Override
    public int getItemCount() {
        return this.values.size();
    }

    public class AdminViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName, textViewUsername;
        private Button buttonDelete;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.admin_list_name);
            textViewUsername = itemView.findViewById(R.id.admin_list_account);
            buttonDelete = itemView.findViewById(R.id.button_delete);

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RequestQueue queue = AppController.getInstance().getRequestQueue();
                    String adminDeleteButtonURL = "http://coms-309-026.class.las.iastate.edu:8080/api/userRelationships/declineFriendRequest/" + textViewUsername.getText().toString();
                    JSONObject temp = new JSONObject();
                    try {
                        temp.put("username", textViewUsername.getText().toString());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, adminDeleteButtonURL, temp,
                            response -> {
                                Log.d(AdminViewHolder.class.getSimpleName(), response.toString());
                            }, error -> {
                        VolleyLog.d(adminDeleteButtonURL, "Error: " + error.getMessage());
                    }){
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
