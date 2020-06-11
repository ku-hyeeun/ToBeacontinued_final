package com.androidapp.tobeacontinue.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.androidapp.tobeacontinue.Calendar.CalendarMemo;

import java.util.ArrayList;

public class CalendarDBHelper extends SQLiteOpenHelper {

    private static final String dbName="calendar_reminder";     //Database Name
    private static final String table = "CalendarMemo";         //Table Name
    private static final int dbVersion=1;                       //Database Version

    private SQLiteDatabase db;                                  //Sqlite db 선언

    public CalendarDBHelper(@Nullable Context context) {        //생성자 정의
        super(context,dbName,null,dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create="CREATE TABLE "+table+"(seq INTEGER PRIMARY KEY AUTOINCREMENT, maintext TEXT, subtext TEXT, timetext TEXT, isdone INTEGER)";
        //table 만들기 -> seq 번호, 메모 내용, 날짜, 시간, 완료여부
        db.execSQL(create);             //db 실행
    }

   @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ table);             //테이블이 존재하면 삭제
        onCreate(sqLiteDatabase);
    }

    //INSERT INTO MemoTable VALUES(NULL,'MAINTEXT','SUBTEXT','TimeTEXT',0);
    public void insertMemo(CalendarMemo memo){
        String sql="INSERT INTO "+table+" VALUES(NULL,'"+memo.maintext+"','"+memo.subtext+"','"+memo.timetext+"',"+memo.getIsdone()+");";
        db.execSQL(sql);
    }

    //데이터 삭제 DELETE FROM Memo Table WHERE seq=0; =>MemoTable의 0번째 지우기
    public  void deleteMemo(int position){
        String sql="DELETE FROM "+table+" WHERE seq="+position+";";
        db.execSQL(sql);
    }

    //데이터 조회 SELECT * FROM MemoTable;
    public ArrayList<CalendarMemo> selectAll(){

        db = this.getReadableDatabase();
        String sql="SELECT * FROM "+table;

        ArrayList<CalendarMemo> list=new ArrayList<>();

        Cursor results=db.rawQuery(sql,null);
        results.moveToFirst();

        while(!results.isAfterLast()){  //  목록순서(seq) ,maintext, subtext, timetext, isdone

            CalendarMemo memo=new CalendarMemo(results.getInt(0), results.getString(1), results.getString(2), results.getString(3), results.getInt(4));
            list.add(memo);
            results.moveToNext();
        }

        results.close();
        return list;
    }

}
