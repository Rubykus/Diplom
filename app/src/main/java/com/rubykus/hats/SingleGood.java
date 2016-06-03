package com.rubykus.hats;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.rubykus.hats.Good.getNameCat;

public class SingleGood extends AppCompatActivity{

    private static final int CM_CARD_ID = 1;

    public int varId;
    public String varName;
    public double varPrice;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_good);

        TextView idGood = (TextView)findViewById(R.id.idGood);
        TextView nameGood = (TextView)findViewById(R.id.nameGood);
        TextView catGood = (TextView)findViewById(R.id.catGood);
        TextView colorGood = (TextView)findViewById(R.id.colorGood);
        TextView sexGood = (TextView)findViewById(R.id.sexGood);
        TextView firmGood = (TextView)findViewById(R.id.firmGood);
        TextView quantityGood = (TextView)findViewById(R.id.quantityGood);
        TextView priceGood = (TextView)findViewById(R.id.priceGood);
        ImageView imgGood = (ImageView)findViewById(R.id.imgGood);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        varId = extras.getInt("id_good");
        varName = extras.getString("name_good");
        varPrice = extras.getDouble("price_good");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(String.valueOf(varName));

        idGood.setText(String.valueOf(varId));
        nameGood.setText(varName);
        catGood.setText(getNameCat(extras.getInt("cat_good")));
        colorGood.setText(extras.getString("color_good"));
        sexGood.setText(extras.getString("sex_good"));
        firmGood.setText(extras.getString("firm_good"));
        quantityGood.setText(extras.getString("quantity_good"));
        priceGood.setText(String.valueOf(varPrice));
        imgGood.setImageURI(Uri.parse(Environment.getExternalStorageDirectory().toString()+"/"+extras.getString("img_good")));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(0, CM_CARD_ID, 0, R.string.add_to_card);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == CM_CARD_ID) {
            Toast.makeText(this, varId+" "+varName+" "+varPrice, Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

}
