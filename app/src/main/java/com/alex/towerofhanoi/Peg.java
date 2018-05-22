package com.alex.towerofhanoi;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by Alex on 10/11/2017.
 *
 * Class to represent a light_wood that holds an array of disks.
 */

class Peg {
    private Disk[] disks;
    private int n, center;                          //The number of disks on this Peg; the x-value for the center of the Peg;
    private int height;                             //The height of the Peg
    private int panelHeight;                        //The height of the panel Pegs are drawn in
    private int[] colors;
    private Bitmap image;
    private Rect dst;
    static final int DISK_HEIGHT_DP = 18;

    /**
     * @param res          image resource for the background of the Pegs
     * @param center       the x-value for the center of the Peg
     * @param height       the height of the Peg
     * @param panelHeight  the height of the panel the Peg is drawn in
     * @param panelWidth   the width of the panel the Peg is drawn in
     */
    Peg(Bitmap res, int center, int height, int panelHeight, int panelWidth, int[] diskColors) {
        this.height = height;
        this.center = center;
        this.panelHeight = panelHeight;
        dst = new Rect(center - panelWidth / 72, panelHeight - height, center + panelWidth / 72, panelHeight);
        n = 0;
        image = res;
        disks = new Disk[12];
        colors = diskColors;
    }

    /**
     * Initially creates the disks at the beginning of a game.
     * Should only be called on the left Peg to start all disks at that point.
     * @param numDisks      how many disks to initially create
     * @param maxDiskSize   the size of the largest Disk
     */
    void populateDisks(int numDisks, int maxDiskSize, int spacing) {
        n = numDisks;
        for (int i = 0; i < n; i++) {
            disks[i] = new Disk(maxDiskSize - spacing * i, GamePanel.dpToPx(DISK_HEIGHT_DP), dst.centerX(),
                    dst.bottom - GamePanel.dpToPx(DISK_HEIGHT_DP) * i, colors[i % colors.length]);
        }
    }

    void draw(Canvas canvas) {
        canvas.drawBitmap(image, null, dst, null);
        for (int i = 0; i < n; i++) {
            disks[i].draw(canvas);
        }
    }

    /**
     * Reposition the top Disk to directly above the Peg.
     */
    void pickUp() {
        if (n > 0) {
            disks[n-1].r.bottom = panelHeight - height;
            disks[n-1].r.top = panelHeight - (height + GamePanel.dpToPx(DISK_HEIGHT_DP));
        }
    }

    /**
     * Move the top Disk from lastPeg to this Peg.
     * @param lastPeg   the Peg where the Disk was located most recently
     */
    void move(Peg lastPeg) {
        n++;
        disks[n-1] = lastPeg.disks[lastPeg.n-1];
        lastPeg.disks[lastPeg.n-1] = null;
        lastPeg.n--;
        disks[n-1].r.left = center - disks[n-1].getWidth() / 2;
        disks[n-1].r.right = center + disks[n-1].getWidth() / 2;
    }

    /**
     * If allowed, reposition the Disk to its proper position on top of other Disks. If this move violates
     * the rules, place the Disk back on the Peg it was picked up from.
     * @param startPeg   the Peg where the Disk was originally picked up from
     */
    void drop(Peg startPeg) {
        if (!checkMove()) {
            startPeg.move(this);
            startPeg.drop(startPeg);
        }
        else {
            if (n == 1) {
                disks[n - 1].r.bottom = panelHeight;
                disks[n - 1].r.top = panelHeight - GamePanel.dpToPx(DISK_HEIGHT_DP);
            } else {
                disks[n - 1].r.bottom = disks[n - 2].r.top;
                disks[n - 1].r.top = disks[n - 1].r.bottom - GamePanel.dpToPx(DISK_HEIGHT_DP);
            }
        }
    }

    /**
     * Check if a move complies with the rules.
     * @return   whether the top Disk is smaller than the Disk directly below it
     */
    boolean checkMove() {
        return (n <= 1 || disks[n-2].getWidth() > disks[n-1].getWidth());
    }

    /**
     * Get the size of the Peg.
     * @return   n: the number of Disks on the Peg
     */
    int getSize() {
        return n;
    }

    /**
     * @return Disk[] disks - the array of disks
     */
    Disk[] getDisks() {
        return disks;
    }

    /**
     * Set the number of disks
     * @param n  the number of disks
     */
    void setSize(int n) {
        this.n = n;
    }

    /**
     * Re-initialize the disks in the disk array
     * @param disks  the array of disks from previous game
     */
    void setDisks(Disk[] disks) {
        for (int i = 0; i < n; i++) {
            this.disks[i] = new Disk(disks[i].getWidth(), disks[i].getHeight(), dst.centerX(), dst.bottom - GamePanel.dpToPx(DISK_HEIGHT_DP) * i, disks[i].getColor());
        }
    }

    void resetDisks() {
        disks[0].resize(dst.bottom, dst.bottom + GamePanel.dpToPx(DISK_HEIGHT_DP));
        for (int i = 1; i < n; i++) {
            disks[i].resize(disks[i-1].r.top, disks[i-1].r.top + GamePanel.dpToPx(DISK_HEIGHT_DP));
        }
    }
}
