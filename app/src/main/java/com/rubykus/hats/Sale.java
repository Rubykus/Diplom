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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;

public class Sale extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CM_DELETE_ID = 1;
    private static final int CM_UPDATE_ID = 2;
    private static final int DIALOG_LIST_GOOD = 1;
    private static final int DIALOG_LIST_CHECK = 2;
    ListView lv;
    DB db;
    SimpleCursorAdapter scAdapter;
    // for dialogs
    Calendar calendar = Calendar.getInstance();
    int index_good;
    int index_check;

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
        String[] from = new String[] { DB.COLUMN_ID, DB.SALE_ID_GOODS, DB.SALE_DATE, DB.SALE_ID_CHECK};
        int[] to = new int[] { R.id.text1, R.id.text2, R.id.text3, R.id.text4 };

        // create adapter and customizable list
        scAdapter = new SimpleCursorAdapter(this, R.layout.item_sale, null, from, to, 0);
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
    public void showDialogInner(int id, final TextView tv){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (id == DIALOG_LIST_GOOD) {
            Cursor cur = db.getAllGood();
            int countRow = cur.getCount();
            final String[] good_name = new String[countRow];
            final int[] good_id = new int[countRow];
            for (int i = 0; i < countRow; i++) {
                cur.moveToNext();
                int index = cur.getInt(cur.getColumnIndex(DB.COLUMN_ID));
                String val = cur.getString(cur.getColumnIndex(DB.GOOD_NAME));
                good_name[i] = val;
                good_id[i] = index;
            }
            builder.setTitle("Выберите категорию")
                    .setItems(good_name, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            tv.setText(good_name[which]);
                            index_good = good_id[which];
                        }
                    });
        } else if (id == DIALOG_LIST_CHECK){
            Cursor cur = db.getAllCheck();
            int countRow = cur.getCount();
            final String[] check_data = new String[countRow];
            for (int i = 0; i < countRow; i++) {
                cur.moveToNext();
                String index = cur.getString(cur.getColumnIndex(DB.COLUMN_ID));
                String date = cur.getString(cur.getColumnIndex(DB.CHECK_DATE));
                String cost = cur.getString(cur.getColumnIndex(DB.CHECK_COST));
                check_data[i] = index+" "+date+" "+cost;
            }
            builder.setTitle("Выберите чек")
                    .setItems(check_data, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            tv.setText(check_data[which]);
                            String[] arr_check = check_data[which].split(" ");
                            index_check = Integer.parseInt(arr_check[0]);
                        }
                    });
        }

        AlertDialog dialog_choose = builder.create();
        dialog_choose.show();
    }
    // get name good
    public String getNameGood(int idGood){
        Cursor cur = db.getAllGood();
        int countRow = cur.getCount();
        HashMap<Integer,String> dataGood = new HashMap<>();
        for (int i = 0; i < countRow; i++){
            cur.moveToNext();
            int index = cur.getInt(cur.getColumnIndex(DB.COLUMN_ID));
            String val = cur.getString(cur.getColumnIndex(DB.GOOD_NAME));
            dataGood.put(index, val);
        }
        return dataGood.get(idGood);
    }
    // initialize dialog
    public void initDialog(Cursor cursor, final long id){
        View view = LayoutInflater.from(Sale.this).inflate(R.layout.dialog_sale, null);
        final TextView saleIdGood = (TextView)view.findViewById(R.id.addIdGood);
        saleIdGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogInner(DIALOG_LIST_GOOD, saleIdGood);
            }
        });
        final TextView dateSale = (TextView)view.findViewById(R.id.addDate);
        final TextView saleIdCheck = (TextView)view.findViewById(R.id.addIdCheck);
        saleIdCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogInner(DIALOG_LIST_CHECK, saleIdCheck);
            }
        });
        final DatePickerDialog.OnDateSetListener listener =  new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                dateSale.setText(dayOfMonth+"/"+monthOfYear+"/"+year);
            }
        };
        dateSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Sale.this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (cursor == null && id == -1) {
            builder.setTitle(R.string.new_sale)
                    .setView(view)
                    .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String textDate = dateSale.getText().toString();
                            try {
                                if ((index_good == 0) || (textDate == getText(R.string.date)) || (index_check == 0)) {
                                    throw new Exception();
                                }
                                db.addSale(index_good, textDate, index_check);
                            } catch (Exception e) {
                                Toast.makeText(Sale.this, R.string.error_validations, Toast.LENGTH_LONG).show();
                            }
                            index_good = 0;
                            getSupportLoaderManager().getLoader(0).forceLoad();
                        }
                    })
                    .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
        } else {
            final int idGood = cursor.getInt(cursor.getColumnIndex(DB.SALE_ID_GOODS));
            saleIdGood.setText(getNameGood(idGood));
            saleIdGood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogInner(DIALOG_LIST_GOOD, saleIdGood);
                }
            });
            dateSale.setText(cursor.getString(cursor.getColumnIndex(DB.SALE_DATE)));
            saleIdCheck.setText(cursor.getString(cursor.getColumnIndex(DB.SALE_ID_CHECK)));
            saleIdCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogInner(DIALOG_LIST_CHECK, saleIdCheck);
                }
            });
            builder.setTitle(R.string.update)
                    .setView(view)
                    .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String textDate = dateSale.getText().toString();
                            try {
                                if (index_good == 0){
                                    db.updateSale(id, idGood, textDate, index_check);
                                } else {
                                    db.updateSale(id, index_good, textDate, index_check);
                                }
                            } catch (Exception e){
                                Toast.makeText(Sale.this, R.string.error_validations, Toast.LENGTH_LONG).show();
                            }
                            index_good = 0;
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
            Intent intent = new Intent(this, Good.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.checks) {
            Intent intent = new Intent(this, Check.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.sales) {
            onBackPressed();
        } else if (id == R.id.about_us) {
            Intent intent = new Intent(this, AboutUs.class);
            startActivity(intent);
        } else if (id == R.id.exit) {
            finish();
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
            db.delSale(acmi.id);
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
            Cursor cursor = db.getAllSale();
            return cursor;
        }

    }
}
