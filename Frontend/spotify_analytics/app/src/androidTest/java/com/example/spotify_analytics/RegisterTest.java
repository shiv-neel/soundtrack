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

public class RegisterTest {

    @Rule
    public ActivityScenarioRule<RegisterActivity> activityScenarioRule = new ActivityScenarioRule<>(RegisterActivity.class);

    @Test
    public void loginTest() {
        String username = "shiv1622s_evil_twin";
        String password = "pw123456";
        String fullname = "Shiv N (evil twin)";
        String email = "plsdontstealmydata@gmail.com";

        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.register_username)).perform(typeText(username));
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.register_password)).perform(typeText(password));
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.register_password_confirm)).perform(typeText(password));
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.register_fullname)).perform(typeText(fullname));
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.register_email)).perform(typeText(email));
        onView(withId(R.id.register_button)).perform(click());
    }
}
