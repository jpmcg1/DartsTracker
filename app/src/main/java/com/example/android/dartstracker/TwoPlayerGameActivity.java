package com.example.android.dartstracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

        // When the enter button is pressed, the score is recorded
        Button enterScore = (Button) findViewById(R.id.enterScoreButton);
        enterScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Read the score input by the user
                mScoreEditText = (EditText) findViewById(R.id.singleScore);
                String score = mScoreEditText.getText().toString();
                Log.v("SCORE INPUT VALUE: ", score);
            }
        });
    }
}
