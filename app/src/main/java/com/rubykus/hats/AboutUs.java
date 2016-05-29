package com.rubykus.hats;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

public class AboutUs extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        ImageView iv = (ImageView)findViewById(R.id.iv);
        String path = Environment.getExternalStorageDirectory().getPath()+"/Download/hat.png";
        iv.setImageURI(Uri.parse(path));
        Toast.makeText(this, path, Toast.LENGTH_LONG).show();
    }
}
