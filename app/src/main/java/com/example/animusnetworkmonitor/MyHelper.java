package com.example.animusnetworkmonitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyHelper extends SQLiteOpenHelper {
    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use for locating paths to the the database
     * @param name    of the database file, or null for an in-memory database
     * @param factory to use for creating cursor objects, or null for the default
     * @param version number of the database (starting at 1); if the database is older,
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    private  static final String name="myDatabase";
    private static final int version=1;

    public MyHelper(Context context) {
        super(context, name, null, version);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE DEVICES (_id INTEGER PRIMARY KEY, IP TEXT, DESCRIPTION TEXT, STATUS TEXT)";
        db.execSQL(sql);

        //insert
        insertData("192.168.1.0","My PC","DOWN",db);
    }

    public void insertData(String ip,String desc,String status,SQLiteDatabase database){
        ContentValues values = new ContentValues();
        values.put("IP",ip);
        values.put("DESCRIPTION",desc);
        values.put("STATUS",status);
        database.insert("DEVICES",null,values);
    }

    public Cursor getDataCursor(SQLiteDatabase database) {
        //getData
        Cursor cursor = database.rawQuery("SELECT _ID,IP,DESCRIPTION,STATUS FROM DEVICES", new String[]{});
        return cursor;
    }
/*
        if (cursor != null)
            cursor.moveToFirst();

        StringBuilder stringBuilder = new StringBuilder();
        //textView.append("Row Count:"+cursor.getCount()+"\n");
        do {
            int id = cursor.getInt(0);
            String ip = cursor.getString(1);
            String desc = cursor.getString(2);
            String status = cursor.getString(3);
            stringBuilder.append("\nID:" + id + " IP" + ip + " DESC:" + desc + " STATUS:" + status);
            images_ArrayList.add("https://cdn.pixabay.com/photo/2020/03/19/04/58/coconut-trees-4946270_960_720.jpg");
            ip_ArrayList.add(ip);
            status_ArrayList.add(status);
            desc_ArrayList.add(desc);
            myDatasetcount++;
        } while (cursor.moveToNext());
        cursor.close();
*/

        /**
         * Called when the database needs to be upgraded. The implementation
         * should use this method to drop tables, add tables, or do anything else it
         * needs to upgrade to the new schema version.
         *
         * <p>
         * The SQLite ALTER TABLE documentation can be found
         * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
         * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
         * you can use ALTER TABLE to rename the old table, then create the new table and then
         * populate the new table with the contents of the old table.
         * </p><p>
         * This method executes within a transaction.  If an exception is thrown, all changes
         * will automatically be rolled back.
         * </p>
         *
         * @param db         The database.
         * @param oldVersion The old database version.
         * @param newVersion The new database version.
         */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
