package com.alex.towerofhanoi;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Alex on 10/7/2017.
 */

public class GamePanel extends View {

    private Peg pegA, pegB, pegC;
    int zone = -1;                                //Section of the GamePanel where user is touching
    Peg startPeg, oldPeg;                         //The Peg where current disk was picked up from; the last Peg the disk was contained in
    int moves = 0;                                //The number of moves
    int numDisks;                                 //The number of disks for a certain game
    MainActivity mainActivity;

    public GamePanel(Context context) {
        super(context);

        setFocusable(true);

        mainActivity = (MainActivity)context;

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (event.getX() < getWidth() / 3) {
                            pegA.pickUp();
                            oldPeg = pegA;
                            startPeg = pegA;
                            zone = 0;
                        }
                        else if (event.getX() < 2 * getWidth() / 3) {
                            pegB.pickUp();
                            oldPeg = pegB;
                            startPeg = pegB;
                            zone = 1;
                        }
                        else {
                            pegC.pickUp();
                            oldPeg = pegC;
                            startPeg = pegC;
                            zone = 2;
                        }
                        if (oldPeg.getSize() == 0)
                            return false;
                        invalidate();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (zone != 0 && event.getX() < getWidth() / 3) {
                            pegA.move(oldPeg);
                            oldPeg = pegA;
                            zone = 0;
                        }
                        else if (zone != 1 && event.getX() > getWidth() / 3 && event.getX() < 2 * getWidth() / 3) {
                            pegB.move(oldPeg);
                            oldPeg = pegB;
                            zone = 1;
                        }
                        else if (zone != 2 && event.getX() > 2 * getWidth() / 3){
                            pegC.move(oldPeg);
                            oldPeg = pegC;
                            zone = 2;
                        }
                        invalidate();
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (event.getX() < getWidth() / 3) {
                            if (pegA.checkMove())
                                moves++;
                            pegA.drop(startPeg);
                        }
                        else if (event.getX() < 2 * getWidth() / 3) {
                            if (pegB.checkMove())
                                moves++;
                            pegB.drop(startPeg);
                        }
                        else {
                            if (pegC.checkMove())
                                moves++;
                            pegC.drop(startPeg);
                        }
                        mainActivity.updateText(moves);
                        update();
                        invalidate();
                        return true;
                }
                return false;
            }
        });

        this.invalidate();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus());

        numDisks = 5;
        int pegHeight = Disk.getHeight() * numDisks + 30;
        pegA = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), getWidth() / 4 - 50, getHeight(), pegHeight);
        pegB = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), getWidth() / 2 - 10, getHeight(), pegHeight);
        pegC = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), 3 * getWidth() / 4 + 50, getHeight(), pegHeight);

        pegA.populateDisks(numDisks);

        oldPeg = pegA;
        startPeg = pegA;
    }

    public void update() {
        if (pegC.getSize() == numDisks) {
            System.out.println("You win!");
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setBackgroundColor(Color.WHITE);

        pegA.draw(canvas);
        pegB.draw(canvas);
        pegC.draw(canvas);
    }

}
