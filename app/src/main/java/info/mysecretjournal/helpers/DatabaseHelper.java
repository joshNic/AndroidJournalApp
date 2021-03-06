package info.mysecretjournal.helpers;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import info.mysecretjournal.model.Entry;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "entries_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Entry.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Entry.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertEntry(String entryTitle, String entryBody ) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Entry.COLUMN_ENTRY_TITLE, entryTitle);
        values.put(Entry.COLUMN_ENTRY_BODY, entryBody);

        // insert row
        long id = db.insert(Entry.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Entry getEntry(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Entry.TABLE_NAME,
                new String[]{Entry.COLUMN_ID, Entry.COLUMN_ENTRY_TITLE, Entry.COLUMN_ENTRY_BODY, Entry.COLUMN_TIMESTAMP},
                Entry.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare entry object
        Entry entry = new Entry(
                cursor.getInt(cursor.getColumnIndex(Entry.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Entry.COLUMN_ENTRY_TITLE)),
                cursor.getString(cursor.getColumnIndex(Entry.COLUMN_ENTRY_BODY)),
                cursor.getString(cursor.getColumnIndex(Entry.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        // return the entry object
        return entry;
    }

    public List<Entry> getAllEntries() {
        List<Entry> entries = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Entry.TABLE_NAME + " ORDER BY " +
                Entry.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Entry entry = new Entry();
                entry.setId(cursor.getInt(cursor.getColumnIndex(Entry.COLUMN_ID)));
                entry.setEntryTitle(cursor.getString(cursor.getColumnIndex(Entry.COLUMN_ENTRY_TITLE)));
                entry.setEntryBody(cursor.getString(cursor.getColumnIndex(Entry.COLUMN_ENTRY_BODY)));
                entry.setTimestamp(cursor.getString(cursor.getColumnIndex(Entry.COLUMN_TIMESTAMP)));

                entries.add(entry);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return entries list
        return entries;
    }

    public int getEntriesCount() {
        String countQuery = "SELECT  * FROM " + Entry.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateEntry(Entry entry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Entry.COLUMN_ENTRY_TITLE, entry.getEntryTitle());
        values.put(Entry.COLUMN_ENTRY_BODY, entry.getEntryBody());

        // updating row
        return db.update(Entry.TABLE_NAME, values, Entry.COLUMN_ID + " = ?",
                new String[]{String.valueOf(entry.getId())});
    }

    public void deleteEntry(Entry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Entry.TABLE_NAME, Entry.COLUMN_ID + " = ?",
                new String[]{String.valueOf(entry.getId())});
        db.close();
    }
}
