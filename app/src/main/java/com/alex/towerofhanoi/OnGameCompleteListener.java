package com.alex.towerofhanoi;

/**
 * Created by Alex on 12/10/2017.
 *
 * Custom listener to inform GameActivity when the puzzle has been solved.
 */

interface OnGameCompleteListener {
    void onGameComplete(boolean isOptimal, int moves, int numDisks);
}
