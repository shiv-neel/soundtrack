package com.example.spotify_analytics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class NotificationsActivity extends AppCompatActivity {

    ImageButton friendRequestsButton;
    TextView requestText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Intent intent = getIntent();
        String userType = intent.getStringExtra("USER_TYPE");

        friendRequestsButton = findViewById(R.id.friend_requests_button);
        requestText = findViewById(R.id.request_text);

        if(userType.equals("ADMIN")){
            requestText.setText("Curator Requests");
        }

        friendRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userType.equals("ADMIN")){
                    Intent sendToCuratorRequests = new Intent(NotificationsActivity.this, AdminCuratorNotificationsActivity.class );
                    startActivity(sendToCuratorRequests);
                }else{
                    Intent sendToFriendRequests = new Intent(NotificationsActivity.this, FriendsNotificationsActivity.class );
                    startActivity(sendToFriendRequests);
                }
            }
        });


    }
}