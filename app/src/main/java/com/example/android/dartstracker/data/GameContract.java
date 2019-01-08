package com.example.android.dartstracker.data;

/*
* API Contract for the Darts game app
* */

import android.net.Uri;
import android.provider.BaseColumns;

public final class GameContract {

    private GameContract() {}

    // Content authority that is used to identify the Content Provider.
    public static final String CONTENT_AUTHORITY = "com.example.android.dartstracker";

    // BASE_CONTENT_URI is used with every URI associated with GamesContract.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // This constant stores the path for each of the tables that will be appended to the
    // base content URI.
    public static final String PATH_GAME = "games";


    // Inner class defines constant values for the game database table.
    // Each entry in the table represents a single player
    public static final class GameEntry implements BaseColumns {

    }

}
