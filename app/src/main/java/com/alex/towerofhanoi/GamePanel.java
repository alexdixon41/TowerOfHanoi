package com.alex.towerofhanoi;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Alex on 10/7/2017.
 *
 * Custom view for game graphics.
 */

public class GamePanel extends View {

    private Peg pegA, pegB, pegC;
    Peg startPeg, lastPeg;                     //The Peg where current disk was picked up from; the last Peg the disk was on
    int moves = 0;                             //The number of moves
    int numDisks;                              //The number of disks for a certain game
    private boolean newGame;                   //Whether the GamePanel should recreate for new game
    private OnGameCompleteListener onGameCompleteListener;
    public OnGameStartedListener onGameStartedListener;
    private Disk[][] diskArrays;
    private int[] sizes;
    private boolean gameStarted;
    private GameActivity mainActivity;
    static int density;
    int[] diskColors;
    boolean moving;                            //Check if any disks are currently being moved
    private SoundPool soundPool;
    private int[] soundEffects;
    private boolean soundEnabled;

    @SuppressWarnings("EmptyMethod")
    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public GamePanel(Context context) {
        super(context);
    }

    /**
     * View that contains Tower of Hanoi game.
     * @param context  .
     * @param n        the number of disks
     * @param pegs     array of pegs from previous game
     * @param m        the number of moves from previous game
     */
    public GamePanel(Context context, int n, @Nullable Peg[] pegs, int m, String[] colorStrings, int numDiskColors) {
        super(context);
        setFocusable(true);
        numDisks = n;
        moves = m;
        mainActivity = (GameActivity)context;
        gameStarted = false;
        density = mainActivity.getResources().getDisplayMetrics().densityDpi;
        soundEnabled = mainActivity.sound;

        // Load sounds for soundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            createNewSoundPool();
        else
            createOldSoundPool();
        soundEffects = new int[3];
        soundEffects[0] = soundPool.load(getContext(), R.raw.pickup, 1);
        soundEffects[1] = soundPool.load(getContext(), R.raw.move, 1);
        soundEffects[2] = soundPool.load(getContext(), R.raw.drop, 1);

        // Store disk colors from GameActivity
        diskColors = new int[numDiskColors];
        for (int i = 0; i < numDiskColors; i++)
            diskColors[i] = Color.parseColor(colorStrings[i]);

        setBackgroundColor(Color.TRANSPARENT);

        if (pegs != null) {
            newGame = false;
            diskArrays = new Disk[][] {pegs[0].getDisks(), pegs[1].getDisks(), pegs[2].getDisks()};
            sizes = new int[] {pegs[0].getSize(), pegs[1].getSize(), pegs[2].getSize()};
        }
        else {
            newGame = true;
        }

        // Handle touch events for game moves
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (event.getX() < getWidth() / 3 && pegA.getSize() > 0) {
                            pegA.pickUp();
                            lastPeg = pegA;
                            startPeg = pegA;
                        }
                        else if (event.getX() > getWidth() / 3 && event.getX() < 2 * getWidth() / 3 && pegB.getSize() > 0) {
                            pegB.pickUp();
                            lastPeg = pegB;
                            startPeg = pegB;
                        }
                        else if (event.getX() > 2 * getWidth() / 3 && pegC.getSize() > 0){
                            pegC.pickUp();
                            lastPeg = pegC;
                            startPeg = pegC;
                        }
                        else
                            return false;
                        moving = true;                          //move has started
                        if (soundEnabled)
                            soundPool.play(soundEffects[0], 1, 1, 1, 0, 1f);
                        if (!gameStarted) {
                            onGameStartedListener.onGameStarted();
                            gameStarted = true;
                        }
                        invalidate();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (moving) {
                            if (lastPeg != pegA && event.getX() < getWidth() / 3) {
                                if (soundEnabled)
                                    soundPool.play(soundEffects[1], 1, 1, 1, 0, 1f);
                                pegA.move(lastPeg);
                                lastPeg = pegA;
                            } else if (lastPeg != pegB && event.getX() > getWidth() / 3 && event.getX() < 2 * getWidth() / 3) {
                                if (soundEnabled)
                                    soundPool.play(soundEffects[1], 1, 1, 1, 0, 1f);
                                pegB.move(lastPeg);
                                lastPeg = pegB;
                            } else if (lastPeg != pegC && event.getX() > 2 * getWidth() / 3) {
                                if (soundEnabled)
                                    soundPool.play(soundEffects[1], 1, 1, 1, 0, 1f);
                                pegC.move(lastPeg);
                                lastPeg = pegC;
                            }
                            invalidate();
                            return true;
                        }
                    case MotionEvent.ACTION_UP:
                        if (moving) {
                            moving = false;                       //move is complete
                            if (soundEnabled)
                                soundPool.play(soundEffects[2], 1, 1, 1, 0, 1f);
                            if (lastPeg.checkMove() && startPeg != lastPeg)
                                moves++;
                            lastPeg.drop(startPeg);
                            mainActivity.updateText(moves);
                            update();
                            invalidate();
                            performClick();
                            return true;
                        }
                }
                return false;
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        pegA.resetDisks();
        pegB.resetDisks();
        pegC.resetDisks();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int pegHeight = dpToPx(Peg.DISK_HEIGHT_DP) * (numDisks + 1);
        if (newGame) {
            pegA = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), getWidth() / 6, pegHeight, getHeight(), getWidth(), diskColors);
            pegB = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), getWidth() / 2, pegHeight, getHeight(), getWidth(), diskColors);
            pegC = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), 5 * getWidth() / 6, pegHeight, getHeight(), getWidth(), diskColors);

            int maxDiskSize;
            int spacing;
            if (mainActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                maxDiskSize = getWidth() / 3 - getWidth() / 30;
                spacing = (maxDiskSize - getHeight() / 20) / (numDisks - 1);
            }
            else {
                maxDiskSize = getHeight() / 3 - getHeight() / 30;
                spacing = (maxDiskSize - getWidth() / 20) / (numDisks - 1);
            }

            pegA.populateDisks(numDisks, maxDiskSize, spacing);

            newGame = false;
        }
        else {
            pegA = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), getWidth() / 6, pegHeight, getHeight(), getWidth(), diskColors);
            pegB = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), getWidth() / 2, pegHeight, getHeight(), getWidth(), diskColors);
            pegC = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), 5 * getWidth() / 6, pegHeight, getHeight(), getWidth(), diskColors);

            pegA.setSize(sizes[0]);
            pegB.setSize(sizes[1]);
            pegC.setSize(sizes[2]);
            pegA.setDisks(diskArrays[0]);
            pegB.setDisks(diskArrays[1]);
            pegC.setDisks(diskArrays[2]);
        }

        lastPeg = pegA;
        startPeg = pegA;
        update();
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        pegA.draw(canvas);
        pegB.draw(canvas);
        pegC.draw(canvas);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        soundPool = new SoundPool.Builder().setAudioAttributes(attributes).build();
    }

    @SuppressWarnings("deprecation")
    private void createOldSoundPool() {
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    }

    public Peg[] getPegs() {
        return new Peg[]{pegA, pegB, pegC};
    }

    /**
     * Check if the puzzle has been completed.
     */
    private void update() {
        if (pegC.getSize() == numDisks) {
            if (moves == Math.pow(2, numDisks) - 1)
                onGameCompleteListener.onGameComplete(true, moves, numDisks);
            else
                onGameCompleteListener.onGameComplete(false, moves, numDisks);
        }
    }

    static int dpToPx(int dp) {
        return (density / DisplayMetrics.DENSITY_DEFAULT) * dp;
    }

    public void setOnGameCompleteListener(OnGameCompleteListener eventListener) {
        onGameCompleteListener = eventListener;
    }

    public void setOnGameStartedListener(OnGameStartedListener eventListener) {
        onGameStartedListener = eventListener;
    }
}
