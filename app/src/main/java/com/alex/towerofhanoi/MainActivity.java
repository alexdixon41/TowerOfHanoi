package com.alex.towerofhanoi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setContentView(R.layout.activity_main);
    }

    /**
     * Start a new game by removing SharedPreferences from previous game and starting
     * new GameActivity Intent.
     * @param v  the view that called this method
     */
    public void newGame(View v) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("pegA");
        editor.remove("pegB");
        editor.remove("pegC");
        editor.remove("moves");
        editor.remove("previous_num_disks").apply();
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    /**
     * Resume a previously started game by starting new GameActivity Intent.
     * @param v  the view that called this method
     */
    public void resumeGame(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    /**
     * Launch the settings Activity.
     * @param v  the view that called this method
     */
    public void settings(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}
