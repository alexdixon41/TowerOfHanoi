package com.alex.towerofhanoi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;


public class IntroActivity extends com.heinrichreimersoftware.materialintro.app.IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullscreen(true);
        super.onCreate(savedInstanceState);

        setButtonBackVisible(false);
        setButtonNextVisible(true);
        setButtonNextFunction(BUTTON_NEXT_FUNCTION_NEXT_FINISH);
        addSlide(new SimpleSlide.Builder()
                .title(R.string.app_name)
                .description(R.string.tutorial_title)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorAccent)
                .image(R.drawable.tutorial_1)
                .scrollable(false)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.move_disks)
                .description(R.string.tutorial_description_1)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorAccent)
                .image(R.drawable.tutorial_2)
                .scrollable(false)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.move_disks)
                .description(R.string.tutorial_description_2)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorAccent)
                .image(R.drawable.tutorial_3)
                .scrollable(false)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.move_disks)
                .description(R.string.tutorial_description_3)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorAccent)
                .image(R.drawable.tutorial_4)
                .scrollable(false)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.size_rule)
                .description(R.string.tutorial_description_4)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorAccent)
                .image(R.drawable.tutorial_5)
                .scrollable(false)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.finish_game)
                .description(R.string.tutorial_description_5)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorAccent)
                .image(R.drawable.tutorial_6)
                .scrollable(false)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.tutorial_end)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .image(R.drawable.splash_icon)
                .scrollable(false)
                .build());
    }

    @Override
    public void onPause() {
        super.onPause();

        Intent intent;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("first_run", true)) {
            intent = new Intent(this, MainActivity.class);
            prefs.edit().putBoolean("first_run", false).apply();
            startActivity(intent);
        }
    }
}
