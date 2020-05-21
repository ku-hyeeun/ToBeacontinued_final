package com.androidapp.tobeacontinue.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.androidapp.tobeacontinue.Todolist.GeofencingMemo;

import java.util.ArrayList;

public class GeoDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Geofencingmemo";
    private static final String TABLE_extra = "extra_todolist";

    public GeoDBHelper(Context context){
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table " + TABLE_extra + "(id INTEGER PRIMARY KEY AUTOINCREMENT, place TEXT, content TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_extra);
        onCreate(db);
    }


    SQLiteDatabase db;

    public void addMemo(GeofencingMemo memo){
        String sql = "INSERT INTO "+TABLE_extra+" VALUES(NULL, '"+memo.placeText+"', '"+memo.contentText+"');";
        db.execSQL(sql);
    }

    public void deleteMemo(int position){
        String sql1 = "DELETE FROM "+TABLE_extra+" WHERE id = "+position+";";
        db.execSQL(sql1);
    }

    public ArrayList<GeofencingMemo> getAllText(){
        db = this.getReadableDatabase();

        String sql = "SELECT * FROM "+TABLE_extra;

        ArrayList<GeofencingMemo> list = new ArrayList<>();

        Cursor results = db.rawQuery(sql,null);
        results.moveToFirst();

        while(!results.isAfterLast()){                  //DB 마지막까지 반복문 돌기
            GeofencingMemo memo = new GeofencingMemo(results.getInt(0),results.getString(1),results.getString(2));
            list.add(memo);
            results.moveToNext();
        }

        return list;
    }
}
