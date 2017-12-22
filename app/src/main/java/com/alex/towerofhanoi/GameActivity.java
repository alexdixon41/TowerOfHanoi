package com.alex.towerofhanoi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;

public class GameActivity extends Activity {

    TextView moveDisplay;
    FrameLayout fullScreenContent;
    GamePanel gamePanel;
    SharedPreferences prefs;
    PopupWindow popupWindow;

    boolean shouldReset = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_game);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        fullScreenContent = findViewById(R.id.fullscreen_content);
        moveDisplay = findViewById(R.id.move_display);

        // Load saved preferences and initialize gamePanel accordingly
        Gson gson = new Gson();
        String peg1 = prefs.getString("pegA", "");
        String peg2 = prefs.getString("pegB", "");
        String peg3 = prefs.getString("pegC", "");
        int moves = prefs.getInt("moves", 0);
        int selectedNumDisks = Integer.parseInt(prefs.getString("numDiskSelection", "5"));
        int previousNumDisks = prefs.getInt("previous_num_disks", 5);
        if (peg1.isEmpty() && peg2.isEmpty() && peg3.isEmpty())
            gamePanel = new GamePanel(this, selectedNumDisks, null, moves);
        else {
            Peg[] pegs = {gson.fromJson(peg1, Peg.class), gson.fromJson(peg2, Peg.class), gson.fromJson(peg3, Peg.class)};
            gamePanel = new GamePanel(this, previousNumDisks, pegs, moves);
        }

        // Show success popup when game is complete
        gamePanel.setOnGameCompleteListener(new OnGameCompleteListener() {
            @Override
            public void onGameComplete(boolean isOptimal, int moves, int numDisks) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                View popupView = inflater.inflate(R.layout.success_popup_layout, fullScreenContent, false);

                TextView successMessage = popupView.findViewById(R.id.success_message);
                if (isOptimal)
                    successMessage.setText(String.format(getResources().getString(R.string.success_message_optimal), moves));
                else
                    successMessage.setText(String.format(getResources().getString(R.string.success_message), moves, numDisks, (int)(Math.pow(2, numDisks) - 1)));

                popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);

                popupWindow.showAtLocation(fullScreenContent, Gravity.CENTER, 0, 0);
            }
        });
        fullScreenContent.addView(gamePanel);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, (int)moveDisplay.getTextSize() + 20, 0, getResources().getDimensionPixelSize(getResources().getIdentifier("navigation_bar_height", "dimen", "android")));
        fullScreenContent.setLayoutParams(params);

        updateText(moves);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (popupWindow != null)
            popupWindow.dismiss();

        SharedPreferences.Editor editor = prefs.edit();
        // When game should be reset, clear peg preferences
        if (shouldReset) {
            editor.remove("pegA");
            editor.remove("pegB");
            editor.remove("pegC");
            editor.remove("moves").apply();
            shouldReset = false;
        }
        else {
            // Save game state information to SharedPreferences
            Gson gson = new Gson();
            Peg[] pegs = gamePanel.getPegs();
            String pegA = gson.toJson(pegs[0]);
            String pegB = gson.toJson(pegs[1]);
            String pegC = gson.toJson(pegs[2]);

            editor.putString("pegA", pegA);
            editor.putString("pegB", pegB);
            editor.putString("pegC", pegC);
            editor.putInt("moves", gamePanel.moves);
            editor.putInt("previous_num_disks", gamePanel.numDisks).apply();
        }
    }

    /**
     * Update the move counter with the current number of moves.
     * @param moves  the number of moves made
     */
    public void updateText(int moves) {
        moveDisplay.setText(String.format(getResources().getString(R.string.moves), moves));
    }

    /**
     * Return to the main menu by finishing this Activity.
     * @param v  the view that called this method
     */
    public void mainMenu(View v) {
        super.finish();
    }

    /**
     * Restart the game by recreating the GameActivity
     * @param v  the view that called this method
     */
    public void restart(View v) {
        shouldReset = true;
        recreate();
    }

}
