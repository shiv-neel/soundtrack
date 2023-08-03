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

public class AdminProfileActivity extends AppCompatActivity {

    ArrayList<listAllUsers> arrayList;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    AdminRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        recyclerView = findViewById(R.id.admin_recycler_view);

        arrayList = new ArrayList<>();
        getData();
    }

    private void getData(){
        RequestQueue queue = Volley.newRequestQueue(AdminProfileActivity.this);
        String url = "https://2368634b-ec4b-4b7b-9e38-f774344ed1bb.mock.pstmn.io/set2";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String name = jsonObject.getString("fullName");
                        String username = jsonObject.getString("username");
                        arrayList.add(new listAllUsers(name, username));
                        buildRecyclerView();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AdminProfileActivity.this, "Fail to get the data..", Toast.LENGTH_SHORT).show();
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
        adapter = new AdminRecyclerViewAdapter(arrayList, AdminProfileActivity.this);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }
}