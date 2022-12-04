package com.murach.hangtest;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        boolean d = false;
        switch (item.getItemId()) {
            case R.id.menu_hang:
                intent = new Intent(getApplicationContext(), HangmanActivity.class);
                break;
            case R.id.menu_c4:
                intent = new Intent(getApplicationContext(), ConnectFourActivity.class);
                break;
            case R.id.menu_simon:
                intent = new Intent(getApplicationContext(), SimonSaysActivity.class);
                break;
            case R.id.menu_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                break;
            default:
                d = true;
        }
        if (d) {
            return super.onOptionsItemSelected(item);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
        return true;
    }

}
