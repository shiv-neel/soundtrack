package com.example.spotify_analytics;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ViewImageActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button backButton;
    private String imageUri;

    private Long postId;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        imageView = findViewById(R.id.view_image_full);
        backButton = findViewById(R.id.view_image_button_back);
        Intent intent = getIntent();
        imageUri = intent.getStringExtra("image_uri");
        postId = intent.getLongExtra("post_id", 0);
        Picasso.get().load(imageUri).into(imageView);

        backButton.setOnClickListener(v -> {
            Intent viewPostIntent = new Intent(ViewImageActivity.this, ViewPostActivity.class);
            viewPostIntent.putExtra("post_id", postId);
            startActivity(viewPostIntent);
        });
    }
}
