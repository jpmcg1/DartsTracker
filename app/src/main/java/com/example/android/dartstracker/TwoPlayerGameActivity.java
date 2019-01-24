package com.example.android.dartstracker;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.dartstracker.data.GameContract.GameEntry;
import com.example.android.dartstracker.data.GameDbHelper;

// TODO: NEED A 'GAME OVER' AND SOMETHING HAPPENS

// TODO: IF THE SCORE IS TOO BIG AND GOES PAST ZERO, AT THE MINUTE IT GIVES THE PLAYER ANOTHER GO -
// NEED TO CHANGE THIS
// TODO: need the scores to go in individually - first player can be a content value insertion, but
// second needs to query the databasse and updat the same row. Otherwise, if player one wins,
// player 2 will still have to play before it is registered

// TODO: Add a reset button on the top of the activity UI

public class TwoPlayerGameActivity extends AppCompatActivity {

    // Current score of Player 1
    private TextView playerOneCurrentScore;
    // Current score of Player 2
    private TextView playerTwoCurrentScore;
    // Edit text field for the score inputted by the user
    private EditText mScoreEditText;

    private LinearLayout mLinearLayout;
    private Button closePopupButton;


    // The score input by Player 1 is saved in this variable
    private int mPlayerOneScore;
    // The score input by Player 2 is saved in this variable
    private int mPlayerTwoScore;
    // Initial score for the game - can add different options
    private int initialScore = 501;

    // Integers to track whose turn is next
    private int PLAYER_ONE_TURN = 1;
    private int PLAYER_TWO_TURN = 2;
    private int mCurrentTurn;

    // String to get all the scores from the SQLite database
    private String databaseQuery = "SELECT * FROM " + GameEntry.TABLE_NAME + ";";

    // CursorAdapter for two player game
    private TwoPlayerGameCursorAdapter mAdapter;

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

        // deleteAllData();

        // Set the current turn to Player 1
        mCurrentTurn = PLAYER_ONE_TURN;

        // Set the current scores to the initial score defined for this game
        // (to start with only 501)
        mPlayerOneScore = initialScore;
        mPlayerTwoScore = initialScore;

        setInitialScore();

        // A cursor to hold the data from the database in order to send to the adapter
        Cursor cursor = mDatabase.rawQuery(databaseQuery, null);
        // Create adapter for the scores and put in the cursor with the data from the database
        mAdapter = new TwoPlayerGameCursorAdapter(this, cursor);
        // Create ListView to populate with the adapter
        ListView itemListView = (ListView) findViewById(R.id.list_two_players);

        // Set the adapter to the ListView
        itemListView.setAdapter(mAdapter);

        // When the enter button is pressed, the score is recorded
        Button enterScore = findViewById(R.id.enterScoreButton);
        enterScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Read the score input by the user
                mScoreEditText = findViewById(R.id.singleScore);
                String score = mScoreEditText.getText().toString();

