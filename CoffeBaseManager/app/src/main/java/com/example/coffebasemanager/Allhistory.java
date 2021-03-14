package com.example.coffebasemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Allhistory extends AppCompatActivity {
    ListView l1;
    private FirebaseDatabase database ;
    private DatabaseReference myRef ;
    String cafenameee=" " ;
    String child ;
    int childcount=0;
    String [] name,price,count;
    TextView total,seatid,entertime,leavetime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allhistory);
        total=(TextView)findViewById(R.id.total);
        seatid=(TextView)findViewById(R.id.seatid);
        entertime=(TextView)findViewById(R.id.time);
        leavetime=(TextView)findViewById(R.id.leavetime);

        getvariable();

    }
    public void getvariable(){
        SharedPreferences namee = getApplicationContext().getSharedPreferences("cafename", Context.MODE_PRIVATE);
        cafenameee=namee.getString("name","datanotfound");
        l1=(ListView)findViewById(R.id.orderlist);
        SharedPreferences childd = getSharedPreferences("childname", Context.MODE_PRIVATE);
        child=childd.getString("childkey","data not found");
        filllist(cafenameee,child);
    }
    public void filllist(final String namee, final String child ){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Order");
        myRef.child(namee).child(child).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                total.setText(dataSnapshot.child("totalprice").getValue().toString());
            entertime.setText(dataSnapshot.child("entertime").getValue().toString());
                leavetime.setText(dataSnapshot.child("endtime").getValue().toString());
                seatid.setText(dataSnapshot.child("seatnumber").getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        myRef.child(namee).child(child).child("item").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                childcount= (int) dataSnapshot.getChildrenCount();
                name=new String[childcount];
                price=new String[childcount];
                count=new String[childcount];
                int i=0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String value =  dataSnapshot1.child("name").getValue().toString();
                    name[i]=value;
                    String value2 = dataSnapshot1.child("price").getValue().toString();
                    price[i]=value2;
                    String value3 = dataSnapshot1.child("count").getValue().toString();
                    count[i]=value3;
                    i++;
                }
                ArrayList<allorderinfo> list= new ArrayList<allorderinfo>();
                for(int j=0 ;j<i;j++){
                    list.add(new allorderinfo(name[j],count[j],price[j]));
                }
               Listadapter adapter=new Listadapter(list);
                l1.setAdapter(adapter);}
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

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
