package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Shami on 2/3/2017.
 */

public class PetDbHelper extends SQLiteOpenHelper {

    public static final String Log_Tag="[DM]Shami "+PetDbHelper.class.getSimpleName();

    ////
    private static final String Database_Name="shelter.db";

    private static final int Database_version=1;

    public PetDbHelper(Context context) {
        super(context, Database_Name, null, Database_version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + PetContract.petEntry.TABLE_NAME + " ("
                + PetContract.petEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PetContract.petEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "
                + PetContract.petEntry.COLUMN_PET_BREED + " TEXT, "
                + PetContract.petEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "
                + PetContract.petEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
