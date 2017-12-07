package com.alex.towerofhanoi;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Alex on 10/11/2017.
 */

public class Disk {
    int[] colors;
    Paint paint;
    Rect r;
    private static final int DISK_HEIGHT = 25;
    private int width;

    Disk(int w, int center, int bottom, int color) {
        width = w;
        r = new Rect(center - w / 2, bottom - DISK_HEIGHT, center + w / 2, bottom);
        paint = new Paint();
        paint.setColor(color);
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(r, paint);
    }

    static public int getHeight() {
        return DISK_HEIGHT;
    }

    public int getWidth() {
        return width;
    }
}
