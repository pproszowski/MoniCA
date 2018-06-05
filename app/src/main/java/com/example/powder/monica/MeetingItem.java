package com.example.powder.monica;

public class MeetingItem {
    private String name;
    private String date;
    private boolean checked;

    public MeetingItem(String name, String date) {
        this.name = name;
        this.date = date;
        this.checked = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isChecked() {
        return checked;
    }

    public void check(){
        checked = true;
    }

    public void unCheck(){
        checked = false;
    }
}
