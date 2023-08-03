package com.example.spotify_analytics;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

public class SpotifyAuthActivity extends AppCompatActivity {
    private Button loginButton;

    private static TextView textView;


    public static final String CLIENT_ID = "48fb61c1ce134332b5a57e9d1ba81b12";
    public static final String CLIENT_SECRET = "a2a1e30ba2ce4f638dd93569fd81fcad";
    public static final URI redirectUri;

    static {
        try {
            redirectUri = new URI("http://10.0.2.2:45457");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(CLIENT_ID)
            .setClientSecret(CLIENT_SECRET)
            .setRedirectUri(redirectUri)
            .build();
    private static final ClientCredentialsRequest clientCredentialsRequest =
            spotifyApi.clientCredentials().build();


    public static String clientCredentials_Async() {
        try {
            final CompletableFuture<ClientCredentials> clientCredentialsFuture = clientCredentialsRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final ClientCredentials clientCredentials = clientCredentialsFuture.join();

            // Set access token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
            System.out.println("Expires in: " + clientCredentials.getExpiresIn());
            return clientCredentials.getAccessToken();
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
        return "";
    }



    public static void searchTracks_Async() {
        try {
            SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks("All of the Lights")
                    .limit(10)
                    .build();
            final CompletableFuture<Paging<Track>> pagingFuture = searchTracksRequest.executeAsync();
            final Paging<Track> trackPaging = pagingFuture.join();

            textView.setText("Song Response: " + trackPaging.getItems()[0].toString());
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_login);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Spotify Login");


        loginButton = findViewById(R.id.spotify_login_btn);
        textView = findViewById(R.id.spotify_access_token);
        loginButton.setOnClickListener(v -> {
            clientCredentials_Async();
            textView.setText("Access Token: " + spotifyApi.getAccessToken());
            searchTracks_Async();

    });
    }
}