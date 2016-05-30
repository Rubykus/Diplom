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
        TextView t1 = (TextView)findViewById(R.id.text1);
        TextView t2 = (TextView)findViewById(R.id.text2);
        ImageView iv = (ImageView)findViewById(R.id.iv);
        String path = Environment.getExternalStorageDirectory().getPath()+"/Download/hat.png";
        String path2 = "/storage/emulated/0/Download/hat.png";
        t1.setText(path);
        t2.setText(path2);
        iv.setImageURI(Uri.parse(path2));
        Toast.makeText(this, String.valueOf(path.equals(path2)), Toast.LENGTH_LONG).show();
    }
}
