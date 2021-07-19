package com.pay.aramydeandroidtest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pay.aramydeandroidtest.GPSstuff.EasyLocationInit;
import com.pay.aramydeandroidtest.GPSstuff.Event;
import com.pay.aramydeandroidtest.GPSstuff.LocationEvent;
import com.pay.aramydeandroidtest.GPSstuff.LockScreenService;
import com.pay.aramydeandroidtest.GPSstuff.MyAdmin;
import com.pay.aramydeandroidtest.db.Contact;
import com.pay.aramydeandroidtest.db.ContactsAdapter;
import com.pay.aramydeandroidtest.db.ContactsAppDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sin;
import static java.lang.StrictMath.cos;

public class MainActivity extends AppCompatActivity {
    private ContactsAdapter contactsAdapter;
    private ArrayList<Contact> contactArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ContactsAppDatabase contactsAppDatabase;


    String geolocation;
    double lat;
    double lon;
    TextView stv_lat_a,stv_lat_b,tv_distance,tv_distanceb,slong_b,slong_a ;

    Intent dialogIntent;
    private static final String TAG = "Welcome";
    private int timeInterval = 400;
    private int fastestTimeInterval = 400;
    private boolean runAsBackgroundService = false;
    private boolean startOrStop = false;
    private int startorNot = 0;
    AlertDialog.Builder alertDialogBuilder;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName compName;
    LinearLayout distanceb,distancestop;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAll();
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this, MyAdmin.class);
    }

    private void initAll() {
        Button start = (Button) findViewById(R.id.start);
        Button stop = (Button) findViewById(R.id.stop);
        distanceb = (LinearLayout) findViewById(R.id.distanceb);
        distancestop = (LinearLayout) findViewById(R.id.distancestop);
        stv_lat_b = findViewById(R.id.stv_lat_b);
        stv_lat_a = findViewById(R.id.stv_lat_a);
        slong_b = findViewById(R.id.slong_b);
        slong_a = findViewById(R.id.slong_a);
        tv_distance = findViewById(R.id.tv_distance);



        stop.setVisibility(View.INVISIBLE);
       // distancestop.setVisibility(View.INVISIBLE);


        dialogIntent = new Intent(this, MainActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (startorNot == 0) {
                    startOrStop = false;
                    v.setVisibility(View.INVISIBLE);
                    stop.setVisibility(View.VISIBLE);
                      distancestop.setVisibility(View.INVISIBLE);
                      distanceb.setVisibility(View.INVISIBLE);

                      new EasyLocationInit(MainActivity.this, timeInterval, fastestTimeInterval, runAsBackgroundService);
                }else startorNot = 0;
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrStop = true;
                v.setVisibility(View.INVISIBLE);
                start.setVisibility(View.VISIBLE);

                distancestop.setVisibility(View.VISIBLE);
                distanceb.setVisibility(View.VISIBLE);

                if (startorNot == 1) {
                    new EasyLocationInit(MainActivity.this, timeInterval, fastestTimeInterval, runAsBackgroundService);
                }else {
                    startorNot = 1;

                }

            }
        });


        recyclerView = findViewById(R.id.recycler_view_contacts);
        contactsAppDatabase= Room.databaseBuilder(getApplicationContext(), ContactsAppDatabase.class,"ContactDB").addCallback(callback).build();

        new GetAllContactsAsyncTask().execute();

        contactsAdapter = new ContactsAdapter(this, contactArrayList, MainActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contactsAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }



    @SuppressLint("SetText")
    @Subscribe
    public void getEvent(final Event event) {
        if(!startOrStop){


        if (event instanceof LocationEvent) {
            if (((LocationEvent) event).location != null) {
                ((TextView) findViewById(R.id.stv_lat_a)).setText("The Latitude is "
                        + ((LocationEvent) event).location.getLatitude());
                ((TextView) findViewById(R.id.slong_a)).setText( " Longitude is "
                        + ((LocationEvent) event).location.getLongitude());
                createContact("Started at LATI : "+((LocationEvent) event).location.getLatitude(), "Started at LONG: "+((LocationEvent) event).location.getLongitude());
                startOrStop=true;
                startorNot = 1;
            }
        }
        }else {
            if (startorNot == 1) {

                if (((LocationEvent) event).location != null) {
                    ((TextView) findViewById(R.id.stv_lat_b)).setText("The Latitude is "
                            + ((LocationEvent) event).location.getLatitude());
                    ((TextView) findViewById(R.id.slong_b)).setText( " Longitude is "
                            + ((LocationEvent) event).location.getLongitude());
                   Double distance = getKilometers( lat,  lon,  ((LocationEvent) event).location.getLatitude(),  ((LocationEvent) event).location.getLongitude());
                    ((TextView) findViewById(R.id.tv_distance)).setText("The Total Distance  Is "
                            + distance);
                    createContact("Stop at LATI : "+((LocationEvent) event).location.getLatitude(), "Started at LONG : "+((LocationEvent) event).location.getLongitude());
                    startorNot = 2;



                }
            }
        }
    }



    @Override
    protected void onStop() {
        super.onStop();

        doBindService();
        // Home button pressed
        EventBus.getDefault().unregister(this);

        boolean active = devicePolicyManager.isAdminActive(compName);

        if (active ) {//&   lockup
            devicePolicyManager.lockNow();
        } else {
            Toast.makeText(this, "You need to enable the Admin Device Features", Toast.LENGTH_SHORT).show();
        }

        Log.i(TAG, "===========================7...................................\n/n onStop >>>>>>........................>>>>>>>>>>>>>>>>> ");


        //   if(!leaveCalled) {
        // Do whatever you like.
    }
    @Override
    public void onPause() {
        if (isApplicationSentToBackground(this)){
            // Do what you want to do on detecting Home Key being Pressed
            dialogIntent = new Intent(this, MainActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        super.onPause();
    }
    public boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
    void doBindService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, LockScreenService.class));
        } else {
            startService(new Intent(this, LockScreenService.class));
        }

    }

    public double getKilometers(double lat1, double long1, double lat2, double long2) {
        Double distance;

        double PI_RAD = Math.PI / 180.0;
        double phi1 = lat1 * PI_RAD;
        double phi2 = lat2 * PI_RAD;
        double lam1 = long1 * PI_RAD;
        double lam2 = long2 * PI_RAD;
        startOrStop=true;

        distance = 6371.01 * cos(sin(phi1) * sin(phi2) + cos(phi1) * cos(phi2) * cos(lam2 - lam1));
        Log.d(TAG, "GPS Enable ======Distance is ===" + distance);
        return distance;}



    public  void showAlert2(String h)
    {

        alertDialogBuilder.setMessage(h);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        Looper.prepare();
        AlertDialog alertDialog = alertDialogBuilder.create();
        Looper.loop();
        alertDialog.show();
    }

    private void createContact(String name, String email) {

        new CreateContactAsyncTask().execute(new Contact(0,name,email));

    }

    private class GetAllContactsAsyncTask extends AsyncTask<Void,Void,Void>{


        @Override
        protected Void doInBackground(Void... voids) {

            contactArrayList.addAll(contactsAppDatabase.getContactDAO().getContacts());
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            contactsAdapter.notifyDataSetChanged();
        }
    }


    private class CreateContactAsyncTask extends AsyncTask<Contact,Void,Void> {



        @Override
        protected Void doInBackground(Contact... contacts) {

            long id = contactsAppDatabase.getContactDAO().addContact(contacts[0]);


            Contact contact = contactsAppDatabase.getContactDAO().getContact(id);

            if (contact != null) {

                contactArrayList.add(0, contact);


            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            contactsAdapter.notifyDataSetChanged();
        }
    }

    RoomDatabase.Callback callback= new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            Log.i(TAG, " on create invoked ");
        }


        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            //  Toast.makeText(getApplicationContext()," On Create Called ",Toast.LENGTH_LONG).show();
            Log.i(TAG, " on open invoked ");

        }

    };
}
