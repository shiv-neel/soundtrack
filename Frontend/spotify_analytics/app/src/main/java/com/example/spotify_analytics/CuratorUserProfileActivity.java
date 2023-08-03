package com.example.spotify_analytics;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CuratorUserProfileActivity extends AppCompatActivity {

    private TextView name, accountName, followerCount, followingCount, friendCount,  linkToFollowers, linkToFollowing, linkToFriends;
    private Button buttonFollow, buttonFriend;

    private ImageView profilePic;

    private String  query, userType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curator_user_profile);

        Intent intent = getIntent();
        query = intent.getStringExtra("USER_NAME");
        userType = intent.getStringExtra("USER_TYPE");

        name = findViewById(R.id.name);
        accountName = findViewById(R.id.user_name);
        followerCount = findViewById(R.id.follower_count);
        followingCount = findViewById(R.id.following_count);
        buttonFollow = findViewById(R.id.button_follow);
        buttonFriend = findViewById(R.id.button_friend);
        linkToFollowers = findViewById(R.id.link_to_followers);
        linkToFollowing = findViewById(R.id.link_to_following);
        linkToFriends = findViewById(R.id.link_to_friends);
        profilePic = findViewById(R.id.profile_pic);

        userInfoJsonRequest();

        buttonFollow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                try {
                    followTheUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        buttonFriend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                try {
                    friendTheUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        linkToFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toFriendsList = new Intent(CuratorUserProfileActivity.this, FriendsListActivity.class);
                toFriendsList.putExtra("USER_NAME", query);
                startActivity(toFriendsList);
            }
        });

        linkToFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent followersList = new Intent(CuratorUserProfileActivity.this, FollowersListActivity.class);
                followersList.putExtra("USER_NAME", query);
                startActivity(followersList);
            }
        });

        linkToFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toFollowingList = new Intent(CuratorUserProfileActivity.this, FollowingListActivity.class);
                toFollowingList.putExtra("USER_NAME", query);
                toFollowingList.putExtra("USER_TYPE", userType);
                startActivity(toFollowingList);
            }
        });
    }

    private void userInfoJsonRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://coms-309-026.class.las.iastate.edu:8080/api/user/getUserInfo/" + query;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(CuratorUserProfileActivity.class.getSimpleName(), response.toString());
                    try {
                        name.setText(response.getString("name"));
                        accountName.setText(response.getString("username"));
                        followerCount.setText(response.getString("numberOfFollowers"));
                        String profileImageUrl = (String) response.getString("profilePicUrl");

                        ImageRequest imageRequest = new ImageRequest(profileImageUrl, new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                profilePic.setImageBitmap(response);
                            }
                        },
                                0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.d(url, "Error: " + error.getMessage());
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
                        queue.add(imageRequest);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
            VolleyLog.d(url, "Error: " + error.getMessage());
        });
        queue.add(jsonObjectRequest);
    }

    private void followTheUser() throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://coms-309-026.class.las.iastate.edu:8080/api/userRelationships/followCurator/" + query;
        JSONObject temp = new JSONObject();
        temp.put("username", accountName.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, temp,
                response -> {
                    Log.d(CuratorUserProfileActivity.class.getSimpleName(), response.toString());
                    buttonFollow.setText("Following");
                }, error -> {
            VolleyLog.d(url, "Error: " + error.getMessage());
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
        queue.add(jsonObjectRequest);
    }

    private void friendTheUser() throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);
        String followRequestUrl = "http://coms-309-026.class.las.iastate.edu:8080/api/api/userRelationships/sendFriendRequest/" + query;
        JSONObject temp = new JSONObject();
        temp.put("username", accountName.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, followRequestUrl, temp,
                response -> {
                    Log.d(GeneralUserProfileActivity.class.getSimpleName(), response.toString());
                    buttonFriend.setText("Following");
                }, error -> {
            VolleyLog.d(followRequestUrl, "Error: " + error.getMessage());
        }) {
            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", getAccessToken());
                return headers;
            }

        };
        queue.add(jsonObjectRequest);
    }

    public String getAccessToken() {
        SharedPreferences seePrefs = getSharedPreferences("ACCESS_TOKEN", Context.MODE_PRIVATE);
        return seePrefs.getString("accessToken", "");
    }
}