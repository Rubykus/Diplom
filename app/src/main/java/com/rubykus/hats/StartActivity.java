package com.rubykus.hats;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class StartActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        ImageButton cat = (ImageButton)findViewById(R.id.imageButton1);
        cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, Categories.class);
                startActivity(intent);
                finish();
            }
        });
        ImageButton good = (ImageButton)findViewById(R.id.imageButton2);
        good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, Good.class);
                startActivity(intent);
                finish();
            }
        });
        ImageButton sale = (ImageButton)findViewById(R.id.imageButton3);
        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, Sale.class);
                startActivity(intent);
                finish();
            }
        });
        ImageButton check = (ImageButton)findViewById(R.id.imageButton4);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, Check.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
