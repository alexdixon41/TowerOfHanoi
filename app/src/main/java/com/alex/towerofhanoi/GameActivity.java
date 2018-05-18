package com.alex.towerofhanoi;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.android.gms.ads.MobileAds;

public class GameActivity extends AppCompatActivity {

    private TextView moveDisplay;                        // Textview to show number of moves
    private FrameLayout fullScreenContent;               // FrameLayout containing GamePanel
    private GamePanel gamePanel;
    private SharedPreferences prefs;
    private PopupWindow popupWindow;
    private Chronometer timer;
    private long time, previousTime;                     // The current game time and saved game time
    private boolean showTimer;                           // Whether or not to show the timer, based on user preference
    private String[] chosenDiskColors;            // String values for chosen disk colors
    private boolean shouldReset = false;
    private boolean gameStarted = false;
    private String backgroundColor;
    boolean sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Immersive mode
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        MobileAds.initialize(this, "ca-app-pub-7938633416120746~9864334238");
        setContentView(R.layout.activity_game);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //Get views
        fullScreenContent = findViewById(R.id.fullscreen_content);
        moveDisplay = findViewById(R.id.move_display);
        timer = findViewById(R.id.time_display);
        LinearLayout timeContainer = findViewById(R.id.time_container);
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);

        //Ads
        AdView bannerAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("BAAAA80F92B4A5B643F09491CD7BC741").addTestDevice("62AC86B88A0AFB414A990F27D6268BB6").build();
        bannerAdView.loadAd(adRequest);

        //Background
        backgroundColor = prefs.getString("background_color", "#00acc1");
        if (backgroundColor.equals("#ffffff") || backgroundColor.equals("#dddddd") || backgroundColor.equals("#00ff7f") || backgroundColor.equals("#d4e157")) {
            moveDisplay.setTextColor(getResources().getColor(R.color.text_color_dark));
            timer.setTextColor(getResources().getColor(R.color.text_color_dark));
        }
        relativeLayout.setBackgroundColor(Color.parseColor(backgroundColor));

        //Timer settings
        showTimer = prefs.getBoolean("timer_setting", true);
        if (showTimer) {                           // enable or disable timer display
            moveDisplay.setGravity(Gravity.START);
            timeContainer.setVisibility(View.VISIBLE);
        }
        else {
            moveDisplay.setGravity(Gravity.CENTER_HORIZONTAL);
            timeContainer.setVisibility(View.GONE);
        }

        //Move display settings
        boolean showMoves = prefs.getBoolean("moves_setting", true);
        if (showMoves)
            moveDisplay.setVisibility(View.VISIBLE);
        else
            moveDisplay.setVisibility(View.INVISIBLE);

        //Load previous time and set display text
        time = previousTime = prefs.getLong("previous_time", 0);
        timer.setText(String.format(getResources().getString(R.string.timer_text), getTimerText(previousTime)));

        //Enable or disable sounds based on Preference
        sound = prefs.getBoolean("sound_effects_setting", true);

        //Disk settings
        String[] diskColorKeys = getResources().getStringArray(R.array.disk_color_keys);
        String[] diskColorStrings = getResources().getStringArray(R.array.disk_color_strings);
        chosenDiskColors = new String[diskColorKeys.length];
        int numChosen = 0;
        for (int i = 0; i < diskColorKeys.length; i++) {
            if (prefs.getBoolean(diskColorKeys[i], false)) {
                chosenDiskColors[numChosen] = diskColorStrings[i];
                numChosen++;
            }
        }
        if (numChosen == 0)
            chosenDiskColors = new String[]{"#20c996", "#6c8fd0", "#f4b710", "#b19cd9"};

        //Load game state values from SharedPreferences and initialize GamePanel accordingly
        Gson gson = new Gson();
        String peg1 = prefs.getString("pegA", "");
        String peg2 = prefs.getString("pegB", "");
        String peg3 = prefs.getString("pegC", "");
        int moves = prefs.getInt("moves", 0);
        int selectedNumDisks = Integer.parseInt(prefs.getString("numDiskSelection", "5"));
        int previousNumDisks = prefs.getInt("previous_num_disks", 5);
        if (peg1.isEmpty() && peg2.isEmpty() && peg3.isEmpty())
            gamePanel = new GamePanel(this, selectedNumDisks, null, moves, chosenDiskColors, numChosen);
        else {
            Peg[] pegs = {gson.fromJson(peg1, Peg.class), gson.fromJson(peg2, Peg.class), gson.fromJson(peg3, Peg.class)};
            gamePanel = new GamePanel(this, previousNumDisks, pegs, moves, chosenDiskColors, numChosen);
        }

        // Start timer when game started
        gamePanel.setOnGameStartedListener(new OnGameStartedListener() {
            @Override
            public void onGameStarted() {
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();
                gameStarted = true;
            }
        });

        timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                time = SystemClock.elapsedRealtime() - chronometer.getBase() + previousTime;
                chronometer.setText(String.format(getResources().getString(R.string.timer_text),
                        getTimerText(time)));
            }
        });

        // Show success popup when game is complete
        gamePanel.setOnGameCompleteListener(new OnGameCompleteListener() {
            @Override
            public void onGameComplete(boolean isOptimal, int moves, int numDisks) {
                timer.stop();
                String bestMoves = accessBestMoves(moves, numDisks);
                String bestTime = accessBestTime(numDisks);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                View popupView = inflater.inflate(R.layout.success_popup_layout, fullScreenContent, false);
                if (backgroundColor.equals("#00acc1"))
                    popupView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                TextView successMoves = popupView.findViewById(R.id.success_moves);
                TextView successMessage = popupView.findViewById(R.id.success_message);
                TextView successTitle = popupView.findViewById(R.id.success_title);
                TextView optimalMovesMessage = popupView.findViewById(R.id.optimal_moves);
                TextView successTime = popupView.findViewById(R.id.success_time);
                TextView bestMoveDisplay = popupView.findViewById(R.id.best_move_display);
                TextView bestTimeDisplay = popupView.findViewById(R.id.best_time_display);

                successMessage.setText(String.format(getResources().getString(R.string.success_message), numDisks));
                optimalMovesMessage.setText(String.format(getResources().getString(R.string.success_optimal_moves), (int)Math.pow(2, numDisks) - 1));
                successMoves.setText(String.format(getResources().getString(R.string.success_moves), "" + moves));
                successTime.setText(String.format(getResources().getString(R.string.success_time), getTimerText(time)));
                bestMoveDisplay.setText(String.format(getResources().getString(R.string.success_moves), bestMoves));
                bestTimeDisplay.setText(String.format(getResources().getString(R.string.success_time), bestTime));
                if (isOptimal) {
                    successTitle.setText(R.string.success_title_optimal);
                }
                else {
                    successTitle.setText(R.string.success_title);
                }
                if (showTimer) {
                    successTime.setVisibility(View.VISIBLE);
                    bestTimeDisplay.setVisibility(View.VISIBLE);
                }
                else {
                    successTime.setVisibility(View.INVISIBLE);
                    bestTimeDisplay.setVisibility(View.INVISIBLE);
                }

                popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setAnimationStyle(R.style.Animation);
                popupWindow.showAtLocation(fullScreenContent, Gravity.CENTER, 0, 0);
            }
        });
        fullScreenContent.addView(gamePanel);
        int height;
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenSize = (int)(displayMetrics.heightPixels / displayMetrics.density);
        if (screenSize <= 400)
            height = 32;
        else if (screenSize <= 720)
            height = 50;
        else
            height = 90;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0, 0, (int)(height * displayMetrics.density) + 50);
        relativeLayout.setLayoutParams(params);

        updateText(moves);
    }

    @Override
    public void onResume() {
        //Resume timer
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        previousTime = prefs.getLong("previous_time", 0);
        if (gameStarted) {
            timer.setBase(SystemClock.elapsedRealtime());
            timer.start();
        }
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        timer.stop();
        if (popupWindow != null)
            popupWindow.dismiss();
        SharedPreferences.Editor editor = prefs.edit();

        if (gamePanel.moving) {
            if (gamePanel.lastPeg != gamePanel.startPeg)
                gamePanel.startPeg.move(gamePanel.lastPeg);
            gamePanel.startPeg.drop(gamePanel.startPeg);
            gamePanel.moving = false;
        }

        // When game should be reset, clear certain preferences
        if (shouldReset) {
            editor.remove("pegA");
            editor.remove("pegB");
            editor.remove("pegC");
            editor.remove("previous_time");
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
            editor.putLong("previous_time", time);
            editor.putInt("moves", gamePanel.moves);
            editor.putInt("previous_num_disks", gamePanel.numDisks).apply();
        }
    }

    /**
     * Convert a long representing milliseconds to a String formatted for display
     * @param time  time in milliseconds
     * @return      formatted String representing time value
     */
    static String getTimerText(long time) {
        int h = (int)(time / 3600000);
        int m = (int)(time - h*3600000)/60000;
        int s = (int)(time - h*3600000 - m*60000)/1000;
        String timerText = "";
        if (h != 0) {
            timerText = timerText + h + ":";
            if (m < 10)
                timerText = timerText + "0";
            if (m == 0) {
                timerText = timerText + "0:";
                if (s < 10)
                    timerText = timerText + "0";
            }
        }
        if (m != 0) {
            timerText = timerText + m + ":";
            if (s < 10)
                timerText = timerText + "0";
        }
        timerText = timerText + s;
        return timerText;
    }

    /**
     * If time for current game is less than the best for the current number of
     * disks, update that SharedPreference. Return the user's best time for the current
     * number of disks.
     * @param numDisks  the current game number of disks
     * @return          the user's best time
     */
    private String accessBestTime(int numDisks) {
        long bestTime;
        switch (numDisks) {
            case 2:
                bestTime = prefs.getLong("best_time_2", 0);
                if (bestTime == 0 || time < bestTime)
                    prefs.edit().putLong("best_time_2", time).apply();
                return getTimerText(bestTime);
            case 3:
                bestTime = prefs.getLong("best_time_3", 0);
                if (bestTime == 0 || time < bestTime)
                    prefs.edit().putLong("best_time_3", time).apply();
                return getTimerText(bestTime);
            case 4:
                bestTime = prefs.getLong("best_time_4", 0);
                if (bestTime == 0 || time < bestTime)
                    prefs.edit().putLong("best_time_4", time).apply();
                return getTimerText(bestTime);
            case 6:
                bestTime = prefs.getLong("best_time_6", 0);
                if (bestTime == 0 || time < bestTime)
                    prefs.edit().putLong("best_time_6", time).apply();
                return getTimerText(bestTime);
            case 7:
                bestTime = prefs.getLong("best_time_7", 0);
                if (bestTime == 0 || time < bestTime)
                    prefs.edit().putLong("best_time_7", time).apply();
                return getTimerText(bestTime);
            case 8:
                bestTime = prefs.getLong("best_time_8", 0);
                if (bestTime == 0 || time < bestTime)
                    prefs.edit().putLong("best_time_8", time).apply();
                return getTimerText(bestTime);
            case 9:
                bestTime = prefs.getLong("best_time_9", 0);
                if (bestTime == 0 || time < bestTime)
                    prefs.edit().putLong("best_time_9", time).apply();
                return getTimerText(bestTime);
            case 10:
                bestTime = prefs.getLong("best_time_10", 0);
                if (bestTime == 0 || time < bestTime)
                    prefs.edit().putLong("best_time_10", time).apply();
                return getTimerText(bestTime);
            case 11:
                bestTime = prefs.getLong("best_time_11", 0);
                if (bestTime == 0 || time < bestTime)
                    prefs.edit().putLong("best_time_11", time).apply();
                return getTimerText(bestTime);
            case 12:
                bestTime = prefs.getLong("best_time_12", 0);
                if (bestTime == 0 || time < bestTime)
                    prefs.edit().putLong("best_time_12", time).apply();
                return getTimerText(bestTime);
            default:
                bestTime = prefs.getLong("best_time_5", 0);
                if (bestTime == 0 || time < bestTime)
                    prefs.edit().putLong("best_time_5", time).apply();
                return getTimerText(bestTime);
        }
    }

    /**
     * If number of moves for current game is less than the best for the current number of
     * disks, update that SharedPreference. Return the user's best number of moves for the current
     * number of disks.
     * @param moves      the number of moves for the current game
     * @param numDisks   the current game number of disks
     * @return           the user's best number of moves
     */
    private String accessBestMoves(int moves, int numDisks) {
        int bestMoves;
        switch (numDisks) {
            case 2:
                bestMoves = prefs.getInt("best_moves_2", 0);
                if (bestMoves == 0 || moves < bestMoves)
                    prefs.edit().putInt("best_moves_2", moves).apply();
                return "" + bestMoves;
            case 3:
                bestMoves = prefs.getInt("best_moves_3", 0);
                if (bestMoves == 0 || moves < bestMoves)
                    prefs.edit().putInt("best_moves_3", moves).apply();
                return "" + bestMoves;
            case 4:
                bestMoves = prefs.getInt("best_moves_4", 0);
                if (bestMoves == 0 || moves < bestMoves)
                    prefs.edit().putInt("best_moves_4", moves).apply();
                return "" + bestMoves;
            case 6:
                bestMoves = prefs.getInt("best_moves_6", 0);
                if (bestMoves == 0 || moves < bestMoves)
                    prefs.edit().putInt("best_moves_6", moves).apply();
                return "" + bestMoves;
            case 7:
                bestMoves = prefs.getInt("best_moves_7", 0);
                if (bestMoves == 0 || moves < bestMoves)
                    prefs.edit().putInt("best_moves_7", moves).apply();
                return "" + bestMoves;
            case 8:
                bestMoves = prefs.getInt("best_moves_8", 0);
                if (bestMoves == 0 || moves < bestMoves)
                    prefs.edit().putInt("best_moves_8", moves).apply();
                return "" + bestMoves;
            case 9:
                bestMoves = prefs.getInt("best_moves_9", 0);
                if (bestMoves == 0 || moves < bestMoves)
                    prefs.edit().putInt("best_moves_9", moves).apply();
                return "" + bestMoves;
            case 10:
                bestMoves = prefs.getInt("best_moves_10", 0);
                if (bestMoves == 0 || moves < bestMoves)
                    prefs.edit().putInt("best_moves_10", moves).apply();
                return "" + bestMoves;
            case 11:
                bestMoves = prefs.getInt("best_moves_11", 0);
                if (bestMoves == 0 || moves < bestMoves)
                    prefs.edit().putInt("best_moves_11", moves).apply();
                return "" + bestMoves;
            case 12:
                bestMoves = prefs.getInt("best_moves_12", 0);
                if (bestMoves == 0 || moves < bestMoves)
                    prefs.edit().putInt("best_moves_12", moves).apply();
                return "" + bestMoves;
            default:
                bestMoves = prefs.getInt("best_moves_5", 0);
                if (bestMoves == 0 || moves < bestMoves)
                    prefs.edit().putInt("best_moves_5", moves).apply();
                return "" + bestMoves;
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
