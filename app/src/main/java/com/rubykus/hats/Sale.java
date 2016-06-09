package com.rubykus.hats;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Sale extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CM_CREATE_CHECK = 1;
    private static final int CM_DELETE_ID = 2;

    ListView lv;
    DB db;
    SimpleCursorAdapter scAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.sales);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        db = new DB(this);
        db.open();

        String[] from = new String[]{DB.COLUMN_ID, DB.SALE_DATE, DB.SALE_LIST_GOOD, DB.SALE_SUM};
        int[] to = new int[]{R.id.textIDSale, R.id.textIDGood, R.id.textDate, R.id.textIDCheck};

        scAdapter = new SimpleCursorAdapter(this, R.layout.item_sale, null, from, to, 0);
        lv = (ListView) findViewById(R.id.lv);
        ColorDrawable sage = new ColorDrawable(this.getResources().getColor(R.color.colorDivider));
        lv.setDivider(sage);
        lv.setDividerHeight(50);
        lv.setAdapter(scAdapter);

        registerForContextMenu(lv);

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.categories) {
            Intent intent = new Intent(this, Categories.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.goods) {
            Intent intent = new Intent(this, Good.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.card) {
            Intent intent = new Intent(this, Basket.class);
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

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_CREATE_CHECK, 0, R.string.create_check);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        if (item.getItemId() == CM_DELETE_ID) {
            db.delSale(acmi.id);
            getSupportLoaderManager().getLoader(0).forceLoad();
            return true;
        } else if (item.getItemId() == CM_CREATE_CHECK) {
            Cursor cursor = scAdapter.getCursor();
            final String sFileName = "sale-" + cursor.getString(cursor.getColumnIndex(DB.COLUMN_ID)) + ".pdf";
            String sBody = "Код продажи: " + cursor.getString(cursor.getColumnIndex(DB.COLUMN_ID))
                    + "\n\nДата: " + cursor.getString(cursor.getColumnIndex(DB.SALE_DATE))
                    + "\n\nТовары:\n" + cursor.getString(cursor.getColumnIndex(DB.SALE_LIST_GOOD))
                    + "\n\nСумма: " + cursor.getString(cursor.getColumnIndex(DB.SALE_SUM));


            try {
                File root = new File(Environment.getExternalStorageDirectory(), "Check");
                if (!root.exists()) {
                    root.mkdirs();
                }
                final File gpxfile = new File(root, sFileName);
                FileWriter writer = new FileWriter(gpxfile);
                writer.write(sBody);
                writer.flush();
                writer.close();

                AlertDialog.Builder builder = new AlertDialog.Builder(Sale.this);
                builder.setMessage("Показать чек?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent();
                                i.setAction(android.content.Intent.ACTION_VIEW);
                                i.setDataAndType(Uri.fromFile(gpxfile), "text/plain");
                                startActivity(i);
                            }
                        })
                        .setNegativeButton("Нет", null);
                builder.show();
            } catch (IOException e) {
                Toast.makeText(this, "Ошибка.", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onContextItemSelected(item);
    }

    protected void onDestroy() {
        super.onDestroy();
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
