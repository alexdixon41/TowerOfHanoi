package com.alex.towerofhanoi;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView moveDisplay;
    FrameLayout fullScreenContent;
    GamePanel gamePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        fullScreenContent = (FrameLayout)findViewById(R.id.fullscreen_content);
        gamePanel = new GamePanel(this);
        fullScreenContent.addView(gamePanel);

        moveDisplay = (TextView)findViewById(R.id.move_display);
        updateText(0);
    }

    public void updateText(final int moves) {
        moveDisplay.setText(String.format(getResources().getString(R.string.moves), moves));
    }

}
