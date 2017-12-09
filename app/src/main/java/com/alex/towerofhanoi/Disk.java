package com.alex.towerofhanoi;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Alex on 10/11/2017.
 */

class Disk {
    private Paint paint;
    Rect r;
    private int width, height;

    Disk(int w, int h, int center, int bottom, int color) {
        width = w;
        height = h;
        r = new Rect(center - w / 2, bottom - height, center + w / 2, bottom);
        paint = new Paint();
        paint.setColor(color);
    }

    void draw(Canvas canvas) {
        canvas.drawRect(r, paint);
    }

    int getHeight() {
        return height;
    }

    int getWidth() {
        return width;
    }
}
