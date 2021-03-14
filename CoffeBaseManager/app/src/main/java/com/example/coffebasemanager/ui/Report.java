package com.example.coffebasemanager.ui;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coffebasemanager.OrderHistory;
import com.example.coffebasemanager.R;
import com.example.coffebasemanager.orderinfo;
import com.example.coffebasemanager.salesitem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class Report extends Fragment {
    String cafenameee = " ";
    String date;
    int childcount;
    ListView l1;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    String[] name, price;
    int count[];
    HashSet<String> item = new HashSet<String>();
    ArrayList<salesitem> list = new ArrayList<salesitem>();int i=0;
    double total=0;
    TextView totaltext;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.report_fragment, container, false);
        final Spinner salesdate=root.findViewById(R.id.salesdate);
        l1 = root.findViewById(R.id.l1);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Order");
        final SharedPreferences namee = getActivity().getSharedPreferences("cafename", Context.MODE_PRIVATE);
        cafenameee = namee.getString("name", "datanotfound");
        totaltext=root.findViewById(R.id.totalprice);

        salesdate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                list=new ArrayList<>();
                item=new HashSet<>();
                total=0;
                date = salesdate.getSelectedItem().toString();
                filllist(cafenameee, date);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getContext(), "Please Select from Date", Toast.LENGTH_SHORT).show();
            }
        });


        return root;
    }

    public void filllist(final String cafenameee, final String datechild) {
        myRef.child(cafenameee).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                            String x= dataSnapshot.getChildrenCount()+"";
                            i=Integer.parseInt(x);
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.child("deactivate").getValue().toString().equals("1") && dataSnapshot1.child("Date").getValue().toString().equals(datechild)) {

                        String totall = dataSnapshot1.child("totalprice").getValue().toString();
                        total+= Double.parseDouble(totall.substring(0, totall.length() - 1));
                        myRef.child(cafenameee).child(dataSnapshot1.getKey()).child("item").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                childcount = (int) dataSnapshot2.getChildrenCount();
                                name = new String[childcount];
                                price = new String[childcount];
                                count = new int[childcount];
                                for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                    String name = dataSnapshot3.child("name").getValue().toString();
                                    String price = dataSnapshot3.child("price").getValue().toString();
                                    int count = Integer.parseInt(dataSnapshot3.child("count").getValue().toString());
                                    int pprice = Integer.parseInt(price.substring(0, price.length() - 1));
                                    int index = 0;
                                    if (item.contains(name)) {

                                        for (int i = 0; i < list.size(); i++) {
                                            if (list.get(i).getName().equalsIgnoreCase(name)){
                                                index = i;
                                            break;}
                                        }

                                        list.get(index).setCount(list.get(index).getCount() + count);
                                        list.get(index).setTotalprice(list.get(index).getCount() * list.get(index).getPrice());
                                    } else {
                                        item.add(name);
                                        list.add(new salesitem(count, pprice, name,(count*pprice)));
                                    }
                                }
                                if(i==dataSnapshot.getChildrenCount()){
                                    fill();}
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void fill() {
        System.out.println("in");
        Listadapter adapter=new Listadapter(list);
        l1.setAdapter(adapter);
        totaltext.setText(total+" $");
    }

    class Listadapter extends BaseAdapter {
        ArrayList<salesitem> listitem = new <salesitem>ArrayList();

        Listadapter(ArrayList<salesitem> listadap) {
            this.listitem = listadap;

        }

        @Override
        public int getCount() {
            return listitem.size();

        }

        @Override
        public Object getItem(int position) {
            return listitem.get(position).getName();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layot = getLayoutInflater();
            View v1 = layot.inflate(R.layout.salesreport_item, null);
            TextView name = v1.findViewById(R.id.itemname);
            TextView price = v1.findViewById(R.id.itemprice);
            TextView count = v1.findViewById(R.id.count);
            TextView totalprice = v1.findViewById(R.id.allprice);
            name.setText(listitem.get(position).getName());
            price.setText(listitem.get(position).getPrice() + "");
            count.setText(listitem.get(position).getCount()+"");
            totalprice.setText(listitem.get(position).getTotalprice()+"");
            return v1;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        final ArrayList<String> Date = new ArrayList<String>();
        SharedPreferences namee = getActivity().getApplicationContext().getSharedPreferences("cafename", Context.MODE_PRIVATE);
        cafenameee = namee.getString("name", "datanotfound");
        myRef.child(cafenameee).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (Date.contains(dataSnapshot1.child("Date").getValue().toString()) == false) {
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

    public void fillspinner(ArrayList<String> datespiner) {
        Spinner spinnerdate = getActivity().findViewById(R.id.salesdate);
        String[] newdate = new String[datespiner.size()];
        for (int i = 0; i < datespiner.size(); i++) {
            newdate[i] = datespiner.get(i);
        }
        CustomAdapter customAdapter = new CustomAdapter(getContext(), newdate);
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
