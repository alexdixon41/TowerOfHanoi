package com.alex.towerofhanoi;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

/**
 * Created by Alex on 10/11/2017.
 */

public class Peg {
    private Disk[] disks;
    private int n, center;              //The number of disks on this Peg; the x-value for the center of the Peg;
    private int height, panelHeight;    //The height of the Peg; the height of the panel Pegs are drawn in
    private final int MAX_DISK_SIZE = 200;
    private int[] colors = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.BLACK, Color.YELLOW};
    public Bitmap image;
    Rect dst;

    /**
     * @param res          image resource for the background of the Pegs
     * @param center       x-value for the center of the Peg
     * @param panelHeight  height of the GamePanel where Pegs are drawn
     * @param height       height of the Peg
     */
    Peg(Bitmap res, int center, int panelHeight, int height) {
        n = 0;
        this.height = height;
        this.panelHeight = panelHeight;
        this.center = center;
        dst = new Rect(center - 10, panelHeight - height, center + 10, panelHeight);
        image = res;
        disks = new Disk[10];
    }

    /**
     * Initially creates the disks at the beginning of a game.
     * Should only be called on the left Peg to start all disks at that point.
     * @param numDisks  how many disks to initially create
     */
    public void populateDisks(int numDisks) {
        n = numDisks;
        int spacing = (MAX_DISK_SIZE - 40) / (n - 1);                  //How much to decrease width for each new disk
        for (int i = 0; i < n; i++) {
            disks[i] = new Disk(MAX_DISK_SIZE - spacing * i, dst.centerX(), dst.bottom - Disk.getHeight() * i, colors[i % 5]);
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, null, dst, null);
        for (int i = 0; i < n; i++) {
            disks[i].draw(canvas);
        }

    }

    public void pickUp() {
        if (n > 0) {
            disks[n-1].r.bottom = panelHeight - height;
            disks[n-1].r.top = panelHeight - (height + Disk.getHeight());
        }
    }

    public void move(Peg oldPeg) {
        n++;
        disks[n-1] = oldPeg.disks[oldPeg.n-1];
        oldPeg.disks[oldPeg.n-1] = null;
        oldPeg.n--;
        disks[n-1].r.left = center - disks[n-1].getWidth() / 2;
        disks[n-1].r.right = center + disks[n-1].getWidth() / 2;
    }

    public void drop(Peg startPeg) {
        if (!checkMove()) {
            startPeg.move(this);
            startPeg.drop(startPeg);
        }
        else {
            if (n == 1) {
                disks[n - 1].r.bottom = panelHeight;
                disks[n - 1].r.top = panelHeight - Disk.getHeight();
            } else {
                disks[n - 1].r.bottom = disks[n - 2].r.top;
                disks[n - 1].r.top = disks[n - 1].r.bottom - Disk.getHeight();
            }
        }
    }

    public boolean checkMove() {
        return (n <= 1 || disks[n-2].getWidth() > disks[n-1].getWidth());
    }

    public int getSize() {
        return n;
    }
}
