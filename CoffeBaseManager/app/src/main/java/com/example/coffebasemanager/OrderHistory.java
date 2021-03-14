package com.example.coffebasemanager;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderHistory extends Fragment{
    private FirebaseDatabase database ;
    private DatabaseReference myRef ;
    String[] name,time,seatid;
    ListView l1;
    String cafenameee=" " ;
    String date; int childcount;
    private OrderHistoryViewModel orderHistoryViewModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        orderHistoryViewModel =
               ViewModelProviders.of(this).get(OrderHistoryViewModel.class);
        final View root = inflater.inflate(R.layout.order_history_fragment, container, false);
        final Spinner spinnerdate=root.findViewById(R.id.spinnerdate);
        l1=root.findViewById(R.id.listview);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Order");
        final SharedPreferences namee = getActivity().getSharedPreferences("cafename", Context.MODE_PRIVATE);
        cafenameee=namee.getString("name","datanotfound");
        spinnerdate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                date=spinnerdate.getSelectedItem().toString();
                filllist(cafenameee,date);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getContext(),"Please Select from Date",Toast.LENGTH_SHORT).show();
            }
        });

        l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final TextView username=(TextView)view.findViewById(R.id.customname);
                final TextView seatid=(TextView)view.findViewById(R.id.seatid);
                myRef.child(cafenameee).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            if (dataSnapshot1.child("username").getValue().equals(username.getText().toString())&&dataSnapshot1.child("deactivate").getValue().equals("1")
                            &&dataSnapshot1.child("seatnumber").getValue().equals(Long.parseLong(seatid.getText().toString()))){
                               SharedPreferences child = getActivity().getSharedPreferences("childname", Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = child.edit();
                                edit.putString("childkey",dataSnapshot1.getKey());
                                edit.apply();
                                Intent move = new Intent(getContext(), Allhistory.class);
                                startActivity(move);
                            }
                        }}
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }
        });



        return root;
    }
    public  void filllist(String cafenameee , final String datechild){
        myRef.child(cafenameee).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                childcount= (int) dataSnapshot.getChildrenCount();
                name=new String[childcount];
                time=new String[childcount];
                seatid=new String[childcount];
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }});
        myRef.child(cafenameee).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if(dataSnapshot1.child("deactivate").getValue().toString().equals("1")&&dataSnapshot1.child("Date").getValue().toString().equals(datechild)){
                        String value =  dataSnapshot1.child("username").getValue().toString();
                        name[i]=value;
                        Long value2 = (Long)(dataSnapshot1.child("seatnumber").getValue());
                        seatid[i]=""+value2;
                        String value3 = dataSnapshot1.child("OrderTime").getValue().toString();
                        time[i]=value3;
                        i++;
                    }}
                ArrayList<orderinfo> list= new ArrayList<orderinfo>();
                for(int j=0 ;j<i;j++){
                    list.add(new orderinfo(name[j],seatid[j],time[j]));

                }

                Listadapter adapter=new Listadapter(list);
                l1.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    class Listadapter extends BaseAdapter {
        ArrayList<orderinfo> listitem =new <orderinfo>ArrayList();
        Listadapter(ArrayList <orderinfo> listadap) {
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
            View v1 =layot.inflate(R.layout.orderinfo,null);
            TextView customname=v1.findViewById(R.id.customname);
            TextView time=v1.findViewById(R.id.time);
            TextView seatid=v1.findViewById(R.id.seatid);
            customname.setText(listitem.get(position).name);
            time.setText(listitem.get(position).time+"");
            seatid.setText(listitem.get(position).seatid);
            return v1;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        final ArrayList<String> Date=new ArrayList<String>();
        SharedPreferences namee = getActivity().getApplicationContext().getSharedPreferences("cafename", Context.MODE_PRIVATE);
        cafenameee=namee.getString("name","datanotfound");
        myRef.child(cafenameee).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if(Date.contains(dataSnapshot1.child("Date").getValue().toString())==false){
                        Date.add(dataSnapshot1.child("Date").getValue().toString());
                    }
                }
                fillspinner(Date);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    public  void fillspinner(ArrayList<String> datespiner){
        Spinner spinnerdate=getActivity().findViewById(R.id.spinnerdate);
       String []newdate=new String[datespiner.size()];
       for(int i=0;i<datespiner.size();i++){
           newdate[i]=datespiner.get(i);
       }
        CustomAdapter customAdapter=new CustomAdapter(getContext(),newdate);
        spinnerdate.setAdapter(customAdapter);
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
