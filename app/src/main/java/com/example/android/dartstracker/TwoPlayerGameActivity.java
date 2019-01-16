package com.example.android.dartstracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
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
    // The score input by Player 1 1 is saved in this variable
    private int mPlayerOneScore;
    // The score input by Player 2 is saved in this variable
    private int mPlayerTwoScore;

    // Integers to track whose turn is next
    private int PLAYER_ONE_TURN = 1;
    private int PLAYER_TWO_TURN = 2;
    private int mCurrentTurn;

    // Initial score
    private int initialScore = 501;

    // ContentValues object to store the input for player 1 and 2 prior to insertion
    // into the database
    private ContentValues newInput = new ContentValues();

    // Current score of Player 1
    private TextView playerOneCurrentScore;
    // Current score of Player 2
    private TextView playerTwoCurrentScore;

    // Database helper object
    private GameDbHelper mDbHelper;
    // Database
    private SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_player_game);

        // Create a database helper object for the game
        mDbHelper = new GameDbHelper(getBaseContext());
        // Create the actual database and make it writable in order to add to it
        mDatabase = mDbHelper.getWritableDatabase();

        // Set the current turn to Player 1
        mCurrentTurn = PLAYER_ONE_TURN;

        // Set the current scores to the initial score defined in the variables
        mPlayerOneScore = initialScore;
        mPlayerTwoScore = initialScore;

        // Use a ContentValues object to insert the initial scores into the table
        ContentValues initialValues = new ContentValues();
        initialValues.put(GameEntry.COLUMN_PLAYER_ONE, initialScore);
        initialValues.put(GameEntry.COLUMN_PLAYER_TWO, initialScore);
        mDatabase.insert(GameEntry.TABLE_NAME, null, initialValues);

        // Set the initial scores in the app interface to 501 for each player
        playerOneCurrentScore = (TextView) findViewById(R.id.playerOneScore);
        playerOneCurrentScore.setText(Integer.toString(initialScore));
        playerTwoCurrentScore = (TextView) findViewById(R.id.playerTwoScore);
        playerTwoCurrentScore.setText(Integer.toString(initialScore));

        // When the enter button is pressed, the score is recorded
        Button enterScore = (Button) findViewById(R.id.enterScoreButton);
        enterScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Read the score input by the user
                mScoreEditText = (EditText) findViewById(R.id.singleScore);
                String score = mScoreEditText.getText().toString();

                // Reset the score on the user interface to empty
                mScoreEditText.setText("");

                // If it is Player 1's turn, add the score to the ContentValue for Player 1
                if (mCurrentTurn == 1) {
                    insertPlayerOneScore(score);

                    // If it is Player 2's turn add the score to the ContentValue for Player 2
                } else if (mCurrentTurn == 2) {
                    insertPlayerTwoScore(score);

                    // Add the data in the ContentValues object to the database
                    insertScoresIntoDatabase();

                    // Update the scores in the UI
                    int currentTotalPlayerOne = currentScore(GameEntry.COLUMN_PLAYER_ONE, GameEntry.TABLE_NAME);
                    Log.d("TOTAL SCORE: ", Integer.toString(currentTotalPlayerOne));
                    mPlayerOneScore = initialScore - currentTotalPlayerOne;
                    playerOneCurrentScore.setText(Integer.toString(mPlayerOneScore));

                    int currentTotalPlayerTwo = currentScore(GameEntry.COLUMN_PLAYER_TWO, GameEntry.TABLE_NAME);
                    Log.d("TOTAL SCORE: ", Integer.toString(currentTotalPlayerTwo));
                    mPlayerTwoScore = initialScore - currentTotalPlayerTwo;
                    playerTwoCurrentScore.setText(Integer.toString(mPlayerTwoScore));
                }
            }
        });
    }

    // Add Player 1 score to the ContentValue variable
    private void insertPlayerOneScore(String score) {
        // Check to see if the input is a valid number between 0 and 180
        if (isInteger(score)) {
            // Add the score into the ContentValues object in preparation for addition
            // into the database
            newInput.put(GameEntry.COLUMN_PLAYER_ONE, Integer.parseInt(score));

            // Set the current turn to Player 2
            mCurrentTurn = PLAYER_TWO_TURN;

            // if the integer is not a number, a toast is shown to the user
        } else {
            Toast.makeText(getBaseContext(), R.string.validScoreInput,
                    Toast.LENGTH_LONG).show();
        }
    }

    // Add Player 2 score to the ContentValue variable and add data in ContentValue object
    // to the database
    private void insertPlayerTwoScore(String score) {
        // Check to see if the input is a valid number between 0 and 180
        if (isInteger(score)) {
        // Add the score into the ContentValues object in preparation for addition
        // into the database
        newInput.put(GameEntry.COLUMN_PLAYER_TWO, Integer.parseInt(score));

        // Set the current turn back to Player 1
        mCurrentTurn = PLAYER_ONE_TURN;

        // if the integer is not a number, a toast is shown to the user
        } else {
            Toast.makeText(getBaseContext(), R.string.validScoreInput,
                    Toast.LENGTH_LONG).show();
        }
    }

    // Add the data in the ContentValues object to the database
    private void insertScoresIntoDatabase() {
        mDatabase.insert(GameEntry.TABLE_NAME, null, newInput);
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

    // Method to get the current score
    private int currentScore(String player, String tableName) {
            mDatabase = mDbHelper.getWritableDatabase();
            // Access the player's column in the database and add up all the scores
            try {
                String sql = "SELECT TOTAL(" + player + ") FROM " + tableName;
                return (int)DatabaseUtils.longForQuery(mDatabase, sql, null);
            } finally {

            }
    }
}
