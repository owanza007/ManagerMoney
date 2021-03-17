package com.second.moneymanager;

public class Data {
    private String type;
    String note;
    int amount;
    long date;

    public Data(String type, int amount, String note, long date) {
    }

    public Data(String type, String note, int amount, long data) {
        this.type = type;
        this.note = note;
        this.amount = amount;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
