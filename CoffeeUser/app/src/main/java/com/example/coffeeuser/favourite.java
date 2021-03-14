package com.example.coffeeuser;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class favourite extends AppCompatActivity {
    ListView l1;
    Button OrderButton,gomenu;
    ArrayList<menuitemlist> itemm = new ArrayList<>();
    public static ArrayList<menuitemlist> order_n;
    public static ArrayList<orderlistinfo> listitem = new <orderlistinfo>ArrayList();
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        listitem=new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        l1 = findViewById(R.id.l1);
      OrderButton = findViewById(R.id.orderr);
      gomenu = findViewById(R.id.gomenu);
        final String cafename=getIntent().getStringExtra("cafename");
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
              //  goorder();
            }
        });
        OrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listitem = filllistitem();
                Intent intent = new Intent(getApplicationContext(), Order.class);
                intent.putExtra("activity", "favourite");
                intent.putExtra("name", cafename);
                startActivity(intent);
            }
        });
        gomenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listitem = filllistitem();
                Intent intent = new Intent(getApplicationContext(), Slidemenu.class);
                intent.putExtra("activity", "favourite");
                intent.putExtra("cafe", cafename);
                startActivity(intent);

            }
        });
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

 public  void  goorder(){
     listitem = filllistitem();
     final String cafename=getIntent().getStringExtra("cafename");
     Intent intent = new Intent(getApplicationContext(), Order.class);
     intent.putExtra("activity", "favourite");
     intent.putExtra("name", cafename);
     startActivity(intent);
 }
    public ArrayList<orderlistinfo> filllistitem() {
        for (int i = 0; i < itemm.size(); i++) {
            listitem.add(new orderlistinfo(itemm.get(i).itemname, itemm.get(i).price, 1));
        }

        return listitem;

    }

    class Listadapter extends BaseAdapter {
        ArrayList<menuitemlist> listitem = new <menuitemlist>ArrayList();

        Listadapter(ArrayList<menuitemlist> listadap) {
            this.listitem = listadap;

        }

        @Override
        public int getCount() {
            return listitem.size();

        }

        @Override
        public Object getItem(int position) {

            return listitem.get(position);
        }

        public String getprice(int position) {

            return listitem.get(position).price;
        }

        public String getname(int position) {

            return listitem.get(position).itemname;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layot = getLayoutInflater();
            View v1 = layot.inflate(R.layout.favoutitecomponent, null);
            CircleImageView image = v1.findViewById(R.id.circuleimage);
            final TextView nameitem = v1.findViewById(R.id.typee);
            final TextView price = v1.findViewById(R.id.pricee);
            TextView Calories = v1.findViewById(R.id.caloriess);
            nameitem.setText(listitem.get(position).itemname);
            Calories.setText(listitem.get(position).calories + "");
            price.setText(listitem.get(position).price);
            String imagee = listitem.get(position).image;

            if (imagee != null) {
                Uri imageUri = Uri.parse(imagee);
                Uri u1 = imageUri;
                Glide.with(getApplicationContext()).load(u1).circleCrop().into(image);
            }

            return v1;
        }
    }


    @Override
    protected void onStart() {
        itemm=new ArrayList<>();
        super.onStart();
        l1 = findViewById(R.id.l1);
        final String cafename = getIntent().getStringExtra("cafename");
        ArrayList<String> listitem = (ArrayList<String>) getIntent().getSerializableExtra("favlist");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        for (int i = 0; i < listitem.size(); i++) {
            myRef.child("Menu").child(cafename).child(listitem.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                          @Override
                                                                                                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                              String value = (String) dataSnapshot.child("calories").getValue();
                                                                                                              Integer cal = Integer.parseInt(value);
                                                                                                              String value2 = String.valueOf(dataSnapshot.child("itemname").getValue());
                                                                                                              String value3 = String.valueOf(dataSnapshot.child("price").getValue());
                                                                                                              String url = String.valueOf(dataSnapshot.child("image").child("imageurl").getValue());
                                                                                                              itemm.add(new menuitemlist(value3, value2, url, cal));

                                                                                                              Listadapter adapterr = new Listadapter(itemm);
                                                                                                              l1.setAdapter(adapterr);

                                                                                                          }

                                                                                                          @Override
                                                                                                          public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                          }

                                                                                                      }
            );


        }

    }
}








