package com.murach.hangtest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class GameSelectActivity extends AppCompatActivity implements View.OnClickListener {
    private Button hangButton;
    private Button c4Button;
    private Button sSButton;
    private Button settingsButton;
    private Button aboutButton;
    private Button msButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);
        hangButton = (Button) findViewById(R.id.buttonHang);
        c4Button = (Button) findViewById(R.id.buttonC4);
        sSButton = (Button) findViewById(R.id.buttonSS);
        settingsButton = (Button) findViewById(R.id.buttonSettings);
        aboutButton = (Button) findViewById(R.id.buttonAbout);
        msButton = (Button) findViewById(R.id.buttonMS);

        msButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
        c4Button.setOnClickListener(this);
        hangButton.setOnClickListener(this);
        sSButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        if (v.getId() == R.id.buttonHang) {
            intent = new Intent(this, HangmanActivity.class);
        } else if (v.getId() == R.id.buttonSS) {
            intent = new Intent(this, SimonSaysActivity.class);
            //startActivity(intent);
        } else if (v.getId() == R.id.buttonC4) {
            intent = new Intent(this, ConnectFourActivity.class);
            //startActivity(intent);
        } else if (v.getId() == R.id.buttonSettings) {
            intent = new Intent(this, SettingsActivity.class);
            //startActivity(intent);
        } else if (v.getId() == R.id.buttonAbout) {
            intent = new Intent(this, AboutActivity.class);
            //startActivity(intent);
        } else if (v.getId() == R.id.buttonMS) {
            intent = new Intent(this, MineActivity.class);
            //startActivity(intent);
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            } else {
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.d("ac2", "error starting activity intent");
        }
    }
}