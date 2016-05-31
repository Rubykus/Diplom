package com.rubykus.hats;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AboutUs extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        TextView about = (TextView)findViewById(R.id.about);
        String text = "Проэкт разработан в рамках дипломной работы, студентам курса РПЗ 12 1/9 Ераком Егором Валерьевичем.";
        about.setText(text);
    }
}
