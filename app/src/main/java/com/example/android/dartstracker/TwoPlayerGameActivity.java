package com.example.android.dartstracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.example.android.dartstracker.data.GameDbHelper;

public class TwoPlayerGameActivity extends AppCompatActivity {

    // Edit text field for the score inputted by the user
    private EditText mScoreEditText;

    // Database helper object
    private GameDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_player_game);
    }
}
