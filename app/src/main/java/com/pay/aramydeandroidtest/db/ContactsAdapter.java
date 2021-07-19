package com.pay.aramydeandroidtest.db;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.pay.aramydeandroidtest.MainActivity;
import com.pay.aramydeandroidtest.R;

import java.util.ArrayList;


public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Contact> contactssList;
    private MainActivity mainActivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView log;
        public TextView lag;


        public MyViewHolder(View view) {
            super(view);
            log = view.findViewById(R.id.log);
            lag = view.findViewById(R.id.lag);

        }
    }


    public ContactsAdapter(Context context, ArrayList<Contact> contacts, MainActivity mainActivity) {
        this.context = context;
        this.contactssList = contacts;
        this.mainActivity = mainActivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {


        final Contact contact = contactssList.get(position);

        holder.log.setText(contact.getLog()+"");
        holder.lag.setText(contact.getLag()+"");

        holder.itemView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

               // mainActivity.addAndEditContacts(true, contact, position);
            }
        });

    }

    @Override
    public int getItemCount() {

        return contactssList.size();
    }


}

