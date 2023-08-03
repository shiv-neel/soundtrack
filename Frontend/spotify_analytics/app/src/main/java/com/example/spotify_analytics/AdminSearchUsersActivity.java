package com.example.spotify_analytics;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminSearchUsersActivity extends AppCompatActivity {

    private ImageButton searchUserButton;
    private EditText searchInputText;

    private ImageView profileImage;

    private TextView usernameResponse, fullNameResponse;

    private Button deleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_search_users);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Find Friends");

        searchUserButton = (ImageButton) findViewById(R.id.admin_search_user_button);
        searchInputText = (EditText) findViewById(R.id.admin_search_user_edit_text);
        usernameResponse = (TextView) findViewById(R.id.admin_username_response);
        fullNameResponse = (TextView) findViewById(R.id.admin_full_name_response);
        profileImage = (ImageView) findViewById(R.id.admin_pfp_response);
        deleteAccount = (Button) findViewById(R.id.delete_account_button);
        deleteAccount.setVisibility(View.GONE);

        searchUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchBoxInput = searchInputText.getText().toString();
                searchForUser(searchBoxInput);
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchBoxInput = searchInputText.getText().toString();
                sendToDeleteAccount(searchBoxInput);
            }
        });
    }

    private void searchForUser(String searchBoxInput) {
        getQueryResponse(searchBoxInput);
    }

    private void getQueryResponse(String query) {
        String searchForUserUrl = "http://coms-309-026.class.las.iastate.edu:8080/api/findUser/searchUsername/" + query;
        RequestQueue queue = Volley.newRequestQueue(this);
        @SuppressLint("SetTextI18n") JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                searchForUserUrl, null,
                response -> {
                    Log.d(AdminSearchUsersActivity.class.getSimpleName(), response.toString());
                    try {
                        response.getString("username");
                        usernameResponse.setText(response.getString("username"));
                        fullNameResponse.setText(response.getString("name"));
                        profileImage.setVisibility(View.VISIBLE);
                        deleteAccount.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
            usernameResponse.setText("User " + query + " not found.");
            fullNameResponse.setText("");
            profileImage.setVisibility(View.INVISIBLE);
            VolleyLog.d(searchForUserUrl, "Error: " + error.getMessage());
        })
        {
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
        queue.add(jsonObjReq);
    }

    private void sendToDeleteAccount(String searchBoxInput) {deleteTheAccount(searchBoxInput);}
    
    private void deleteTheAccount(String query){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://coms-309-026.class.las.iastate.edu:8080/api/adminFunction/deleteUser/" + query;
        @SuppressLint("SetTextI18n") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    Log.d(AdminSearchUsersActivity.class.getSimpleName(), response.toString());
                }, error -> {
            VolleyLog.d(url, "Error: " + error.getMessage());
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
    public String getAccessToken() {
        SharedPreferences seePrefs = getSharedPreferences("ACCESS_TOKEN", Context.MODE_PRIVATE);
        return seePrefs.getString("accessToken", "");
    }




}