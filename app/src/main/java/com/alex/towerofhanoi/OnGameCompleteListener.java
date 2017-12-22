package com.alex.towerofhanoi;

/**
 * Created by Alex on 12/10/2017.
 */

public interface OnGameCompleteListener {
    void onGameComplete(boolean isOptimal, int moves, int numDisks);
}
