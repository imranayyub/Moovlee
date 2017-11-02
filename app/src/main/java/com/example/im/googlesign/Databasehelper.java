package com.example.im.googlesign;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.NoCopySpan;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Im on 02-11-2017.
 */

public class Databasehelper extends SQLiteOpenHelper {
SQLiteDatabase db;

    public static final String DATABASE_NAME="contact.db";
    public static final int DATABASE_VERSION=1;
    public static final String TABLE_NAME="contact";
    public static final String COLUMN_EMAIL="email";
    public static final String COLUMN_NAME="name";
    public static final String COLUMN_TIME="time";
    public static final String COLUMN_APP="app";
    public static final String TABLE_CREATE="create table contact (NAME not null , EMAIL not null,APP not null,TIME not null);";


    String DB_PATH = null;


    public Databasehelper(Context context)
    {
super(context, DATABASE_NAME, null, DATABASE_VERSION);

         }

    @Override
    public void onCreate(SQLiteDatabase db) {
db.execSQL(TABLE_CREATE);
        this.db=db;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
String query="Drop if Exist "+TABLE_NAME;
        db.execSQL(query);
        onCreate(db);
    }

public void insert(String name , String email,String app,String time){
    db=this.getWritableDatabase();
    ContentValues values= new ContentValues();
    values.put(COLUMN_EMAIL,email);
    values.put(COLUMN_NAME,name);
    values.put(COLUMN_APP,app);
    values.put(COLUMN_TIME,time);
    String query="Delete from "+ TABLE_NAME+ " where "+TABLE_NAME+".email== "+email+" And "+TABLE_NAME+".app== "+app;
    db.insert(TABLE_NAME,null,values);

    }
public  void delete()
{
    String query = "Delete from "+TABLE_NAME;
    db.execSQL(query);
}
}