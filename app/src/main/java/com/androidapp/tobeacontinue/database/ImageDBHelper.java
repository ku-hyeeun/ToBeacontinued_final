package com.androidapp.tobeacontinue.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ImageDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "imageDB";
    private static final String DB_TABLE = "table_image";

    private static final String KEY_IMAGE = "image_data";

    private static final String CREATE_TABLE_IMAGE =
            "CREATE TABLE "+DB_TABLE+"("+KEY_IMAGE+" TEXT);";

    public ImageDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_IMAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DB_TABLE);
        onCreate(db);
    }

    SQLiteDatabase db;

    public void insertImage(String imageUri){
        if(imageUri != null) {
            String sql1 = "INSERT INTO " + CREATE_TABLE_IMAGE + " VALUES('" + imageUri + "');";
            db.execSQL(sql1);
        }
    }

}
