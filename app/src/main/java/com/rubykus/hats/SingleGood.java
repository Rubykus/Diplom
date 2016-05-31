package com.rubykus.hats;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.TextView;

import static com.rubykus.hats.Good.getNameCat;

public class SingleGood extends Activity{
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

        idGood.setText(extras.getString("id_good"));
        nameGood.setText(extras.getString("name_good"));
        catGood.setText(getNameCat(extras.getInt("cat_good")));
        colorGood.setText(extras.getString("color_good"));
        sexGood.setText(extras.getString("sex_good"));
        firmGood.setText(extras.getString("firm_good"));
        quantityGood.setText(extras.getString("quantity_good"));
        priceGood.setText(extras.getString("price_good"));
        imgGood.setImageURI(Uri.parse(Environment.getExternalStorageDirectory().toString()+"/"+extras.getString("img_good")));
    }
}
