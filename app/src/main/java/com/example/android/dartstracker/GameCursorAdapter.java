package com.example.android.dartstracker;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.view.LayoutInflater;


// An adapter for a list view in order to show the individual consecutive darts scores for each
// player.
public class GameCursorAdapter extends CursorAdapter {

    // Constructs a new GameCursorAdapter
    public GameCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // Create a blank list item view, no data is set or bound to the view just yet
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(..., parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
