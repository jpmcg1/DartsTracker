package com.example.android.dartstracker;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.example.android.dartstracker.data.GameContract.GameEntry;

import org.w3c.dom.Text;


// An adapter for a list view in order to show the individual consecutive darts scores for each
// player.
public class TwoPlayerGameCursorAdapter extends CursorAdapter {

    // Constructs a new TwoPlayerGameCursorAdapter
    public TwoPlayerGameCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // Create a blank list item view, no data is set or bound to the view just yet
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_two_players,
                parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView playerOneAttemptNumber = (TextView) view.findViewById(R.id.player_one_attempt_number);
        TextView playerTwoAttemptNumber = (TextView) view.findViewById(R.id.player_two_attempt_number);
        TextView playerOneScore = (TextView) view.findViewById(R.id.player_one_score);
        TextView playerTwoScore = (TextView) view.findViewById(R.id.player_two_score);

        String playerOneId = cursor.getString(cursor.getColumnIndex(GameEntry._ID));
        String playerOneAmount = cursor.getString(cursor.getColumnIndex(GameEntry.COLUMN_PLAYER_ONE));
        String playerTwoId = cursor.getString(cursor.getColumnIndex(GameEntry._ID));
        String playerTwoAmount = cursor.getString(cursor.getColumnIndex(GameEntry.COLUMN_PLAYER_TWO));

        playerOneAttemptNumber.setText(playerOneId);
        playerTwoAttemptNumber.setText(playerTwoId);
        playerOneScore.setText(playerOneAmount);
        playerTwoScore.setText(playerTwoAmount);
    }
}
