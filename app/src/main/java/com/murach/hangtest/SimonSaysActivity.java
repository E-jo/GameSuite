package com.murach.hangtest;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class SimonSaysActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {
    private TextView tvRed;
    private TextView tvGreen;
    private TextView tvYel;
    private TextView tvBlue;
    //private TextView tvSequence;
    private TextView tvWin;
    private TextView tvLoss;
    private TextView tvStreak;
    private Button btnSimon;
    private Button btnGen;
    private Button btnMelody;
    private ArrayList<String> sequence1 = new ArrayList<>();
    private ArrayList<String> sequence2 = new ArrayList<>();
    private int player;
    private int count = 1;
    private int win;
    private int loss;
    private int streak;
    private boolean clearing = false;
    private SharedPreferences savedValues;
    private SharedPreferences prefs;
    private int speed;
    private boolean playTones;
    SoundPool notes;
    int noteA;
    int noteC;
    int noteD;
    int noteE;

    private void loadTones() {
        notes = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        switch (speed) {
            case 100:
                noteA = notes.load(this, R.raw.note_a_tenth_sec, 1);
                noteC = notes.load(this, R.raw.note_c_tenth_sec, 1);
                noteD = notes.load(this, R.raw.note_d_tenth_sec, 1);
                noteE = notes.load(this, R.raw.note_e_tenth_sec, 1);
                break;
            case 250:
                noteA = notes.load(this, R.raw.note_a_quarter_sec, 1);
                noteC = notes.load(this, R.raw.note_c_quarter_sec, 1);
                noteD = notes.load(this, R.raw.note_d_quarter_sec, 1);
                noteE = notes.load(this, R.raw.note_e_quarter_sec, 1);
                break;
            case 500:
                noteA = notes.load(this, R.raw.note_a_half_sec, 1);
                noteC = notes.load(this, R.raw.note_c_half_sec, 1);
                noteD = notes.load(this, R.raw.note_d_half_sec, 1);
                noteE = notes.load(this, R.raw.note_e_half_sec, 1);
                break;
            case 750:
                noteA = notes.load(this, R.raw.note_a_three_quarters_sec, 1);
                noteC = notes.load(this, R.raw.note_c_three_quarters_sec, 1);
                noteD = notes.load(this, R.raw.note_d_three_quarters_sec, 1);
                noteE = notes.load(this, R.raw.note_e_three_quarters_sec, 1);
                break;
            default:
                noteA = notes.load(this, R.raw.note_a_full_sec, 1);
                noteC = notes.load(this, R.raw.note_c_full_sec, 1);
                noteD = notes.load(this, R.raw.note_d_full_sec, 1);
                noteE = notes.load(this, R.raw.note_e_full_sec, 1);
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simon_says);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        speed = Integer.parseInt(prefs.getString("pref_ss_speed", "1000"));
        playTones = prefs.getBoolean("pref_ss_sound", true);

        if (playTones) {
            loadTones();
        }

        tvRed = (TextView) findViewById(R.id.tvRed);
        tvGreen = (TextView) findViewById(R.id.tvGreen);
        tvYel = (TextView) findViewById(R.id.tvYel);
        tvBlue = (TextView) findViewById(R.id.tvBlue);
        //tvSequence = (TextView) findViewById(R.id.tvSequence);
        tvWin = (TextView) findViewById(R.id.tvWin);
        tvLoss = (TextView) findViewById(R.id.tvLoss);
        tvStreak = (TextView) findViewById(R.id.tvStreak);
        btnSimon = (Button) findViewById(R.id.btnSimon);
        btnGen = (Button) findViewById(R.id.btnGen);
        btnMelody = (Button) findViewById(R.id.melody);
        tvRed.setOnTouchListener(this);
        tvBlue.setOnTouchListener(this);
        tvYel.setOnTouchListener(this);
        tvGreen.setOnTouchListener(this);
        tvYel.setOnClickListener(this);
        tvRed.setOnClickListener(this);
        tvGreen.setOnClickListener(this);
        tvBlue.setOnClickListener(this);
        btnSimon.setOnClickListener(this);
        btnGen.setOnClickListener(this);
        btnMelody.setOnClickListener(this);
        player = 1;
        //get saved values
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
        win = savedValues.getInt("win", 0);
        loss = savedValues.getInt("loss", 0);
        streak = savedValues.getInt("streak", 0);
        count = savedValues.getInt("count", 1);
        tvWin.setText("Correct: " + win);
        tvLoss.setText("Incorrect: " + loss);
        tvStreak.setText("Longest streak: " + streak);
        //tvSequence.setText(count);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.simonsays, menu);
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
            case R.id.menu_mine:
                intent = new Intent(getApplicationContext(), MineActivity.class);
                break;
            case R.id.menu_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                break;
            case R.id.menu_about:
                intent = new Intent(getApplicationContext(), AboutActivity.class);
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

    @Override
    public void onPause() {
        super.onPause();
        if (notes != null) {
            notes.release();
        }
        Editor editor = savedValues.edit();
        editor.putInt("win", win);
        editor.putInt("loss", loss);
        editor.putInt("streak", streak);
        editor.putInt("count", count);
        editor.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        speed = Integer.parseInt(prefs.getString("pref_ss_speed", "4"));
        playTones = prefs.getBoolean("pref_ss_sound", true);
        if (playTones) {
            loadTones();
        }
        win = savedValues.getInt("win", 0);
        loss = savedValues.getInt("loss", 0);
        streak = savedValues.getInt("streak", 0);
        count = savedValues.getInt("count", 1);
        tvWin.setText("Correct: " + win);
        tvLoss.setText("Incorrect: " + loss);
        tvStreak.setText("Longest streak: " + streak);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();

        try {
            switch (v.getId()) {
                case R.id.tvRed:
                    if (action == MotionEvent.ACTION_DOWN) {
                        if (playTones) {
                            notes.play(noteA, 1, 1, 1, 0, 1);
                        }
                        /*final MediaPlayer noteA;
                        switch (speed) {
                            case 100:
                                noteA = MediaPlayer.create(this, R.raw.note_a_tenth_sec);
                                break;
                            case 250:
                                noteA = MediaPlayer.create(this, R.raw.note_a_quarter_sec);
                                break;
                            case 500:
                                noteA = MediaPlayer.create(this, R.raw.note_a_half_sec);
                                break;
                            case 750:
                                noteA = MediaPlayer.create(this, R.raw.note_a_three_quarters_sec);
                                break;
                            default:
                                noteA = MediaPlayer.create(this, R.raw.note_a_full_sec);
                        }
                        noteA.start();
                        noteA.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                noteA.release();
                            }
                        }); */
                        tvRed.setBackgroundResource(R.drawable.ssredborder);
                    } else if (action == MotionEvent.ACTION_UP) {
                        tvRed.setBackgroundResource(R.drawable.ssred);
                        if (player == 1) {
                            sequence1.add("Red");
                            //tvSequence.setText(sequence1.toString());
                        } else {
                            sequence2.add("Red");
                            //tvSequence.setText(sequence2.toString());
                        }
                    }
                    return true;
                case R.id.tvGreen:
                    if (action == MotionEvent.ACTION_DOWN) {
                        if (playTones) {
                            notes.play(noteE, 1, 1, 1, 0, 1);
                        }
                        /*final MediaPlayer noteE;
                        switch (speed) {
                            case 100:
                                noteE = MediaPlayer.create(this, R.raw.note_e_tenth_sec);
                                break;
                            case 250:
                                noteE = MediaPlayer.create(this, R.raw.note_e_quarter_sec);
                                break;
                            case 500:
                                noteE = MediaPlayer.create(this, R.raw.note_e_half_sec);
                                break;
                            case 750:
                                noteE = MediaPlayer.create(this, R.raw.note_e_three_quarters_sec);
                                break;
                            default:
                                noteE = MediaPlayer.create(this, R.raw.note_e_full_sec);
                        }
                        noteE.start();
                        noteE.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                noteE.release();
                            }
                        }); */
                        tvGreen.setBackgroundResource(R.drawable.ssgreenborder);
                    } else if (action == MotionEvent.ACTION_UP) {
                        tvGreen.setBackgroundResource(R.drawable.ssgreen);
                        if (player == 1) {
                            sequence1.add("Green");
                            //tvSequence.setText(sequence1.toString());
                        } else {
                            sequence2.add("Green");
                            //tvSequence.setText(sequence2.toString());
                        }
                    }
                    return true;
                case R.id.tvYel:
                    if (action == MotionEvent.ACTION_DOWN) {
                        if (playTones) {
                            notes.play(noteC, 1, 1, 1, 0, 1);
                        }
                        /*final MediaPlayer noteC;
                        switch (speed) {
                            case 100:
                                noteC = MediaPlayer.create(this, R.raw.note_c_tenth_sec);
                                break;
                            case 250:
                                noteC = MediaPlayer.create(this, R.raw.note_c_quarter_sec);
                                break;
                            case 500:
                                noteC = MediaPlayer.create(this, R.raw.note_c_half_sec);
                                break;
                            case 750:
                                noteC = MediaPlayer.create(this, R.raw.note_c_three_quarters_sec);
                                break;
                            default:
                                noteC = MediaPlayer.create(this, R.raw.note_c_full_sec);
                        }
                        noteC.start();
                        noteC.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                noteC.release();
                            }
                        }); */
                        tvYel.setBackgroundResource(R.drawable.ssyelborder);
                    } else if (action == MotionEvent.ACTION_UP) {
                        tvYel.setBackgroundResource(R.drawable.ssyel);
                        if (player == 1) {
                            sequence1.add("Yellow");
                            //tvSequence.setText(sequence1.toString());
                        } else {
                            sequence2.add("Yellow");
                            //tvSequence.setText(sequence2.toString());
                        }
                    }
                    return true;
                case R.id.tvBlue:
                    if (action == MotionEvent.ACTION_DOWN) {
                        if (playTones) {
                            notes.play(noteD, 1, 1, 1, 0, 1);
                        }
                        /*final MediaPlayer noteD;
                        switch (speed) {
                            case 100:
                                noteD = MediaPlayer.create(this, R.raw.note_d_tenth_sec);
                                break;
                            case 250:
                                noteD = MediaPlayer.create(this, R.raw.note_d_quarter_sec);
                                break;
                            case 500:
                                noteD = MediaPlayer.create(this, R.raw.note_d_half_sec);
                                break;
                            case 750:
                                noteD = MediaPlayer.create(this, R.raw.note_d_three_quarters_sec);
                                break;
                            default:
                                noteD = MediaPlayer.create(this, R.raw.note_d_full_sec);
                        }
                        noteD.start();
                        noteD.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                noteD.release();
                            }
                        }); */
                        tvBlue.setBackgroundResource(R.drawable.ssblueborder);
                    } else if (action == MotionEvent.ACTION_UP) {
                        tvBlue.setBackgroundResource(R.drawable.ssblue);
                        if (player == 1) {
                            sequence1.add("Blue");
                            //tvSequence.setText(sequence1.toString());
                        } else {
                            sequence2.add("Blue");
                            //tvSequence.setText(sequence2.toString());
                        }
                    }
                    return true;
            }
        } catch (Exception e) {
            //Toast.makeText(this, "Error. System requirements not met. Please select a slower speed.", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void generateSeq() {
        //disable all buttons to prevent user interrupting the sequence
        tvRed.setEnabled(false);
        tvYel.setEnabled(false);
        tvBlue.setEnabled(false);
        tvGreen.setEnabled(false);
        btnSimon.setEnabled(false);
        btnGen.setEnabled(false);
        clearing = false;
        player = 1;
        sequence1.clear();
        sequence2.clear();

        new CountDownTimer(count * speed * 2, speed) {
            public void onTick(long millisUntilFinished) {
                if (!clearing) {
                    //highlight one color with a border and add to sequence
                    int rNum = new Random().nextInt(4);
                    switch (rNum) {
                        case 0:
                            tvRed.setBackgroundResource(R.drawable.ssredborder);
                            tvRed.callOnClick();
                            break;
                        case 1:
                            tvYel.setBackgroundResource(R.drawable.ssyelborder);
                            tvYel.callOnClick();
                            break;
                        case 2:
                            tvGreen.setBackgroundResource(R.drawable.ssgreenborder);
                            tvGreen.callOnClick();
                            break;
                        case 3:
                            tvBlue.setBackgroundResource(R.drawable.ssblueborder);
                            tvBlue.callOnClick();
                            break;
                    }
                    clearing = true;
                } else {
                    //clear borders
                    tvBlue.setBackgroundResource(R.drawable.ssblue);
                    tvRed.setBackgroundResource(R.drawable.ssred);
                    tvYel.setBackgroundResource(R.drawable.ssyel);
                    tvGreen.setBackgroundResource(R.drawable.ssgreen);
                    clearing = false;
                }
            }
            public void onFinish() {
                //ensure cleared borders
                tvBlue.setBackgroundResource(R.drawable.ssblue);
                tvRed.setBackgroundResource(R.drawable.ssred);
                tvYel.setBackgroundResource(R.drawable.ssyel);
                tvGreen.setBackgroundResource(R.drawable.ssgreen);
                btnSimon.callOnClick();
                //reenable all buttons
                tvRed.setEnabled(true);
                tvYel.setEnabled(true);
                tvBlue.setEnabled(true);
                tvGreen.setEnabled(true);
                btnSimon.setEnabled(true);
                btnGen.setEnabled(true);
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.melody) {
            Log.d("Melody", "Started");
            Random r = new Random();
            int reps = 5;
            for (int i = 0; i < reps; i++) {
                int note = 0;
                int noteChoice = r.nextInt(4);
                int loop = r.nextInt(2);
                switch (noteChoice) {
                    case 0:
                        notes.play(noteA, 1,1,1, loop, 1);
                        Log.d("Note", String.valueOf(noteChoice));
                        break;
                    case 1:
                        notes.play(noteC, 1,1,1, loop, 1);
                        Log.d("Note", String.valueOf(noteChoice));
                        break;
                    case 2:
                        notes.play(noteD, 1,1,1, loop, 1);
                        Log.d("Note", String.valueOf(noteChoice));
                        break;
                    case 3:
                        notes.play(noteE, 1,1,1, loop, 1);
                        Log.d("Note", String.valueOf(noteChoice));
                        break;
                }
            }
        }
        if (view.getId() == R.id.btnSimon) {
            if (player == 2) {
                if (sequence2.equals(sequence1)) {
                    Toast toast = Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                    streak = (count > streak ? count: streak);
                    count++;
                    win++;
                } else {
                    Toast toast = Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();                    loss++;
                    count = 1;
                }
                tvWin.setText("Correct: " + win);
                tvLoss.setText("Incorrect: " + loss);
                tvStreak.setText("Longest streak: " + streak);
                player = 1;
                sequence1.clear();
                sequence2.clear();
                //tvSequence.setText(count);
                //tvSequence.setText(sequence1.toString());
            } else {
                player = 2;
                //tvSequence.setText(count);
                //tvSequence.setText(sequence2.toString());
            }
        } else if (view.getId() == R.id.btnGen) {
            generateSeq();
        }
        try {
            if (view.getId() == R.id.tvYel) {
                if (playTones) {
                    notes.play(noteC, 1, 1, 1, 0, 1);
                }
                /*
                final MediaPlayer noteC;
                switch (speed) {
                    case 100:
                        noteC = MediaPlayer.create(this, R.raw.note_c_tenth_sec);
                        break;
                    case 250:
                        noteC = MediaPlayer.create(this, R.raw.note_c_quarter_sec);
                        break;
                    case 500:
                        noteC = MediaPlayer.create(this, R.raw.note_c_half_sec);
                        break;
                    case 750:
                        noteC = MediaPlayer.create(this, R.raw.note_c_three_quarters_sec);
                        break;
                    default:
                        noteC = MediaPlayer.create(this, R.raw.note_c_full_sec);
                }
                noteC.start();
                noteC.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        noteC.release();
                    }
                }); */
                if (player == 1) {
                    sequence1.add("Yellow");
                    //tvSequence.setText(sequence1.toString());
                } else {
                    sequence2.add("Yellow");
                    //tvSequence.setText(sequence2.toString());
                }
            } else if (view.getId() == R.id.tvBlue) {
                if (playTones) {
                    notes.play(noteD, 1, 1, 1, 0, 1);
                }
                /*final MediaPlayer noteD;
                switch (speed) {
                    case 100:
                        noteD = MediaPlayer.create(this, R.raw.note_d_tenth_sec);
                        break;
                    case 250:
                        noteD = MediaPlayer.create(this, R.raw.note_d_quarter_sec);
                        break;
                    case 500:
                        noteD = MediaPlayer.create(this, R.raw.note_d_half_sec);
                        break;
                    case 750:
                        noteD = MediaPlayer.create(this, R.raw.note_d_three_quarters_sec);
                        break;
                    default:
                        noteD = MediaPlayer.create(this, R.raw.note_d_full_sec);
                }
                noteD.start();
                noteD.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        noteD.release();
                    }
                });*/
                if (player == 1) {
                    sequence1.add("Blue");
                    //tvSequence.setText(sequence1.toString());
                } else {
                    sequence2.add("Blue");
                    //tvSequence.setText(sequence2.toString());
                }
            } else if (view.getId() == R.id.tvRed) {
                if (playTones) {
                    notes.play(noteA, 1, 1, 1, 0, 1);
                }
                /*final MediaPlayer noteA;
                switch (speed) {
                    case 100:
                        noteA = MediaPlayer.create(this, R.raw.note_a_tenth_sec);
                        break;
                    case 250:
                        noteA = MediaPlayer.create(this, R.raw.note_a_quarter_sec);
                        break;
                    case 500:
                        noteA = MediaPlayer.create(this, R.raw.note_a_half_sec);
                        break;
                    case 750:
                        noteA = MediaPlayer.create(this, R.raw.note_a_three_quarters_sec);
                        break;
                    default:
                        noteA = MediaPlayer.create(this, R.raw.note_a_full_sec);
                }
                noteA.start();
                noteA.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        noteA.release();
                    }
                });*/
                if (player == 1) {
                    sequence1.add("Red");
                    //tvSequence.setText(sequence1.toString());
                } else {
                    sequence2.add("Red");
                    //tvSequence.setText(sequence2.toString());
                }
            } else if (view.getId() == R.id.tvGreen) {
                if (playTones) {
                    notes.play(noteE, 1, 1, 1, 0, 1);
                }
                /*
                final MediaPlayer noteE;
                switch (speed) {
                    case 100:
                        noteE = MediaPlayer.create(this, R.raw.note_e_tenth_sec);
                        break;
                    case 250:
                        noteE = MediaPlayer.create(this, R.raw.note_e_quarter_sec);
                        break;
                    case 500:
                        noteE = MediaPlayer.create(this, R.raw.note_e_half_sec);
                        break;
                    case 750:
                        noteE = MediaPlayer.create(this, R.raw.note_e_three_quarters_sec);
                        break;
                    default:
                        noteE = MediaPlayer.create(this, R.raw.note_e_full_sec);
                }
                noteE.start();
                noteE.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        noteE.release();
                    }
                }); */
                if (player == 1) {
                    sequence1.add("Green");
                    //tvSequence.setText(sequence1.toString());
                } else {
                    sequence2.add("Green");
                    //tvSequence.setText(sequence2.toString());
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}