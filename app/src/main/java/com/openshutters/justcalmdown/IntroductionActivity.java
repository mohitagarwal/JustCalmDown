package com.openshutters.justcalmdown;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;


public class IntroductionActivity extends AppIntro {

    private SharedPreferences prfs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_up, android.R.anim.fade_out);
        prfs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (prfs.getBoolean(getString(R.string.pref_intro_seen), false)) {
            navigateToMainScreen();
        }

        addSlide(AppIntroFragment.newInstance("Exhale", "Exhale out through mouth completely,\nyou can feel your stomach going in as you exhale", R.drawable.image9, getResources().getColor(R.color.theme_primary)));
        addSlide(AppIntroFragment.newInstance("Inhale", "Inhale through nose till you feel a continuous vibration,\nfollow the outward arrows", R.drawable.image2, getResources().getColor(R.color.theme_primary)));
        addSlide(AppIntroFragment.newInstance("Hold your breath", "When the vibration stops, hold you breath till you feel the vibrations again, and start to exhale thereafter", R.drawable.image5, getResources().getColor(R.color.theme_primary)));
        addSlide(AppIntroFragment.newInstance("Exhale slowly", "Exhale out slowly through nose,\nfeel your stomach going inside,\nfollow the arrows and the jerky vibrations to exhale out", R.drawable.image9, getResources().getColor(R.color.theme_primary)));
        addSlide(SetTimeLimitFragment.newInstance());

        showSkipButton(false);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        prfs.edit().putBoolean(getString(R.string.pref_intro_seen), true).apply();
        navigateToMainScreen();
    }

    private void navigateToMainScreen() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
