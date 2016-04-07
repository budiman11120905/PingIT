package edu.gcc.whiletrue.pingit;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by stuart on 2/20/16.
 */

@RunWith(AndroidJUnit4.class)
public class FAQUITest {

    String email = "unittest@gmail.com";
    String pass = "justinrocks";

    @Rule//startup activity to test
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<StartupActivity>(
            StartupActivity.class){
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent result = new Intent(targetContext, StartupActivity.class);
            //add extra to indicate to the intent to start on the login screen
            result.putExtra("startFragment", 1);

            return result;
        }
    };

    public void settingsAndLogout(){
        //tap the Settings icon
        //openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withId(R.id.menu_home_settings)).perform(click());

        //assert that we are on the Settings page
        onView(withId(R.id.settingsFragmentContainer)).check(matches(isDisplayed()));

        onView(withId(R.id.logoutBtn)).perform(click());
        onView(withText(R.string.dialogYes)).perform(click());//click yes to logout
    }

    public void loginTestUser(){
        //Enter credentials and sign in

        onView(withId(R.id.loginEmailTxt))
                .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.loginPasswordTxt))
                .perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.loginBtn))
                .perform(click());

        //TODO: Assert that the HomeActivity has been launched to complete the test.
        onView(withId(R.id.faq_container)).check(matches(isDisplayed()));
        //Is this  method OK or should it not rely on the FAQ page being first?

    }

    //Open the Settings menu from the FAQ page
    @Test
    public void test25(){
        loginTestUser();
        //tap FAQ tab
        onView(withText(R.string.faqSectionTitle)).perform(click());
        //assert that we are on the FAQ tab
        onView(withId(R.id.faq_container)).check(matches(isDisplayed()));

        //tap the Settings icon
        //openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withId(R.id.menu_home_settings)).perform(click());

        //assert that we are on the Settings page
        onView(withId(R.id.settingsFragmentContainer)).check(matches(isDisplayed()));

        onView(withId(R.id.logoutBtn)).perform(click());
        onView(withText(R.string.dialogYes)).perform(click());//click yes to logout

    }

    //Switch to the Chat page from the FAQ page
    @Test
    public void test26() {
        loginTestUser();

        //tap FAQ tab
        onView(withText(R.string.faqSectionTitle)).perform(click());
        //assert that we are on the FAQ tab
        onView(withId(R.id.faq_container)).check(matches(isDisplayed()));

        //tap Chat tab
        onView(withText(R.string.chatSectionTitle)).perform(click());
        //assert that we are on the Pings tab
        onView(withId(R.id.fragment_chat_page)).check(matches(isDisplayed()));

        settingsAndLogout();

    }

    //Switch to the Pings page from the FAQ page
    @Test
    public void test27(){
        loginTestUser();

        //tap FAQ tab
        onView(withText(R.string.faqSectionTitle)).perform(click());
        //assert that we are on the FAQ tab
        onView(withId(R.id.faq_container)).check(matches(isDisplayed()));

        //tap Pings tab
        onView(withText(R.string.pingsSectionTitle)).perform(click());
        //assert that we are on the Pings tab
        onView(withId(R.id.fragment_pings_page)).check(matches(isDisplayed()));

        settingsAndLogout();

    }

    //Test that pressing the software Back button brings us back to the FAQ, Chat, or Pings page
    @Test
    public void test54() {
        loginTestUser();

        //tap FAQ tab
        onView(withText(R.string.faqSectionTitle)).perform(click());
        //assert that we are on the FAQ tab
        onView(withId(R.id.faq_container)).check(matches(isDisplayed()));

        //tap the Settings icon
        //openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withId(R.id.menu_home_settings)).perform(click());

        //assert that we are on the Settings page
        onView(withId(R.id.settingsFragmentContainer)).check(matches(isDisplayed()));

        //Press the software or hardware Back button
        onView(withId(R.id.settingsFragmentContainer)).perform(ViewActions.pressBack());
        //assert that we are on the FAQ tab
        onView(withId(R.id.faq_container)).check(matches(isDisplayed()));

        settingsAndLogout();

    }

    //Check that pressing the on-screen Up button on the settings page
    //brings us back to the previous tab on the home screen
    @Test
    public void test55(){
        loginTestUser();

        //tap FAQ tab
        onView(withText(R.string.faqSectionTitle)).perform(click());
        //assert that we are on the FAQ tab
        onView(withId(R.id.faq_container)).check(matches(isDisplayed()));

        //tap the Settings icon
        //openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withId(R.id.menu_home_settings)).perform(click());

        //assert that we are on the Settings page
        onView(withId(R.id.settingsFragmentContainer)).check(matches(isDisplayed()));

        onView(withContentDescription("Navigate up")).perform(click());
        //assert that we are on the FAQ tab
        onView(withId(R.id.faq_container)).check(matches(isDisplayed()));

        settingsAndLogout();

    }
}
