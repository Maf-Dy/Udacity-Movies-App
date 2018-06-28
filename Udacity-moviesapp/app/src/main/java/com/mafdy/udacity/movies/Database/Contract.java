package com.mafdy.udacity.movies.Database;

import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {

    public static final String AUTHORITY = "com.mafdy.udacity.moviesdb";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVOURITES = "favourites";

    public static final class TableEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITES).build();

        public static final String TABLE_NAME = "favouritemovies";


        public static final String COLUMN_MOVIEDBID = "moviedbid";


    }
}
