package com.snowplowanalytics.snowplowtrackerdemo.uitest;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.view.WindowManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.annotation.UiThreadTest;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.snowplowanalytics.snowplowtrackerdemo.Demo;
import com.snowplowanalytics.snowplowtrackerdemo.R;

import static android.content.Context.KEYGUARD_SERVICE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.snowplowanalytics.snowplowtrackerdemo.BuildConfig.MICRO_ENDPOINT;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UITest {

    private String uri;

    @Rule
    public ActivityTestRule<Demo> activityRule
            = new ActivityTestRule<>(Demo.class);

    @UiThreadTest
    @Before
    public void setUp() throws Throwable {
        final Activity activity = activityRule.getActivity();
        activityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                KeyguardManager mKG = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
                KeyguardManager.KeyguardLock mLock = mKG.newKeyguardLock(KEYGUARD_SERVICE);
                mLock.disableKeyguard();

                //turn the screen on
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
            }
        });
    }

    @Before
    public void initValidString() {
        // use serveo for local testing
        uri = MICRO_ENDPOINT;
    }

    @Test
    public void startDemoTest() throws InterruptedException {

        Espresso.closeSoftKeyboard();

        // Type text and then press the button.
        onView( withId(R.id.emitter_uri_field) ).check(matches(withHint("Enter endpoint here…")));
        onView(withId(R.id.emitter_uri_field)).perform(replaceText(uri), closeSoftKeyboard());
        onView(withId(R.id.emitter_uri_field)).check(matches(withText(uri)));
        onView(withId(R.id.btn_lite_start)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_lite_start)).perform(click());

        Thread.sleep(20000);

        // Check that the text was changed.
        onView(withId(R.id.emitter_uri_field))
                .check(matches(withText(uri)));
    }
}
