package com.rubykus.hats;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.TextUtils;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pkmmte.view.CircularImageView;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


import static com.rubykus.hats.SingleGood.data;

public class Card extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int CM_DELETE_ID = 1;

    ListView lv;
    ArrayList<HashMap<String, String>> myArrList;
    SimpleAdapter adapter;
    Gson gson = new Gson();
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

        adapter = getAdapterForPreferences();

        if (adapter.isEmpty()){
            getSupportActionBar().setTitle(R.string.empty_card);
        }else{
            getSupportActionBar().setTitle(R.string.card);
        }

        lv = (ListView)findViewById(R.id.lv);
        lv.setAdapter(adapter);
        clear = (Button)findViewById(R.id.clearCard);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.edit().clear().commit();
                lv.setAdapter(getAdapterForPreferences());
            }
        });
        Button add = (Button)findViewById(R.id.toOrder);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!myArrList.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String date = sdf.format(new Date());
                    double sum = 0;
                    String info = "";
                    final DecimalFormat twoDForm = new DecimalFormat("#.##");
                    for (int i = 0; i < myArrList.size(); i++) {
                        Type type = new TypeToken<HashMap<String, String>>() {
                        }.getType();
                        HashMap<String, String> myMap = gson.fromJson(String.valueOf(myArrList.get(i)), type);
                        String cost = myMap.get("cost");
                        sum += Double.parseDouble(cost);
                        info += myMap.get("name") + "     " + myMap.get("count") + "X" + myMap.get("price") + "      " + myMap.get("cost") + " \n";
                    }
                    final String finalDate = date;
                    final String finalInfo = info;
                    final double finalSum = sum;
                    AlertDialog.Builder builder = new AlertDialog.Builder(Card.this);
                    builder.setTitle("Приобрести")
                            .setMessage("Вы действительно желаете приобрести все товары на суму "
                                    +Double.valueOf(twoDForm.format(finalSum))+" грн.")
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db.open();
                                    db.addSale(finalDate, finalInfo, Double.valueOf(twoDForm.format(finalSum)));
                                    db.close();
                                    clear.performClick();
                                }
                            })
                            .setNegativeButton("Нет", null);
                    builder.show();
                } else {
                    Toast.makeText(Card.this, "Ваша корзина пуста.", Toast.LENGTH_LONG).show();
                }
            }
        });
        registerForContextMenu(lv);
    }
    public SimpleAdapter getAdapterForPreferences(){
        data = getSharedPreferences("card", MODE_PRIVATE);
        String json_get = data.getString("card_array", "");
        if (json_get.isEmpty()){
            myArrList = new ArrayList<>();
        } else {
            myArrList = (ArrayList<HashMap<String, String>>) gson.fromJson(json_get, ArrayList.class).clone();
        }
        return new CustomAdapter(this, myArrList, R.layout.item_card,
                new String[] {"count", "cost"},
                new int[] {R.id.cardInfo, R.id.cost});
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, "Удалить запись");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем инфу о пункте списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // удаляем Map из коллекции, используя позицию пункта в списке
            myArrList.remove(acmi.position);
            SharedPreferences.Editor editor = data.edit();
            String json_put = gson.toJson(myArrList);
            editor.putString("card_array", json_put);
            editor.commit();
            // уведомляем, что данные изменились
            adapter.notifyDataSetChanged();
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
            Intent intent = new Intent(this, AboutUs.class);
            startActivity(intent);
        } else if (id == R.id.exit) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public class CustomAdapter extends SimpleAdapter {
        LayoutInflater inflater;
        Context context;
        ArrayList<HashMap<String, String>> arrayList;

        public CustomAdapter(Context context, ArrayList<HashMap<String, String>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            this.context = context;
            this.arrayList = data;
            inflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(Card.this).inflate(R.layout.item_card, null);
            CircularImageView imgGood = (CircularImageView)view.findViewById(R.id.imageGood);
            TextView info = (TextView)view.findViewById(R.id.cardInfo);
            TextView cost = (TextView)view.findViewById(R.id.cost);

            Type type = new TypeToken<HashMap<String, String>>(){}.getType();
            HashMap<String, String> mapRabbit = gson.fromJson(String.valueOf(arrayList.get(position)), type);
            String image = mapRabbit.get("img");
            String img[] = image.split("~");
            String textComplete = TextUtils.join("/", img);
            imgGood.setImageURI(Uri.parse(Environment.getExternalStorageDirectory().toString() + "/" + textComplete));
            String textInfo = mapRabbit.get("name")+" "+mapRabbit.get("count")+"X"
                    +mapRabbit.get("price");
            info.setText(textInfo);
            cost.setText(String.format("%.2f" , Double.parseDouble(mapRabbit.get("cost"))));

            return view;
        }

    }
}
