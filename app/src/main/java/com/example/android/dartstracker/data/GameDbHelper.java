package com.example.android.dartstracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.dartstracker.data.GameContract.GameEntry;

/*
* Database helper for the Darts app. Manages database creation.
* */
public class GameDbHelper extends SQLiteOpenHelper {

    // Name of the SQLite database
    public static final String DATABASE_NAME = "game.db";
    // Database version, starts at 1
    public static final int DATABASE_VERSION = 1;
    // String input into SQLite to delete the table
    public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + GameEntry.TABLE_NAME;

    // Constructor
    public GameDbHelper(Context context) {
        super(context,
                DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // String input into SQLite to create the database
        // using constants from the Contract class
        String CREATE_TABLE = "CREATE TABLE " + GameEntry.TABLE_NAME + " ("
                + GameEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GameEntry.COLUMN_PLAYER_ONE + " INTEGER, "
                + GameEntry.COLUMN_PLAYER_TWO + " INTEGER, "
                + GameEntry.COLUMN_PLAYER_THREE + " INTEGER, "
                + GameEntry.COLUMN_PLAYER_FOUR + " INTEGER);";

        // Create the database
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE);
        onCreate(db);
    }
}
