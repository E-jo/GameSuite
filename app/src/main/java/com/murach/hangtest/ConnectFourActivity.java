package com.murach.hangtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.TransitionInflater;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectFourActivity extends AppCompatActivity implements View.OnClickListener {
    private int redWins;
    private int yelWins;
    private TextView[][] textViews = new TextView[8][8];
    private String[][] boardState = new String[8][8];
    private static final String TAG = "C4Activity";
    private String player;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button7;
    private Button button8;
    private TextView tvRedWins;
    private TextView tvYelWins;
    private TextView tvTurn;
    private int col;
    private int row;
    private String winner;
    private SharedPreferences savedValues;
    private SharedPreferences prefs;
    private int speed;
    private boolean playSound;
    SoundPool dropFx = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    int dropRed;
    int dropYel;
    private String boardStateStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_four);
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);
        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(this);
        button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(this);
        button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(this);
        button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(this);
        button7 = (Button) findViewById(R.id.button7);
        button7.setOnClickListener(this);
        button8 = (Button) findViewById(R.id.button8);
        button8.setOnClickListener(this);
        tvRedWins = (TextView) findViewById(R.id.tvRedWins);
        tvYelWins = (TextView) findViewById(R.id.tvYelWins);
        tvTurn = (TextView) findViewById(R.id.tvTurn);
        player = "red";

        //get saved values
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        speed = Integer.parseInt(prefs.getString("pref_c4_speed", "75"));
        playSound = prefs.getBoolean("pref_c4_sound", true);
        dropRed = dropFx.load(this, R.raw.drop_red, 1);
        dropYel = dropFx.load(this, R.raw.drop_yel, 1);

        //load all the textViews into one array
        //initialize parallel string array with "empty" spaces
        int count = 1;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String tvID = "textView" + count;
                count++;
                int resID = getResources().getIdentifier(tvID, "id", getPackageName());
                textViews[i][j] = ((TextView) findViewById(resID));
                boardState[i][j] = "empty";
                //for testing, display board state
                //textViews[i][j].setText(boardState[i][j]);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.connectfour, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        boolean d = false;
        switch (item.getItemId()) {
            case R.id.menu_simon:
                intent = new Intent(getApplicationContext(), SimonSaysActivity.class);
                break;
            case R.id.menu_hang:
                intent = new Intent(getApplicationContext(), HangmanActivity.class);
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
        SharedPreferences.Editor editor = savedValues.edit();
        editor.putInt("redWins", redWins);
        editor.putInt("yelWins", yelWins);
        editor.putString("player", player);

        //put the boardState array into one long string for storage
        boardStateStr = "";
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardStateStr = boardStateStr + boardState[i][j] + " ";
            }
        }
        editor.putString("boardState", boardStateStr);
        Log.d(TAG, boardStateStr);
        editor.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        speed = Integer.parseInt(prefs.getString("pref_c4_speed", "75"));
        playSound = prefs.getBoolean("pref_c4_sound", true);

        //retrieve current player and set turn box
        player = savedValues.getString("player", "red");
        if (player.equalsIgnoreCase("red")) {
            tvTurn.setBackgroundResource(R.drawable.border2);
        } else {
            tvTurn.setBackgroundResource(R.drawable.border3);
        }

        //get board state from string
        boardStateStr = savedValues.getString("boardState", null);

        if (boardStateStr != null) {
            //make a one-dimensional temp string array to hold board state
            int index = 0;
            String[] tempBoardState = boardStateStr.split(" ");

            //transfer values back into two-dimensional boardstate string array and refresh screen
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    boardState[i][j] = tempBoardState[index];
                    //textViews[i][j].setText(boardState[i][j]);
                    if (boardState[i][j].equalsIgnoreCase("empty")) {
                        textViews[i][j].setBackgroundResource(R.drawable.border);
                    } else if (boardState[i][j].equalsIgnoreCase("red")) {
                        textViews[i][j].setBackgroundResource(R.drawable.border2);
                    } else if (boardState[i][j].equalsIgnoreCase("yellow")) {
                        textViews[i][j].setBackgroundResource(R.drawable.border3);
                    }
                    index++;
                }
            }

            //retrieve and set win totals
            redWins = savedValues.getInt("redWins", 0);
            yelWins = savedValues.getInt("yelWins", 0);
            tvRedWins.setText(redWins + " wins");
            tvYelWins.setText(yelWins + " wins");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                makeMove(0);
                break;
            case R.id.button2:
                makeMove(1);
                break;
            case R.id.button3:
                makeMove(2);
                break;
            case R.id.button4:
                makeMove(3);
                break;
            case R.id.button5:
                makeMove(4);
                break;
            case R.id.button6:
                makeMove(5);
                break;
            case R.id.button7:
                makeMove(6);
                break;
            case R.id.button8:
                makeMove(7);
                break;
        }
    }

    private void clearScreen() {
        //disable buttons so user can't interrupt animation
        button1.setEnabled(false);
        button2.setEnabled(false);
        button3.setEnabled(false);
        button4.setEnabled(false);
        button5.setEnabled(false);
        button6.setEnabled(false);
        button7.setEnabled(false);
        button8.setEnabled(false);

        new CountDownTimer(2100, 300) {
            //flag to regulate clearing and drawing cycles
            boolean clear = false;
            //to avoid overlapping sound effects
            boolean firstTime = true;
            public void onTick(long millisUntilFinished) {
                if (clear) {
                    for (int y = 0; y < 8; y++) {
                        for (int j = 0; j < 8; j++) {
                            textViews[y][j].setBackgroundResource(R.drawable.border);
                        }
                    }
                    clear = false;
                } else {
                    //player has already been cycled over at this point so this is inverted
                    if (playSound) {
                        int fX = (player.equalsIgnoreCase("yellow") ? dropRed : dropYel);
                        if (!firstTime) {
                            dropFx.play(fX, 1, 1, 1, 0, .91875f);
                        } else {
                            firstTime = false;
                        }
                    }
                    if (winner == "red") {
                        for (int y = 0; y < 8; y++) {
                            for (int j = 0; j < 8; j++) {
                                textViews[y][j].setBackgroundResource(R.drawable.border2);
                            }
                        }
                    } else if (winner == "yellow") {
                        for (int y = 0; y < 8; y++) {
                            for (int j = 0; j < 8; j++) {
                                textViews[y][j].setBackgroundResource(R.drawable.border3);
                            }
                        }
                    } clear = true;
                }
            }

            public  void onFinish() {
                if (winner == "red") {
                    redWins++;
                } else if (winner == "yellow") {
                    yelWins++;
                }
                tvRedWins.setText(redWins + " wins");
                tvYelWins.setText(yelWins + " wins");
                for (int y = 0; y < 8; y++) {
                    for (int j = 0; j < 8; j++) {
                        textViews[y][j].setBackgroundResource(R.drawable.border);
                        boardState[y][j] = "empty";
                        //textViews[y][j].setText(boardState[y][j]);
                    }
                }
                //enable buttons
                button1.setEnabled(true);
                button2.setEnabled(true);
                button3.setEnabled(true);
                button4.setEnabled(true);
                button5.setEnabled(true);
                button6.setEnabled(true);
                button7.setEnabled(true);
                button8.setEnabled(true);
            }
        }.start();
    }

    //find an open space in the selected column
    private void makeMove(int x) {
        col = x;
        for (int i = 7; i >= 0; i--) {
            if (boardState[i][col].equalsIgnoreCase("empty")) {
                boardState[i][col] = player;
                //textViews[i][col].setText(boardState[i][col]);
                row = i;
                if (speed != 0) {
                    animateMove();
                } else {
                    textViews[i][col].setBackgroundResource((player.equalsIgnoreCase("red") ? R.drawable.border2 : R.drawable.border3));
                    afterMove();
                }
                return;
            }
        }
    }

    private void animateMove() {
        //disable buttons so user can't interrupt animation
        button1.setClickable(false);
        button2.setClickable(false);
        button3.setClickable(false);
        button4.setClickable(false);
        button5.setClickable(false);
        button6.setClickable(false);
        button7.setClickable(false);
        button8.setClickable(false);

        //Toast.makeText(this, "animate row " + row + " col " + col, Toast.LENGTH_SHORT).show();
        new CountDownTimer(row * speed, speed) {
            boolean topRow = true;
            int i = 0;

            public void onTick(long millisUntilFinished) {
                //clear box above current box unless we are in the top row
                if (!topRow) {
                    textViews[i-1][col].setBackgroundResource(R.drawable.border);
                }
                textViews[i][col].setBackgroundResource((player.equalsIgnoreCase("red") ? R.drawable.border2 : R.drawable.border3));
                topRow = false;
                i++;
            }

            public  void onFinish() {
                //delete upper row if not base row
                if (row != 0) {
                    textViews[i - 1][col].setBackgroundResource(R.drawable.border);
                }
                //textViews[i][col].setBackgroundResource((player.equalsIgnoreCase("red") ? R.drawable.border2 : R.drawable.border3));
                //correct animation mistakes (the necessity of this became clear, the animation
                // would sometimes mess up, so this corrects it)
                for (int y = 0; y < 8; y++) {
                    for (int j = 0; j < 8; j++) {
                        if (boardState[y][j].equalsIgnoreCase("empty")) {
                            textViews[y][j].setBackgroundResource(R.drawable.border);
                        } else if (boardState[y][j].equalsIgnoreCase("red")) {
                            textViews[y][j].setBackgroundResource(R.drawable.border2);
                        } else if (boardState[y][j].equalsIgnoreCase("yellow")) {
                            textViews[y][j].setBackgroundResource(R.drawable.border3);
                        }
                    }
                }
                afterMove();
                //enable buttons
                button1.setClickable(true);
                button2.setClickable(true);
                button3.setClickable(true);
                button4.setClickable(true);
                button5.setClickable(true);
                button6.setClickable(true);
                button7.setClickable(true);
                button8.setClickable(true);
            }
        }.start();
    }

    private void afterMove() {
        //drop sfx
        if (playSound) {
            int fX = (player.equalsIgnoreCase("red") ? dropRed : dropYel);
            dropFx.play(fX, 1, 1, 1, 0, .91875f);
        }
        //check for draw
        if (checkFilled()) {
            //reset board
            for (int y = 0; y < 8; y++) {
                for (int j = 0; j < 8; j++) {
                    textViews[y][j].setBackgroundResource(R.drawable.border);
                    boardState[y][j] = "empty";
                    //textViews[y][j].setText(boardState[y][j]);
                }
            }
        } else {
            //check for win
            winner = checkWin();
            if (winner != null) {
                clearScreen();
            }
        }
        //switch player
        if (player.equalsIgnoreCase("red")) {
            player = "yellow";
            tvTurn.setBackgroundResource(R.drawable.border3);
        } else {
            player = "red";
            tvTurn.setBackgroundResource(R.drawable.border2);
        }
    }

    //check for filled board (draw)
    private boolean checkFilled() {
        //Toast.makeText(this, "checkFilled()", Toast.LENGTH_LONG).show();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (boardState[i][j].equalsIgnoreCase("empty")) {
                    //Toast.makeText(this, "empty found", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }
        Toast.makeText(this, "Draw!", Toast.LENGTH_LONG).show();
        return true;
    }

    private String checkWin() {
        int redCount;
        int yelCount;

        //check rows for connect-4
        for (int i = 0; i < 8; i++) {
            redCount = 0;
            yelCount = 0;
            for (int j = 0; j < 8; j++) {
                if (boardState[i][j].equalsIgnoreCase("red")) {
                    redCount++;
                    yelCount = 0;
                } else if (boardState[i][j].equalsIgnoreCase("yellow")) {
                    yelCount++;
                    redCount = 0;
                } else if (boardState[i][j].equalsIgnoreCase("empty")) {
                    yelCount = 0;
                    redCount = 0;
                }
                if (redCount == 4) {
                    return "red";
                } else if (yelCount == 4) {
                    return "yellow";
                }
            }
        }

        //check columns for connect-4
        for (int j = 0; j < 8; j++) {
            redCount = 0;
            yelCount = 0;
            for (int i = 0; i < 8; i++) {
                if (boardState[i][j].equalsIgnoreCase("red")) {
                    redCount++;
                    yelCount = 0;
                } else if (boardState[i][j].equalsIgnoreCase("yellow")) {
                    yelCount++;
                    redCount = 0;
                } else if (boardState[i][j].equalsIgnoreCase("empty")) {
                    yelCount = 0;
                    redCount = 0;
                }
                if (redCount == 4) {
                    return "red";
                } else if (yelCount == 4) {
                    return "yellow";
                }
            }
        }

        //check major diagonals for connect-4
        //longest
        int j = 0;
        redCount = 0;
        yelCount = 0;
        for (int i = 0; i < 8; i++) {
            if (boardState[i][j].equalsIgnoreCase("red")) {
                redCount++;
                yelCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("yellow")) {
                yelCount++;
                redCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("empty")) {
                yelCount = 0;
                redCount = 0;
            }
            if (redCount == 4) {
                return "red";
            } else if (yelCount == 4) {
                return "yellow";
            }
            j++;
        }
        //diagonals lower:

        //2nd longest major diagonal (lower)
        j = 0;
        redCount = 0;
        yelCount = 0;
        for (int i = 1; i < 8; i++) {
            if (boardState[i][j].equalsIgnoreCase("red")) {
                redCount++;
                yelCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("yellow")) {
                yelCount++;
                redCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("empty")) {
                yelCount = 0;
                redCount = 0;
            }
            if (redCount == 4) {
                return "red";
            } else if (yelCount == 4) {
                return "yellow";
            }
            j++;
        }

        //3rd longest major diagonal (lower)
        j = 0;
        redCount = 0;
        yelCount = 0;
        for (int i = 2; i < 8; i++) {
            if (boardState[i][j].equalsIgnoreCase("red")) {
                redCount++;
                yelCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("yellow")) {
                yelCount++;
                redCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("empty")) {
                yelCount = 0;
                redCount = 0;
            }
            if (redCount == 4) {
                return "red";
            } else if (yelCount == 4) {
                return "yellow";
            }
            j++;
        }

        //4th longest major diagonal (lower)
        j = 0;
        redCount = 0;
        yelCount = 0;
        for (int i = 3; i < 8; i++) {
            if (boardState[i][j].equalsIgnoreCase("red")) {
                redCount++;
                yelCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("yellow")) {
                yelCount++;
                redCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("empty")) {
                yelCount = 0;
                redCount = 0;
            }
            if (redCount == 4) {
                return "red";
            } else if (yelCount == 4) {
                return "yellow";
            }
            j++;
        }

        //5th longest major diagonal (lower))
        j = 0;
        redCount = 0;
        yelCount = 0;
        for (int i = 4; i < 8; i++) {
            if (boardState[i][j].equalsIgnoreCase("red")) {
                redCount++;
                yelCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("yellow")) {
                yelCount++;
                redCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("empty")) {
                yelCount = 0;
                redCount = 0;
            }
            if (redCount == 4) {
                return "red";
            } else if (yelCount == 4) {
                return "yellow";
            }
            j++;
        }
        //2nd longest major diagonal (upper)
        j = 0;
        redCount = 0;
        yelCount = 0;
        for (int i = 1; i < 8; i++) {
            if (boardState[j][i].equalsIgnoreCase("red")) {
                redCount++;
                yelCount = 0;
            } else if (boardState[j][i].equalsIgnoreCase("yellow")) {
                yelCount++;
                redCount = 0;
            } else if (boardState[j][i].equalsIgnoreCase("empty")) {
                yelCount = 0;
                redCount = 0;
            }
            if (redCount == 4) {
                return "red";
            } else if (yelCount == 4) {
                return "yellow";
            }
            j++;
        }

        //3rd longest major diagonal (upper)
        j = 0;
        redCount = 0;
        yelCount = 0;
        for (int i = 2; i < 8; i++) {
            if (boardState[j][i].equalsIgnoreCase("red")) {
                redCount++;
                yelCount = 0;
            } else if (boardState[j][i].equalsIgnoreCase("yellow")) {
                yelCount++;
                redCount = 0;
            } else if (boardState[j][i].equalsIgnoreCase("empty")) {
                yelCount = 0;
                redCount = 0;
            }
            if (redCount == 4) {
                return "red";
            } else if (yelCount == 4) {
                return "yellow";
            }
            j++;
        }

        //4th longest major diagonal (upper)
        j = 0;
        redCount = 0;
        yelCount = 0;
        for (int i = 3; i < 8; i++) {
            if (boardState[j][i].equalsIgnoreCase("red")) {
                redCount++;
                yelCount = 0;
            } else if (boardState[j][i].equalsIgnoreCase("yellow")) {
                yelCount++;
                redCount = 0;
            } else if (boardState[j][i].equalsIgnoreCase("empty")) {
                yelCount = 0;
                redCount = 0;
            }
            if (redCount == 4) {
                return "red";
            } else if (yelCount == 4) {
                return "yellow";
            }
            j++;
        }

        //5th longest major diagonal (upper))
        j = 0;
        redCount = 0;
        yelCount = 0;
        for (int i = 4; i < 8; i++) {
            if (boardState[j][i].equalsIgnoreCase("red")) {
                redCount++;
                yelCount = 0;
            } else if (boardState[j][i].equalsIgnoreCase("yellow")) {
                yelCount++;
                redCount = 0;
            } else if (boardState[j][i].equalsIgnoreCase("empty")) {
                yelCount = 0;
                redCount = 0;
            }
            if (redCount == 4) {
                return "red";
            } else if (yelCount == 4) {
                return "yellow";
            }
            j++;
        }

        //check longest minor diagonal
        j = 0;
        redCount = 0;
        yelCount = 0;
        for (int i = 7; i >= 0; i--) {
            if (boardState[i][j].equalsIgnoreCase("red")) {
                redCount++;
                yelCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("yellow")) {
                yelCount++;
                redCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("empty")) {
                yelCount = 0;
                redCount = 0;
            }
            if (redCount == 4) {
                return "red";
            } else if (yelCount == 4) {
                return "yellow";
            }
            j++;
        }

        //2nd longest minor (upper)
        j = 0;
        redCount = 0;
        yelCount = 0;
        for (int i = 6; i >= 0; i--) {
            if (boardState[i][j].equalsIgnoreCase("red")) {
                redCount++;
                yelCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("yellow")) {
                yelCount++;
                redCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("empty")) {
                yelCount = 0;
                redCount = 0;
            }
            if (redCount == 4) {
                return "red";
            } else if (yelCount == 4) {
                return "yellow";
            }
            j++;
        }
        j = 0;
        redCount = 0;
        yelCount = 0;
        for (int i = 5; i >= 0; i--) {
            if (boardState[i][j].equalsIgnoreCase("red")) {
                redCount++;
                yelCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("yellow")) {
                yelCount++;
                redCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("empty")) {
                yelCount = 0;
                redCount = 0;
            }
            if (redCount == 4) {
                return "red";
            } else if (yelCount == 4) {
                return "yellow";
            }
            j++;
        }
        j = 0;
        redCount = 0;
        yelCount = 0;
        for (int i = 4; i >= 0; i--) {
            if (boardState[i][j].equalsIgnoreCase("red")) {
                redCount++;
                yelCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("yellow")) {
                yelCount++;
                redCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("empty")) {
                yelCount = 0;
                redCount = 0;
            }
            if (redCount == 4) {
                return "red";
            } else if (yelCount == 4) {
                return "yellow";
            }
            j++;
        }

        //2nd longest minor (lower)
        j = 1;
        redCount = 0;
        yelCount = 0;
        for (int i = 7; i >= 1; i--) {
            if (boardState[i][j].equalsIgnoreCase("red")) {
                redCount++;
                yelCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("yellow")) {
                yelCount++;
                redCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("empty")) {
                yelCount = 0;
                redCount = 0;
            }
            if (redCount == 4) {
                return "red";
            } else if (yelCount == 4) {
                return "yellow";
            }
            j++;
        }
        j = 2;
        redCount = 0;
        yelCount = 0;
        for (int i = 7; i >= 2; i--) {
            if (boardState[i][j].equalsIgnoreCase("red")) {
                redCount++;
                yelCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("yellow")) {
                yelCount++;
                redCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("empty")) {
                yelCount = 0;
                redCount = 0;
            }
            if (redCount == 4) {
                return "red";
            } else if (yelCount == 4) {
                return "yellow";
            }
            j++;
        }
        j = 3;
        redCount = 0;
        yelCount = 0;
        for (int i = 7; i >= 3; i--) {
            if (boardState[i][j].equalsIgnoreCase("red")) {
                redCount++;
                yelCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("yellow")) {
                yelCount++;
                redCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("empty")) {
                yelCount = 0;
                redCount = 0;
            }
            if (redCount == 4) {
                return "red";
            } else if (yelCount == 4) {
                return "yellow";
            }
            j++;
        }
        j = 4;
        redCount = 0;
        yelCount = 0;
        for (int i = 7; i >= 4; i--) {
            if (boardState[i][j].equalsIgnoreCase("red")) {
                redCount++;
                yelCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("yellow")) {
                yelCount++;
                redCount = 0;
            } else if (boardState[i][j].equalsIgnoreCase("empty")) {
                yelCount = 0;
                redCount = 0;
            }
            if (redCount == 4) {
                return "red";
            } else if (yelCount == 4) {
                return "yellow";
            }
            j++;
        }
        return null;
    }
}