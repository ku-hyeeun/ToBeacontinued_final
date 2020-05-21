package com.androidapp.tobeacontinue.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.androidapp.tobeacontinue.Todolist.CalendarMemo;

import java.util.ArrayList;

public class CalendarDBHelper {

    private static final String dbName="myMemotest1";
    private static final String table1="MemoTable";
    private static final int dbVersion=1;

    //db관련객체
    private OpenHelper opener;
    private SQLiteDatabase db;
    private Context context;

    public CalendarDBHelper(Context context) {
        this.context = context;
        this.opener=new OpenHelper(context,dbName,null,dbVersion);
        db=opener.getWritableDatabase();
    }


    private class OpenHelper extends SQLiteOpenHelper{

        public OpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            //onCreate에서 DB생성 , 생성된 DB가 없을때 한번만 호출됨.
            String create="CREATE TABLE "+table1+"("+
                    "seq integer PRIMARY KEY AUTOINCREMENT, "+
                    "maintext text,"+
                    "subtext text,"+
                    "timetext text,"+
                    "isdone integer)";

            sqLiteDatabase.execSQL(create);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ table1);
            onCreate(sqLiteDatabase);
        }

        @Override
        public void close(){
            db.close();
        }
    }

    //INSERT INTO MemoTable VALUES(NULL,"MAINTEXT","SUBTEXT",0,"TimeTEXT"); (NULL,'"+memo.maintext+"','"+memo.subtext+"',"+memo.getIsdone()+");";
    public void insertMemo(CalendarMemo memo){
        String sql="INSERT INTO "+table1+" VALUES(NULL,'"+memo.maintext+"','"+memo.subtext+"','"+memo.timetext+"',"+memo.getIsdone()+");";
        db.execSQL(sql);
    }

    //데이터삭제 DELETE FROM Memo Table WHERE seq=0; =>MemoTable의 0번쨰 지우기
    public  void deleteMemo(int position){
        String sql="DELETE FROM "+table1+" WHERE seq="+position+";";
        db.execSQL(sql);
    }

    //데이터조회 SELECT * FROM MemoTable;
    public ArrayList<CalendarMemo> selectAll(){

        String sql="SELECT * FROM "+table1;
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