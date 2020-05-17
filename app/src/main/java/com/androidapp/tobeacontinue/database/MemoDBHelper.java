package com.androidapp.tobeacontinue.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.androidapp.tobeacontinue.Todolist.Memo;

import java.util.ArrayList;

public class MemoDBHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "memo_reminder";  //DB Name
    private static final String TABLE1 = "HouseMemo";             //DB Table Name
    private static final String TABLE2 = "OutsideMemo";
    private static final String TABLE3 = "SchoolMemo";
    private static final String TABLE4 = "CafeteriaMemo";

    public static final int DATABASE_VERSION = 1;                //DB Version


    SQLiteDatabase db;

    public MemoDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //DB Table 만들기
    @Override
    public void onCreate(SQLiteDatabase db) {

        String create1 = "CREATE TABLE "+TABLE1+" (id INTEGER PRIMARY KEY AUTOINCREMENT, maintext TEXT, subtext TEXT, isdone INTEGER) ";
        String create2 = "CREATE TABLE "+TABLE2+" (id INTEGER PRIMARY KEY AUTOINCREMENT, maintext TEXT, subtext TEXT, isdone INTEGER) ";
        String create3 = "CREATE TABLE "+TABLE3+" (id INTEGER PRIMARY KEY AUTOINCREMENT, maintext TEXT, subtext TEXT, isdone INTEGER) ";
        String create4 = "CREATE TABLE "+TABLE4+" (id INTEGER PRIMARY KEY AUTOINCREMENT, maintext TEXT, subtext TEXT, isdone INTEGER) ";
        db.execSQL(create1);
        db.execSQL(create2);
        db.execSQL(create3);
        db.execSQL(create4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE1);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE2);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE3);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE4);
        onCreate(sqLiteDatabase);
    }


    //db에 data 삽입 구조
    //INSERT INTO MemoTable VALUES(NULL,'MAINTEXT','SUBTEXT',0);
    public void insertMemo1(Memo memo){

        String sql1 = "INSERT INTO "+TABLE1+" VALUES(NULL, '"+memo.contents+"', '"+memo.createDateStr+"', "+ memo.getIsdone()+");";
        db.execSQL(sql1);

    }
    public void insertMemo2(Memo memo){

        String sql2 = "INSERT INTO "+TABLE2+" VALUES(NULL, '"+memo.contents+"', '"+memo.createDateStr+"', "+ memo.getIsdone()+");";
        db.execSQL(sql2);

    }
    public void insertMemo3(Memo memo){

        String sql3 = "INSERT INTO "+TABLE3+" VALUES(NULL, '"+memo.contents+"', '"+memo.createDateStr+"', "+ memo.getIsdone()+");";
        db.execSQL(sql3);

    }
    public void insertMemo4(Memo memo){

        String sql4 = "INSERT INTO "+TABLE4+" VALUES(NULL, '"+memo.contents+"', '"+memo.createDateStr+"', "+ memo.getIsdone()+");";
        db.execSQL(sql4);

    }

    //db에 data 삭제 구조
    //DELETE FROM MemoTable WHERE id = 0;
    public void deleteMemo1(int position){
        String sql1 = "DELETE FROM "+TABLE1+" WHERE id = "+position+";";
        db.execSQL(sql1);
    }
    public void deleteMemo2(int position){

        String sql2 = "DELETE FROM "+TABLE2+" WHERE id = "+position+";";
        db.execSQL(sql2);

    }
    public void deleteMemo3(int position){

        String sql3 = "DELETE FROM "+TABLE3+" WHERE id = "+position+";";
        db.execSQL(sql3);
    }
    public void deleteMemo4(int position){
        String sql4 = "DELETE FROM "+TABLE4+" WHERE id = "+position+";";
        db.execSQL(sql4);
    }

    //db data조회 구조
    //SELECT * FROM MemoTable;
    public ArrayList<Memo> selectAll1(){
        db = this.getReadableDatabase();

        String sql = "SELECT * FROM "+TABLE1;

        ArrayList<Memo> list = new ArrayList<>();

        Cursor results = db.rawQuery(sql,null);
        results.moveToFirst();

        while(!results.isAfterLast()){                  //DB 마지막까지 반복문 돌기
            Memo memo = new Memo(results.getInt(0),results.getString(1),results.getString(2),results.getInt(3));
            list.add(memo);
            results.moveToNext();
        }

        return list;
    }

    public ArrayList<Memo> selectAll2(){

        db = this.getReadableDatabase();
        String sql = "SELECT * FROM "+TABLE2;

        ArrayList<Memo> list = new ArrayList<>();

        Cursor results = db.rawQuery(sql,null);
        results.moveToFirst();

        while(!results.isAfterLast()){                  //DB 마지막까지 반복문 돌기
            Memo memo = new Memo(results.getInt(0),results.getString(1),results.getString(2),results.getInt(3));
            list.add(memo);
            results.moveToNext();
        }

        return list;
    }

    public ArrayList<Memo> selectAll3(){
        db = this.getReadableDatabase();
        String sql = "SELECT * FROM "+TABLE3;

        ArrayList<Memo> list = new ArrayList<>();

        Cursor results = db.rawQuery(sql,null);
        results.moveToFirst();

        while(!results.isAfterLast()){                  //DB 마지막까지 반복문 돌기
            Memo memo = new Memo(results.getInt(0),results.getString(1),results.getString(2),results.getInt(3));
            list.add(memo);
            results.moveToNext();
        }

        return list;
    }

    public ArrayList<Memo> selectAll4(){
        db = this.getReadableDatabase();
        String sql = "SELECT * FROM "+TABLE4;

        ArrayList<Memo> list = new ArrayList<>();

        Cursor results = db.rawQuery(sql,null);
        results.moveToFirst();

        while(!results.isAfterLast()){                  //DB 마지막까지 반복문 돌기
            Memo memo = new Memo(results.getInt(0),results.getString(1),results.getString(2),results.getInt(3));
            list.add(memo);
            results.moveToNext();
        }

        return list;
    }



}