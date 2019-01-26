package com.example.android.dartstracker;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class MainActivity extends AppCompatActivity {

    // Main activity layout over which the pop up will appear
    private LinearLayout mLinearLayout;

    // Starting score for the game - updated depending on the user's choice of players
    private int mNumberOfPlayers;

    // The starting values for the game chosen by the user - this is passed via the intent to the
    // appropriate activity. The string variable is the key for passing the int via the intent.
    public static final String mInitialValue = "Initial Value";
    public static final int mThreeHundredAndOne = 301;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If the user chooses a 2 player game
        final Button twoPlayerGame = (Button) findViewById(R.id.twoPlayers);
        twoPlayerGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set the number of players to 2
                mNumberOfPlayers = 2;
                // Initiate the pop up for the user to choose the starting score for the game
                setInitialScorePopup();
            }
        });
    }


    // Create a pop up after the number of players is chosen to ask what starting score
    // for the game is required. Send the starting score to the new activity via the Intent
    public void setInitialScorePopup() {
        Log.d("Value of players: ", Integer.toString(mNumberOfPlayers));
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        // The xml file for the pop up
        View scorePopupView = inflater.inflate(R.layout.set_score_pop_up, null);
        // Create the pop up and set it the xml View
        final PopupWindow popupWindow = new PopupWindow(scorePopupView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // The activity over which the pop up will appear
        mLinearLayout = (LinearLayout) findViewById(R.id.mainActivity);
        // Show the pop up over the activity
        popupWindow.showAtLocation(mLinearLayout, Gravity.CENTER, 0, 0);
    }

    // Opens the appropriate activity depending on the user's choice of number of players.
    // The starting score for the game is passed to the new activity with the intent.
    public void openNewActivity(View view) {
        if (mNumberOfPlayers == 2) {
            // If the user chooses the game to start with a score of 301
            if (view.getId() == R.id.set_score_301) {
                Intent twoPlayerIntent = new Intent(view.getContext(), TwoPlayerGameActivity.class);
                twoPlayerIntent.putExtra(mInitialValue, mThreeHundredAndOne);
                startActivity(twoPlayerIntent);
            }
        }
    }
}
