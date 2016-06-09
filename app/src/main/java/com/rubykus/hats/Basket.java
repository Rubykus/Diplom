package com.rubykus.hats;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.pkmmte.view.CircularImageView;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Basket extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CM_DELETE_ID = 1;

    ListView lv;
    CursorAdapterForBasket adapter;
    DB db;
    Button clear;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        db = new DB(this);
        db.open();
        Cursor cursor = db.getBasket();
        if (cursor.getCount() == 0){
            getSupportActionBar().setTitle(R.string.empty_basket);
        }else {
            getSupportActionBar().setTitle(R.string.basket);
        }

        String[] from = new String[] {};
        int[] to = new int[] {};

        adapter = new CursorAdapterForBasket(this, R.layout.item_basket, null, from, to, 0);
        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(adapter);

        registerForContextMenu(lv);

        getSupportLoaderManager().initLoader(0, null, this);

        clear = (Button)findViewById(R.id.clearCard);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Basket.this);
                builder.setMessage("Вы уверены что хотите удалить ВСЕ товары из корзины?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.delBasket();
                                getSupportLoaderManager().getLoader(0).forceLoad();
                            }
                        })
                        .setNegativeButton("Нет", null);
                builder.show();
            }
        });
        Button add = (Button)findViewById(R.id.toOrder);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = db.getBasket();
                if (cursor.getCount() != 0) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String date = sdf.format(new Date());
                    double sum = 0;
                    String info = "";
                    final DecimalFormat twoDForm = new DecimalFormat("#.##");
                    int countRow = cursor.getCount();
                    for (int i = 0; i < countRow; i++) {
                        cursor.moveToNext();
                        sum += cursor.getDouble(cursor.getColumnIndex(DB.BASKET_SUM));
                        info += cursor.getString(cursor.getColumnIndex(DB.BASKET_NAME_GOOD)) + "     " +
                                cursor.getInt(cursor.getColumnIndex(DB.BASKET_COUNT_GOOD))+"X"+
                                cursor.getDouble(cursor.getColumnIndex(DB.BASKET_PRICE_GOOD))+ "      " +
                                cursor.getDouble(cursor.getColumnIndex(DB.BASKET_SUM))+"\n";
                    }
                    final String finalDate = date;
                    final String finalInfo = info;
                    final double finalSum = sum;
                    AlertDialog.Builder builder = new AlertDialog.Builder(Basket.this);
                    builder.setTitle("Приобрести")
                            .setMessage("Вы действительно желаете приобрести все товары на суму "
                                    +Double.valueOf(twoDForm.format(sum))+" грн.")
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db.addSale(finalDate, finalInfo, Double.valueOf(twoDForm.format(finalSum)));
                                    db.delBasket();
                                    getSupportLoaderManager().getLoader(0).forceLoad();
                                }
                            })
                            .setNegativeButton("Нет", null);
                    builder.show();
                } else {
                    Toast.makeText(Basket.this, "Ваша корзина пуста.", Toast.LENGTH_LONG).show();
                }
            }
        });
        registerForContextMenu(lv);
    }
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, "Удалить");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        if (item.getItemId() == CM_DELETE_ID) {
            db.delItemBasket(acmi.id);
            getSupportLoaderManager().getLoader(0).forceLoad();
            return true;
        }
        return super.onContextItemSelected(item);
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
            onBackPressed();
        } else if (id == R.id.sales) {
            Intent intent = new Intent(this, Sale.class);
            startActivity(intent);
            finish();
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

    public class CursorAdapterForBasket extends SimpleCursorAdapter {

        private int layout;

        public CursorAdapterForBasket(Context _context, int _layout, Cursor _cursor, String[] _from, int[] _to, int _flags) {
            super(_context, _layout, _cursor, _from, _to, _flags);
            layout = _layout;
        }

        @Override
        public void bindView(View view, Context _context, Cursor _cursor){
            String info = _cursor.getString(_cursor.getColumnIndex(DB.BASKET_NAME_GOOD))+" "+
                    _cursor.getInt(_cursor.getColumnIndex(DB.BASKET_COUNT_GOOD))+"X"+
                    _cursor.getDouble(_cursor.getColumnIndex(DB.BASKET_PRICE_GOOD));
            Double sum = _cursor.getDouble(_cursor.getColumnIndex(DB.BASKET_SUM));
            String img = _cursor.getString(_cursor.getColumnIndex(DB.BASKET_IMG_GOOD));
            CircularImageView imgGood = (CircularImageView) view.findViewById(R.id.imageGood);
            Uri path = Uri.parse(Environment.getExternalStorageDirectory().toString()+"/"+img);
            imgGood.setImageURI(path);
            TextView basketInfo = (TextView)view.findViewById(R.id.basketInfo);
            basketInfo.setText(info);
            TextView itemSum = (TextView)view.findViewById(R.id.cost);
            itemSum.setText(String.valueOf(sum));
        }
        @Override
        public View newView(Context _context, Cursor _cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(_context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(layout, parent, false);
            return view;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new MyCursorLoader(this, db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
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
            Cursor cursor = db.getBasket();
            return cursor;
        }

    }

}
