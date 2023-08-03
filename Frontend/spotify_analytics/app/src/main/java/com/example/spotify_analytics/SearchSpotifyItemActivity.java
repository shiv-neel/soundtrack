package com.example.spotify_analytics;

import static com.example.spotify_analytics.SpotifyAuthActivity.CLIENT_ID;
import static com.example.spotify_analytics.SpotifyAuthActivity.CLIENT_SECRET;
import static com.example.spotify_analytics.SpotifyAuthActivity.clientCredentials_Async;
import static com.example.spotify_analytics.SpotifyAuthActivity.redirectUri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Album;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchAlbumsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchArtistsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

public class SearchSpotifyItemActivity extends AppCompatActivity {

    private RadioGroup itemTypeRadioGroup;
    private RadioButton artistRadioButton;
    private RadioButton albumRadioButton;
    private RadioButton songRadioButton;
    private ItemType selectedItemType = ItemType.ARTIST;

    private TextView searchSpotifyItemTextView;
    private EditText searchSpotifyItemInput;
    private ImageButton searchSpotifyItemButton;

    private TextView searchResponse;

    private TextView createWrappedPostTextView;
    private ArrayList<SpotifyItem> searchResults;

    private SpotifyApi spotifyApi;

    private RecyclerView searchResultsRecyclerView;

    private enum ItemType {
        ARTIST, ALBUM, SONG
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_spotify_item);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Create Post -- Spotify Search");

        /* SPOTIFY API CONNECTION */
        spotifyApi = new SpotifyApi.Builder().setClientId(CLIENT_ID).setClientSecret(CLIENT_SECRET).setRedirectUri(redirectUri).build();

        spotifyApi.setAccessToken(clientCredentials_Async());

        itemTypeRadioGroup = findViewById(R.id.search_item_type_radio_group);
        artistRadioButton = findViewById(R.id.radio_button_artist);
        albumRadioButton = findViewById(R.id.radio_button_album);
        songRadioButton = findViewById(R.id.radio_button_song);
        searchSpotifyItemTextView = findViewById(R.id.search_spotify_item_query_text);
        searchSpotifyItemInput = findViewById(R.id.search_spotify_item_query_edit_text);
        searchSpotifyItemButton = findViewById(R.id.search_spotify_item_button);
        searchResultsRecyclerView = findViewById(R.id.search_results_recycler_view);
        createWrappedPostTextView = findViewById(R.id.create_wrapped_post_text_view);
        searchResults = new ArrayList<>();

        createWrappedPostTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchSpotifyItemActivity.this, CreateWrappedPostActivity.class);
                startActivity(intent);
            }
        });

        itemTypeRadioGroup.check(R.id.radio_button_artist);

        itemTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_button_artist:
                        selectedItemType = ItemType.ARTIST;
                        searchSpotifyItemTextView.setText("Search for an artist:");
                        break;
                    case R.id.radio_button_album:
                        selectedItemType = ItemType.ALBUM;
                        searchSpotifyItemTextView.setText("Search for an album:");
                        break;
                    case R.id.radio_button_song:
                        selectedItemType = ItemType.SONG;
                        searchSpotifyItemTextView.setText("Search for a song:");
                        break;
                }
            }
        });
        searchSpotifyItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchResults.clear();
                String query = searchSpotifyItemInput.getText().toString();
                if (query.isEmpty()) {
                    return;
                }
                switch (selectedItemType) {
                    case ARTIST:
                        searchArtist_Async(query);
                        break;
                    case ALBUM:
                        searchAlbum_Async(query);
                        break;
                    case SONG:
                        searchTracks_Async(query);
                        break;
                }
            }
        });
    }

    private static String concatenateArtists(ArtistSimplified[] artists) {
        StringBuilder sb = new StringBuilder();
        for (ArtistSimplified artist : artists) {
            sb.append(artist.getName()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    private void searchArtist_Async(String query) {
        try {
            SearchArtistsRequest searchArtistsRequest = spotifyApi.searchArtists(query).limit(10).build();
            final CompletableFuture<Paging<Artist>> pagingFuture = searchArtistsRequest.executeAsync();
            final Paging<Artist> artistPaging = pagingFuture.join();

            for (Artist artist : artistPaging.getItems()) {
                String artistName = artist.getName();
                String artistImageUrl;
                String artistGenre;
                if (artist.getImages().length > 0) {
                    artistImageUrl = artist.getImages()[0].getUrl();
                } else {
                    artistImageUrl = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png";
                }
                if (artist.getGenres().length > 0) {
                    artistGenre = artist.getGenres()[0] + " artist";
                } else {
                    artistGenre = "";
                }
                SpotifyItem thisItem = new SpotifyItem(artistName, artistImageUrl, artistGenre);
                searchResults.add(thisItem);
            }
            // searchResponse.setText(testing.toString());
            SearchItemAdapter adapter = new SearchItemAdapter(this, searchResults);
            searchResultsRecyclerView.setAdapter(adapter);
            searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }

    private void searchAlbum_Async(String query) {
        try {
            SearchAlbumsRequest searchAlbumsRequest = spotifyApi.searchAlbums(query).limit(10).build();
            final CompletableFuture<Paging<AlbumSimplified>> pagingFuture = searchAlbumsRequest.executeAsync();
            final Paging<AlbumSimplified> albumPaging = pagingFuture.join();

            for (AlbumSimplified album : albumPaging.getItems()) {
                String albumName = album.getName();
                String albumImageUrl;
                if (album.getImages().length > 0) {
                    albumImageUrl = album.getImages()[0].getUrl();
                } else {
                    albumImageUrl = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png";
                }
                String albumArtists = concatenateArtists(album.getArtists());
                SpotifyItem thisItem = new SpotifyItem(albumName, albumImageUrl, albumArtists);
                searchResults.add(thisItem);
            }
            // searchResponse.setText(testing.toString());
            SearchItemAdapter adapter = new SearchItemAdapter(this, searchResults);
            searchResultsRecyclerView.setAdapter(adapter);
            searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }

    public void searchTracks_Async(String query) {
        try {
            SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(query).limit(10).build();
            final CompletableFuture<Paging<Track>> pagingFuture = searchTracksRequest.executeAsync();
            final Paging<Track> trackPaging = pagingFuture.join();

            for (Track track : trackPaging.getItems()) {
                String trackName = track.getName();
                String trackImageUrl;
                if (track.getAlbum().getImages().length > 0) {
                    trackImageUrl = track.getAlbum().getImages()[0].getUrl();
                } else {
                    trackImageUrl = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png";
                }
                String trackArtists = concatenateArtists(track.getArtists());
                SpotifyItem thisItem = new SpotifyItem(trackName, trackImageUrl, trackArtists);
                searchResults.add(thisItem);
            }
            // searchResponse.setText(testing.toString());
            SearchItemAdapter adapter = new SearchItemAdapter(this, searchResults);
            searchResultsRecyclerView.setAdapter(adapter);
            searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }
}