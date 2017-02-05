package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.pets.data.PetContract;

/**
 * Created by Shami on 2/4/2017.
 */

public class PetCursorAdapter extends CursorAdapter {


    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView petName=(TextView)view.findViewById(R.id.name);
        TextView summary=(TextView)view.findViewById(R.id.summary);

        String Name=cursor.getString(cursor.getColumnIndex(PetContract.petEntry.COLUMN_PET_NAME));
        String breed=cursor.getString(cursor.getColumnIndex(PetContract.petEntry.COLUMN_PET_BREED));

        petName.setText(Name);
        summary.setText(breed);

    }
}
