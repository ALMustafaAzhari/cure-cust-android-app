package com.andriod.cust.cure.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.andriod.cust.cure.bean.Company;
import com.andriod.cust.cure.bean.Item;
import com.andriod.cust.cure.bean.Request;
import com.andriod.cust.cure.util.Utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseService extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseService";
    private static final int DATABASE_VERSION = 1;
    private static String DB_PATH = null ;
    private static final String DATABASE_NAME = "curepharmdb.db";

    public DatabaseService(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DB_PATH = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
        try {
            createDataBase(context);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public List<Item> findAllItems() {
        String selectQuery = "SELECT IT.ID , IT.BRAND_NAME , IT.GENERIC_NAME , CO.NAME , CO.COUNTRY_CODE "
                + "FROM ITEM IT , COMPANY CO "
                + "WHERE IT.COMPANY_ID = CO.ID";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        List<Item> items = new ArrayList<>();
        if (cursor.moveToFirst()) {
            Item item ;
            Company company ;
            do {
                item = new Item() ;
                item.setId(cursor.getLong(0));
                item.setBrandName(cursor.getString(1));
                item.setGenericName(cursor.getString(2));
                company = new Company();
                company.setName(cursor.getString(3));
                company.setCountryCode(cursor.getString(4));
                item.setCompany(company);
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }


    public Item findItemById(Long id) {
        String selectQuery = "SELECT IT.ID , IT.BRAND_NAME , IT.GENERIC_NAME , CO.NAME , CO.COUNTRY_CODE "
                + "FROM ITEM IT , COMPANY CO "
                + "WHERE IT.ID = ?"
                + "AND IT.COMPANY_ID = CO.ID";

        String[] selectionArgs = { String.valueOf(id)};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,selectionArgs );
        Item item = null ;
        if (cursor.moveToFirst()) {
            Company company ;
            item = new Item() ;
            item.setId(cursor.getLong(0));
            item.setBrandName(cursor.getString(1));
            item.setGenericName(cursor.getString(2));
            company = new Company();
            company.setName(cursor.getString(3));
            company.setCountryCode(cursor.getString(4));
            item.setCompany(company);
        }
        cursor.close();
        db.close();
        return item;
    }


    public Request findRequestById(Long id) {
        String selectQuery = "SELECT RE.ID , RE.ENTRY_DATE ,RE.LAST_MODIFY_DATE, IT.ID , IT.BRAND_NAME , IT.GENERIC_NAME , CO.NAME , CO.COUNTRY_CODE "
                + "FROM REQUEST RE , ITEM IT , COMPANY CO "
                + "WHERE RE.ID = ? "
                + "AND RE.ITEM_ID = IT.ID "
                + "AND IT.COMPANY_ID = CO.ID";

        String[] selectionArgs = { String.valueOf(id)};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,selectionArgs );
        Request request = null ;
        if (cursor.moveToFirst()) {
            request = new Request() ;
            request.setId(cursor.getLong(0));
            try {
                request.setEntryDate(Utils.ISO_DATETIME_FORMAT.parse(cursor.getString(1)));
            } catch (ParseException e) {
                Log.e(TAG, "Error parse date : "+cursor.getString(1));
                e.printStackTrace();
            }

            try {
                request.setLastModifyDate(Utils.ISO_DATETIME_FORMAT.parse(cursor.getString(2)));
            } catch (ParseException e) {
                Log.e(TAG, "Error parse date : "+cursor.getString(2));
                e.printStackTrace();
            }

            Item item = new Item() ;
            item.setId(cursor.getLong(3));
            item.setBrandName(cursor.getString(4));
            item.setGenericName(cursor.getString(5));
            Company company = new Company();
            company.setName(cursor.getString(6));
            company.setCountryCode(cursor.getString(7));
            item.setCompany(company);
            request.setItem(item);
        }
        cursor.close();
        db.close();
        return request;
    }


    public List<Request> findAllRequests() {
        String selectQuery = "SELECT RE.ID , RE.ENTRY_DATE ,RE.LAST_MODIFY_DATE, IT.ID , IT.BRAND_NAME , IT.GENERIC_NAME , CO.NAME , CO.COUNTRY_CODE "
                + "FROM REQUEST RE , ITEM IT , COMPANY CO "
                + "WHERE RE.ITEM_ID = IT.ID "
                + "AND IT.COMPANY_ID = CO.ID "
                + "ORDER BY RE.LAST_MODIFY_DATE DESC ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<Request> requests = new ArrayList<>();
        if (cursor.moveToFirst()) {
            Request request = null ;
            do {
                request = new Request() ;
                request.setId(cursor.getLong(0));
                try {
                    request.setEntryDate(Utils.ISO_DATETIME_FORMAT.parse(cursor.getString(1)));
                } catch (ParseException e) {
                    Log.e(TAG, "Error parse date : "+cursor.getString(1));
                    e.printStackTrace();
                }

                try {
                    request.setLastModifyDate(Utils.ISO_DATETIME_FORMAT.parse(cursor.getString(2)));
                } catch (ParseException e) {
                    Log.e(TAG, "Error parse date : "+cursor.getString(2));
                    e.printStackTrace();
                }

                Item item = new Item() ;
                item.setId(cursor.getLong(3));
                item.setBrandName(cursor.getString(4));
                item.setGenericName(cursor.getString(5));
                Company company = new Company();
                company.setName(cursor.getString(6));
                company.setCountryCode(cursor.getString(7));
                item.setCompany(company);
                request.setItem(item);
                requests.add(request);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return requests;
    }


    public List<Item> itemsSearch(String text) {
        String selectQuery = "SELECT IT.ID , IT.BRAND_NAME , IT.GENERIC_NAME , CO.NAME , CO.COUNTRY_CODE "
                + "FROM ITEM IT , COMPANY CO "
                + "WHERE IT.COMPANY_ID = CO.ID "
                + "AND IT.ID IN (SELECT ID FROM ITEM_VT WHERE ITEM_VT MATCH ?)";

        SQLiteDatabase db = this.getReadableDatabase();
        text = "*"+text+"*";
        String[] selectionArgs = { text};
        Cursor cursor = db.rawQuery(selectQuery, selectionArgs);

        List<Item> items = new ArrayList<>();
        if (cursor.moveToFirst()) {
            Item item ;
            Company company ;
            do {
                item = new Item() ;
                item.setId(cursor.getLong(0));
                item.setBrandName(cursor.getString(1));
                item.setGenericName(cursor.getString(2));
                company = new Company();
                company.setName(cursor.getString(3));
                company.setCountryCode(cursor.getString(4));
                item.setCompany(company);
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }


    public void addRequest(Request request) {
        Request result = findRequestById(request.getId());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if ( result != null) {
            values.put("LAST_MODIFY_DATE", Utils.ISO_DATETIME_FORMAT.format(new Date()));
            String[] selectionArgs = { String.valueOf(request.getId())};
            db.update("REQUEST", values,"ID=?",selectionArgs);
        }

        else {
            values.put("ID", request.getId());
            values.put("ITEM_ID", request.getItem().getId());
            values.put("ENTRY_DATE", Utils.ISO_DATETIME_FORMAT.format(request.getEntryDate()));
            values.put("LAST_MODIFY_DATE", Utils.ISO_DATETIME_FORMAT.format(new Date()));
            db.insert("REQUEST", null, values);
        }
        db.close();
    }


    private  void createDataBase(Context context) throws IOException
    {
        boolean dbExist = checkDataBase();
        if (!dbExist) {
            try {
                this.getReadableDatabase();
                copyDataBase(context);
                createItemVT();
            }
            catch (IOException e) {
                e.printStackTrace();
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase()
    {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch (SQLiteException e) {
            Log.e(TAG, "" + e);
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    private void copyDataBase(Context context) throws IOException
    {
        InputStream myInput  = context.getAssets().open(DATABASE_NAME);

        String outFileName = DB_PATH;

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

    private void createItemVT() {
        SQLiteDatabase  db = this.getWritableDatabase()  ;
        String CREATE_ITEMVT_TABLE = "CREATE VIRTUAL TABLE ITEM_VT USING FTS4(ID,BODY)";
        db.execSQL(CREATE_ITEMVT_TABLE);
        db.close();

        db = this.getWritableDatabase()  ;
        String INSERT_TO_ITEMVT = "INSERT INTO ITEM_VT(ID,BODY) " +
                "SELECT IT.ID , IT.BRAND_NAME || ' '|| IT.GENERIC_NAME ||' '||CO.NAME FROM ITEM IT , COMPANY CO " +
                "WHERE IT.COMPANY_ID = CO.ID";

        db.execSQL(INSERT_TO_ITEMVT);
        db.close();
    }

}
