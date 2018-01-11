package com.alex.towerofhanoi;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private int zone = -1;                        //Section of the GamePanel where user is touching
    private Peg startPeg;
    private Peg lastPeg;                          //The Peg where current disk was picked up from; the last Peg the disk was contained in
    int moves = 0;                                //The number of moves
    int numDisks;                                 //The number of disks for a certain game
    private boolean newGame;                      //Whether the GamePanel should recreate for new game
    private OnGameCompleteListener onGameCompleteListener;
    public OnGameStartedListener onGameStartedListener;
    private Disk[][] diskArrays;
    private int[] sizes;
    private boolean gameStarted;
    private GameActivity mainActivity;
    static int density;

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
    public GamePanel(Context context, int n, @Nullable Peg[] pegs, int m) {
        super(context);
        setFocusable(true);
        numDisks = n;
        moves = m;
        mainActivity = (GameActivity)context;
        gameStarted = false;
        density = mainActivity.getResources().getDisplayMetrics().densityDpi;

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
                        if (!gameStarted) {
                            onGameStartedListener.onGameStarted();
                            gameStarted = true;
                        }
                        if (event.getX() < getWidth() / 3) {
                            pegA.pickUp();
                            lastPeg = pegA;
                            startPeg = pegA;
                            zone = 0;
                        }
                        else if (event.getX() < 2 * getWidth() / 3) {
                            pegB.pickUp();
                            lastPeg = pegB;
                            startPeg = pegB;
                            zone = 1;
                        }
                        else {
                            pegC.pickUp();
                            lastPeg = pegC;
                            startPeg = pegC;
                            zone = 2;
                        }
                        if (lastPeg.getSize() == 0)
                            return false;
                        invalidate();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (zone != 0 && event.getX() < getWidth() / 3) {
                            pegA.move(lastPeg);
                            lastPeg = pegA;
                            zone = 0;
                        }
                        else if (zone != 1 && event.getX() > getWidth() / 3 && event.getX() < 2 * getWidth() / 3) {
                            pegB.move(lastPeg);
                            lastPeg = pegB;
                            zone = 1;
                        }
                        else if (zone != 2 && event.getX() > 2 * getWidth() / 3){
                            pegC.move(lastPeg);
                            lastPeg = pegC;
                            zone = 2;
                        }
                        invalidate();
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (event.getX() < getWidth() / 3) {
                            if (pegA.checkMove() && startPeg != pegA)
                                moves++;
                            pegA.drop(startPeg);
                        }
                        else if (event.getX() < 2 * getWidth() / 3) {
                            if (pegB.checkMove() && startPeg != pegB)
                                moves++;
                            pegB.drop(startPeg);
                        }
                        else {
                            if (pegC.checkMove() && startPeg != pegC)
                                moves++;
                            pegC.drop(startPeg);
                        }
                        mainActivity.updateText(moves);
                        update();
                        invalidate();
                        performClick();
                        return true;
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
            pegA = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), getWidth() / 6, pegHeight, getHeight(), getWidth());
            pegB = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), getWidth() / 2, pegHeight, getHeight(), getWidth());
            pegC = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), 5 * getWidth() / 6, pegHeight, getHeight(), getWidth());

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
            pegA = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), getWidth() / 6, pegHeight, getHeight(), getWidth());
            pegB = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), getWidth() / 2, pegHeight, getHeight(), getWidth());
            pegC = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), 5 * getWidth() / 6, pegHeight, getHeight(), getWidth());

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
