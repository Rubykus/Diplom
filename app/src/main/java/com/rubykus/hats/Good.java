package com.rubykus.hats;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class Good extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CM_DELETE_ID = 1;
    private static final int CM_UPDATE_ID = 2;
    private static final int CM_ADD_ID = 1;
    private static final int CM_CAT_ID = 2;
    private static final int CM_ALL_ID = 3;
    private static final int CM_PRICE_LIST_ID = 4;
    private static final int ADD_DIALOG = 1;
    private static final int SORT_DIALOG = 2;
    GridView gv;
    static DB db;
    MyCursorAdapter scAdapter;
    int index_cat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.goods);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        db = new DB(this);
        db.open();

        String[] from = new String[] {};
        int[] to = new int[] { R.id.nameGood, R.id.quantityGood, R.id.imageGood};

        scAdapter = new MyCursorAdapter(this, R.layout.item_good, null, from, to, 0);
        gv = (GridView) findViewById(R.id.gridGoods);
        gv.setAdapter(scAdapter);

        registerForContextMenu(gv);

        getSupportLoaderManager().initLoader(0, null, this);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = scAdapter.getCursor();
                initGood(cursor);
            }
        });

    }

    public void initGood(Cursor cursor){
        Intent intent = new Intent(this, SingleGood.class);
        intent.putExtra("id_good", cursor.getInt(cursor.getColumnIndex(DB.COLUMN_ID)));
        intent.putExtra("name_good", cursor.getString(cursor.getColumnIndex(DB.GOOD_NAME)));
        intent.putExtra("cat_good", cursor.getInt(cursor.getColumnIndex(DB.GOOD_ID_CAT)));
        intent.putExtra("color_good", cursor.getString(cursor.getColumnIndex(DB.GOOD_COLOR)));
        intent.putExtra("sex_good", cursor.getString(cursor.getColumnIndex(DB.GOOD_SEX)));
        intent.putExtra("firm_good", cursor.getString(cursor.getColumnIndex(DB.GOOD_FIRM)));
        intent.putExtra("quantity_good", cursor.getString(cursor.getColumnIndex(DB.GOOD_QUANTITY)));
        intent.putExtra("price_good", cursor.getDouble(cursor.getColumnIndex(DB.GOOD_PRICE)));
        intent.putExtra("img_good", cursor.getString(cursor.getColumnIndex(DB.GOOD_IMAGE)));
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, CM_ADD_ID,0, R.string.add);
        menu.add(0, CM_CAT_ID,0, R.string.categories);
        menu.add(0, CM_ALL_ID,0, R.string.all_good);
        menu.add(0, CM_PRICE_LIST_ID,0, "Прайс-лист");
        return true;
    }
    public void showDialogInner(final TextView tv, final int id){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Cursor cur = db.getAllCat();
        int countRow = cur.getCount();
        final String[] cat_name = new String[countRow];
        final int[] cat_id = new int[countRow];
        for (int i = 0; i < countRow; i++) {
            cur.moveToNext();
            int index = cur.getInt(cur.getColumnIndex(DB.COLUMN_ID));
            String val = cur.getString(cur.getColumnIndex(DB.CAT_NAME));
            cat_name[i] = val;
            cat_id[i] = index;
        }
        builder.setTitle("Выберите категорию")
            .setItems(cat_name, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (id == ADD_DIALOG) {
                        tv.setText(cat_name[which]);
                        index_cat = cat_id[which];
                    } else if (id == SORT_DIALOG) {
                            Cursor cursor = db.getAllGoodByCat(cat_id[which]);
                            if (cursor.getCount() == 0) {
                                Toast.makeText(Good.this, "Товаров с такой категорией не найдено.", Toast.LENGTH_LONG).show();
                            } else {
                                getSupportActionBar().setTitle(cat_name[which]);
                                scAdapter.swapCursor(cursor);
                            }
                    }
                }
            });
        AlertDialog dialog_choose = builder.create();
        dialog_choose.show();
    }
    public static String getNameCat(int idCat){
        Cursor cur = db.getAllCat();
        int countRow = cur.getCount();
        HashMap<Integer,String> dataCat = new HashMap<>();
        for (int i = 0; i < countRow; i++){
            cur.moveToNext();
            int index = cur.getInt(cur.getColumnIndex(DB.COLUMN_ID));
            String val = cur.getString(cur.getColumnIndex(DB.CAT_NAME));
            dataCat.put(index, val);
        }
        return dataCat.get(idCat);
    }
    public void initDialog(Cursor cursor, final long id){
        View view = LayoutInflater.from(Good.this).inflate(R.layout.dialog_good, null);
        final EditText goodName = (EditText)view.findViewById(R.id.goodAddName);
        final TextView goodIdCat = (TextView)view.findViewById(R.id.goodAddCat);
        goodIdCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogInner(goodIdCat, ADD_DIALOG);
            }
        });
        final EditText goodColor = (EditText)view.findViewById(R.id.goodAddColor);
        final EditText goodSex = (EditText)view.findViewById(R.id.goodAddSex);
        final EditText goodFirm = (EditText)view.findViewById(R.id.goodAddFirm);
        final EditText goodQuantity = (EditText)view.findViewById(R.id.goodAddQuantity);
        final EditText goodPrice = (EditText)view.findViewById(R.id.goodAddPrice);
        goodPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(7,2)});
        final EditText goodImg = (EditText)view.findViewById(R.id.goodAddImg);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (cursor == null && id == -1) {
            builder.setTitle(R.string.new_sale)
                    .setView(view)
                    .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                String textName = goodName.getText().toString();
                                int idCat = index_cat;
                                String textColor = goodColor.getText().toString();
                                String textSex = goodSex.getText().toString();
                                String textFirm = goodFirm.getText().toString();
                                int textQuantity = Integer.parseInt(goodQuantity.getText().toString());
                                double textPrice = Double.parseDouble(goodPrice.getText().toString());
                                String textImg = goodImg.getText().toString();
                                if ( index_cat == 0 || textName.isEmpty() || textColor.isEmpty() || textSex.isEmpty() ||
                                        textFirm.isEmpty() || textImg.isEmpty()) {
                                    throw new Exception();
                                }
                                db.addGood(textName, idCat, textColor, textSex, textFirm, textQuantity, textPrice, textImg);
                            } catch (Exception e) {
                                Toast.makeText(Good.this, R.string.error_validations, Toast.LENGTH_LONG).show();
                            }
                            index_cat = 0;
                            getSupportLoaderManager().getLoader(0).forceLoad();
                        }
                    })
                    .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
        } else {
            goodName.setText(cursor.getString(cursor.getColumnIndex(DB.GOOD_NAME)));
            final int idCat = cursor.getInt(cursor.getColumnIndex(DB.GOOD_ID_CAT));
            goodIdCat.setText(getNameCat(idCat));
            goodIdCat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogInner(goodIdCat, ADD_DIALOG);
                }
            });
            goodColor.setText(cursor.getString(cursor.getColumnIndex(DB.GOOD_COLOR)));
            goodSex.setText(cursor.getString(cursor.getColumnIndex(DB.GOOD_SEX)));
            goodFirm.setText(cursor.getString(cursor.getColumnIndex(DB.GOOD_FIRM)));
            goodQuantity.setText(cursor.getString(cursor.getColumnIndex(DB.GOOD_QUANTITY)));
            goodPrice.setText(cursor.getString(cursor.getColumnIndex(DB.GOOD_PRICE)));
            goodImg.setText(cursor.getString(cursor.getColumnIndex(DB.GOOD_IMAGE)));
            builder.setTitle(R.string.update)
                    .setView(view)
                    .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                String textName = goodName.getText().toString();
                                String textColor = goodColor.getText().toString();
                                String textSex = goodSex.getText().toString();
                                String textFirm = goodFirm.getText().toString();
                                int textQuantity = Integer.parseInt(goodQuantity.getText().toString());
                                double textPrice = Double.parseDouble(goodPrice.getText().toString());
                                String textImg = goodImg.getText().toString();
                                if ( textName.isEmpty() || textColor.isEmpty() || textSex.isEmpty() ||
                                        textFirm.isEmpty() || textImg.isEmpty()) {
                                    throw new Exception();
                                }
                                if (index_cat == 0) {
                                    db.updateGood(id, textName, idCat, textColor, textSex, textFirm, textQuantity, textPrice, textImg);
                                } else {
                                    db.updateGood(id, textName, index_cat, textColor, textSex, textFirm, textQuantity, textPrice, textImg);

                                }
                            } catch (Exception e) {
                                Toast.makeText(Good.this, R.string.error_validations, Toast.LENGTH_LONG).show();
                            }
                            index_cat = 0;
                            getSupportLoaderManager().getLoader(0).forceLoad();
                        }
                    })
                    .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
        }
        AlertDialog dialog_sale = builder.create();
        dialog_sale.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == CM_ADD_ID) {
            initDialog(null, -1);
        } else if (id == CM_CAT_ID){
            showDialogInner(null, SORT_DIALOG);
        } else if (id == CM_ALL_ID){
            Cursor cursor = db.getAllGood();
            scAdapter.swapCursor(cursor);
            getSupportActionBar().setTitle(R.string.goods);
        } else if (id == CM_PRICE_LIST_ID) {
            Cursor cursor = db.getAllGood();
            final String sFileName = "price-list.pdf";
            int rowCount = cursor.getCount();

            PdfPTable table = new PdfPTable(3);

            PdfPCell cell = new PdfPCell(new Phrase("Good"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Quantity"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Price"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            table.setHeaderRows(1);

            for (int i= 0; i<rowCount; i++){
                cursor.moveToNext();
                table.addCell(cursor.getString(cursor.getColumnIndex(DB.GOOD_NAME)));
                table.addCell(cursor.getString(cursor.getColumnIndex(DB.GOOD_QUANTITY)));
                table.addCell(cursor.getString(cursor.getColumnIndex(DB.GOOD_PRICE))+" UAH");
            }

            Document doc = new Document();

            try {

                File root = new File(Environment.getExternalStorageDirectory(), "Price-list");
                if (!root.exists()) {
                    root.mkdirs();
                }

                File priceList = new File(root, sFileName);
                FileOutputStream fOut = new FileOutputStream(priceList);

                PdfWriter.getInstance(doc, fOut);

                doc.open();

                Paragraph p1 = new Paragraph("Price-list");
                p1.setAlignment(Paragraph.ALIGN_CENTER);
                p1.setSpacingAfter(10);

                doc.add(p1);
                doc.add(table);

            } catch (DocumentException de) {
                Log.e("PDFCreator", "DocumentException:" + de);
            } catch (IOException e) {
                Log.e("PDFCreator", "ioException:" + e);
            } finally {
                doc.close();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(Good.this);
            builder.setMessage("Открыть прайс-лист?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Price-list/" + sFileName);
                            Uri path = Uri.fromFile(pdfFile);

                            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                            pdfIntent.setDataAndType(path, "application/pdf");
                            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            try {
                                startActivity(pdfIntent);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(Good.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Нет", null);
            builder.show();

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.categories) {
            Intent intent = new Intent(this, Categories.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.goods) {
            onBackPressed();
        } else if (id == R.id.card) {
            Intent intent = new Intent(this, Basket.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.sales) {
            Intent intent = new Intent(this, Sale.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.about_us) {
            Intent intent = new Intent(this, AboutUs.class);
            startActivity(intent);
        } else if (id == R.id.exit) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_UPDATE_ID, 0, R.string.update);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        if (item.getItemId() == CM_DELETE_ID) {
            db.delGood(acmi.id);
            getSupportLoaderManager().getLoader(0).forceLoad();
            return true;
        } else {
            Cursor cursor = scAdapter.getCursor();
            initDialog(cursor, acmi.id);
        }
        return super.onContextItemSelected(item);
    }

    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new MyCursorLoader(this, db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    static class MyCursorLoader extends CursorLoader {

        DB db;

        public MyCursorLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = db.getAllGood();
            return cursor;
        }

    }
}
