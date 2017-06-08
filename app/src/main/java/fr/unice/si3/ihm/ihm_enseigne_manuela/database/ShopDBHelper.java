package fr.unice.si3.ihm.ihm_enseigne_manuela.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.unice.si3.ihm.ihm_enseigne_manuela.model.Shop;


public class ShopDBHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "tobeortohave_database";

    private SQLiteDatabase myDataBase;
    private final Context myContext;

    private Cursor cursor;

    public ShopDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    public void openDataBase() throws SQLException, IOException {
        //Open the database
        String myPath = myContext.getDatabasePath(DB_NAME).getAbsolutePath();
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (!dbExist) {
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            try {
                // Copy the database in assets to the application database.
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database", e);
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = myContext.getDatabasePath(DB_NAME).getAbsolutePath();
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //database doesn't exist yet.
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = myContext.getDatabasePath(DB_NAME).getAbsolutePath();
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public List<Shop> getShopList() {
        if (myDataBase == null)
            try {
                openDataBase();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        List<Shop> shopList = new ArrayList<>();
        Cursor cursor = myDataBase.rawQuery("SELECT * FROM shops ORDER BY city", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            System.out.println(cursor.getString(0));
            Shop shop = new Shop(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(5));
            shopList.add(shop);
            cursor.moveToNext();
        }
        cursor.close();
        return shopList;
    }

    public List<String> getShopNameList() {
        if (myDataBase == null)
            try {
                openDataBase();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        List<String> shopList = new ArrayList<>();
        Cursor cursor = myDataBase.rawQuery("SELECT * FROM shops ORDER BY city", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            shopList.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return shopList;
    }

    public Shop getShopWithCursor(Cursor cursor) {
        Shop shop = null;
        if (!cursor.isAfterLast()) {
            shop = new Shop(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(5));
            cursor.moveToNext();
        } else {
            cursor.close();
        }
        return shop;
    }

    public Cursor makeRequestDatabase() {
        cursor = myDataBase.rawQuery("SELECT * FROM shops ORDER BY city DESC", null);
        return cursor;
    }

    public Cursor getCursor() {
        return cursor;
    }
}

