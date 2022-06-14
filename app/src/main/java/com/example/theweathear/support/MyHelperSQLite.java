package com.example.theweathear.support;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.theweathear.model.City;

public class MyHelperSQLite extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "The_Weather.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "my_city";
    private static final String ID = "id";
    private static final String NAME_CITY = "name_city";
    private static final String LAT = "lat";
    private static final String LON = "lon";


    public MyHelperSQLite(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME_CITY + " TEXT, " +
                LAT + " TEXT, " +
                LON + " TEXT); ";
        db.execSQL(query);


    }
    public void addCity(City city){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAME_CITY,city.getName());
        cv.put(LAT,city.getLat());
        cv.put(LON,city.getLon());
        long result = db.insert(TABLE_NAME,null,cv);
        if (result == -1 ){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Ok", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        onCreate(db);
    }
}
