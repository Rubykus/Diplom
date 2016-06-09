package com.rubykus.hats;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import static com.rubykus.hats.Good.db;
import static com.rubykus.hats.Good.getNameCat;

public class SingleGood extends AppCompatActivity {

    private static final int CM_CARD_ID = 1;
    public int varId;
    public String varName;
    public double varPrice;
    public String textImg;
    int varCount;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_good);

        TextView idGood = (TextView) findViewById(R.id.idGood);
        TextView nameGood = (TextView) findViewById(R.id.nameGood);
        TextView catGood = (TextView) findViewById(R.id.catGood);
        TextView colorGood = (TextView) findViewById(R.id.colorGood);
        TextView sexGood = (TextView) findViewById(R.id.sexGood);
        TextView firmGood = (TextView) findViewById(R.id.firmGood);
        TextView quantityGood = (TextView) findViewById(R.id.quantityGood);
        TextView priceGood = (TextView) findViewById(R.id.priceGood);
        ImageView imgGood = (ImageView) findViewById(R.id.imgGood);

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
        textImg = extras.getString("img_good");
        imgGood.setImageURI(Uri.parse(Environment.getExternalStorageDirectory().toString() + "/" + textImg));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, CM_CARD_ID, 0, R.string.add_to_basket);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == CM_CARD_ID) {
            final EditText count = new EditText(this);
            count.setInputType(InputType.TYPE_CLASS_NUMBER);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.add_to_basket)
                    .setMessage("Введите количество товара.")
                    .setView(count)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                varCount = Integer.parseInt(count.getText().toString());
                                double cost = varCount * varPrice;
                                Cursor cursor = db.getBasket();
                                int countRow = cursor.getCount();
                                int counter = 0;
                                for (int i = 0; i < countRow; i++) {
                                    cursor.moveToNext();
                                    if (varId == cursor.getInt(cursor.getColumnIndex(DB.BASKET_ID_GOOD))) {
                                        db.updateItemBasket(cursor.getLong(cursor.getColumnIndex(DB.COLUMN_ID)),
                                                cursor.getInt(cursor.getColumnIndex(DB.BASKET_ID_GOOD)),
                                                cursor.getString(cursor.getColumnIndex(DB.BASKET_NAME_GOOD)),
                                                cursor.getInt(cursor.getColumnIndex(DB.BASKET_COUNT_GOOD)) + varCount,
                                                cursor.getDouble(cursor.getColumnIndex(DB.BASKET_PRICE_GOOD)),
                                                cursor.getDouble(cursor.getColumnIndex(DB.BASKET_SUM)) + cost,
                                                cursor.getString(cursor.getColumnIndex(DB.BASKET_IMG_GOOD))
                                        );
                                    } else {
                                        counter++;
                                    }
                                }
                                if (counter == countRow) {
                                    db.addItemBasket(varId, varName, varCount, varPrice, cost, textImg);
                                    Toast.makeText(SingleGood.this, "Товар добавлен.", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(SingleGood.this, "Товар обновлен.", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            } catch (Exception e){
                                Toast.makeText(SingleGood.this, "Введите количество.", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancle, null);
            AlertDialog dialog_card = builder.create();
            dialog_card.show();
        }

        return super.onOptionsItemSelected(item);
    }

}
