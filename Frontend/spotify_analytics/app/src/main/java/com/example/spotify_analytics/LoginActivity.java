package com.example.spotify_analytics;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText usernameInput, passwordInput;
    private TextView dontHaveAccountLink;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");

        loginButton = (Button) findViewById(R.id.login_button);
        usernameInput = (EditText) findViewById(R.id.login_username);
        passwordInput = (EditText) findViewById(R.id.login_password);
        dontHaveAccountLink = (TextView) findViewById(R.id.dont_have_account);

        dontHaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToRegisterActivity();
            }

        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void sendUserToRegisterActivity() {
        Intent loginIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(loginIntent);
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

    private void loginUser() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (TextUtils.isEmpty(username)) {
            usernameInput.setError("username is required");
            usernameInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return;
        }
        try {
            attemptLogin(username, password);
        } catch (JSONException e) {
            // TODO add an error toast
            throw new RuntimeException(e);
        }
    }

    private void setAccessTokenCookie(String token) {
        SharedPreferences prefs = getSharedPreferences("ACCESS_TOKEN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("accessToken", token);
        editor.putLong("tokenTimestamp", System.currentTimeMillis());
        editor.apply();
    }

    private void attemptLogin(String username, String password) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);
        String loginUserUrl = "http://coms-309-026.class.las.iastate.edu:8080/api/auth/login";
        JSONObject body = new JSONObject();
        body.put("username", username);
        body.put("password", password);
        @SuppressLint("SetTextI18n") JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                loginUserUrl, body,
                response -> {
                    Log.d(LoginActivity.class.getSimpleName(), response.toString());
                    try {
                        String cookie = response.get("tokenType") + response.get("accessToken").toString();
                        loginButton.setText("Success");
                        setAccessTokenCookie(cookie);
                        sendUserToMainActivity();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
            VolleyLog.d(loginUserUrl, "Error: " + error.getMessage());
            if (error.getMessage() == null) {
                loginButton.setText("Username or password is incorrect.");
            }
            else {
                loginButton.setText(error.getMessage());
            }
        })
        {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        queue.add(jsonObjReq);

        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }
}