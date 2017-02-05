/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private final String Log_tag="[DM]SHAMI "+CatalogActivity.class.getSimpleName();

    PetDbHelper mDbHelper;
    PetCursorAdapter petCursorAdapter;

    private static final int Pet_Loader=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        mDbHelper = new PetDbHelper(this);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        petCursorAdapter=new PetCursorAdapter(this,null);
        ListView pet_list=(ListView)findViewById(R.id.listview);
        View empty = getLayoutInflater().inflate(R.layout.empty_view, null, false);
        addContentView(empty, new AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT, AppBarLayout.LayoutParams.MATCH_PARENT));
        pet_list.setEmptyView(empty);
        pet_list.setAdapter(petCursorAdapter);

        pet_list.setOnItemClickListener(new AdapterView.OnItemClickListener(

        ) {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent=new Intent(CatalogActivity.this,EditorActivity.class);
                Uri currentPetUri= ContentUris.withAppendedId(PetContract.petEntry.CONTENT_URI,id);
                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });
        getSupportLoaderManager().initLoader(Pet_Loader,null,this);
       // displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {

        String[] projection=
                {
                        PetContract.petEntry._ID,
                        PetContract.petEntry.COLUMN_PET_NAME,
                        PetContract.petEntry.COLUMN_PET_BREED,
                        PetContract.petEntry.COLUMN_PET_GENDER,
                        PetContract.petEntry.COLUMN_PET_WEIGHT
                };

        Cursor cursor=getContentResolver().query(PetContract.petEntry.CONTENT_URI,projection,null,null,null);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insert_dumpy();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void insert_dumpy()
    {
        Uri mNewUri;
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PetContract.petEntry.COLUMN_PET_NAME,"TOTO");
        values.put(PetContract.petEntry.COLUMN_PET_BREED,"Terrier");
        values.put(PetContract.petEntry.COLUMN_PET_GENDER,PetContract.petEntry.GENDER_MALE);
        values.put(PetContract.petEntry.COLUMN_PET_WEIGHT,7);

        mNewUri=getContentResolver().insert(PetContract.petEntry.CONTENT_URI,values);
        displayDatabaseInfo();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection=
                {
                        PetContract.petEntry._ID,
                        PetContract.petEntry.COLUMN_PET_NAME,
                        PetContract.petEntry.COLUMN_PET_BREED,
                        //            PetContract.petEntry.COLUMN_PET_GENDER,
                        //          PetContract.petEntry.COLUMN_PET_WEIGHT
                };
        return new CursorLoader(this,PetContract.petEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        petCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        petCursorAdapter.swapCursor(null);
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deletePet() {
        long rowsEffected=getContentResolver().delete(PetContract.petEntry.CONTENT_URI,null,null);
        Toast.makeText(getApplicationContext(),"Record is Deleted",Toast.LENGTH_SHORT).show();

    }



}