package com.example.spotify_analytics;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class CreatePostTest {

    @Rule
    public ActivityScenarioRule<SearchSpotifyItemActivity> activityScenarioRule = new ActivityScenarioRule<>(SearchSpotifyItemActivity.class);

    @Test
    public void createArtistPost() {
        onView(withId(R.id.radio_button_artist)).perform(click());
        onView(withId(R.id.search_spotify_item_query_edit_text)).perform(typeText("The Weeknd"));
        onView(withId(R.id.search_spotify_item_button)).perform(click());
        onView(withId(R.id.search_results_recycler_view)).perform(click());
        onView(withId(R.id.create_post_description)).perform(typeText("This is a test post"));
    }

    @Test
    public void createAlbumPost() {
        onView(withId(R.id.radio_button_album)).perform(click());
        onView(withId(R.id.search_spotify_item_query_edit_text)).perform(typeText("Starboy"));
        onView(withId(R.id.search_spotify_item_button)).perform(click());
        onView(withId(R.id.search_results_recycler_view)).perform(click());
        onView(withId(R.id.create_post_description)).perform(typeText("This is a test post"));
    }

    @Test
    public void createSongPost() {
        onView(withId(R.id.radio_button_song)).perform(click());
        onView(withId(R.id.search_spotify_item_query_edit_text)).perform(typeText("Starboy"));
        onView(withId(R.id.search_spotify_item_button)).perform(click());
        onView(withId(R.id.search_results_recycler_view)).perform(click());
        onView(withId(R.id.create_post_description)).perform(typeText("This is a test post"));
    }
}
