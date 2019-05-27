package com.example.a15011022_digipad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notes_dp";

    private static final String TABLE_NAME = "NOTES";
    private static final String COL1 = "ID";
    private static final String COL2 = "HEADLINE";
    private static final String COL3 = "CONTENT";
    private static final String COL4 = "ADDRESS";
    private static final String COL5 = "DATE";
    private static final String COL6 = "COLOR";
    private static final String COL7 = "PRIORITY";

    SQLiteDatabase db;

    //create database
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    //table when first run
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " +  TABLE_NAME + "( " + COL1 + "  INTEGER PRIMARY KEY AUTOINCREMENT," +  COL2 + " VARCHAR," + COL3 + " TEXT," +
                COL4 + " VARCHAR," + COL5 + " TEXT," + COL6 +" VARCHAR, " + COL7 +  " SMALLINT);" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //insert a note to database
    public long insertRecod(Not not){
        db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, not.getBaslik());
        contentValues.put(COL3, not.getIcerik());
        contentValues.put(COL4, not.getAdres());
        contentValues.put(COL5, not.getTarih());
        contentValues.put(COL6, not.getRenk());
        contentValues.put(COL7, not.getOncelik());

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        //close connection
        return result;//return note id for further use
    }

    //update an existing note
    public int updateRecord (Not not){
        db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, not.getBaslik());
        contentValues.put(COL3, not.getIcerik());
        contentValues.put(COL4, not.getAdres());
        contentValues.put(COL5, not.getTarih());
        contentValues.put(COL6, not.getRenk());
        contentValues.put(COL7, not.getOncelik());

        int result = db.update(TABLE_NAME, contentValues, COL1 + " =? ",new String[]{ String.valueOf(not.getId()) });
        db.close();
        //close connection
        return result; // return number of affected rows to check
    }

    //delete given note
    public void deleteRecord( Not not){
        db = this.getWritableDatabase();

        db.delete(TABLE_NAME, COL1 + " =?", new String[]{String.valueOf(not.getId())});

        db.close();
    }

    //get single note from databse by its id
    public Not getNote(long id){
        db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,new String[]{COL1,COL2,COL3,COL4,COL5,COL6,COL7},
                COL1 + "=?",new String[]{String.valueOf(id)},null,null,null,null);


        if (  cursor != null )
            cursor.moveToFirst();

        //create a Not instance
        Not not = new Not(
                cursor.getString(cursor.getColumnIndex(COL2)),
                cursor.getString(cursor.getColumnIndex(COL4)),
                cursor.getString(cursor.getColumnIndex(COL3)),
                cursor.getString(cursor.getColumnIndex(COL6)),
                cursor.getInt(cursor.getColumnIndex(COL7)),
                cursor.getString(cursor.getColumnIndex(COL5))
        );
        //set unique note id
        not.setId(id);

        //close cursor and database
        cursor.close();
        db.close();

        //return Non instance
        return not;
    }

    //get all the notes to display in recycler view
    public List<Not> getAllNotes() {
        List<Not> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " +
                COL7 + " DESC";

        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Not not = new Not(                cursor.getString(cursor.getColumnIndex(COL2)),
                        cursor.getString(cursor.getColumnIndex(COL4)),
                        cursor.getString(cursor.getColumnIndex(COL3)),
                        cursor.getString(cursor.getColumnIndex(COL6)),
                        cursor.getInt(cursor.getColumnIndex(COL7)),
                        cursor.getString(cursor.getColumnIndex(COL5)));

                not.setId( cursor.getLong( cursor.getColumnIndex( COL1 ) ) );
                notes.add(not);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    //get the number of all notes in database
    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();
        // return count
        return count;
    }
}
