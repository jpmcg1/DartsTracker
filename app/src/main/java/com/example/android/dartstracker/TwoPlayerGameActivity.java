package com.example.android.dartstracker;

import android.content.ContentValues;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.dartstracker.data.GameContract.GameEntry;
import com.example.android.dartstracker.data.GameDbHelper;

public class TwoPlayerGameActivity extends AppCompatActivity {

    // Current score of Player 1
    private TextView playerOneCurrentScore;
    // Current score of Player 2
    private TextView playerTwoCurrentScore;
    // Edit text field for the score inputted by the user
    private EditText mScoreEditText;


    // The score input by Player 1 1 is saved in this variable
    private int mPlayerOneScore;
    // The score input by Player 2 is saved in this variable
    private int mPlayerTwoScore;
    // Initial score
    private int initialScore = 501;

    // Integers to track whose turn is next
    private int PLAYER_ONE_TURN = 1;
    private int PLAYER_TWO_TURN = 2;
    private int mCurrentTurn;


    // ContentValues object to store the input for player 1 and 2 prior to insertion
    // into the database
    private ContentValues newInput = new ContentValues();

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
        /*ContentValues initialValues = new ContentValues();
        initialValues.put(GameEntry.COLUMN_PLAYER_ONE, initialScore);
        initialValues.put(GameEntry.COLUMN_PLAYER_TWO, initialScore);
        mDatabase.insert(GameEntry.TABLE_NAME, null, initialValues);*/

        // Set the initial scores in the app interface to 501 for each player
        playerOneCurrentScore = findViewById(R.id.playerOneScore);
        playerOneCurrentScore.setText(Integer.toString(initialScore));
        playerTwoCurrentScore = findViewById(R.id.playerTwoScore);
        playerTwoCurrentScore.setText(Integer.toString(initialScore));

        // When you want to clear the table and delete all the entries
        deleteAllData(GameEntry.TABLE_NAME);

        // When the enter button is pressed, the score is recorded
        Button enterScore = findViewById(R.id.enterScoreButton);
        enterScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Read the score input by the user
                mScoreEditText = findViewById(R.id.singleScore);
                String score = mScoreEditText.getText().toString();

                // Reset the score on the user interface to empty
                mScoreEditText.setText("");

                // Calculate the current score for each player
                int currentTotalPlayerOne = currentScore(GameEntry.COLUMN_PLAYER_ONE, GameEntry.TABLE_NAME);
                int currentTotalPlayerTwo = currentScore(GameEntry.COLUMN_PLAYER_TWO, GameEntry.TABLE_NAME);

                // TODO: NEED TO CHANGE THESE IF/ELSE TO WHILE LOOPS SO THAT THE SCORE IS INSERTED BEFORE
                // THE SCORES ARE INSERTED INTO THE DATABASE, NOT JUST A TOAST MESSAGE

                // TODO: ALSO NEED TO DELETE ALL THE DATA IN THE SQL TABLE, OR ELSE THE CODE BELOW
                // WILL INSTANTLY NOT WORK AS THE CURRENT TOTAL SCORES ARE NOT BETWEEN 0 AND 501

                // TODO: NEED TO ADD SOMETHING THAT SAYS 'IS THE SCORE ZERO' AND IF YES,
                // was the last shot a double,  and if yes to both
                // GAME OVER AND SOMETHING HAPPENS... at the end of onCreate say 'if
                // currentScore = 0 then blah blah? Or create new method?

                // If it is Player 1's turn, add the score to the ContentValue for Player 1
                if (mCurrentTurn == 1) {
                    // Check to see if the input will make the score < 2 and return if true
                    if (!isTheScoreValid(currentTotalPlayerOne, score)) {
                        Toast.makeText(getBaseContext(), R.string.validScoreInput,
                                Toast.LENGTH_LONG).show();
                        return;

                        // Check to see if the input is a valid number between 0 and 180 and
                        // return if not
                    } else if (!isInteger(score)) {
                        Toast.makeText(getBaseContext(), R.string.validScoreInput,
                                Toast.LENGTH_LONG).show();
                        return;

                        // If the input is valid then insert the score
                    } else {
                        insertPlayerOneScore(score);
                    }

                    // If it is Player 2's turn add the score to the ContentValue for Player 2
                } else if (mCurrentTurn == 2) {
                    // Check to see if the input will make the score < 2 and return if true
                    if (!isTheScoreValid(currentTotalPlayerTwo, score)) {
                        Toast.makeText(getBaseContext(), R.string.validScoreInput,
                                Toast.LENGTH_LONG).show();
                        return;

                        // Check to see if the input is a valid number between 0 and 180 and
                        // return if not
                    } else if (!isInteger(score)) {
                        Toast.makeText(getBaseContext(), R.string.validScoreInput,
                                Toast.LENGTH_LONG).show();
                        return;

                        // If the input is valid then insert the score
                    } else {
                        insertPlayerTwoScore(score);
                    }

                    // Add the data in the ContentValues object to the database
                    insertScoresIntoDatabase();

                    // Calculate the current score for each player after this score insertion into
                    // database
                    int newCurrentTotalPlayerOne = currentScore(
                            GameEntry.COLUMN_PLAYER_ONE, GameEntry.TABLE_NAME);
                    int newCurrentTotalPlayerTwo = currentScore(
                            GameEntry.COLUMN_PLAYER_TWO, GameEntry.TABLE_NAME);

                    // Update the scores in the UI
                    mPlayerOneScore = initialScore - newCurrentTotalPlayerOne;
                    playerOneCurrentScore.setText(Integer.toString(mPlayerOneScore));
                    mPlayerTwoScore = initialScore - newCurrentTotalPlayerTwo;
                    playerTwoCurrentScore.setText(Integer.toString(mPlayerTwoScore));
                }
            }
        });
    }

    // Is the score a valid one - if the new total score 1 or less it is invalid and returns false
    private boolean isTheScoreValid(int currentScore, String newScore) {
        int newTotalScore = initialScore - currentScore - Integer.parseInt(newScore);
        if (newTotalScore < 2) {
            return false;
        }
        return true;
    }

    // Add Player 1 score to the ContentValues object
    private void insertPlayerOneScore(String score) {
            // Add the score into the ContentValues object in preparation for addition
            // into the database
            newInput.put(GameEntry.COLUMN_PLAYER_ONE, Integer.parseInt(score));

            // Set the current turn to Player 2
            mCurrentTurn = PLAYER_TWO_TURN;
    }

    // Add Player 2 score to the ContentValue variable and add data in ContentValue object
    // to the database
    private void insertPlayerTwoScore(String score) {
            // Add the score into the ContentValues object in preparation for addition
            // into the database
            newInput.put(GameEntry.COLUMN_PLAYER_TWO, Integer.parseInt(score));

            // Set the current turn back to Player 1
            mCurrentTurn = PLAYER_ONE_TURN;
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
            return (int) DatabaseUtils.longForQuery(mDatabase, sql, null);
        } finally {

        }
    }

    private void deleteAllData(String tableName) {
        mDatabase = mDbHelper.getWritableDatabase();
        mDatabase.execSQL("delete from " + tableName);
    }
}
