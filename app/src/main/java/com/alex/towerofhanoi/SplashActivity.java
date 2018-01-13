package com.alex.towerofhanoi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent;
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("first_run", true)) {
            intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
        }
        else {
            intent = new Intent(this, MainActivity.class);
            new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(1200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        startActivity(intent);
                        finish();
                    }
                }
            }.start();
        }
    }
}
