package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.example.android.pets.data.PetContract.CONTENT_AUTHORITY;
import static com.example.android.pets.data.PetContract.PATH_PETS;

/**
 * Created by Shami on 2/4/2017.
 */

public class PetProvider extends ContentProvider {

    public static final String LOG_TAG ="[DM]SHAMI "+ PetProvider.class.getSimpleName();

    PetDbHelper mDbHelper;
    @Override
    public boolean onCreate() {

        mDbHelper=new PetDbHelper(getContext());
        return true;
    }

    private static final int PETS = 100;

    private static final int PET_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {

        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PETS, PETS);

        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PETS+"/#", PET_ID);


    }



    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor=null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                cursor=database.query(PetContract.petEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case PET_ID:
                selection = PetContract.petEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(PetContract.petEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {
        String name = values.getAsString(PetContract.petEntry.COLUMN_PET_NAME);
        String breed=values.getAsString(PetContract.petEntry.COLUMN_PET_BREED);
        int weight=values.getAsInteger(PetContract.petEntry.COLUMN_PET_WEIGHT);
        if (name == null||breed==null||weight<1) {
            throw new IllegalArgumentException("Pet requires a name");
        }


        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id=db.insert(PetContract.petEntry.TABLE_NAME, null, values);
        Log.v(LOG_TAG,"The Inserted Row ID is "+id);

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);

    }



    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                selection = PetContract.petEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
    }
    }

        private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

            // If the {@link PetEntry#COLUMN_PET_NAME} key is present,

            if (values.containsKey(PetContract.petEntry.COLUMN_PET_NAME)) {
                String name = values.getAsString(PetContract.petEntry.COLUMN_PET_NAME);
                if (name == null) {
                    throw new IllegalArgumentException("Pet requires a name");
                }
            }

            // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
            // check that the gender value is valid.
            if (values.containsKey(PetContract.petEntry.COLUMN_PET_GENDER)) {
                Integer gender = values.getAsInteger(PetContract.petEntry.COLUMN_PET_GENDER);
                if (gender == null) {
                    throw new IllegalArgumentException("Pet requires valid gender");
                }
            }

            if (values.containsKey(PetContract.petEntry.COLUMN_PET_WEIGHT)) {
                // Check that the weight is greater than or equal to 0 kg
                Integer weight = values.getAsInteger(PetContract.petEntry.COLUMN_PET_WEIGHT);
                if (weight != null && weight < 0) {
                    throw new IllegalArgumentException("Pet requires valid weight");
                }
            }

            if (values.size() == 0) {
                return 0;
            }
            int mRowsUpdated=0;
            SQLiteDatabase database=mDbHelper.getWritableDatabase();
            mRowsUpdated=database.update(PetContract.petEntry.TABLE_NAME,values,selection,selectionArgs);
            if (mRowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return mRowsUpdated;
        }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                rowsDeleted=database.delete(PetContract.petEntry.TABLE_NAME, selection, selectionArgs);
                if(rowsDeleted!=0)
                {  getContext().getContentResolver().notifyChange(uri, null);}
                return rowsDeleted;
            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = PetContract.petEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted=database.delete(PetContract.petEntry.TABLE_NAME, selection, selectionArgs);
                if(rowsDeleted!=0)
                {  getContext().getContentResolver().notifyChange(uri, null);}
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }



    }
    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetContract.petEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetContract.petEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
