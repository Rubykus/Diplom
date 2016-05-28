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
    public static final String CAT_NAME = "name";
    private static final String DB_CREATE_CATEGORIES =
            "create table " + DB_CAT + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    CAT_NAME + " text" +
                    ");";

    // create table check
    private static final String DB_CHECK = "check_table";
    public static final String CHECK_DATE = "date_check";
    public static final String CHECK_COST = "total_cost";
    private static final String DB_CREATE_CHECK =
            "create table " + DB_CHECK + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    CHECK_DATE + " text," +
                    CHECK_COST + " real" +
                    ");";

    // create table sales
    private static final String DB_SALE = "sale";
    public static final String SALE_ID_GOODS = "id_goods";
    public static final String SALE_DATE = "date";
    public static final String SALE_ID_CHECK = "id_check";
    private static final String DB_CREATE_SALE =
            "create table " + DB_SALE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    SALE_ID_GOODS + " int," +
                    SALE_DATE + " text," +
                    SALE_ID_CHECK + " int" +
                    ");";

    // create table goods
    private static final String DB_GOOD = "goods";
    public static final String GOOD_NAME = "name";
    public static final String GOOD_ID_CAT = "id_cat";
    public static final String GOOD_COLOR = "color";
    public static final String GOOD_SEX = "sex";
    public static final String GOOD_FIRM = "firm";
    public static final String GOOD_QUANTITY = "quantity";
    public static final String GOOD_PRICE = "price";
    public static final String GOOD_IMAGE = "image";
    private static final String DB_CREATE_GOOD =
            "create table " + DB_GOOD + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    GOOD_NAME + " text," +
                    GOOD_ID_CAT + " int," +
                    GOOD_COLOR + " text," +
                    GOOD_SEX + " text," +
                    GOOD_FIRM + " text," +
                    GOOD_QUANTITY + " int," +
                    GOOD_PRICE + " real," +
                    GOOD_IMAGE + " text" +
                    ");";
    // create methods to with categories
    public Cursor getAllCat() {
        return mDB.query(DB_CAT, null, null, null, null, null, null);
    }
    public void addCat(String name) {
        ContentValues cv = new ContentValues();
        cv.put(CAT_NAME, name);
        mDB.insert(DB_CAT, null, cv);
    }
    public void updateCat(long id, String name) {
        ContentValues cv = new ContentValues();
        cv.put(CAT_NAME, name);
        mDB.update(DB_CAT, cv, COLUMN_ID + " = " + id, null);
    }
    public void delCat(long id) {
        mDB.delete(DB_CAT, COLUMN_ID + " = " + id, null);
    }

    // create methods to with check
    public Cursor getAllCheck() {
        return mDB.query(DB_CHECK, null, null, null, null, null, null);
    }
    public void addCheck(String date, double cost) {
        ContentValues cv = new ContentValues();
        cv.put(CHECK_DATE, date);
        cv.put(CHECK_COST, cost);
        mDB.insert(DB_CHECK, null, cv);
    }
    public void updateCheck(long id, String date, double cost) {
        ContentValues cv = new ContentValues();
        cv.put(CHECK_DATE, date);
        cv.put(CHECK_COST, cost);
        mDB.update(DB_CHECK, cv, COLUMN_ID + " = " + id, null);
    }
    public void delCheck(long id) {
        mDB.delete(DB_CHECK, COLUMN_ID + " = " + id, null);
    }

    // create methods to with sale
    public Cursor getAllSale() {
        return mDB.query(DB_SALE, null, null, null, null, null, null);
    }
    public void addSale(int id_goods, String date, int id_check) {
        ContentValues cv = new ContentValues();
        cv.put(SALE_ID_GOODS, id_goods);
        cv.put(SALE_DATE, date);
        cv.put(SALE_ID_CHECK, id_check);
        mDB.insert(DB_SALE, null, cv);
    }
    public void updateSale(long id, int id_goods, String date, int id_check) {
        ContentValues cv = new ContentValues();
        cv.put(SALE_ID_GOODS, id_goods);
        cv.put(SALE_DATE, date);
        cv.put(SALE_ID_CHECK, id_check);
        mDB.update(DB_SALE, cv, COLUMN_ID + " = " + id, null);
    }
    public void delSale(long id) {
        mDB.delete(DB_SALE, COLUMN_ID + " = " + id, null);
    }

    // create methods to with good
    public Cursor getAllGood() {
        return mDB.query(DB_GOOD, null, null, null, null, null, null);
    }
    public void addGood(String name, int id_cat, String color, String sex,
                        String firm, int quantity, double price, String image) {
        ContentValues cv = new ContentValues();
        cv.put(GOOD_NAME, name);
        cv.put(GOOD_ID_CAT, id_cat);
        cv.put(GOOD_COLOR, color);
        cv.put(GOOD_SEX, sex);
        cv.put(GOOD_FIRM, firm);
        cv.put(GOOD_QUANTITY, quantity);
        cv.put(GOOD_PRICE, price);
        cv.put(GOOD_IMAGE, image);
        mDB.insert(DB_GOOD, null, cv);
    }
    public void updateGood(long id, String name, int id_cat, String color, String sex,
                           String firm, int quantity, double price, String image) {
        ContentValues cv = new ContentValues();
        cv.put(GOOD_NAME, name);
        cv.put(GOOD_ID_CAT, id_cat);
        cv.put(GOOD_COLOR, color);
        cv.put(GOOD_SEX, sex);
        cv.put(GOOD_FIRM, firm);
        cv.put(GOOD_QUANTITY, quantity);
        cv.put(GOOD_PRICE, price);
        cv.put(GOOD_IMAGE, image);
        mDB.update(DB_GOOD, cv, COLUMN_ID + " = " + id, null);
    }
    public void delGood(long id) {
        mDB.delete(DB_GOOD, COLUMN_ID + " = " + id, null);
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
                cv.put(CAT_NAME, cat_name[i]);
                db.insert(DB_CAT, null, cv);
            }
            // create table check
            db.execSQL(DB_CREATE_CHECK);
            String[] check_date = new String[] {"12-9-2015", "12-9-2015", "12-9-2015"};
            double[] cost = new double[]{ 12.12, 144.4, 888.6 };
            ContentValues ch = new ContentValues();
            for (int i = 0; i < 3; i++) {
                ch.put(CHECK_DATE, check_date[i]);
                ch.put(CHECK_COST, cost[i]);
                db.insert(DB_CHECK, null, ch);
            }
            // create table sale
            db.execSQL(DB_CREATE_SALE);
            int[] sale_id_good = new int[] {1,5,9};
            String[] sale_date = new String[] {"12-9-2015", "12-9-2015", "12-9-2015"};
            int[] sale_id_check = new int[] {6,4,7};
            ContentValues cs = new ContentValues();
            for (int i = 0; i < 3; i++) {
                cs.put(SALE_ID_GOODS, sale_id_good[i]);
                cs.put(SALE_DATE, sale_date[i]);
                cs.put(SALE_ID_CHECK, sale_id_check[i]);
                db.insert(DB_SALE, null, cs);
            }
            // create table good
            db.execSQL(DB_CREATE_GOOD);
            String[] good_name = new String[] {"Good1", "Good2", "Good3"};
            int[] good_id_cat = new int[] {5,6,8};
            String[] good_color = new String[] {"red", "blue", "green"};
            String[] good_sex = new String[] {"m", "w", "m"};
            String[] good_firm = new String[] {"adidas", "nice", "puma"};
            int[] good_quantity = new int[] {12,54,78};
            double[] good_price = new double[] {450.12, 87.02, 96.89};
            String[] good_image = new String[] {"img1", "img2", "img3"};
            ContentValues cg = new ContentValues();
            for (int i = 0; i < 3; i++) {
                cg.put(GOOD_NAME, good_name[i]);
                cg.put(GOOD_ID_CAT, good_id_cat[i]);
                cg.put(GOOD_COLOR, good_color[i]);
                cg.put(GOOD_SEX, good_sex[i]);
                cg.put(GOOD_FIRM, good_firm[i]);
                cg.put(GOOD_QUANTITY, good_quantity[i]);
                cg.put(GOOD_PRICE, good_price[i]);
                cg.put(GOOD_IMAGE, good_image[i]);
                db.insert(DB_GOOD, null, cg);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

}
