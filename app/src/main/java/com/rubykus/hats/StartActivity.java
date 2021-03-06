package com.rubykus.hats;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import com.pkmmte.view.CircularImageView;

public class StartActivity extends Activity {
    static int screenHeight;
    static int screenWidth;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        double height = displaymetrics.heightPixels;
        double width = displaymetrics.widthPixels;
        screenHeight = (int)height;
        screenWidth = (int)width;

        CircularImageView cat = (CircularImageView)findViewById(R.id.imageCat);
        cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, Categories.class);
                startActivity(intent);
                finish();
            }
        });
        CircularImageView good = (CircularImageView)findViewById(R.id.imageGood);
        good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, Good.class);
                startActivity(intent);
                finish();
            }
        });
        CircularImageView sale = (CircularImageView)findViewById(R.id.imageSale);
        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, Sale.class);
                startActivity(intent);
                finish();
            }
        });
        CircularImageView check = (CircularImageView)findViewById(R.id.imageCard);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, Basket.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
