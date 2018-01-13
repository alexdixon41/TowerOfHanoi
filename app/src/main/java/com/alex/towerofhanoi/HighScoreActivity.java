package com.alex.towerofhanoi;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HighScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        int[] bestMoves = new int[] {prefs.getInt("best_moves_2", 0), prefs.getInt("best_moves_3", 0),
                prefs.getInt("best_moves_4", 0), prefs.getInt("best_moves_5", 0), prefs.getInt("best_moves_6", 0),
                prefs.getInt("best_moves_7", 0), prefs.getInt("best_moves_8", 0), prefs.getInt("best_moves_9", 0),
                prefs.getInt("best_moves_10", 0), prefs.getInt("best_moves_11", 0), prefs.getInt("best_moves_12", 0)};

        long[] bestTimes = new long[] {prefs.getLong("best_time_2", 0), prefs.getLong("best_time_3", 0),
                prefs.getLong("best_time_4", 0), prefs.getLong("best_time_5", 0), prefs.getLong("best_time_6", 0),
                prefs.getLong("best_time_7", 0), prefs.getLong("best_time_8", 0), prefs.getLong("best_time_9", 0),
                prefs.getLong("best_time_10", 0), prefs.getLong("best_time_11", 0), prefs.getLong("best_time_12", 0)};

        setContentView(R.layout.activity_high_score);

        TextView[] bestMoveDisplays = new TextView[] {findViewById(R.id.best_moves_2), findViewById(R.id.best_moves_3),
                findViewById(R.id.best_moves_4), findViewById(R.id.best_moves_5), findViewById(R.id.best_moves_6),
                findViewById(R.id.best_moves_7), findViewById(R.id.best_moves_8), findViewById(R.id.best_moves_9),
                findViewById(R.id.best_moves_10), findViewById(R.id.best_moves_11), findViewById(R.id.best_moves_12)};
        TextView[] bestTimeDisplays = new TextView[] {findViewById(R.id.best_time_2), findViewById(R.id.best_time_3),
                findViewById(R.id.best_time_4), findViewById(R.id.best_time_5), findViewById(R.id.best_time_6),
                findViewById(R.id.best_time_7), findViewById(R.id.best_time_8), findViewById(R.id.best_time_9),
                findViewById(R.id.best_time_10), findViewById(R.id.best_time_11), findViewById(R.id.best_time_12)};

        for (int i = 0; i < bestMoves.length; i++) {
            if (bestMoves[i] == 0)
                bestMoveDisplays[i].setText(R.string.none);
            else
                bestMoveDisplays[i].setText(String.format(getResources().getString(R.string.integer_holder), bestMoves[i]));
        }
        for (int i = 0; i < bestTimes.length; i++) {
            if (bestTimes[i] == 0)
                bestTimeDisplays[i].setText(R.string.none);
            else
                bestTimeDisplays[i].setText(GameActivity.getTimerText(bestTimes[i]));
        }
    }
}
