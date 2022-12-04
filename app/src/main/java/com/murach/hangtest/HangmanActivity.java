package com.murach.hangtest;

import java.util.concurrent.*;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class HangmanActivity extends AppCompatActivity
        implements OnClickListener, OnEditorActionListener , SharedPreferences.OnSharedPreferenceChangeListener  {

    private int winTotal = 0;
    private int lossTotal = 0;
    private EditText guessEditText;
    private TextView displayTextView;
    private TextView msgTextView;
    private TextView winTextView;
    private TextView lossTextView;
    private TextView remainTextView;
    private TextView missesTextView;
    private ImageView hangView;
    private Button guessButton;
    private Button resetButton;
    private Button solutionButton;
    private ArrayList<String> wordList = new ArrayList<>();
    private ArrayList<String> misses = new ArrayList<>();
    private StringBuilder displayWord = new StringBuilder();
    private String displayWordString;
    private StringBuilder missedLetters = new StringBuilder();
    private String wordString;
    private SharedPreferences savedValues;
    private static final String TAG = "HangTestActivity";
    private SharedPreferences prefs;
    private String mask;
    private int difficulty;
    private int minLength;
    private int maxLength;
    private boolean needLoad;
    private boolean includeHyphens;
    private boolean hideKeyboard;
    private boolean dictionaryMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hangman);

        //get widget references
        guessEditText = (EditText) findViewById(R.id.editTextGuess);
        displayTextView = (TextView) findViewById(R.id.textDisplay);
        msgTextView = (TextView) findViewById(R.id.textMsg);
        winTextView = (TextView) findViewById(R.id.textWins);
        lossTextView = (TextView) findViewById(R.id.textLosses);
        remainTextView = (TextView) findViewById(R.id.textRemaining);
        missesTextView = (TextView) findViewById(R.id.textMisses);
        guessButton = (Button) findViewById(R.id.buttonGuess);
        resetButton = (Button) findViewById(R.id.buttonReset);
        solutionButton = (Button) findViewById(R.id.buttonSolution);
        hangView = (ImageView) findViewById(R.id.imageView);

        //listener for text box
        guessEditText.setOnEditorActionListener(this);

        //button click listeners
        solutionButton.setOnClickListener(this);
        guessButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        //get saved values
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);

        //get prefs
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        prefs.registerOnSharedPreferenceChangeListener(this);
        mask = prefs.getString("pref_hangman_mask", "*");
        difficulty = Integer.parseInt(prefs.getString("pref_hangman_difficulty", "1"));
        minLength = Integer.parseInt(prefs.getString("pref_hangman_min", "3"));
        maxLength = Integer.parseInt(prefs.getString("pref_hangman_max", "16"));
        includeHyphens = prefs.getBoolean("pref_hangman_hyphens", true);
        dictionaryMode = prefs.getBoolean("pref_hangman_dictionary", true);
        hideKeyboard = prefs.getBoolean("pref_hangman_hide", false);
        loadList();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equalsIgnoreCase("pref_hangman_difficulty" ) ||
                s.equalsIgnoreCase("pref_hangman_min") ||
                s.equalsIgnoreCase("pref_hangman_max") ||
                s.equalsIgnoreCase("pref_hangman_hyphens")) {
                    needLoad = true;
        }
    }

    private void loadList() {
        Log.d(TAG, "loadList() called");
        wordList.clear();
        String filename;
        switch (difficulty) {
            case 1:
                filename = "easy.txt";
                break;
            case 2:
                filename = "medium.txt";
                break;
            case 3:
                filename = "hard.txt";
                break;
            default:
                filename = "easy.txt";
        }
        //load word list from text file
        AssetManager am = this.getAssets();
        try {
            InputStream in = am.open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                //one of the word lists has some extra characters at the end of words
                line = line.replace("%", "").replace("!", "");

                if (line.length() >= minLength && line.length() <= maxLength) {
                    if (line.contains("-")) {
                        if (includeHyphens) { wordList.add(line); }
                    } else {
                        wordList.add(line);
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            System.err.format("Error occurred while trying to read the word list.");
            e.printStackTrace();
        }
        Toast.makeText(this, String.valueOf(wordList.size()) + " words in list", Toast.LENGTH_SHORT).show();
        resetDisplayWord();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hangman, menu);
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

        Editor editor = savedValues.edit();
        editor.putString("wordString", wordString);
        Log.d(TAG, missedLetters.toString());
        editor.putString("missedLetters", missedLetters.toString());
        editor.putString("displayWord", displayWord.toString());
        editor.putString("oldMask", mask);
        editor.putInt("winTotal", winTotal);
        editor.putInt("lossTotal", lossTotal);
        editor.commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        //get preferences
        mask = prefs.getString("pref_hangman_mask", "*");
        difficulty = Integer.parseInt(prefs.getString("pref_hangman_difficulty", "1"));
        minLength = Integer.parseInt(prefs.getString("pref_hangman_min", "3"));
        maxLength = Integer.parseInt(prefs.getString("pref_hangman_max", "16"));
        includeHyphens = prefs.getBoolean("pref_hangman_hyphens", true);
        dictionaryMode = prefs.getBoolean("pref_hangman_dictionary", true);
        hideKeyboard = prefs.getBoolean("pref_hangman_hide", false);

        Log.d(TAG, "Hyphens: " + String.valueOf(includeHyphens));
        Log.d(TAG, "Need Load: " + String.valueOf(needLoad));

        //validate min/max
        if (minLength > maxLength) {
            minLength = maxLength;
            Editor prefEdit = prefs.edit();
            prefEdit.putString("pref_hangman_min", String.valueOf(maxLength));
            prefEdit.commit();
        }

        if (needLoad) {
            loadList();
        } else {
            //update display word if masking character has been changed
            String oldMask = savedValues.getString("oldMask", "*");
            displayWordString = savedValues.getString("displayWord", "")
                    .replace(oldMask, mask);
            wordString = savedValues.getString("wordString", "");

            //repopulate the misses list from string
            String missesString = savedValues.getString("missedLetters", "");
            misses.clear();
            for (int i = 0; i < missesString.length(); i++) {
                misses.add(missesString.substring(i, i + 1));
            }
            //rebuild the misses StringBuilder
            Log.d(TAG, "missesString: " + missesString);
            Log.d(TAG, "misses: " + misses);
            missedLetters.delete(0, missedLetters.length());
            for (String miss : misses) {
                missedLetters.append(miss);
            }
        }
        needLoad = false;

        winTotal = savedValues.getInt("winTotal", 0);
        lossTotal = savedValues.getInt("lossTotal", 0);

        Log.d(TAG, "missedLetters: " + missedLetters.toString()
                .replace("[", "").replace("]", ""));

        remainTextView.setText(7 - misses.size() + " Guesses Remaining");
        hangView.setImageResource(R.drawable.hang + misses.size());
        missesTextView.setText("Missed:  " + missedLetters.toString());

        displayWord = new StringBuilder(displayWordString);
        Log.d(TAG, "displayWordStr: " + displayWordString);
        Log.d(TAG, "displayWord: " + displayWord);
        displayTextView.setText(displayWord);
        Log.d(TAG, "wordString: " + wordString);
        winTextView.setText("Wins: " + winTotal);
        lossTextView.setText("Losses: " + lossTotal);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonReset) {
            resetDisplayWord();
        } else if (v.getId() == R.id.buttonGuess) {
            checkGuess(guessEditText.getText().toString());
        } else if (v.getId() == R.id.buttonSolution) {
            msgTextView.setText(wordString);
            if (msgTextView.getVisibility() == View.VISIBLE) {
                msgTextView.setVisibility(View.INVISIBLE);
            } else if (msgTextView.getVisibility() == View.INVISIBLE) {
                msgTextView.setVisibility((View.VISIBLE));
            }
        }
    }
    
    public void resetDisplayWord() {
        Log.d(TAG, "resetDisplayWord called. " + wordString);
        //reset hangman graphic
        hangView.setImageResource(R.drawable.hang);
        
        //reset misses and associated stringbuilder
        misses.clear();
        missedLetters.delete(0, missedLetters.length());

        remainTextView.setText((7 - misses.size()) + " Guesses Remaining");

        //shuffle word list and get new word
        Collections.shuffle(wordList);
        wordString = wordList.get(0);

        //Toast.makeText(this, wordString, Toast.LENGTH_LONG).show();
        //create appropriate length masked display word
        displayWord.delete(0, displayWord.length());
        for (int i = 0; i < wordString.length(); i++) {
            displayWord.append(mask);
        }

        //give any hyphens
        for (int m = 0; m < wordString.length(); m++) {
            if (wordString.substring(m, m + 1).equalsIgnoreCase("-")) {
                displayWord.replace(m, m + 1, "-");
            }
        }

        displayWordString = displayWord.toString();
        Log.d(TAG, "displayWord: " + displayWordString);
        //reset display and misses text views
        displayTextView.setText(displayWordString);
        missedLetters.delete(0, missedLetters.length());
        missesTextView.setText("Missed:  " + missedLetters);
        Log.d(TAG, "wordString: " + wordString);

        //reset dev mode answer window
        msgTextView.setText(wordString);
    }

    public boolean checkGuess(String s) {
        String guess = s.toLowerCase();
        Log.d(TAG, "checkGuess called");

        //check for previously guessed or blank - clear entry and exit
        if (misses.contains(guess) || guess.isEmpty()) {
            Log.d(TAG, "bad guess caught");
            guessEditText.setText("");
            return false;
        }

        //check for solved puzzle or end of game and reset
        if (misses.size() == 7 || displayWord.toString().indexOf(mask) == -1) {
            resetDisplayWord();
            return false;
        }

        boolean found = false;
        for (int m = 0; m < wordString.length(); m++) {
            if (wordString.substring(m, m + 1).equalsIgnoreCase(guess)) {
                displayWord.replace(m, m + 1, guess);
                found = true;
            }
        }
        displayTextView.setText(displayWord);
        guessEditText.setText("");
        if (found) {
            if (displayWord.toString().indexOf(mask) == -1) {
                remainTextView.setText("Solved!  " + wordString);
                winTotal++;
                winTextView.setText("Wins: " + winTotal);
                if (dictionaryMode) {
                    displayDefinition(wordString);
                }
            }
            return true;
        } else {
            misses.add(guess);
            hangView.setImageResource(R.drawable.hang + misses.size());
            remainTextView.setText(7 - misses.size() + " Guesses Remaining");
            missedLetters.delete(0, missedLetters.length());
            for (String miss : misses) {
                missedLetters.append(miss);
            }
            missesTextView.setText("Missed:  " + missedLetters);
            if (misses.size() == 7) {
                lossTotal++;
                if (dictionaryMode) {
                    displayDefinition(wordString);
                }
                remainTextView.setText("Lost!\t" + wordString);
                lossTextView.setText("Losses: " + lossTotal);
            }
            return false;
        }
    }

    private void displayDefinition(String wordString) {
        new DictionaryTask().execute();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            Log.d(TAG, "onEditorAction");
            checkGuess(guessEditText.getText().toString());

            //hide soft keyboard if that preference is set
            if (hideKeyboard) {
                View view = this.getCurrentFocus();
                if (view == null) {
                    view = new View(this);
                }
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            return true;
        } else {
            return false;
        }
    }
    
    public void dictionaryES() {
        ExecutorService es = Executors.newSingleThreadExecutor();
      
               
    }

    @SuppressLint("StaticFieldLeak")
    public class DictionaryTask extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display progress dialog
            progressDialog = new ProgressDialog(HangmanActivity.this);
            progressDialog.setMessage("Looking up \"" + wordString + "\" in dictionary...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String dictionaryUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + wordString;
            StringBuilder result = new StringBuilder();
            URL url;
            HttpsURLConnection urlConnection = null;
            try {
                url = new URL(dictionaryUrl);
                urlConnection = (HttpsURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                int data = inputStreamReader.read();

                while (data != -1) {
                    result.append((char) data);
                    data = inputStreamReader.read();
                }
                Log.d("API", result.toString());
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            // dismiss the progress dialog after receiving data from API
            progressDialog.dismiss();
            try {
                JSONArray resultArray = new JSONArray(s);
                JSONObject resultObj = resultArray.getJSONObject(0);
                JSONArray meaningsArray = (JSONArray) resultObj.get("meanings");
                JSONObject meaningsObj = meaningsArray.getJSONObject(0);
                JSONArray defArray = (JSONArray) meaningsObj.get("definitions");
                JSONObject defObj = defArray.getJSONObject(0);
                String def = (String) defObj.get("definition");

                Log.d("postAPI", def);
                AlertDialog.Builder builder = new AlertDialog.Builder(HangmanActivity.this);
                builder.setTitle("Definition")
                        .setMessage(wordString + ": " + def)
                        .setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // just dismiss
                            }
                        });
                AlertDialog defDialog = builder.create();
                defDialog.show();
            } catch (JSONException e) {
                Log.d("postAPI", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
