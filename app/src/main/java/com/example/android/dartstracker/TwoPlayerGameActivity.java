package com.example.android.dartstracker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
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

// TODO: IF THE SCORE IS TOO BIG AND GOES PAST ZERO, AT THE MINUTE IT GIVES THE PLAYER ANOTHER GO -
// NEED TO CHANGE THIS

// TODO: Add a reset button on the top of the activity UI

// TOTO: Add a button to the top to select the inital score

// TODO: The player turn needs to be remembered if the activity is changed, also the
// heading fonts

public class TwoPlayerGameActivity extends AppCompatActivity {

    // Current score of Player 1
    private TextView mPlayerOneCurrentScore;
    // Current score of Player 2
    private TextView mPlayerTwoCurrentScore;
    // Edit text field for the score inputted by the user
    private EditText mScoreEditText;
    // Player 1 title which will change depending on whose turn it is
    private TextView mPlayerOneTitle;
    // Player 2 title which will change depending on whose turn it is
    private TextView mPlayerTwoTitle;

    // Activity linear layout
    private LinearLayout mLinearLayout;
    // Button on the pop up to return to the main activity after game is over
    private Button mReturnToMainActivityButton;
    // Button on the popup to replay a two player game after game is won
    private Button mReplayButton;

    // Initial score for the game - can add different options
    private int mInitialScore = 501;
    // The score input by Player 1 is saved in this variable
    private int mPlayerOneScore;
    // The score input by Player 2 is saved in this variable
    private int mPlayerTwoScore;

    // Integers to track whose turn is next
    private int PLAYER_ONE_TURN = 1;
    private int PLAYER_TWO_TURN = 2;
    private int mCurrentTurn;

    // String to get all the scores from the SQLite database
    private String mDatabaseQuery = "SELECT * FROM " + GameEntry.TABLE_NAME + ";";
    // Score input by the user
    private String mScore;

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

        // Set the current turn to Player 1
        mCurrentTurn = PLAYER_ONE_TURN;

        // Set the scores to the UI - upon creation it will be the initial score,
        // but if the user leaves and returns to the game it should show the current score
        setScore();

        alterHeadingStyle(PLAYER_ONE_TURN);

        // A cursor to hold the data from the database in order to send to the adapter
        Cursor cursor = mDatabase.rawQuery(mDatabaseQuery, null);
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
                mScore = mScoreEditText.getText().toString();

