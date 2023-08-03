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

public class AdminSearchUserTest {

    @Rule
    public ActivityScenarioRule<AdminSearchUsersActivity> activityScenarioRule = new ActivityScenarioRule<>(AdminSearchUsersActivity.class);

    @Test
    public void searchUser() {
        String username = "shiv1622";
        String fullname = "Shiv N";

        onView(withId(R.id.admin_search_user_edit_text)).perform(typeText(username));
        onView(withId(R.id.admin_search_user_button)).perform(click());

        onView(withId(R.id.admin_username_response)).check(matches(withText(username)));
        onView(withId(R.id.admin_full_name_response)).check(matches(withText(fullname)));
    }

    @Test
    public void deleteUser() {
        String username = "shiv1622";
        onView(withId(R.id.admin_search_user_edit_text)).perform(typeText(username));
        onView(withId(R.id.admin_search_user_button)).perform(click());

        // onView(withId(R.id.delete_account_button)).perform(click());
    }

}
