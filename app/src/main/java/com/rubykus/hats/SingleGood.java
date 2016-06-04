package com.rubykus.hats;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import static com.rubykus.hats.Good.getNameCat;

public class SingleGood extends AppCompatActivity {

    private static final int CM_CARD_ID = 1;

    public int varId;
    public String varName;
    public double varPrice;
    public String textImg;
    int varCount;

    static SharedPreferences data;
    SharedPreferences.Editor editor;

    ArrayList<HashMap<String, String>> myArrList;
    HashMap<String, String> map;

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
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(0, CM_CARD_ID, 0, R.string.add_to_card);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == CM_CARD_ID) {
            final EditText count = new EditText(this);
            count.setInputType(InputType.TYPE_CLASS_NUMBER);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.add_to_card)
                    .setMessage("Введите количество товара.")
                    .setView(count)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                map = new HashMap<String, String>();
                                map.put("id", String.valueOf(varId));
                                map.put("name", varName);
                                varCount = Integer.parseInt(count.getText().toString());
                                map.put("count", count.getText().toString());
                                map.put("price", String.valueOf(varPrice));
                                double cost = varCount * varPrice;
                                map.put("cost", String.valueOf(cost));
                                String img[] = textImg.split("/");
                                textImg = TextUtils.join("~", img);
                                map.put("img", textImg);

                                Gson gson = new Gson();
                                data = getSharedPreferences("card", MODE_PRIVATE);
                                String json_get = data.getString("card_array", "");
                                if (!json_get.isEmpty()){
                                    myArrList = (ArrayList)gson.fromJson(json_get, ArrayList.class).clone();
                                }else{
                                    myArrList = new ArrayList<>();
                                }

                                ArrayList jsonArrayList = new ArrayList();
                                for (int i = 0; i<myArrList.size(); i++) {
                                    Type type = new TypeToken<HashMap<String, String>>(){}.getType();
                                    HashMap<String, String> myMap = gson.fromJson(String.valueOf(myArrList.get(i)), type);
                                    jsonArrayList.add(myMap);
                                }

                                int counter = 0;
                                for (int i = 0; i<jsonArrayList.size(); i++) {
                                    HashMap<String, String> mapRabbit = (HashMap<String, String>) jsonArrayList.get(i);
                                    if (mapRabbit.get("id").equals(map.get("id"))){
                                        int count_old = Integer.parseInt(mapRabbit.get("count"));
                                        double cost_old = Double.parseDouble(mapRabbit.get("cost"));
                                        mapRabbit.put("count", String.valueOf(count_old+varCount));
                                        mapRabbit.put("cost", String.valueOf(cost_old+varCount*varPrice));
                                        jsonArrayList.set(i, mapRabbit);
                                        counter = 1;
                                    }
                                }
                                if (counter == 0){
                                    myArrList.add(map);
                                }else {
                                    myArrList = (ArrayList<HashMap<String, String>>) jsonArrayList.clone();
                                }

                                editor = data.edit();
                                String json_put = gson.toJson(myArrList);
                                editor.putString("card_array", json_put);
                                editor.apply();
                                Toast.makeText(SingleGood.this, "Добавленно.", Toast.LENGTH_LONG).show();
                                finish();
                            } catch (Exception e){
                                Toast.makeText(SingleGood.this, "Введите количество товара.", Toast.LENGTH_LONG).show();
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
