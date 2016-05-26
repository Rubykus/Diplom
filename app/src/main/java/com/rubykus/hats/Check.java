package com.rubykus.hats;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Objects;

public class Check extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CM_DELETE_ID = 1;
    private static final int CM_EDIT_ID = 2;
    private static final int DIALOG_ADD = 1;
    private static final int DIALOG_EDIT = 2;
    ListView lv;
    DB db;
    SimpleCursorAdapter scAdapter;
    // for dialogs
    Calendar calendar = Calendar.getInstance();

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
        String[] from = new String[] { DB.COLUMN_ID, DB.CHECK_DATE, DB.CHECK_COST};
        int[] to = new int[] { R.id.text1, R.id.text2, R.id.text3 };

        // create adapter and customizable list
        scAdapter = new SimpleCursorAdapter(this, R.layout.item_check, null, from, to, 0);
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
    // initialize dialog
    public void initDialog(Cursor cursor, final long id){
        View view = LayoutInflater.from(Check.this).inflate(R.layout.dialog_check, null);
        final TextView dateCheck = (TextView)view.findViewById(R.id.dateAddCheck);
        final EditText costCheck = (EditText)view.findViewById(R.id.costAddCheck);
        final DatePickerDialog.OnDateSetListener listener =  new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                dateCheck.setText(dayOfMonth+"/"+monthOfYear+"/"+year);
            }
        };
        dateCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Check.this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (cursor == null && id == -1) {
            builder.setTitle(R.string.new_check)
                    .setView(view)
                    .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String textDateCheck = dateCheck.getText().toString();
                            double textCostCheck = Double.parseDouble(costCheck.getText().toString());
                            db.addCheck(textDateCheck, textCostCheck);
                            getSupportLoaderManager().getLoader(0).forceLoad();
                        }
                    })
                    .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
        } else {
            dateCheck.setText(cursor.getString(cursor.getColumnIndex(DB.CHECK_DATE)));
            costCheck.setText(cursor.getString(cursor.getColumnIndex(DB.CHECK_COST)));
            builder.setTitle(R.string.new_check)
                    .setView(view)
                    .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String textDateCheck = dateCheck.getText().toString();
                            double textCostCheck = Double.parseDouble(costCheck.getText().toString());
                            db.updateCheck(id,textDateCheck, textCostCheck);
                            getSupportLoaderManager().getLoader(0).forceLoad();
                        }
                    })
                    .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
        }
        AlertDialog dialog_check = builder.create();
        dialog_check.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
            onBackPressed();
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
        menu.add(0, CM_EDIT_ID, 0, R.string.update);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete);
    }

    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        if (item.getItemId() == CM_DELETE_ID) {
            db.delCheck(acmi.id);
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
            Cursor cursor = db.getAllCheck();
            return cursor;
        }

    }
}