                // If the user input is not null, continue
                if (mScore != null && !mScore.isEmpty()) {
                    // Reset the score on the user interface to empty
                    mScoreEditText.setText("");

                    // Calculate the current score for each player
                    int currentTotalPlayerOne = currentScore(
                            GameEntry.COLUMN_PLAYER_ONE, GameEntry.TABLE_NAME);
                    int currentTotalPlayerTwo = currentScore(
                            GameEntry.COLUMN_PLAYER_TWO, GameEntry.TABLE_NAME);

                    // If it is Player 1's turn...
                    if (mCurrentTurn == 1) {
                        // Check to see if the game is over
                        // i.e. if the initial socre (501) minus the current total (451) is
                        // equal to the input score (50), then the total is 0 and then game
                        // is won.
                        if (mInitialScore - currentTotalPlayerOne == Integer.parseInt(mScore)) {
                            gameIsWon();
                            return;
                        }
                        // Check to see if the input will make the score < 0 and return if true
                        if (!isTheScoreValid(currentTotalPlayerOne, mScore)) {
                            Toast.makeText(getBaseContext(), R.string.scoreOverZero,
                                    Toast.LENGTH_LONG).show();
                            // If the score goes over 0, then the player loses their turn and the
                            // score "0" is put into the ContentValue
                            mCurrentTurn = 2;
                            alterHeadingStyle(PLAYER_TWO_TURN);
                            insertPlayerOneScore("0");
                            return;
                        }

                        // Check to see if the input is a valid number between 0 and 180 and
                        // return if not
                        if (!isInteger(mScore)) {
                            Toast.makeText(getBaseContext(), R.string.validScoreInput,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        // If the input is valid then insert the score to the ContentValue object
                        insertPlayerOneScore(mScore);
                        // Change the headings to show it is player 2's turn
                        alterHeadingStyle(PLAYER_TWO_TURN);


                        // If it is Player 2's turn...
                    } else if (mCurrentTurn == 2) {
                        // Check to see if the game is over
                        // i.e. if the initial socre (501) minus the current total (451) is
                        // equal to the input score (50), then the total is 0 and then game
                        // is won.
                        if (mInitialScore - currentTotalPlayerTwo == Integer.parseInt(mScore)) {
                            gameIsWon();
                            return;
                        }
                        // Check to see if the input will make the score < 0 and return if true
                        if (!isTheScoreValid(currentTotalPlayerTwo, mScore)) {
                            Toast.makeText(getBaseContext(), R.string.scoreOverZero,
                                    Toast.LENGTH_LONG).show();
                            // If the score goes over 0, then the player loses their turn and the
                            // score "0" is put into the ContentValue
                            mCurrentTurn = 1;
                            alterHeadingStyle(PLAYER_ONE_TURN);
                            insertPlayerTwoScore("0");
                            // Insert the ContentValue object into the SQLite database
                            insertScoresIntoDatabase();
                            // Update the adapter to show the up to date list of scores in the UI
                            updateAdapter();
                            return;
                        }

                        // Check to see if the input is a valid number between 0 and 180 and
                        // return if not
                        if (!isInteger(mScore)) {
                            Toast.makeText(getBaseContext(), R.string.validScoreInput,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // If the input is valid then insert the score to the ContentValue object
                        insertPlayerTwoScore(mScore);
                        // Change the heading to show it is player 1's turn again
                        alterHeadingStyle(PLAYER_ONE_TURN);

                        // Insert the ContentValue object into the SQLite database
                        insertScoresIntoDatabase();
                        // Update the adapter to show the up to date list of scores in the UI
                        updateAdapter();

                        // Calculate the current score for each player after the score
                        // insertion into database and set the new score to the UI
                        int newCurrentTotalPlayerOne = currentScore(
                                GameEntry.COLUMN_PLAYER_ONE, GameEntry.TABLE_NAME);
                        mPlayerOneScore = mInitialScore - newCurrentTotalPlayerOne;
                        mPlayerOneCurrentScore.setText(Integer.toString(mPlayerOneScore));
                        int newCurrentTotalPlayerTwo = currentScore(
                                GameEntry.COLUMN_PLAYER_TWO, GameEntry.TABLE_NAME);
                        mPlayerTwoScore = mInitialScore - newCurrentTotalPlayerTwo;
                        mPlayerTwoCurrentScore.setText(Integer.toString(mPlayerTwoScore));
                    }

                } else {
                    Toast.makeText(getBaseContext(), R.string.validScoreInput,
                            Toast.LENGTH_LONG).show();
                }
                // Reset the score to null
                mScore = null;
            }
        });
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int savedPlayerOneScore = savedInstanceState.getInt("mPlayerOneScore");
        Log.d("VALUE OF SAVED NO: ", Integer.toString(savedPlayerOneScore));
    }


    // Method to alter the heading and score in the xml file, depending on whose turn it is
    private void alterHeadingStyle(int currentTurn) {
        if (currentTurn == 1) {
            // Set the player 1 title to Bold to indicate it is player one's turn
            mPlayerOneTitle = (TextView) findViewById(R.id.playerOneTitle);
            mPlayerOneTitle.setTypeface(null, Typeface.BOLD);
            mPlayerOneTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f);
            mPlayerOneCurrentScore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f);

