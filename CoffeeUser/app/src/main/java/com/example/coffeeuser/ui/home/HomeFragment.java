package com.example.coffeeuser.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.coffeeuser.R;
import com.example.coffeeuser.menuitemlist;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    String [] pricee;int []Caloriess;String [] itemname;
    String[] image; ListView l1; int childcount=0;  SharedPreferences share;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        l1=(ListView)root.findViewById(R.id.list1);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        SharedPreferences result = getActivity().getSharedPreferences("cafename", Context.MODE_PRIVATE);
        String cafename=result.getString("name","datanotfound");
        myRef.child("Menu").child(cafename).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    childcount= (int) dataSnapshot.getChildrenCount();

                }
                pricee=new String[childcount];
                Caloriess=new int[childcount];
                image=new String[childcount];
                itemname=new String[childcount];
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});
        share = getActivity().getSharedPreferences("childcount", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = share.edit();
        edit.putInt("childcount",childcount);
        edit.apply();
        myRef.child("Menu").child(cafename).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if(dataSnapshot1.child("deactivate").getValue().toString().equals("0")){
                        String value = (String) dataSnapshot1.child("calories").getValue();
                        Caloriess[i]=Integer.parseInt(value);
                        String value2 = String.valueOf(dataSnapshot1.child("itemname").getValue());
                        itemname[i]=value2;
                        String value3 = String.valueOf(dataSnapshot1.child("price").getValue());
                        pricee[i]=value3;
                        String url = String.valueOf(dataSnapshot1.child("image").child("imageurl").getValue());
                        image[i]=url;
                        i++;
                    }}
                ArrayList<menuitemlist> list= new ArrayList<menuitemlist>();
                for(int j=0 ;j<pricee.length;j++){
                    list.add(new menuitemlist(pricee[j],itemname[j],image[j],Caloriess[j]));

                }
                Listadapter adapter=new Listadapter(list);
                l1.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return root;
    }
    class Listadapter extends BaseAdapter {
        ArrayList <menuitemlist>listitem =new <menuitemlist>ArrayList();
        Listadapter(ArrayList <menuitemlist> listadap) {
            this.listitem=listadap;

        }
        @Override
        public int getCount() {
            return listitem.size();

        }

        @Override
        public Object getItem(int position) {
            return listitem.get(position).itemname;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layot = getLayoutInflater();
            View v1 =layot.inflate(R.layout.menu_components,null);
            CircleImageView image=v1.findViewById(R.id.profile_image);
            TextView nameitem=v1.findViewById(R.id.type);
            TextView price=v1.findViewById(R.id.price);
            TextView Calories=v1.findViewById(R.id.Calories);
            nameitem.setText(listitem.get(position).itemname);
            Calories.setText(listitem.get(position).calories+"");
            price.setText(listitem.get(position).price);
                String u1 = listitem.get(position).image;
            Glide.with(getContext()).load(u1).circleCrop().into(image);
            NumberPicker cal=v1.findViewById(R.id.cal);
            cal.setMinValue(1);
            cal.setMaxValue(20);
            cal.setWrapSelectorWheel(true);


            return v1;
        }
    }

    }
