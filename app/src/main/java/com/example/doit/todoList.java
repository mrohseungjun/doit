package com.example.doit;

//테이블이라고 생각하고, 테이블에 들어갈 속성값을 넣기
//파이어베이스는 RDBMS와 다르기 때문에 테이블이라는 개념이 없음. 원래는 키값이라고 부름
public class todoList {
    String content;
    String date;
    int chkId;  // 체크 여부 확인
    String uid;

    public todoList(){}

    public todoList(String content, String date, int chkId, String uid) {
        this.content = content;
        this.date = date;
        this.chkId = chkId;
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
