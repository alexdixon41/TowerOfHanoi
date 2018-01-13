package com.alex.towerofhanoi;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

class Disk {
    private Paint paint;
    Rect r;
    private int width;
    private int height;
    private int color;

    Disk(int w, int h, int center, int bottom, int color) {
        width = w;
        height = h;
        this.color = color;
        r = new Rect(center - w / 2, bottom - height, center + w / 2, bottom);
        paint = new Paint();
        paint.setColor(color);
    }

    void draw(Canvas canvas) {
        canvas.drawRect(r, paint);
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    int getColor() {
        return color;
    }

    void resize(int bottom, int top) {
        this.r = new Rect(r.left, top, r.right, bottom);
    }
}