                // If the user input is not null, continue
                if (score != null && !score.isEmpty()) {
                    // Reset the score on the user interface to empty
                    mScoreEditText.setText("");

                    // Calculate the current score for each player
                    int currentTotalPlayerOne = currentScore(
                            GameEntry.COLUMN_PLAYER_ONE, GameEntry.TABLE_NAME);
                    int currentTotalPlayerTwo = currentScore(
                            GameEntry.COLUMN_PLAYER_TWO, GameEntry.TABLE_NAME);

                    // If it is Player 1's turn, add the score to the ContentValue for Player 1
                    if (mCurrentTurn == 1) {
                        // Check to see if the input will make the score < 2 and return if true
                        if (!isTheScoreValid(currentTotalPlayerOne, score)) {
                            Toast.makeText(getBaseContext(), R.string.scoreOverZero,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Check to see if the input is a valid number between 0 and 180 and
                        // return if not
                        if (!isInteger(score)) {
                            Toast.makeText(getBaseContext(), R.string.validScoreInput,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        // If the input is valid then insert the score
                        insertPlayerOneScore(score);


                        // If it is Player 2's turn add the score to the ContentValue for Player 2
                    } else if (mCurrentTurn == 2) {
                        // Check to see if the input will make the score < 20 and return if true
                        if (!isTheScoreValid(currentTotalPlayerTwo, score)) {
                            Toast.makeText(getBaseContext(), R.string.scoreOverZero,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Check to see if the input is a valid number between 0 and 180 and
                        // return if not
                        if (!isInteger(score)) {
                            Toast.makeText(getBaseContext(), R.string.validScoreInput,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // If the input is valid then insert the score
                        insertPlayerTwoScore(score);
                        insertScoresIntoDatabase();
                        updateAdapter();

                        // Calculate the current score for each player after the score
                        // insertion into database
                        int newCurrentTotalPlayerOne = currentScore(
                                GameEntry.COLUMN_PLAYER_ONE, GameEntry.TABLE_NAME);
                        mPlayerOneScore = initialScore - newCurrentTotalPlayerOne;
                        // If the score is 0, the game is over
                        if (mPlayerOneScore == 0) {
                            gameIsWon();
                            return;
                        } else {
                            // Update the scores in the UI
                            playerOneCurrentScore.setText(Integer.toString(mPlayerOneScore));
                        }

                        int newCurrentTotalPlayerTwo = currentScore(
                                GameEntry.COLUMN_PLAYER_TWO, GameEntry.TABLE_NAME);
                        mPlayerTwoScore = initialScore - newCurrentTotalPlayerTwo;
                        // If the score is 0, the game is over
                        if (mPlayerTwoScore == 0) {
                            gameIsWon();
                            return;
                        } else {
                            // Update the scores in the UI
                            playerTwoCurrentScore.setText(Integer.toString(mPlayerTwoScore));
                        }
                    }

                } else {
                    Toast.makeText(getBaseContext(), R.string.validScoreInput,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    // In the pop-up that appears after the game is won, clicking the 'replay' button will
    // start a new game in this activity by recalling the OnCreate method
    private void replayGame() {
        Button replayButton = (Button) findViewById(R.id.replayButton);
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
    }

    private void showPopup() {
        mLinearLayout = (LinearLayout) findViewById(R.id.linear_layout_two_player);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.game_finish_pop_up, null);
        closePopupButton = (Button) popupView.findViewById(R.id.closePopupButton);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(mLinearLayout, Gravity.CENTER, 0,0);
        closePopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }


    private void gameIsWon() {
        Toast.makeText(getBaseContext(), R.string.gameIsWon,
                Toast.LENGTH_LONG).show();
        // When you want to clear the table and delete all the entries, like when the activity
        // opens up for a new game
        deleteAllData();
        showPopup();
    }


    private void setInitialScore() {
        // Set the initial scores in the app interface to 501 for each player
        playerOneCurrentScore = findViewById(R.id.playerOneScore);
        playerOneCurrentScore.setText(Integer.toString(initialScore));
        playerTwoCurrentScore = findViewById(R.id.playerTwoScore);
        playerTwoCurrentScore.setText(Integer.toString(initialScore));
    }


    // When the scores are input by the user, the cursor gets the new additional info from the
    // database and it is set to the adapter so the new scores are added to the ListView.
    private void updateAdapter() {
        Cursor newCursor = mDatabase.rawQuery(databaseQuery, null);
        mAdapter.swapCursor(newCursor);
    }


    // If the final shot is not a double then return false, whilst also changing
    // the players turn as in Darts a failed double on a final shot counts as a turn
    // This only applies if each of the 3 shots is input individually,
    // otherwise there is no way to know if the last shot was a double
    private boolean isTheFinalScoreADouble(int currentScore, String newScore) {
        int newTotalScore = initialScore - currentScore - Integer.parseInt(newScore);
        if (newTotalScore == 0 && Integer.parseInt(newScore) % 2 != 0) {
            if (mCurrentTurn == 1) {
                mCurrentTurn = 2;
            } else {
                mCurrentTurn = 1;
            }
            return false;
        }
        return true;
    }


    // Is the score a valid one - if the new total score 1 or less it is invalid and returns false
    private boolean isTheScoreValid(int currentScore, String newScore) {
        int newTotalScore = initialScore - currentScore - Integer.parseInt(newScore);
        if (newTotalScore < 0) {
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


    // Add the data in the ContentValues object to the database and clear the contentValue object
    private void insertScoresIntoDatabase() {
        mDatabase.insert(GameEntry.TABLE_NAME, null, newInput);
        newInput.clear();
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

        // If the table is empty, return the current score as 0.
        // If the table is not empty, calculate the current scire by adding up all the
        // scores in a column
        String count = "SELECT COUNT(*) FROM " + tableName;
        Cursor mcursor = mDatabase.rawQuery(count, null);

        if (mcursor != null) {
            String sql = "SELECT TOTAL(" + player + ") FROM " + tableName;
            long total = DatabaseUtils.longForQuery(mDatabase, sql, null);
            mcursor.close();
            return (int) total;
        } else {
            return 0;
        }
    }


    // Deletes all data in the database but does ot delete the table itself
    private void deleteAllData() {
        mDatabase = mDbHelper.getWritableDatabase();
        //mDatabase.execSQL("delete from " + GameEntry.TABLE_NAME);
        mDatabase.execSQL("delete from " + GameEntry.TABLE_NAME);
        // Reset the _ID to 1
        mDatabase.execSQL("delete from sqlite_sequence where name = 'games'");
    }
}
