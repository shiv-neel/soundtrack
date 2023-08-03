package com.example.spotify_analytics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.spotify_analytics.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {


    private TextView usernameTextView;

    private TextView fullNameTextView;

    private TextView emailTextView;
    private Button logoutButton;

    private void sendUserToRegisterActivity() {
        Intent registerIntent = new Intent(SettingsActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        logoutButton = findViewById(R.id.logout_button);
        usernameTextView = findViewById(R.id.settings_username);
        fullNameTextView = findViewById(R.id.settings_full_name);
        emailTextView = findViewById(R.id.settings_email);

        try {
            setUser();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    private void logout() {
        SharedPreferences seePrefs = getSharedPreferences("ACCESS_TOKEN", Context.MODE_PRIVATE);
        String accessToken = seePrefs.getString("accessToken", "");
        if (accessToken.equals("")) {
            sendUserToLoginActivity();
            return;
        }
        SharedPreferences.Editor editor = seePrefs.edit();
        editor.putString("accessToken", "");
        editor.apply();
        sendUserToRegisterActivity();
    }


    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(loginIntent);
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
                        emailTextView.setText(response.getString("email"));
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

    private String getAccessToken() {
        SharedPreferences preferences = getSharedPreferences("ACCESS_TOKEN", Context.MODE_PRIVATE);
        return preferences.getString("accessToken", "");
    }
}