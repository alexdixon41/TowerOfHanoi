package com.alex.towerofhanoi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.widget.Toast;

public class BackgroundPicker extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_picker);

        final GridLayout gridLayout = findViewById(R.id.choice_grid);
        gridLayout.post(new Runnable() {

            @Override
            public void run() {
                GridLayout.LayoutParams mParams;

                for (int i = 0; i < gridLayout.getChildCount(); i++) {
                    mParams = (GridLayout.LayoutParams) gridLayout.getChildAt(i).getLayoutParams();
                    mParams.height = gridLayout.getChildAt(i).getWidth();
                    gridLayout.getChildAt(i).setLayoutParams(mParams);
                }
                gridLayout.postInvalidate();
            }
        });

    }

    public void setBackgroundColor(View v) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("background_color", v.getTag().toString()).apply();
        Toast.makeText(this, R.string.background_success, Toast.LENGTH_SHORT).show();
        this.finish();
    }


    // Set background image
    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String path;
            if (selectedImage != null)
                path = selectedImage.getEncodedPath();
            else
                path = null;
            if (path != null) {
                prefs = PreferenceManager.getDefaultSharedPreferences(this);
                prefs.edit().putString("background_path", selectedImage.toString()).apply();
            }
            Toast.makeText(this, R.string.background_success, Toast.LENGTH_SHORT).show();
            this.finish();
        }
        else
            Toast.makeText(this, R.string.background_failure, Toast.LENGTH_SHORT).show();
    }

    public void setImageBackground(View v) {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19){
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 0);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, 0);
        }
    }*/
}
