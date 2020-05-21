package com.androidapp.tobeacontinue.Todolist;

import java.io.Serializable;

public class GeofencingMemo implements Serializable {

    int id;                                //db저장을 위함
    public String placeText;
    public String contentText;

    public GeofencingMemo(int id, String placeText, String contentText){
        this.id = id;
        this.placeText = placeText;
        this.contentText = contentText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaceText() {
        return placeText;
    }

    public void setPlaceText(String placeText) {
        this.placeText = placeText;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

}
