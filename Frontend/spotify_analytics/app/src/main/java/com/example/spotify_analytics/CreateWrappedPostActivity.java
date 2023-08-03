package com.example.spotify_analytics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateWrappedPostActivity extends AppCompatActivity {
    private Button artistsButton;
    private Button albumsButton;
    private Button songsButton;
    private EditText numItemsEditText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wrapped_post);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Create Wrapped Post");
        numItemsEditText = findViewById(R.id.wrapped_post_number_of_items_edit_text);
        artistsButton = findViewById(R.id.generate_wrapped_post_artists_button);
        albumsButton = findViewById(R.id.generate_wrapped_post_albums_button);
        songsButton = findViewById(R.id.generate_wrapped_post_songs_button);

        artistsButton.setOnClickListener(v -> {
            String numItems = numItemsEditText.getText().toString();
            if (numItems.equals("")) {
                numItems = "5";
            }
            createWrappedPost("Artist", Integer.parseInt(numItems));
            sendUserToMainActivity();
        });

        albumsButton.setOnClickListener(v -> {
            String numItems = numItemsEditText.getText().toString();
            if (numItems.equals("")) {
                numItems = "5";
            }
            Snackbar snackbar = Snackbar.make(albumsButton, "Not yet implemented", Snackbar.LENGTH_LONG);
            snackbar.setAction("Dismiss", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        });

        songsButton.setOnClickListener(v -> {
            String numItems = numItemsEditText.getText().toString();
            if (numItems.equals("")) {
                numItems = "5";
            }
            createWrappedPost("Track", Integer.parseInt(numItems));
        });
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    private void createWrappedPost(String type, int numItems) {
        String createWrappedPostUrl = "http://coms-309-026.class.las.iastate.edu:8080/api/post/wrappedPostTop" + type + "s/" + numItems;
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                createWrappedPostUrl, null,
                response -> {
                    Log.d(CreateWrappedPostActivity.class.getSimpleName(), response.toString());
                    sendUserToMainActivity();
                }, error -> {
            VolleyLog.d(createWrappedPostUrl, "Error: " + error.getMessage());
        })
        {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() {
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
}


