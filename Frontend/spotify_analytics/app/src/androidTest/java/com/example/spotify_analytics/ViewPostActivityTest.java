package com.example.spotify_analytics;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class ViewPostActivityTest {
    @Rule
    public ActivityScenarioRule<ViewPostActivity> activityScenarioRule = new ActivityScenarioRule<>(ViewPostActivity.class);


    @Test
    public void addComment() {
        // onView(withId(R.id.view_post_comment_edit_text)).perform(typeText("This is a comment"));
        // onView(withId(R.id.view_post_comment_button)).perform(click());
    }
}
