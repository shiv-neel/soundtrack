package com.example.spotify_analytics;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateUploadPostActivity extends AppCompatActivity {

    private TextView primaryDataTextView;
    private TextView secondaryDataTextView;
    private ImageView itemImageView;

    private TextView descriptionTextView;

    private Button uploadButton;

    private String imageUri;

    private TextView usernameTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_upload_post);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Create Post -- Add Comment");
        primaryDataTextView = findViewById(R.id.create_post_primary_data);
        secondaryDataTextView = findViewById(R.id.create_post_secondary_data);
        itemImageView = findViewById(R.id.create_post_item_image);
        descriptionTextView = findViewById(R.id.create_post_description);
        usernameTextView = findViewById(R.id.create_post_item_header_text);
        uploadButton = findViewById(R.id.create_post_submit_button);

        primaryDataTextView.setText(getIntent().getStringExtra("primary_data"));
        secondaryDataTextView.setText(getIntent().getStringExtra("secondary_data"));
        imageUri = getIntent().getStringExtra("image_uri");
        Picasso.get().load(imageUri).into(itemImageView);
        try {
            setUsername();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        uploadButton.setOnClickListener(v -> {
            try {
                createPost();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void setUsername() throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);
        String getUserUrl = "http://coms-309-026.class.las.iastate.edu:8080/api/user/getCurrentUser";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                getUserUrl, null,
                response -> {
                    Log.d(CreateUploadPostActivity.class.getSimpleName(), response.toString());
                    try {
                        usernameTextView.setText(response.getString("username"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
            usernameTextView.setText("username");
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


    private void createPost() throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);
        String createPostUrl = "http://coms-309-026.class.las.iastate.edu:8080/api/post/create";
        JSONObject body = new JSONObject();
        body.put("primaryData", primaryDataTextView.getText().toString());
        body.put("secondaryData", secondaryDataTextView.getText().toString());
        body.put("imageUri", imageUri);
        body.put("description", descriptionTextView.getText().toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                createPostUrl, body,
                response -> {
                    Log.d(CreateUploadPostActivity.class.getSimpleName(), response.toString());
                    uploadButton.setText("Success");
                    sendUserToMainActivity();
                }, error -> {
            VolleyLog.d(createPostUrl, "Error: " + error.getMessage());
            if (error.getMessage() == null) {
                uploadButton.setText("Could not create post");
            }
            else {
                uploadButton.setText("Success");
                sendUserToMainActivity();
            }
        })
        {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                SharedPreferences preferences = getSharedPreferences("ACCESS_TOKEN", Context.MODE_PRIVATE);
                String accessToken = preferences.getString("accessToken", "");

                headers.put("Content-Type", "application/json");
                headers.put("Authorization", accessToken);
                return headers;
            }
        };
        queue.add(jsonObjReq);

        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }
}