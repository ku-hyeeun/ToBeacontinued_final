package com.androidapp.tobeacontinue.Todolist;

//하나의 일정에 체크박스, 내용, 날짜가 있는 데 그 데이터를 담기 위한 클래스 정의
public class Note{
    int _id;                       //id -> 일정 db 저장을 위함
    String contents;               //내용
    String createDateStr;          //날짜

    public Note(int _id,String contents,String createDateStr){
        this._id = _id;
        this.contents = contents;
        this.createDateStr = createDateStr;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }
}
