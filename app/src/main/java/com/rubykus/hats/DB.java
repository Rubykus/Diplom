package com.rubykus.hats;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB {

    // create constants name and version
    private static final String DB_NAME = "mydb";
    private static final int DB_VERSION = 1;
    public static final String COLUMN_ID = "_id";
    // create table categories
    private static final String DB_CAT = "categories";
    public static final String COLUMN_NAME = "name";
    private static final String DB_CREATE_CATEGORIES =
            "create table " + DB_CAT + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_NAME + " text" +
                    ");";

    // create table check
    private static final String DB_CHECK = "check_table";
    public static final String COLUMN_DATA_CHECK = "data_check";
    public static final String COLUMN_TOTAL_COST = "total_cost";
    private static final String DB_CREATE_CHECK =
            "create table " + DB_CHECK + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_DATA_CHECK + " text," +
                    COLUMN_TOTAL_COST + " real" +
                    ");";

    // create methods to with categories
    public Cursor getAllCat() {
        return mDB.query(DB_CAT, null, null, null, null, null, null);
    }
    public void addCat(String name) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        mDB.insert(DB_CAT, null, cv);
    }
    public void delCat(long id) {
        mDB.delete(DB_CAT, COLUMN_ID + " = " + id, null);
    }

    // create methods to with check
    public Cursor getAllCheck() {
        return mDB.query(DB_CHECK, null, null, null, null, null, null);
    }
    public void addCheck(String date, Float cost) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DATA_CHECK, date);
        cv.put(COLUMN_TOTAL_COST, cost);
        mDB.insert(DB_CHECK, null, cv);
    }
    public void delCheck(long id) {
        mDB.delete(DB_CHECK, COLUMN_ID + " = " + id, null);
    }

    // create a variable to work with DBHelper
    private final Context mCtx;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;
    public DB(Context ctx) {
        mCtx = ctx;
    }

    // open connection
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // close connection
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }


    // create DBHelper
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // create db
        @Override
        public void onCreate(SQLiteDatabase db) {

            // create table categories
            db.execSQL(DB_CREATE_CATEGORIES);
            String[] cat_name = new String[] {"Ушанка", "Бандана", "Бейсболка", "Шляпа", "Классическая шапка"};
            ContentValues cv = new ContentValues();
            for (int i = 0; i < 5; i++) {
                cv.put(COLUMN_NAME, cat_name[i]);
                db.insert(DB_CAT, null, cv);
            }
            // create table check
            db.execSQL(DB_CREATE_CHECK);
            String[] check_date = new String[] {"12-9-2015", "12-9-2015", "12-9-2015"};
            double[] cost = new double[]{ 12.12, 144.4, 888.6 };
            ContentValues ch = new ContentValues();
            for (int i = 0; i < 3; i++) {
                ch.put(COLUMN_DATA_CHECK, check_date[i]);
                ch.put(COLUMN_TOTAL_COST, cost[i]);
                db.insert(DB_CHECK, null, ch);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

}
