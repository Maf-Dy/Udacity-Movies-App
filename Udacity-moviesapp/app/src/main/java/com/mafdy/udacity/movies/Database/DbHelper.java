package com.mafdy.udacity.movies.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mafdyfavouritemovies.db";

    private static final int VERSION = 1;


   public  DbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE = "CREATE TABLE " + Contract.TableEntry.TABLE_NAME + " (" +
                Contract.TableEntry._ID + " INTEGER PRIMARY KEY, " +
                Contract.TableEntry.COLUMN_MOVIEDBID + " INTEGER UNIQUE NOT NULL " + ");";

        db.execSQL(CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Contract.TableEntry.TABLE_NAME);
        onCreate(db);
    }
}
