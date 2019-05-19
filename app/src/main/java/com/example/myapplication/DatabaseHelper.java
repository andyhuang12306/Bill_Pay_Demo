package com.example.myapplication;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "oncreate");

        String sqlBill="Create table Bill("+"bill_no text not null,"+"bill_name text,"+"bill_amount int );";
        String sqlPay="Create table Payment("+"pay_no text not null,"+"pay_name text,"+"pay_amount int );";
        try{
            db.execSQL(sqlBill);
            db.execSQL(sqlPay);
        }catch (SQLException e){
            Log.e("ERROR", e.toString());
        }

        String name = "Singtel Mobile Data";
        String amount = "50";

        for (int i = 0; i < 30; i++) {
            String sql = "insert into Bill" + "(bill_no, bill_name, bill_amount) values('" + i + "','" + (name + i) + "','" + (amount + i) + "');";
            try {
                db.execSQL(sql);
            } catch (SQLException e) {
                Log.e("ERROR", e.toString());
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
