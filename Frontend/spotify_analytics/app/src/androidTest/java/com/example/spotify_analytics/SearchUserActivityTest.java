package com.example.spotify_analytics;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class SearchUserActivityTest {

    @Rule
    public ActivityScenarioRule<SearchUserActivity> activityScenarioRule = new ActivityScenarioRule<>(SearchUserActivity.class);

    @Test
    public void searchUser() {
        String username = "shiv1622";
        String fullname = "Shiv N";

        onView(withId(R.id.search_user_edit_text)).perform(typeText(username));
        onView(withId(R.id.search_user_button)).perform(click());

        onView(withId(R.id.username_response)).check(matches(withText(username)));
        onView(withId(R.id.full_name_response)).check(matches(withText(fullname)));
    }
}
