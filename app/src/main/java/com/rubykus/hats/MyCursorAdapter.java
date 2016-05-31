package com.rubykus.hats;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;


public class MyCursorAdapter extends SimpleCursorAdapter {

    private int layout;

    public MyCursorAdapter(Context _context, int _layout, Cursor _cursor, String[] _from, int[] _to, int _flags) {
        super(_context, _layout, _cursor, _from, _to, _flags);
        layout = _layout;
    }

    @Override
    public void bindView(View view, Context _context, Cursor _cursor){
        String name = _cursor.getString(_cursor.getColumnIndex(DB.GOOD_NAME));
        int id_cat = _cursor.getInt(_cursor.getColumnIndex(DB.GOOD_ID_CAT));
        String color = _cursor.getString(_cursor.getColumnIndex(DB.GOOD_COLOR));
        String sex = _cursor.getString(_cursor.getColumnIndex(DB.GOOD_SEX));
        String firm = _cursor.getString(_cursor.getColumnIndex(DB.GOOD_FIRM));
        int quantity = _cursor.getInt(_cursor.getColumnIndex(DB.GOOD_QUANTITY));
        double price = _cursor.getDouble(_cursor.getColumnIndex(DB.GOOD_PRICE));
        String img = _cursor.getString(_cursor.getColumnIndex(DB.GOOD_IMAGE));
        CircularImageView imgGood = (CircularImageView)view.findViewById(R.id.imageGood);
        Uri path = Uri.parse(Environment.getExternalStorageDirectory().toString()+"/"+img);
        imgGood.setImageURI(path);
        TextView goodName = (TextView)view.findViewById(R.id.nameGood);
        goodName.setText(name);
        TextView desGood = (TextView)view.findViewById(R.id.descrGood);
        String description = "Описание: "+name+", "+Good.getNameCat(id_cat)+", цвет "+color
                +", пол "+sex+", фирма "+firm;
        desGood.setText(description);
        TextView quantityV = (TextView)view.findViewById(R.id.quantityGood);
        String texQuantity = "Осталось: "+quantity;
        quantityV.setText(texQuantity);
        TextView priceV = (TextView)view.findViewById(R.id.goodPrice);
        priceV.setText(String.valueOf(price));
    }
    @Override
    public View newView(Context _context, Cursor _cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(_context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layout, parent, false);
        return view;
    }
}
