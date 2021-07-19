package com.pay.aramydeandroidtest.db;

import androidx.room.*;

@Database(entities = {Contact.class},version = 1)
public abstract class ContactsAppDatabase extends RoomDatabase {

    public abstract ContactDAO getContactDAO();

}
