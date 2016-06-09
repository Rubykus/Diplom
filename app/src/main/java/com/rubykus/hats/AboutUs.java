package com.rubykus.hats;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;


public class AboutUs extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.about_us);
        TextView about = (TextView)findViewById(R.id.about);
        String text = "Проэкт разработан в рамках дипломной работы, студентам курса РПЗ 12 1/9 Ераком Егором Валерьевичем.";
        about.setText(text);
    }

}
