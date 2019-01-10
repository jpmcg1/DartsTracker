package com.example.android.dartstracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.dartstracker.data.GameDbHelper;

public class TwoPlayerGameActivity extends AppCompatActivity {

    // Edit text field for the score inputted by the user
    private EditText mScoreEditText;

    private int mIndividualScore;

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

                // if the user input is a integer between 0 and 180, store the integer in a variable
                if (isInteger(score)) {
                    mIndividualScore = Integer.parseInt(score);
                    Log.v("SCORE INPUT VALUE: ", Integer.toString(mIndividualScore));
                    // if the integer is not a number, a toast is shown to the user
                } else {
                    Toast.makeText(getBaseContext(), "PLEASE ENTER A VALID NUMBER",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // A method to check whether the input from the user is a number between 0 and 180
    private static boolean isInteger(String string) {
        try {
            int score = Integer.parseInt(string);
            if (0 <= score && score < 181) {
                return true;
            }
        } catch (NumberFormatException ex) {
            return false;
        }
        return false;
    }
}
