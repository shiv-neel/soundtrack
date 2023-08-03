package com.example.spotify_analytics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonArray;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postsListRecyclerView;
    private Toolbar mToolbar;
    protected static TextView usernameTextView;
    private TextView fullNameTextView;
    private ArrayList<UserPost> posts = new ArrayList<>();
    private PostAdapter adapter = new PostAdapter(this, posts);

    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Home");

        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.nav_view);
        usernameTextView = navigationView.getHeaderView(0).findViewById(R.id.nav_header_username);
        fullNameTextView = navigationView.getHeaderView(0).findViewById(R.id.nav_header_fullname);
        postsListRecyclerView = findViewById(R.id.posts_list_recycler_view);

        SharedPreferences seePrefs = getSharedPreferences("ACCESS_TOKEN", Context.MODE_PRIVATE);
        String accessToken = seePrefs.getString("accessToken", "");
        navigationView.setNavigationItemSelectedListener(item -> {
            UserMenuSelector(item);
            return false;
        });

        if (accessToken.equals("")) {
            sendUserToLoginActivity();
        } else {
            try {
                setUser();
                populateFeed();
            } catch (JSONException e) {
                sendUserToLoginActivity();
                throw new RuntimeException(e);
            }
        }
    }

    private void sendUserToViewPostActivity(){
        Intent viewPostIntent = new Intent(MainActivity.this, ViewPostActivity.class);
        viewPostIntent.putExtra("post_id", 1L);
        startActivity(viewPostIntent);
    }

    private void populateFeed() throws JSONException {
        getFeed();
        postsListRecyclerView.setAdapter(adapter);
        postsListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getFeed() throws JSONException {
        final int LIMIT = 20;
        RequestQueue queue = Volley.newRequestQueue(this);
        String getFeedUrl = "http://coms-309-026.class.las.iastate.edu:8080/api/user/getFeed/" + LIMIT;
        @SuppressLint("NotifyDataSetChanged") JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET,
                getFeedUrl, null,
                response -> {
            Log.d(MainActivity.class.getSimpleName(), response.toString());
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject post = response.getJSONObject(i);
                            Long _postId = post.getLong("postId");
                            String username = post.getString("originalPosterUsername");
                            Long posterId = post.getLong("originalPosterId");
                            String primaryData = post.getString("primaryData");
                            String secondaryData = post.getString("secondaryData");
                            String imageUri = post.getString("imageUri");
                            String description = post.getString("description");
                            int numLikes = post.getInt("numLikes");
                            int numComments = post.getInt("numComments");
                            UserPost _post = new UserPost(_postId, posterId, username, primaryData, secondaryData, imageUri, description, numLikes, numComments);
                            posts.add(_post);
                            adapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
            usernameTextView.setText("Error: " + error.toString());
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

        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }


    private String getAccessToken() {
        SharedPreferences preferences = getSharedPreferences("ACCESS_TOKEN", Context.MODE_PRIVATE);
        return preferences.getString("accessToken", "");
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getSharedPreferences("ACCESS_TOKEN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        long expirationTime = 3600 * 1000; // 1 hour in milliseconds
        long currentTime = System.currentTimeMillis();
        long tokenTimestamp = preferences.getLong("tokenTimestamp", 0);
        boolean isExpired = (tokenTimestamp + expirationTime) < currentTime;
        if (isExpired || preferences.getString("accessToken", "").equals("")) {
            editor.putString("accessToken", "");
            editor.apply();
            sendUserToLoginActivity();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    public void sendUserToWrappedPostActivity(){
        Intent wrappedPostIntent = new Intent(MainActivity.this, CreateWrappedPostActivity.class);
        startActivity(wrappedPostIntent);
    }

    private void setUser() throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);
        String getUserUrl = "http://coms-309-026.class.las.iastate.edu:8080/api/user/getCurrentUser";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                getUserUrl, null,
                response -> {
                    Log.d(CreateUploadPostActivity.class.getSimpleName(), response.toString());
                    try {
                        usernameTextView.setText(response.getString("username"));
                        fullNameTextView.setText(response.getString("fullName"));
                        JSONArray jsonArray = response.getJSONArray("roles");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        userType = jsonObject.getString("name");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
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

        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }


    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                // navigate to nav_home intent
                Intent homeIntent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(homeIntent);
                break;
            case R.id.nav_spotify_login:
                // navigate to nav_spotify_login intent
                Intent spotifyLoginIntent = new Intent(MainActivity.this, SpotifyAuthActivity.class);
                startActivity(spotifyLoginIntent);
                break;
            case R.id.nav_user_search:
                if(userType.equals("ADMIN")){
                    Intent adminSearchUser = new Intent(MainActivity.this, AdminSearchUsersActivity.class);
                    startActivity(adminSearchUser);
                }else {
                    Intent searchUserIntent = new Intent(MainActivity.this, SearchUserActivity.class);
                    startActivity(searchUserIntent);
                }
                break;
            case R.id.nav_notifications:
                Intent notifUserIntent = new Intent(MainActivity.this, NotificationsActivity.class);
                notifUserIntent.putExtra("USER_TYPE", userType);
                startActivity(notifUserIntent);
                break;
            case R.id.nav_user_login:
                Intent registerUserIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerUserIntent);
                break;
            case R.id.nav_create_post:
                Intent createPostIntent = new Intent(MainActivity.this, SearchSpotifyItemActivity.class);
                startActivity(createPostIntent);
                break;
            case R.id.nav_settings:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.nav_user_profile:
                if(userType.equals("USER")){
                    Intent userIntent = new Intent (MainActivity.this, GeneralUserProfileEditableActivity.class);
                    startActivity(userIntent);
                } else if(userType.equals("CURATOR")){
                    Intent curatorIntent = new Intent (MainActivity.this, GeneralUserProfileEditableActivity.class);
                    startActivity(curatorIntent);
                }
                break;
        }
    }

}