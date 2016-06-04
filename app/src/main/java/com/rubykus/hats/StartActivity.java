package com.rubykus.hats;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pkmmte.view.CircularImageView;

public class StartActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        CircularImageView cat = (CircularImageView)findViewById(R.id.imageCat);
        /*cat.setImageResource(R.drawable.categories);*/
        cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, Categories.class);
                startActivity(intent);
                finish();
            }
        });
        CircularImageView good = (CircularImageView)findViewById(R.id.imageGood);
        /*good.setImageResource(R.drawable.goods);*/
        good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, Good.class);
                startActivity(intent);
                finish();
            }
        });
        CircularImageView sale = (CircularImageView)findViewById(R.id.imageSale);
        /*sale.setImageResource(R.drawable.sale);*/
        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, Sale.class);
                startActivity(intent);
                finish();
            }
        });
        CircularImageView check = (CircularImageView)findViewById(R.id.imageCheck);
        /*check.setImageResource(R.drawable.check);*/
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, Card.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
