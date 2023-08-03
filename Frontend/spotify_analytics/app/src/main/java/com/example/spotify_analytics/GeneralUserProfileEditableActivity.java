package com.example.spotify_analytics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GeneralUserProfileEditableActivity extends AppCompatActivity {
    private TextView name, accountName, friendCount, curatorCount, Friends, Following;

    private ImageView profilePic, settingsView;

    private Button buttonApplyCurator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_user_profile_editable);

        name = (TextView) findViewById(R.id.name);
        accountName = (TextView) findViewById(R.id.user_name);
        friendCount = (TextView) findViewById(R.id.friend_count);
        curatorCount = (TextView) findViewById(R.id.curator_count);
        Friends = (TextView) findViewById(R.id.link_to_friends);
        Following = (TextView) findViewById(R.id.link_to_curators);
        profilePic = (ImageView) findViewById(R.id.profile_pic);
        buttonApplyCurator = (Button) findViewById(R.id.button_apply_curator);

        userInfoJsonRequest();

        buttonApplyCurator.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    sendRequestToApplyForCurator();
                    buttonApplyCurator.setText("Application Pending");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }

    private void userInfoJsonRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String GetCurrentUserURL = "http://coms-309-026.class.las.iastate.edu:8080/api/user/getCurrentUser";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GetCurrentUserURL, null,
                response -> {
                    Log.d(GeneralUserProfileEditableActivity.class.getSimpleName(), response.toString());
                    try {
                        name.setText(response.getString("fullName"));
                        accountName.setText(response.getString("username"));
                        //friendCount.setText(response.getString("numberOfFriends"));
                        //curatorCount.setText(response.getString("numberFollowing"));
                        String profileImageUrl = (String) response.getString("profilePicture");

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
            VolleyLog.d(GetCurrentUserURL, "Error: " + error.getMessage());
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


    private void sendRequestToApplyForCurator()throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);
        String applyForCuratorURL = "http://coms-309-026.class.las.iastate.edu:8080/api/curatorRequests/requestCurator/" + accountName.getText().toString();
        JSONObject temp = new JSONObject();
        temp.put("username", accountName.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, applyForCuratorURL, temp,
                response -> {
                    Log.d(GeneralUserProfileEditableActivity.class.getSimpleName(), response.toString());
                    buttonApplyCurator.setText("Applied");
                }, error -> {
            VolleyLog.d(applyForCuratorURL, "Error: " + error.getMessage());
        }) {
            /**
             * Passing some request headers
             */
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


