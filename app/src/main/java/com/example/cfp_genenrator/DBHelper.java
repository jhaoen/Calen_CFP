package com.example.cfp_genenrator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    //TASK 1: DEFINE THE DATABASE AND TABLE
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DisplayInfo";
    private static final String DATABASE_TABLE = "ListInfo";


    //TASK 2: DEFINE THE COLUMN NAMES FOR THE TABLE

    private static final String ID = "_id";
    private static final String EVENT = "event";
    private static final String STARTDAY = "time";

    private static final String WHERE = "location";

    private static final String COUNTRY = "country";

    private static final String DESTCODE = "destcode";

    private static int count;

    public DBHelper (Context context){
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database){

        if (!isTableExists(database)) {
            String table = "CREATE TABLE " + DATABASE_TABLE + "("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + EVENT + " TEXT,"
                    + STARTDAY + " TEXT,"
                    + WHERE + " TEXT,"
                    + COUNTRY + " TEXT,"
                    + DESTCODE + " TEXT" + ")";
            database.execSQL(table);
            count = 0;
        }
    }

    private boolean isTableExists(SQLiteDatabase database) {
        Cursor cursor = database.rawQuery(
                "SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + DATABASE_TABLE + "'",
                null
        );
        boolean tableExists = cursor.getCount() > 0;
        cursor.close();
        return tableExists;
    }
    @Override
    public void onUpgrade(SQLiteDatabase database,
                          int oldVersion,
                          int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(database);
    }

    //********** DATABASE OPERATIONS:  ADD, EDIT, DELETE


    public void addUser(String[] info) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(EVENT,info[0]);

        values.put(STARTDAY, info[1]);

        values.put(WHERE, info[2]);

        values.put(COUNTRY, info[3]);

        values.put(DESTCODE,info[4]);

        db.insert(DATABASE_TABLE, null, values);

        db.close();
    }

    public DisplayInfo getUser(String Event) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                DATABASE_TABLE,
                new String[]{EVENT, STARTDAY, WHERE, COUNTRY, DESTCODE},
                EVENT + "=?",
                new String[]{Event},
                null, null, null, null );

        if (cursor != null)
            cursor.moveToFirst();


        DisplayInfo info = new DisplayInfo();
        info.Event = cursor.getString(cursor.getColumnIndex("event"));
        info.StartDay = cursor.getString(cursor.getColumnIndex("time"));
        info.Where = cursor.getString(cursor.getColumnIndex("country"));
        info.arrivecountry = cursor.getString(cursor.getColumnIndex("location"));
        info.destcode = cursor.getString(cursor.getColumnIndex("destcode"));

        db.close();
        return info;
    }


    public int getTaskCount() {
        return count;
    }

    public ArrayList<DisplayInfo> getAllUser() {

        ArrayList<DisplayInfo> taskList = new ArrayList<DisplayInfo>();
        String queryList = "SELECT * FROM " + DATABASE_TABLE;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(queryList, null);

        //COLLECT EACH ROW IN THE TABLE
        if (cursor.moveToFirst()){
            do {
                DisplayInfo info = new DisplayInfo();
                info.Event = cursor.getString(cursor.getColumnIndex("event"));
                info.StartDay = cursor.getString(cursor.getColumnIndex("time"));
                info.Where = cursor.getString(cursor.getColumnIndex("location"));
                info.arrivecountry = cursor.getString(cursor.getColumnIndex("country"));
                info.destcode = cursor.getString(cursor.getColumnIndex("destcode"));


                //ADD TO THE QUERY LIST
                taskList.add(info);
            } while (cursor.moveToNext());
        }
        return taskList;
    }

}
