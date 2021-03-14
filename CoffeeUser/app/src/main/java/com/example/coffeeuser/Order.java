package com.example.coffeeuser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coffeeuser.ui.share.ShareFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class Order extends AppCompatActivity {
    ListView l1;
    public static ArrayList<orderlistinfo> mainorder_n;
    ArrayList<orderlistinfo> list;
    int totalprice = 0;
    TextView total;
    String date;
    String datechild;
    String ordertime, firsttime;
    Button confirm, cancel, Update;
    Spinner spinnerseat;
    int[] seatdeactive;
    String seatorder;
    int spinnercount;
    String push;
    String cafename="";
    int point =0;
   // int  flag =0;
    SharedPreferences seatt ;
    SharedPreferences.Editor edit ;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        String activity =getIntent().getExtras().getString("activity");
        if(activity!=null &&activity.equalsIgnoreCase("favourite")){
            cafename =getIntent().getExtras().getString("name");
        }
        else {
            SharedPreferences    result = getSharedPreferences("cafename", Context.MODE_PRIVATE);
        cafename = result.getString("name", "datanotfound");}
        SharedPreferences pointt = getSharedPreferences("point", Context.MODE_PRIVATE);
        point = Integer.parseInt(pointt.getString("pointt", "0"));
        spinnerseat = (Spinner) findViewById(R.id.spinnerseat);
        l1 = (ListView) findViewById(R.id.Orders);
        String p = getIntent().getStringExtra("activity");
        if (p.equalsIgnoreCase("favourite")) {
            mainorder_n = favourite.listitem;
        } else if (p.equalsIgnoreCase("ToolsFragment") || p.equalsIgnoreCase("ShareFragment") || p.equalsIgnoreCase("SlideshowFragment") || p.equalsIgnoreCase("GalleryFragment")) {
            mainorder_n = ShareFragment.order_n;
        }
        total = (TextView) findViewById(R.id.total);
        confirm = (Button) findViewById(R.id.confirm);
        cancel = (Button) findViewById(R.id.cancel2);
        Update = (Button) findViewById(R.id.update);
        list = new ArrayList<orderlistinfo>();
        final Calendar rightNow = Calendar.getInstance();
        int year = rightNow.get(Calendar.YEAR);
        int mounth = rightNow.get(Calendar.MONTH) + 1;
        int day = rightNow.get(Calendar.DAY_OF_MONTH);
        date = day + "/" + mounth + "/" + year;
        datechild = "" + day + mounth + year;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                confirm();
            }
        });

        SharedPreferences time = getSharedPreferences("entertime", Context.MODE_PRIVATE);
        firsttime = time.getString("time", ordertime);
        spinnerseat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerseat = (Spinner) findViewById(R.id.spinnerseat);
                spinnercount = Integer.parseInt(spinnerseat.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (!(mainorder_n.isEmpty())) {
            for (int j = 0; j < mainorder_n.size(); j++) {
                list.add(new orderlistinfo(mainorder_n.get(j).name, mainorder_n.get(j).price, mainorder_n.get(j).quintity));
            }
        }
        Listadapter adapter = new Listadapter(list);
        l1.setAdapter(adapter);
        for (int j = 0; j < list.size(); j++) {
            int count = list.get(j).quintity;
            String price = list.get(j).price;
            String pricewithsplit = "";//cut $ from end
            pricewithsplit = price.substring(0, price.length() - 1);
            totalprice += (Integer.parseInt(pricewithsplit) * (count));
        }
        total.setText(totalprice + "");

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar rightNow = Calendar.getInstance();
                if (mainorder_n.isEmpty() || totalprice == 0) {
                    Toast.makeText(getApplicationContext(), " No Order ", Toast.LENGTH_LONG).show();
                } else {

                    int x = point + 1;
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference dataa = database.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    dataa.child("point").setValue(x + "");
                    SharedPreferences emailuser = getSharedPreferences("useremail", Context.MODE_PRIVATE);
                    String email = emailuser.getString("useremail", "datanotfound");
                    SharedPreferences username = getSharedPreferences("usernamee", Context.MODE_PRIVATE);
                    String name = username.getString("nameuser", "datanotfound");
                    String totalprice = "";
                    if (x >= 2) {
                        Double newprice = (Double) (Double.parseDouble(total.getText().toString()) * .5);
                        totalprice = newprice + "";
                    } else {
                        totalprice = total.getText().toString();
                    }


                    DatabaseReference data = database.getReference().child("Order").child(cafename);
                    push = data.push().getKey();
                    int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
                    int minutes = rightNow.get(Calendar.MINUTE);
                    ordertime = currentHourIn24Format + ":" + minutes;
                    for (int i = 0; i < list.size(); i++) {
                        orderitem a = new orderitem(list.get(i).price, "" + list.get(i).quintity, list.get(i).name);
                        data.child(push).child("item").child(list.get(i).name).setValue(a);
                    }
                    data.child(push).child("username").setValue(username);
                    data.child(push).child("Date").setValue(date);
                    data.child(push).child("OrderTime").setValue(ordertime);
                    data.child(push).child("entertime").setValue(firsttime);
                    data.child(push).child("useremail").setValue(email);
                    data.child(push).child("username").setValue(name);
                    data.child(push).child("deactivate").setValue("0");
                    data.child(push).child("seatnumber").setValue(spinnercount);
                    data.child(push).child("totalprice").setValue(totalprice + "$");
                    Toast.makeText(getApplicationContext(), "Your Order Had Been Confirmed", Toast.LENGTH_SHORT).show();
                }

                FirebaseDatabase upload = FirebaseDatabase.getInstance();
                DatabaseReference data = upload.getReference().child("Seat").child(cafename).child("" + spinnercount);
                data.setValue("");
//                seatt = getSharedPreferences("seat", Context.MODE_PRIVATE);
//                edit = seatt.edit();
//                edit.putInt("flag",0);
//                edit.apply();
//               flag=0;
              SavePreferences();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                total.setText("");
                l1.setAdapter(null);
                ShareFragment.order_n.clear();
            }
        });
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainorder_n.isEmpty() || totalprice == 0) {
                    Toast.makeText(getApplicationContext(), " No Order ", Toast.LENGTH_LONG).show();
                } else {
                    final int x = point;
                    SharedPreferences emailuser = getSharedPreferences("useremail", Context.MODE_PRIVATE);
                    final String email = emailuser.getString("useremail", "datanotfound");
                    final SharedPreferences username = getSharedPreferences("usernamee", Context.MODE_PRIVATE);
                    final String name = username.getString("nameuser", "datanotfound");

                    LoadPreferences();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference data = database.getReference().child("Order").child(cafename);

//                   if(flag==1){
//                        seatt = getSharedPreferences("seat", Context.MODE_PRIVATE);
//                        edit = seatt.edit();
//                        edit.putInt("flag",0);
//                        edit.apply();
//                        flag=0;
//
//
//                    if (x >= 2) {
//                        Double newprice = (Double) (Double.parseDouble(total.getText().toString()) * .5);
//
//                        Double totalprice = newprice ;
//                        data.child(push).child("totalprice").setValue(totalprice + "$");
//                    } else {
//                        Double totalprice = Double.parseDouble(total.getText().toString());
//
//                        data.child(push).child("totalprice").setValue(totalprice + "$");
//                    }
//                    }
                //    else {

                        data.child(push).child("totalprice").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Double totalprice=0.0;
                                if (x >= 2) {
                                    Double newprice =  (Double.parseDouble(total.getText().toString()) * .5);
                                     totalprice = newprice ;
                                } else {
                                     totalprice = Double.parseDouble(total.getText().toString());
                                }
                                String price = dataSnapshot.getValue().toString();
                                 totalprice += Double.parseDouble(price.substring(0, price.length()-1));


                             data.child(push).child("totalprice").setValue(totalprice + "$");

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

               //     }

                    for (int i = 0; i < list.size(); i++) {
                        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
                        int minutes = rightNow.get(Calendar.MINUTE);
                        ordertime = currentHourIn24Format + ":" + minutes;
                        final int finalI = i;
                        data.child(push).child("item").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(list.get(finalI).name)){
                                   int c = Integer.parseInt(dataSnapshot.child(list.get(finalI).name).child("count").getValue().toString());
                                    data.child(push).child("item").child(list.get(finalI).name).child("count").setValue(c+list.get(finalI).quintity);
                                    data.child(push).child("username").setValue(username);
                                    data.child(push).child("Date").setValue(date);
                                    data.child(push).child("OrderTime").setValue(ordertime);
                                    data.child(push).child("entertime").setValue(firsttime);
                                    data.child(push).child("useremail").setValue(email);
                                    data.child(push).child("username").setValue(name);
                                    data.child(push).child("deactivate").setValue("0");
                                    data.child(push).child("seatnumber").setValue(spinnercount);

                                    FirebaseDatabase upload = FirebaseDatabase.getInstance();
                                    DatabaseReference dataa = upload.getReference().child("Seat").child(cafename).child("" + spinnercount);
                                    Toast.makeText(getApplicationContext(),"Your Order Had Been Updated",Toast.LENGTH_SHORT).show();
                                    dataa.setValue("");
                                }
                                else {
                                    orderitem a = new orderitem(list.get(finalI).price, "" + list.get(finalI).quintity, list.get(finalI).name);
                                    data.child(push).child("item").child(list.get(finalI).name).setValue(a);
                                    data.child(push).child("username").setValue(username);
                                    data.child(push).child("Date").setValue(date);
                                    data.child(push).child("OrderTime").setValue(ordertime);
                                    data.child(push).child("entertime").setValue(firsttime);
                                    data.child(push).child("useremail").setValue(email);
                                    data.child(push).child("username").setValue(name);
                                    data.child(push).child("deactivate").setValue("0");
                                    data.child(push).child("seatnumber").setValue(spinnercount);

                                    FirebaseDatabase upload = FirebaseDatabase.getInstance();
                                    DatabaseReference dataa = upload.getReference().child("Seat").child(cafename).child("" + spinnercount);
                                    Toast.makeText(getApplicationContext(),"Your Order Had Been Updated",Toast.LENGTH_SHORT).show();
                                    dataa.setValue("");
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                    }
//                    data.child(push).child("username").setValue(username);
//                    data.child(push).child("Date").setValue(date);
//                    data.child(push).child("OrderTime").setValue(ordertime);
//                    data.child(push).child("entertime").setValue(firsttime);
//                    data.child(push).child("useremail").setValue(email);
//                    data.child(push).child("username").setValue(name);
//                    data.child(push).child("deactivate").setValue("0");
//                    data.child(push).child("seatnumber").setValue(spinnercount);
//
//                    FirebaseDatabase upload = FirebaseDatabase.getInstance();
//                    DatabaseReference dataa = upload.getReference().child("Seat").child(cafename).child("" + spinnercount);
//                    Toast.makeText(getApplicationContext(),"Your Order Had Been Updated",Toast.LENGTH_SHORT).show();
//                    dataa.setValue("");
                }

            }});



    }

    public void confirm() {
        final Calendar rightNow = Calendar.getInstance();

        if (mainorder_n.isEmpty() || totalprice == 0) {
            Toast.makeText(getApplicationContext(), " No Order ", Toast.LENGTH_LONG).show();
        } else {

            int x = point + 1;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference dataa = database.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            dataa.child("point").setValue(x + "");
            SharedPreferences emailuser = getSharedPreferences("useremail", Context.MODE_PRIVATE);
            String email = emailuser.getString("useremail", "datanotfound");
            SharedPreferences username = getSharedPreferences("usernamee", Context.MODE_PRIVATE);
            String name = username.getString("nameuser", null);
            String totalprice = "";
            if (x >= 2) {
                Double newprice = (Double) (Double.parseDouble(total.getText().toString()) * .5);
                totalprice = newprice + "";
            } else {
                totalprice = total.getText().toString();
            }


            DatabaseReference data = database.getReference().child("Order").child(cafename);
            push = data.push().getKey();
            int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
            int minutes = rightNow.get(Calendar.MINUTE);
            ordertime = currentHourIn24Format + ":" + minutes;
            for (int i = 0; i < list.size(); i++) {
                orderitem a = new orderitem(list.get(i).price, "" + list.get(i).quintity, list.get(i).name);
                data.child(push).child("item").child(list.get(i).name).setValue(a);
            }
            data.child(push).child("username").setValue(username);
            data.child(push).child("Date").setValue(date);
            data.child(push).child("OrderTime").setValue(ordertime);
            data.child(push).child("entertime").setValue(firsttime);
            data.child(push).child("useremail").setValue(email);
            data.child(push).child("username").setValue(name);
            data.child(push).child("deactivate").setValue("0");
            data.child(push).child("seatnumber").setValue(spinnercount);
            data.child(push).child("totalprice").setValue(totalprice + "$");
            Toast.makeText(getApplicationContext(), "Your Order Had Been Confirmed", Toast.LENGTH_SHORT).show();
        }

        FirebaseDatabase upload = FirebaseDatabase.getInstance();
        DatabaseReference data = upload.getReference().child("Seat").child(cafename).child("" + spinnercount);
        data.setValue("");
    }

    @Override
    public void onResume() {
        super.onResume();

        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {

        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        list.clear();
        total.setText("");
        l1.setAdapter(null);
        String p = getIntent().getStringExtra("activity");
        if(p.equalsIgnoreCase("ToolsFragment") || p.equalsIgnoreCase("ShareFragment") || p.equalsIgnoreCase("SlideshowFragment") || p.equalsIgnoreCase("GalleryFragment"))
        ShareFragment.order_n.clear();
        else if (p.equalsIgnoreCase("favourite"))
            favourite.listitem.clear();
        SavePreferences();
        final String cafename=getIntent().getStringExtra("name");
        Intent  update=new Intent(getApplicationContext(),Slidemenu.class);
        update.putExtra("cafename",cafename);
        update.putExtra("activity","Order");
        startActivity(update);
        super.onBackPressed();
    }

    public void SavePreferences() {
        String seat = "" + spinnercount;
        String path = "" + push;
        seatt = getSharedPreferences("seat", Context.MODE_PRIVATE);
      edit = seatt.edit();
        edit.putString("seatid", seat);
        edit.putString("path", path);
        edit.putString("name", cafename);
       // edit.putInt("flag",1);
        edit.apply();
    }

    public void LoadPreferences() {
    seatt = getSharedPreferences("seat", Context.MODE_PRIVATE);
        spinnercount = Integer.parseInt(seatt.getString("seatid", null));
        push = seatt.getString("path", null);
        cafename=seatt.getString("name", null);
       // flag=seatt.getInt("flag",0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final String cafename=getIntent().getStringExtra("name");
        seatdeactivecount(cafename);

    }

    public void fillspinner(final String cafename) {
        final int[] seattcount = new int[1];
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("Coffee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String name = dataSnapshot1.child("coffeename").getValue().toString();
                    if (name.equalsIgnoreCase(cafename)) {
                        String seat = dataSnapshot1.child("Aboutus").child("seat").getValue().toString();
                        seattcount[0] = Integer.parseInt(seat);

                    }
                }

                spinnerseat = (Spinner) findViewById(R.id.spinnerseat);
                String[] seatid = new String[seattcount[0]];
                for (int i = 0; i < seatid.length; i++) {
                    seatid[i] = (i + 1) + "";
                }


                ArrayList<String> seat = new ArrayList<String>();
                for (int i = 0; i < seatid.length; i++) {
                    for (int j = 0; j < seatdeactive.length; j++) {
                        if (Integer.parseInt(seatid[i]) == seatdeactive[j]) {
                            seatid[i] = null;
                            break;

                        }
                    }
                }
                for (int i = 0; i < seatid.length; i++) {
                    if (seatid[i] != null) {
                        seat.add(seatid[i]);
                    }
                }
                String newseat[] = new String[seat.size()];
                for (int i = 0; i < newseat.length; i++) {
                    newseat[i] = seat.get(i);
                }
                CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), newseat);
                spinnerseat.setAdapter(customAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void seatdeactivecount(final String cafename) {
        final int[] childcount = new int[1];
        FirebaseDatabase upload = FirebaseDatabase.getInstance();
        DatabaseReference data = upload.getReference().child("Seat").child(cafename);
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    childcount[0]++;


                }
                seatdeactive(childcount[0], cafename);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void seatdeactive(int childcount, final String cafename) {
        FirebaseDatabase upload = FirebaseDatabase.getInstance();
        DatabaseReference data = upload.getReference().child("Seat").child(cafename);
        seatdeactive = new int[childcount];
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    seatdeactive[i] = Integer.parseInt(dataSnapshot1.getKey());
                    i++;
                }
                fillspinner(cafename);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    class Listadapter extends BaseAdapter {
        ArrayList<orderlistinfo> listitem = new <orderlistinfo>ArrayList();

        Listadapter(ArrayList<orderlistinfo> listadap) {
            this.listitem = listadap;

        }

        @Override
        public int getCount() {
            return listitem.size();

        }

        @Override
        public Object getItem(int position) {
            return listitem.get(position).price;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layot = getLayoutInflater();
            View v1 = layot.inflate(R.layout.componetorder, null);
            TextView name = v1.findViewById(R.id.tx1);
            TextView price = v1.findViewById(R.id.tx2);
            TextView quintity = v1.findViewById(R.id.tx3);
            name.setText(listitem.get(position).name);
            price.setText(listitem.get(position).price);
            quintity.setText("" + listitem.get(position).quintity);


            return v1;
        }
    }

    public class CustomAdapter extends BaseAdapter {
        Context context;
        String[] seatid;
        LayoutInflater inflter;

        public CustomAdapter(Context applicationContext, String[] countryNames) {
            this.context = applicationContext;
            this.seatid = countryNames;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return seatid.length;
        }

        @Override
        public Object getItem(int i) {
            return seatid[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.spinneritem, null);
            TextView seat = (TextView) view.findViewById(R.id.textView);
            seat.setText(seatid[i]);
            return view;
        }
    }
}