            // Set the player 2 title to normal
            mPlayerTwoTitle = (TextView) findViewById(R.id.playerTwoTitle);
            mPlayerTwoTitle.setTypeface(null, Typeface.NORMAL);
            mPlayerTwoTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f);
            mPlayerTwoCurrentScore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f);
        }

        if (currentTurn == 2) {
            // Set the player 2 title to Bold to indicate it is player one's turn
            mPlayerTwoTitle = (TextView) findViewById(R.id.playerTwoTitle);
            mPlayerTwoTitle.setTypeface(null, Typeface.BOLD);
            mPlayerTwoTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f);
            mPlayerTwoCurrentScore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f);

            // Set the player 1 title to normal
            mPlayerOneTitle = (TextView) findViewById(R.id.playerOneTitle);
            mPlayerOneTitle.setTypeface(null, Typeface.NORMAL);
            mPlayerOneTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f);
            mPlayerOneCurrentScore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f);
        }
    }


    // When the game is won, a pop up appears on the activity with Buttons to replay the game
    // or to go back to the main screen
    private void showPopup() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        // The xml file for the pop up
        View popupView = inflater.inflate(R.layout.game_finish_pop_up, null);
        // Create the pop up and set it the xml View
        final PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // The activity over which the pop up will appear is
        mLinearLayout = (LinearLayout) findViewById(R.id.linear_layout_two_player);
        // Show the pop up over the activity
        popupWindow.showAtLocation(mLinearLayout, Gravity.CENTER, 0,0);

        // Add a close button onto the popup
        mReturnToMainActivityButton = (Button) popupView.findViewById(R.id.returnToMainActivityButton);
        mReturnToMainActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When the close button is pressed, the pop up disappears
                popupWindow.dismiss();
                // The data from the previous game is deleted
                deleteAllData();
                // Return to main activity
                Intent returnToMainActivityIntent = new Intent(view.getContext(), MainActivity.class);
                startActivity(returnToMainActivityIntent);
            }
        });

        // Add a replay button to replay a two player game
        mReplayButton = (Button) popupView.findViewById(R.id.replayButton);
        mReplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When the close button is pressed, the pop up disappears
                popupWindow.dismiss();
                // The data from the previous game is deleted
                deleteAllData();
                recreate();
            }
        });
    }


    private void gameIsWon() {
        showPopup();
    }


    private void setScore() {
        // Set the initial scores in the app interface to 501 for each player
        mPlayerOneCurrentScore = findViewById(R.id.playerOneScore);
        mPlayerTwoCurrentScore = findViewById(R.id.playerTwoScore);

        int newCurrentTotalPlayerOne = currentScore(
                GameEntry.COLUMN_PLAYER_ONE, GameEntry.TABLE_NAME);
        mPlayerOneScore = mInitialScore - newCurrentTotalPlayerOne;
        mPlayerOneCurrentScore.setText(Integer.toString(mPlayerOneScore));
        int newCurrentTotalPlayerTwo = currentScore(
                GameEntry.COLUMN_PLAYER_TWO, GameEntry.TABLE_NAME);
        mPlayerTwoScore = mInitialScore - newCurrentTotalPlayerTwo;
        mPlayerTwoCurrentScore.setText(Integer.toString(mPlayerTwoScore));
    }


    // When the scores are input by the user, the cursor gets the new additional info from the
    // database and it is set to the adapter so the new scores are added to the ListView.
    private void updateAdapter() {
        Cursor newCursor = mDatabase.rawQuery(mDatabaseQuery, null);
        mAdapter.swapCursor(newCursor);
    }


    // If the final shot is not a double then return false, whilst also changing
    // the players turn as in Darts a failed double on a final shot counts as a turn
    // This only applies if each of the 3 shots is input individually,
    // otherwise there is no way to know if the last shot was a double
    private boolean isTheFinalScoreADouble(int currentScore, String newScore) {
        int newTotalScore = mInitialScore - currentScore - Integer.parseInt(newScore);
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


    // Is the score a valid one - if the new total score is less than zero it returns false
    private boolean isTheScoreValid(int currentScore, String newScore) {
        int newTotalScore = mInitialScore - currentScore - Integer.parseInt(newScore);
        return (newTotalScore > -1);
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
