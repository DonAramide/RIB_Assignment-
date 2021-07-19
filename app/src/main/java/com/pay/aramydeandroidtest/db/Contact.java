package com.pay.aramydeandroidtest.db;

import androidx.room.*;

import androidx.room.ColumnInfo;


@Entity(tableName = "contacts")
public class Contact {


    @ColumnInfo(name="contact_log")
    private String log;

    @ColumnInfo(name="contact_lag")
    private String lag;

    @ColumnInfo(name="contact_id")
    @PrimaryKey(autoGenerate =true)
    private long id;


    @Ignore
    public Contact() {
    }



    public Contact(long id, String log, String lag) {

        this.log = log;
        this.lag = lag;
        this.id = id;
    }


    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getLag() {
        return lag;
    }

    public void setLag(String lag) {
        this.lag = lag;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


}
