package com.pay.aramydeandroidtest.db;
import androidx.room.*;


import java.util.List;

@Dao
public interface  ContactDAO {

    @Insert
    public  long addContact(Contact contact);

    @Update
    public void updateContact(Contact contact);

    @Delete
    public void deleteContact(Contact contact);

    @Query("select * from contacts")
    public List<Contact> getContacts();

    @Query("select * from contacts where contact_id ==:contactId")
    public Contact getContact(long contactId);


}