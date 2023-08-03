package com.example.spotify_analytics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import com.example.spotify_analytics.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SearchUserActivity extends AppCompatActivity {
    private ImageButton searchUserButton;
    private EditText searchInputText;

    private ImageView profileImage;

    private TextView usernameResponse;

    private TextView fullNameResponse;

    private String searchBoxInput, userType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Find Friends");

        searchUserButton = (ImageButton) findViewById(R.id.search_user_button);
        searchInputText = (EditText) findViewById(R.id.search_user_edit_text);
        usernameResponse = (TextView) findViewById(R.id.username_response);
        fullNameResponse = (TextView) findViewById(R.id.full_name_response);
        profileImage = (ImageView) findViewById(R.id.pfp_response);



        searchUserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchBoxInput = searchInputText.getText().toString();
                    searchForUser(searchBoxInput);
                }
            }
        );

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userType.equals("CURATOR")){
                    sendToCuratorAccount();
                }else if (userType.equals("USER")){
                    sendToGeneralUserAccount();
                }
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
                    Log.d(SearchUserActivity.class.getSimpleName(), response.toString());
                    try {
                        response.getString("username");
                        usernameResponse.setText(response.getString("username"));
                        fullNameResponse.setText(response.getString("name"));
                        userType = response.getString("userType");
                        System.out.println(userType);
                        profileImage.setVisibility(View.VISIBLE);
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

    public String getAccessToken() {
        SharedPreferences seePrefs = getSharedPreferences("ACCESS_TOKEN", Context.MODE_PRIVATE);
        return seePrefs.getString("accessToken", "");
    }

    private void sendToGeneralUserAccount(){
        Intent sendToGeneralUserProfile = new Intent(SearchUserActivity.this, GeneralUserProfileActivity.class);
        sendToGeneralUserProfile.putExtra("USER_NAME", searchBoxInput);
        sendToGeneralUserProfile.putExtra("USER_TYPE", userType);
        startActivity(sendToGeneralUserProfile);
    }

    private void sendToCuratorAccount(){
        Intent sendToCuratorProfile = new Intent(SearchUserActivity.this, CuratorUserProfileActivity.class);
        sendToCuratorProfile.putExtra("USER_NAME", searchBoxInput);
        sendToCuratorProfile.putExtra("USER_TYPE", userType);
        startActivity(sendToCuratorProfile);
    }

}