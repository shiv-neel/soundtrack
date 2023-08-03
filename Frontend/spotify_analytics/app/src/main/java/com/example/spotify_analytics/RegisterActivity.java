package com.example.spotify_analytics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText usernameInput, fullNameInput, emailInput, passwordInput, confirmPasswordInput;
    private TextView signInLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Register");

        signInLink = (TextView) findViewById(R.id.sign_in_link);
        registerButton = (Button) findViewById(R.id.register_button);
        usernameInput = (EditText) findViewById(R.id.register_username);
        fullNameInput = (EditText) findViewById(R.id.register_fullname);
        emailInput = (EditText) findViewById(R.id.register_email);
        passwordInput = (EditText) findViewById(R.id.register_password);
        confirmPasswordInput = (EditText) findViewById(R.id.register_password_confirm);
        // msgResponse = (TextView) findViewById(R.id.msg_response);

        signInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToLoginActivity();
            }

    });

            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    registerUser();
                }
            });
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void registerUser() {
        String username = usernameInput.getText().toString();
        String fullName = fullNameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        if (TextUtils.isEmpty(username)) {
            usernameInput.setError("username is required");
            usernameInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(fullName)) {
            fullNameInput.setError("Name is required");
            fullNameInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 8) {
            if (TextUtils.isEmpty(password)) {
                passwordInput.setError("Password is required");
            }
            else if (password.length() < 8) {
                passwordInput.setError("Password must be at least 8 characters");
            }
            passwordInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInput.setError("Confirm password is required");
            confirmPasswordInput.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            confirmPasswordInput.requestFocus();
            return;
        }
        try {
            attemptRegister(username, fullName, email, password);
        } catch (JSONException e) {
            // TODO add an error toast
            throw new RuntimeException(e);
        }
    }


    private void attemptRegister(String username, String fullName, String email, String password) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);
        String registerUserUrl = "http://coms-309-026.class.las.iastate.edu:8080/api/auth/register";
        JSONObject body = new JSONObject();
        body.put("username", username);
        body.put("name", fullName);
        body.put("email", email);
        body.put("password", password);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                registerUserUrl, body,
                response -> {
                    Log.d(RegisterActivity.class.getSimpleName(), response.toString());
                    registerButton.setText("Success");
                    sendUserToLoginActivity();
                }, error -> {
            VolleyLog.d(registerUserUrl, "Error: " + error.getMessage());
            if (error.getMessage() == null) {
                registerButton.setText("User already exists");
            }
            else {
                registerButton.setText("Success");
                sendUserToLoginActivity();
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