package com.example.spotify_analytics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminCuratorNotificationsActivity extends AppCompatActivity {

    ArrayList<UserLists> arrayList;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    AdminCuratorNotificationsRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_curator_notifications);

        recyclerView = findViewById(R.id.recycler_view);

        arrayList = new ArrayList<>();
        getData();

        buildRecyclerView();
    }
    private void getData(){
        RequestQueue queue = Volley.newRequestQueue(AdminCuratorNotificationsActivity.this);
        String getPendingFriendsUrl = "https://coms-309-026.class.las.iastate.edu/api/curatorRequests/getPendingCuratorRequests" + MainActivity.usernameTextView.getText().toString();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getPendingFriendsUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String name = jsonObject.getString("fullName");
                        String username = jsonObject.getString("username");
                        arrayList.add(new UserLists(name, username));
                        buildRecyclerView();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AdminCuratorNotificationsActivity.this, "Fail to get the data..", Toast.LENGTH_SHORT).show();
            }
        }){
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", getAccessToken());
                return headers;
            }
        };
        queue.add(jsonArrayRequest);
    }

    public String getAccessToken() {
        SharedPreferences seePrefs = getSharedPreferences("ACCESS_TOKEN", Context.MODE_PRIVATE);
        return seePrefs.getString("accessToken", "");
    }

    private void buildRecyclerView(){
        adapter = new AdminCuratorNotificationsRecyclerViewAdapter(arrayList, AdminCuratorNotificationsActivity.this);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }
}