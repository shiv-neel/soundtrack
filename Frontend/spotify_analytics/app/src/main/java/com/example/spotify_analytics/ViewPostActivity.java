package com.example.spotify_analytics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewPostActivity extends AppCompatActivity {

    private TextView primaryDataTextView;
    private TextView secondaryDataTextView;

    private String imageUri;

    private ImageView itemImageView;

    private TextView descriptionTextView;

    private TextView usernameTextView;

    private TextView likeCountTextView;
    private TextView commentCountTextView;

    private RecyclerView commentsRecyclerView;

    private ArrayList<Comment> comments = new ArrayList<>();

    private CommentAdapter adapter = new CommentAdapter(this, comments);

    private EditText commentEditText;
    private Button addCommentButton;

    private ImageView likeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        Objects.requireNonNull(getSupportActionBar()).setTitle("View Post");

        primaryDataTextView = findViewById(R.id.view_post_primary_data);
        secondaryDataTextView = findViewById(R.id.view_post_secondary_data);
        descriptionTextView = findViewById(R.id.view_post_description);
        itemImageView = findViewById(R.id.view_post_item_image);
        usernameTextView = findViewById(R.id.view_post_item_header_text);
        commentsRecyclerView = findViewById(R.id.view_post_comments_recycler_view);
        likeCountTextView = findViewById(R.id.view_post_like_count);
        commentCountTextView = findViewById(R.id.view_post_comment_count);
        commentEditText = findViewById(R.id.view_post_comment_edit_text);
        addCommentButton = findViewById(R.id.view_post_comment_button);
        likeButton = findViewById(R.id.view_post_like_icon);
        Long postId = getIntent().getLongExtra("post_id", 18);

        itemImageView.setOnClickListener(v -> {
            Intent viewImageIntent = new Intent(ViewPostActivity.this, ViewImageActivity.class);
            viewImageIntent.putExtra("image_uri", imageUri);
            viewImageIntent.putExtra("post_id", postId);
            startActivity(viewImageIntent);
        });


        likeButton.setOnClickListener(v -> {
            try {
                likePost(postId);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            sendUserToViewPostActivity(postId);
        });

        try {
            getPostByPostId(postId);
            getCommentsForPost(postId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        commentsRecyclerView.setAdapter(adapter);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentEditText.setText("");
        addCommentButton.setOnClickListener(v -> {
            try {
                addCommentToPost(postId);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            sendUserToViewPostActivity(postId);
        });

        likeButton.setOnClickListener(v -> {
            try {
                likePost(postId);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            sendUserToViewPostActivity(postId);
        });
    }

    private void sendUserToViewPostActivity(Long postId){
        Intent viewPostIntent = new Intent(ViewPostActivity.this, ViewPostActivity.class);
        viewPostIntent.putExtra("post_id", postId);
        startActivity(viewPostIntent);
    }

    private String getAccessToken() {
        SharedPreferences preferences = getSharedPreferences("ACCESS_TOKEN", Context.MODE_PRIVATE);
        return preferences.getString("accessToken", "");
    }

    private void addCommentToPost(Long postId) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);
        String addCommentUrl = "http://coms-309-026.class.las.iastate.edu:8080/api/post/comment/add";

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("postId", postId);
        jsonBody.put("comment", commentEditText.getText().toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                addCommentUrl, jsonBody,
                response -> {
                    Log.d(ViewPostActivity.class.getSimpleName(), response.toString());
                    Comment comment = new Comment(
                            -1L,
                            -1L,
                            postId,
                            commentEditText.getText().toString(),
                            new Date()
                    );
                    comments.add(comment);
                    adapter.notifyDataSetChanged();
                    try {
                        getCommentsForPost(postId);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    commentEditText.setText("");
                }, error -> {
            VolleyLog.d(addCommentUrl, "Error: " + error.getMessage());
            commentEditText.setText("");
        }) {
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

    private void getPostByPostId(Long postId) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);
        String getPostUrl = "http://coms-309-026.class.las.iastate.edu:8080/api/post/view/getPostById/" + postId;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                getPostUrl, null,
                response -> {
                    Log.d(ViewPostActivity.class.getSimpleName(), response.toString());
                    try {
                        usernameTextView.setText(response.getString("originalPosterUsername"));
                        primaryDataTextView.setText(response.getString("primaryData"));
                        secondaryDataTextView.setText(response.getString("secondaryData"));
                        imageUri = response.getString("imageUri");
                        descriptionTextView.setText(response.getString("description"));
                        likeCountTextView.setText(String.valueOf(response.getInt("numLikes")));
                        commentCountTextView.setText(String.valueOf(response.getInt("numComments")));

                        Picasso.get().load(imageUri).into(itemImageView);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
            VolleyLog.d(getPostUrl, "Error: " + error.getMessage());
        }) {
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

    private void getCommentsForPost(Long postId) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);
        String geCommentsUrl = "http://coms-309-026.class.las.iastate.edu:8080/api/post/view/getCommentsForPostById/" + postId;

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET,
                geCommentsUrl, null,
                response -> {
                    Log.d(ViewPostActivity.class.getSimpleName(), response.toString());
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject comment = response.getJSONObject(i);
                            // Extract the data from the JSON object and do something with it
                            Long commentId = comment.getLong("id");
                            Long userId = comment.getLong("commenterUserID");
                            String commentText = comment.getString("commentText");
                            JSONArray dateArray = comment.getJSONArray("createdAt");
                            Date createdAt = new Date((int) dateArray.get(0), (int) dateArray.get(1),
                                    (int) dateArray.get(2), (int) dateArray.get(3), (int) dateArray.get(4));
                            comments.add(new Comment(commentId, userId, postId, commentText, createdAt));
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
            VolleyLog.d(geCommentsUrl, "Error: " + error.getMessage());
        }) {
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
    }

    private void likePost(Long postId) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);
        String likePostUrl = "http://coms-309-026.class.las.iastate.edu:8080/api/post/like/add/" + postId;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                likePostUrl, null,
                response -> {
                    Log.d(ViewPostActivity.class.getSimpleName(), response.toString());
                },
                error -> {
            VolleyLog.d(likePostUrl, "Error: " + error.getMessage());
        }) {
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
    }


}