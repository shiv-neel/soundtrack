package com.example.spotify_analytics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
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

public class FriendsListActivity extends AppCompatActivity {
    ArrayList<listOfFriends> arrayList;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    private String  query;

    FriendsListRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        Intent intent = getIntent();
        query = intent.getStringExtra("USER_NAME");

        recyclerView = findViewById(R.id.recycler_view);

        arrayList = new ArrayList<>();
        getData();
    }

    private void getData(){
        RequestQueue queue = Volley.newRequestQueue(FriendsListActivity.this);
        String getFriendListUrl = "http://coms-309-026.class.las.iastate.edu:8080/api/user/getUserFriends/" + query;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getFriendListUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String name = jsonObject.getString("name");
                        String username = jsonObject.getString("username");
                        arrayList.add(new listOfFriends(name, username));
                        buildRecyclerView();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FriendsListActivity.this, "Fail to get the data..", Toast.LENGTH_SHORT).show();
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
        adapter = new FriendsListRecyclerViewAdapter(arrayList, FriendsListActivity.this);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }


}