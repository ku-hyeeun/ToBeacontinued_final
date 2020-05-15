package com.androidapp.tobeacontinue.Todolist;

import java.io.Serializable;

//하나의 일정에 체크박스, 내용, 날짜가 있는 데 그 데이터를 담기 위한 클래스 정의
public class Memo implements Serializable {

    int id;                                //db저장을 위함
    public String contents;               //내용
    public String createDateStr;          //날짜
    int isdone;                            //완료 여부
    boolean isSelected;

    public Memo(int id, String contents, String createDateStr, int isdone){
        this.id = id;
        this.contents = contents;
        this.createDateStr = createDateStr;
        this.isdone = isdone;
    }

    public Memo(int id, String contents, String createDateStr, int isdone, boolean isSelected) {
        this.id = id;
        this.contents = contents;
        this.createDateStr = createDateStr;
        this.isdone = isdone;
        this.isSelected = isSelected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getIsdone() {
        return isdone;
    }

    public void setIsdone(int isdone) {
        this.isdone = isdone;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}