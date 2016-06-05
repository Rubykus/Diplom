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

    // create table sales
    private static final String DB_SALE = "sale";
    public static final String SALE_DATE = "date";
    public static final String SALE_LIST_GOOD = "info";
    public static final String SALE_SUM = "sum";
    private static final String DB_CREATE_SALE =
            "create table " + DB_SALE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    SALE_DATE + " text," +
                    SALE_LIST_GOOD + " text," +
                    SALE_SUM + " real" +
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

    // create methods to with sale
    public Cursor getAllSale() {
        return mDB.query(DB_SALE, null, null, null, null, null, null);
    }
    public void addSale(String date, String info, double sum) {
        ContentValues cv = new ContentValues();
        cv.put(SALE_DATE, date);
        cv.put(SALE_LIST_GOOD, info);
        cv.put(SALE_SUM, sum);
        mDB.insert(DB_SALE, null, cv);
    }
    public void delSale(long id) {
        mDB.delete(DB_SALE, COLUMN_ID + " = " + id, null);
    }

    // create methods to with good
    public Cursor getAllGood() {
        return mDB.query(DB_GOOD, null, null, null, null, null, null);
    }
    public Cursor getAllGoodByCat(int id_cat) {
        return mDB.query(DB_GOOD, null, GOOD_ID_CAT+"=?", new String[]{String.valueOf(id_cat)}, null, null, null);
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
            String[] cat_name = new String[] {"Шапка", "Кепка", "Шляпа", "Ушанка", "Бререт"};
            ContentValues cv = new ContentValues();
            for (int i = 0; i < 5; i++) {
                cv.put(CAT_NAME, cat_name[i]);
                db.insert(DB_CAT, null, cv);
            }
            // create table sale
            db.execSQL(DB_CREATE_SALE);
            // create table good
            db.execSQL(DB_CREATE_GOOD);
            String[] good_name = new String[] {"Lonsdale", "DAKINE Chase", "DAKINE Scruntch", "AC ZIGGY BEANIE", "Finn Flare", "Dakine Taryn",
                    "Бейсболка Traum","Converse All Star","Goorin Brothers","True Spin","Fete Бейсболка","Seafolly",
                    "Kent & Aver","Шляпа ALDO","DEL MARE","Beretta","Eterno","River Island",
                    "Celina Crystal","PARADISE WINTER","GULLIVER","Garcia Fleece","Gamakatsu","Salmo",
                    "Canoe","Dakine","Top Shop","Ferz","To be Queen","Paola Belleza"};
            String[] good_sex = new String[] {"женский","универсальный","мужской","женский","мужской","мужской",
                    "универсальный","универсальный","универсальный","универсальный","универсальный","женский",
                    "женский","мужской","женский","мужской","женский","женский",
                    "женский","женский","мужской","мужской","мужской","мужской",
                    "женский","женский","женский","женский","женский","женский"};
            String[] good_firm = new String[] {"Lonsdale","DAKINE","DAKINE","O`Neill","Finn Flare","DAKINE",
                    "Traum","Converse","Goorin Bros","True Spin","Fete","Seafolly",
                    "Kent & Aver","ALDO","DEL MARE","Beretta","Eterno","River Island",
                    "Eisbar","ROXY","Dakine","Abu","Gamakatsu","Salmo",
                    "Canoe","Dakine","Top Shop","Ferz","To be Queen","Paola Belleza"};
            int[] good_id_cat = new int[] {1,1,1,1,1,1,
                    2,2,2,2,2,2,
                    3,3,3,3,3,3,
                    4,4,4,4,4,4,
                    5,5,5,5,5,5};
            String[] good_color = new String[] {"мультиколор","мультиколор","чёрный","чёрный","синий","чёрный",
                    "мультиколор","синий","мультиколор","синий","белый","бежевый",
                    "мультиколор","серый","мультиколор","зеленый","коричневый","бежевый",
                    "синий","чёрный","серый","зеленый","чёрный","зеленый",
                    "чёрный","чёрный","бежевый","чёрный","чёрный","бежевый"};
            int[] good_quantity = new int[] {5,15,4,21,16,34,
                    21,12,32,22,25,23,
                    23,61,45,12,74,13,
                    32,15,45,23,65,9,
                    10,20,30,14,51,69};
            double[] good_price = new double[] {197,249,599,274,254,689,
                    196,239,950,132,314,790,
                    239,399,199,1366,233,1031,
                    2569,1000,1129,423,332,125,
                    284,729,650,790,371,255};
            String[] good_image = new String[] {"Download/lonsdale.jpg","Download/dakine_chase.jpg","Download/dakine_scruntch.jpg","Download/ac_ziggy.jpg","Download/finn_flare.jpg","Download/dakine_taryn.jpg",
                    "Download/traum.jpg","Download/converse.jpg","Download/goorin_bros.jpg","Download/true_spin.jpg","Download/fete.jpg","Download/seafolly.jpg",
                    "Download/kent_aver.jpg","Download/aldo.jpg","Download/del_mare.jpg","Download/beretta.jpg","Download/eterno.jpg","Download/river.jpg",
                    "Download/eisbar.jpg","Download/roxy.jpg","Download/dakine.jpg","Download/garcia.jpg","Download/gamakatsu.jpg","Download/salmo.jpg",
                    "Download/canoe_b.jpg","Download/dakine_b.jpg","Download/top.jpg","Download/ferz_b.jpg","Download/to_be.jpg","Download/paola.jpg"};
            ContentValues cg = new ContentValues();
            for (int i = 0; i < good_name.length; i++) {
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
