package com.example.spotify_analytics;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class GeneralUserProfileActivityTest{

        @Rule
        public ActivityScenarioRule<GeneralUserProfileActivity> activityScenarioRule = new ActivityScenarioRule<>(GeneralUserProfileActivity.class);

        @Test
        public void addFriend(){
                onView(withId(R.id.button_make_friend)).perform(click());
        }
}
