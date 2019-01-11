package com.example.android.dartstracker;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.dartstracker.data.GameContract;
import com.example.android.dartstracker.data.GameContract.GameEntry;
import com.example.android.dartstracker.data.GameDbHelper;

// TODO: After every successful click of the ENTER button, we need to find a way to shift between
// player one and player 2 so that the scores are added to the correct columns in the database

public class TwoPlayerGameActivity extends AppCompatActivity {

    // Edit text field for the score inputted by the user
    private EditText mScoreEditText;
    // The score input by the user is save in this variable
    private int mIndividualScore;

    // Current score of Player 1
    private TextView playerOneCurrentScore;
    // Current score of Player 2
    private TextView playerTwoCurrentScore;

    // Database helper object
    private GameDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_player_game);

        // Create a database helper object for the game
        mDbHelper = new GameDbHelper(getBaseContext());
        // Create the actual database and make it writable in order to add to it
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Use a ContentValues object to insert the scores into the table
        ContentValues initialValues = new ContentValues();
        initialValues.put(GameEntry.COLUMN_PLAYER_ONE, 501);
        initialValues.put(GameEntry.COLUMN_PLAYER_TWO, 501);

        long newRowId = database.insert(GameEntry.TABLE_NAME, null, initialValues);

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

                    // Add the score into the database


                    // if the integer is not a number, a toast is shown to the user
                } else {
                    Toast.makeText(getBaseContext(), R.string.validScoreInput,
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
