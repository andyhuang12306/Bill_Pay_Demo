package com.example.myapplication;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class PaymentContentProvider extends ContentProvider {

    private static final String DB_NAME="BankCenter.db";
    private static final String TABLE_NAME="Payment";
    private static final int DB_VERSION=1;
    private DatabaseHelper mDatabaseHelper;

    @Override
    public boolean onCreate() {
        mDatabaseHelper =new DatabaseHelper(getContext(), DB_NAME, null, DB_VERSION);
        return true;
    }
    
    @Override
    public Cursor query(  Uri uri,   String[] projection,   String selection,   String[] selectionArgs,   String sortOrder) {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor c = db.query(TABLE_NAME, projection, null, null, null, null, null);
        return c;
    }

    @Override
    public String getType(  Uri uri) {
        return null;
    }

    @Override
    public Uri insert(  Uri uri,   ContentValues values) {
        String payNo = values.get("pay_no").toString();
        String payName = values.get("pay_name").toString();
        String payAmount = values.get("pay_amount").toString();
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String sql="insert into "+TABLE_NAME+"(pay_no, pay_name, pay_amount) values('"+payNo+"','"+payName+"','"+payAmount+"');";
        try{
            db.execSQL(sql);
        }catch (SQLException e){
            Log.e("ERROR", e.toString());
        }
        return null;
    }

    @Override
    public int delete(  Uri uri,   String selection,   String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        db.delete(selection, selectionArgs[0]+"='"+selectionArgs[1]+"'", null);
        return 0;
    }

    @Override
    public int update(  Uri uri,   ContentValues values,   String selection,   String[] selectionArgs) {
        return 0;
    }
}
