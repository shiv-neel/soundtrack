package com.SoundTrack.Backend.controller;

import com.SoundTrack.Backend.security.SecurityConstants;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.hc.core5.http.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/spotifyAuth")
public class SpotifyAuthController {

    private static final String CLIENT_ID = "48fb61c1ce134332b5a57e9d1ba81b12";
    private static final String CLIENT_SECRET = "a2a1e30ba2ce4f638dd93569fd81fcad";
    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://" + SecurityConstants.URL_PROD + ":8080/api/spotifyAuth/getUserCode/");
    private String code = ""; //“code” for the user access code which will eventually ask Spotify for a user access token

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(CLIENT_ID)
            .setClientSecret(CLIENT_SECRET)
            .setRedirectUri(redirectUri)
            .build();

    @Operation(summary = "Method to authenticate the user's spotify account with SoundTrack",
            description = "When the user calls this method, returns a URI that takes the user to spotify's third party authentication. This will need to be opened in some web browser. (by Ian)")
    @GetMapping("login")
    public ResponseEntity<String> spotifyLogin(){
        try {
            AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                    .scope("user-read-private, user-read-email, user-top-read") //This is the level of access the user will need to authorize for us for certain requests.
                    //See the spotify API library to read what methods require which permissions
                    .show_dialog(true)
                    .build();
            final URI uri = authorizationCodeUriRequest.execute();
            return new ResponseEntity<>(uri.toString(), HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>("Error: caught some exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Method to get the user's authentication token",
            description = "When called, this method returns the user's spotify authentication token as well as a redirect from the web browser back to the app. " +
                    "This user code will need to be used when creating wrapped-style posts. (by Ian)")
    @GetMapping("getUserCode/")
    public ResponseEntity<String> getSpotifyUserCode(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException {
        code = userCode;
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
                .build();
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
            String accessToken = authorizationCodeCredentials.getAccessToken();
            spotifyApi.setAccessToken(accessToken);

        } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {
            return new ResponseEntity<>(e.getMessage() , HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String returnToken = spotifyApi.getAccessToken();
        if (returnToken != null){
            return new ResponseEntity<>(returnToken, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Could not generate access token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
