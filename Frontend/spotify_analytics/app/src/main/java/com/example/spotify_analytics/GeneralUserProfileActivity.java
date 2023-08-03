package com.example.spotify_analytics;

import androidx.appcompat.app.AppCompatActivity;

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
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.spotify_analytics.app.AppController;
import com.example.spotify_analytics.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class GeneralUserProfileActivity extends AppCompatActivity {
    private TextView name, accountName, friendCount, curatorCount, Friends, Following;
    private Button buttonFriend;
    private String  query, userType;
    private ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_user_profile);

        Intent intent = getIntent();
        query = intent.getStringExtra("USER_NAME");
        userType = intent.getStringExtra("USER_TYPE");

        name = (TextView) findViewById(R.id.name);
        accountName = (TextView) findViewById(R.id.user_name);
        friendCount = (TextView) findViewById(R.id.friend_count);
        curatorCount = (TextView) findViewById(R.id.curator_count);
        buttonFriend = (Button) findViewById(R.id.button_make_friend);
        Friends = (TextView) findViewById(R.id.link_to_friends);
        Following = (TextView) findViewById(R.id.link_to_curators);
        profilePic = (ImageView) findViewById(R.id.profile_pic);

        userInfoJsonRequest();

        buttonFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    followRequestUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toFriendsList = new Intent(GeneralUserProfileActivity.this, FriendsListActivity.class);
                toFriendsList.putExtra("USER_NAME", query);
                startActivity(toFriendsList);
            }
        });

        Following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toFollowingList = new Intent(GeneralUserProfileActivity.this, FollowingListActivity.class);
                toFollowingList.putExtra("USER_NAME", query);
                toFollowingList.putExtra("USER_TYPE", userType);
                startActivity(toFollowingList);
            }
        });
    }

    private void userInfoJsonRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String generalUserInfoUrl = "http://coms-309-026.class.las.iastate.edu:8080/api/user/getUserInfo/" + query;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, generalUserInfoUrl, null,
                response -> {
                    Log.d(GeneralUserProfileActivity.class.getSimpleName(), response.toString());
                    try {
                        name.setText(response.getString("fullName"));
                        accountName.setText(response.getString("username"));
                        //friendCount.setText(response.getString("numberOfFriends"));
                        //curatorCount.setText(response.getString("numberFollowing"));
                        System.out.println(response.getString("profilePicture"));
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
                                        // Handle error
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
            VolleyLog.d(generalUserInfoUrl, "Error: " + error.getMessage());
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
        queue.add(jsonObjectRequest);// Adding request to request queue
    }

    private void followRequestUser() throws JSONException {
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

    public String getAccessToken() {
        SharedPreferences seePrefs = getSharedPreferences("ACCESS_TOKEN", Context.MODE_PRIVATE);
        return seePrefs.getString("accessToken", "");
    }
}