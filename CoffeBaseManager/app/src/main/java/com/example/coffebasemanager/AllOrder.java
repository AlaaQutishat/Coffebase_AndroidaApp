package com.example.coffebasemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AllOrder extends AppCompatActivity {
    ListView l1;
    String cafenameee=" " ;
    TextView total;
    String [] name,price,count;
    Button complete,cancle;
    private FirebaseDatabase database ;
    private DatabaseReference myRef ;
     String username ;
     String seatid;
     String path;
     int childcount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_order);
        total=(TextView)findViewById(R.id.total);
        complete=(Button)findViewById(R.id.complete);
        cancle=(Button)findViewById(R.id.cancle);
        l1=(ListView)findViewById(R.id.Orderlist);
        SharedPreferences namee = getApplicationContext().getSharedPreferences("cafename", Context.MODE_PRIVATE);
        cafenameee=namee.getString("name","datanotfound");
        SharedPreferences child = getSharedPreferences("childname", Context.MODE_PRIVATE);
        username=child.getString("username","data not found");
        seatid=child.getString("seat",null);
        path=child.getString("path",null);
       filllist(cafenameee,path);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar rightNow = Calendar.getInstance();
                int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
                int minutes = rightNow.get(Calendar.MINUTE);
                String endtime=currentHourIn24Format+":"+minutes;
                myRef.child(cafenameee).child(path).child("endtime").setValue(endtime);
                myRef.child(cafenameee).child(path).child("deactivate").setValue("1");
              FirebaseDatabase  data = FirebaseDatabase.getInstance();
                DatabaseReference   myRef2 = database.getReference("Seat");
                myRef2.child(cafenameee).child(seatid).removeValue();
                Toast.makeText(getApplicationContext(),"Order completed",Toast.LENGTH_LONG).show();

            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase  data = FirebaseDatabase.getInstance();
                DatabaseReference   myRef2 = database.getReference("Seat");
                myRef2.child(cafenameee).child(seatid).removeValue();

                DatabaseReference   myRef22 = database.getReference("Order");
                myRef22.child(cafenameee).child(path).removeValue();
                Toast.makeText(getApplicationContext(),"Order Canceled",Toast.LENGTH_LONG).show();

            }
        });

    }
    public void filllist(final String namee , final String path ){
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference("Order");

        myRef.child(namee).child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                total.setText(dataSnapshot.child("totalprice").getValue().toString());
               }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        myRef.child(namee).child(path).child("item").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                childcount= (int) dataSnapshot.getChildrenCount();
                name = new String[childcount];
                price = new String[childcount];
                count = new String[childcount];
                int i=0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                  String itemname=dataSnapshot1.child("name").getValue().toString();
                  name[i]=itemname;
                    String pricee=dataSnapshot1.child("price").getValue().toString();
                    price[i]=pricee;
                    String countt=dataSnapshot1.child("count").getValue().toString();
                    count[i]=countt;
                    i++;
                }
                ArrayList<allorderinfo> list= new ArrayList<allorderinfo>();
                for(int j=0 ;j<i;j++){
                    list.add(new allorderinfo(name[j],count[j],price[j]));
                }
                Listadapter adapter=new Listadapter(list);
                l1.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});



    }
    class Listadapter extends BaseAdapter {
        ArrayList<allorderinfo> listitem =new <allorderinfo>ArrayList();
        Listadapter(ArrayList <allorderinfo> listadap) {
            this.listitem=listadap;

        }
        @Override
        public int getCount() {
            return listitem.size();

        }

        @Override
        public Object getItem(int position) {
            return listitem.get(position).name;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layot = getLayoutInflater();
            View v1 =layot.inflate(R.layout.allorderinfo,null);
            TextView name=v1.findViewById(R.id.name);
            TextView price=v1.findViewById(R.id.price);
            TextView count=v1.findViewById(R.id.count);
            name.setText(listitem.get(position).name);
            price.setText(listitem.get(position).price);
            count.setText(listitem.get(position).count);
            return v1;
        }
    }


}
