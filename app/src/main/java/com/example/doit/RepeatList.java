package com.example.doit;

public class RepeatList {
    String content;
    int chkId;
    String uid;
    String date;  // 원본이 등록된 날짜

    public RepeatList(){}

    public RepeatList(String content, int chkId, String uid, String date) {
        this.content = content;
        this.chkId = chkId;
        this.uid = uid;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getChkId() {
        return chkId;
    }

    public void setChkId(int chkId) {
        this.chkId = chkId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
