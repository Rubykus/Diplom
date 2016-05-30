package com.rubykus.hats;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;

public class Good extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CM_DELETE_ID = 1;
    private static final int CM_UPDATE_ID = 2;
    ListView lv;
    static DB db;
    MyCursorAdapter scAdapter;
    // for dialog cat
    int index_cat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // open a connection to the db
        db = new DB(this);
        db.open();

        // forming matching columns
        String[] from = new String[] {};
        int[] to = new int[] { R.id.nameGood, R.id.descrGood, R.id.quantityGood, R.id.imageGood};

        // create adapter and customizable list
        scAdapter = new MyCursorAdapter(this, R.layout.item_good, null, from, to, 0);
        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(scAdapter);

        // add context menu for list
        registerForContextMenu(lv);

        // create loader for reading data
        getSupportLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    // initialize dialog good list
    public void showDialogInner(final TextView tv){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Cursor cur = db.getAllCat();
        int countRow = cur.getCount();
        final String[] cat_name = new String[countRow];
        final int[] cat_id = new int[countRow];
        for (int i = 0; i < countRow; i++) {
            cur.moveToNext();
            int index = cur.getInt(cur.getColumnIndex(DB.COLUMN_ID));
            String val = cur.getString(cur.getColumnIndex(DB.CAT_NAME));
            cat_name[i] = val;
            cat_id[i] = index;
        }
        builder.setTitle("Выберите категорию")
                .setItems(cat_name, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        tv.setText(cat_name[which]);
                        index_cat = cat_id[which];
                    }
                });
        AlertDialog dialog_choose = builder.create();
        dialog_choose.show();
    }
    // get name good
    public static String getNameCat(int idCat){
        Cursor cur = db.getAllCat();
        int countRow = cur.getCount();
        HashMap<Integer,String> dataCat = new HashMap<>();
        for (int i = 0; i < countRow; i++){
            cur.moveToNext();
            int index = cur.getInt(cur.getColumnIndex(DB.COLUMN_ID));
            String val = cur.getString(cur.getColumnIndex(DB.CAT_NAME));
            dataCat.put(index, val);
        }
        return dataCat.get(idCat);
    }
    // initialize dialog
    public void initDialog(Cursor cursor, final long id){
        View view = LayoutInflater.from(Good.this).inflate(R.layout.dialog_good, null);
        final EditText goodName = (EditText)view.findViewById(R.id.goodAddName);
        final TextView goodIdCat = (TextView)view.findViewById(R.id.goodAddCat);
        goodIdCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogInner(goodIdCat);
            }
        });
        final EditText goodColor = (EditText)view.findViewById(R.id.goodAddColor);
        final EditText goodSex = (EditText)view.findViewById(R.id.goodAddSex);
        final EditText goodFirm = (EditText)view.findViewById(R.id.goodAddFirm);
        final EditText goodQuantity = (EditText)view.findViewById(R.id.goodAddQuantity);
        final EditText goodPrice = (EditText)view.findViewById(R.id.goodAddPrice);
        final EditText goodImg = (EditText)view.findViewById(R.id.goodAddImg);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (cursor == null && id == -1) {
            builder.setTitle(R.string.new_sale)
                    .setView(view)
                    .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                String textName = goodName.getText().toString();
                                int idCat = index_cat;
                                String textColor = goodColor.getText().toString();
                                String textSex = goodSex.getText().toString();
                                String textFirm = goodFirm.getText().toString();
                                int textQuantity = Integer.parseInt(goodQuantity.getText().toString());
                                double textPrice = Double.parseDouble(goodPrice.getText().toString());
                                String textImg = goodImg.getText().toString();
                                if ( index_cat == 0 || textName.isEmpty() || textColor.isEmpty() || textSex.isEmpty() ||
                                        textFirm.isEmpty() || textImg.isEmpty()) {
                                    throw new Exception();
                                }
                                db.addGood(textName, idCat, textColor, textSex, textFirm, textQuantity, textPrice, textImg);
                            } catch (Exception e) {
                                Toast.makeText(Good.this, R.string.error_validations, Toast.LENGTH_LONG).show();
                            }
                            index_cat = 0;
                            getSupportLoaderManager().getLoader(0).forceLoad();
                        }
                    })
                    .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
        } else {
            goodName.setText(cursor.getString(cursor.getColumnIndex(DB.GOOD_NAME)));
            final int idCat = cursor.getInt(cursor.getColumnIndex(DB.GOOD_ID_CAT));
            goodIdCat.setText(getNameCat(idCat));
            goodIdCat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogInner(goodIdCat);
                }
            });
            goodColor.setText(cursor.getString(cursor.getColumnIndex(DB.GOOD_COLOR)));
            goodSex.setText(cursor.getString(cursor.getColumnIndex(DB.GOOD_SEX)));
            goodFirm.setText(cursor.getString(cursor.getColumnIndex(DB.GOOD_FIRM)));
            goodQuantity.setText(cursor.getString(cursor.getColumnIndex(DB.GOOD_QUANTITY)));
            goodPrice.setText(cursor.getString(cursor.getColumnIndex(DB.GOOD_PRICE)));
            goodImg.setText(cursor.getString(cursor.getColumnIndex(DB.GOOD_IMAGE)));
            builder.setTitle(R.string.update)
                    .setView(view)
                    .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                String textName = goodName.getText().toString();
                                String textColor = goodColor.getText().toString();
                                String textSex = goodSex.getText().toString();
                                String textFirm = goodFirm.getText().toString();
                                int textQuantity = Integer.parseInt(goodQuantity.getText().toString());
                                double textPrice = Double.parseDouble(goodPrice.getText().toString());
                                String textImg = goodImg.getText().toString();
                                if ( textName.isEmpty() || textColor.isEmpty() || textSex.isEmpty() ||
                                        textFirm.isEmpty() || textImg.isEmpty()) {
                                    throw new Exception();
                                }
                                if (index_cat == 0) {
                                    db.updateGood(id, textName, idCat, textColor, textSex, textFirm, textQuantity, textPrice, textImg);
                                } else {
                                    db.updateGood(id, textName, index_cat, textColor, textSex, textFirm, textQuantity, textPrice, textImg);

                                }
                            } catch (Exception e) {
                                Toast.makeText(Good.this, R.string.error_validations, Toast.LENGTH_LONG).show();
                            }
                            index_cat = 0;
                            getSupportLoaderManager().getLoader(0).forceLoad();
                        }
                    })
                    .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
        }
        AlertDialog dialog_sale = builder.create();
        dialog_sale.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add) {
            initDialog(null, -1);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.categories) {
            Intent intent = new Intent(this, Categories.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.goods) {
            onBackPressed();
        } else if (id == R.id.checks) {
            Intent intent = new Intent(this, Check.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.sales) {
            Intent intent = new Intent(this, Sale.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.about_us) {

        } else if (id == R.id.exit) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    // my cod
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_UPDATE_ID, 0, R.string.update);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        if (item.getItemId() == CM_DELETE_ID) {
            // obtain from the context menu item list data
            // retrieve the record id and delete the corresponding entry in the database
            db.delGood(acmi.id);
            // obtain new cursor with data
            getSupportLoaderManager().getLoader(0).forceLoad();
            return true;
        } else {
            Cursor cursor = scAdapter.getCursor();
            initDialog(cursor, acmi.id);
        }
        return super.onContextItemSelected(item);
    }

    protected void onDestroy() {
        super.onDestroy();
        // close connection
        db.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new MyCursorLoader(this, db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    static class MyCursorLoader extends CursorLoader {

        DB db;

        public MyCursorLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = db.getAllGood();
            return cursor;
        }

    }
}
