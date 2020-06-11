package com.androidapp.tobeacontinue.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.androidapp.tobeacontinue.Todolist.GeofencingMemo;

import java.util.ArrayList;

public class GeoDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Geofencingmemo";       //Database Name
    private static final String TABLE_extra = "extra_todolist";         //Table Name

    public GeoDBHelper(Context context){                                //생성자 정의
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table " + TABLE_extra + "(id INTEGER PRIMARY KEY AUTOINCREMENT, place TEXT, content TEXT)";
        //table 만들기 -> id 번호, 장소, 메모내용
        db.execSQL(createTable);        //db 실행
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_extra);          //테이블이 존재하면 삭제
        onCreate(db);
    }


    SQLiteDatabase db;                  //Sqlite db 선언


    //INSERT INTO extra_todolist VALUES(NULL,'place','content');
    public void addMemo(GeofencingMemo memo){
        String sql = "INSERT INTO "+TABLE_extra+" VALUES(NULL, '"+memo.placeText+"', '"+memo.contentText+"');";
        db.execSQL(sql);
    }

    //데이터 삭제
    public void deleteMemo(int position){
        String sql1 = "DELETE FROM "+TABLE_extra+" WHERE id = "+position+";";
        db.execSQL(sql1);
    }

    //데이터 조회
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
