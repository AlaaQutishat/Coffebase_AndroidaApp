package com.example.coffebasemanager.ui.gallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.coffebasemanager.R;
import com.example.coffebasemanager.edititem;
import com.example.coffebasemanager.menulistitem;
import com.example.coffebasemanager.ui.home.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    String[] pricee;
    String[] itemname;
    int childcount = 0;
    int Deactivate[];
    FirebaseDatabase database;
    DatabaseReference myRef;
    String cafename;
    ListView l1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        l1 = root.findViewById(R.id.menulist);
        TextView cafenamee = root.findViewById(R.id.coffeename);
        SharedPreferences result = getActivity().getSharedPreferences("cafename", Context.MODE_PRIVATE);
        cafename = result.getString("name", "datanotfound");
        cafenamee.setText(cafename);
        filllist();
        return root;
    }

    public void filllist() {
        SharedPreferences result = getActivity().getSharedPreferences("cafename", Context.MODE_PRIVATE);
        cafename = result.getString("name", "datanotfound");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        myRef.child("Menu").child(cafename).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                childcount = (int) dataSnapshot.getChildrenCount();
                pricee = new String[childcount];
                itemname = new String[childcount];
                Deactivate = new int[childcount];
                int i = 0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String value1 = String.valueOf(dataSnapshot1.child("itemname").getValue());
                    itemname[i] = value1;
                    String value2 = String.valueOf(dataSnapshot1.child("price").getValue());
                    pricee[i] = value2;
                    int value3 = Integer.parseInt(dataSnapshot1.child("deactivate").getValue().toString());
                    Deactivate[i] = value3;
                    i++;
                }
                ArrayList<edititem> list = new ArrayList<edititem>();
                for (int j = 0; j < pricee.length; j++) {
                    list.add(new edititem(pricee[j], itemname[j], Deactivate[j]));

                }
                Listadapter adapter = new Listadapter(list);
                l1.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    class Listadapter extends BaseAdapter {
        ArrayList<edititem> listitem = new <edititem>ArrayList();

        Listadapter(ArrayList<edititem> listadap) {
            this.listitem = listadap;

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater layot = getLayoutInflater();
            View v1 = layot.inflate(R.layout.editmenu, null);
            final TextView nameitem = v1.findViewById(R.id.name);
            final EditText price = v1.findViewById(R.id.price);
            nameitem.setText(listitem.get(position).itemname);
            price.setText(listitem.get(position).price);
            final CheckBox check = v1.findViewById(R.id.check1);
            if (listitem.get(position).Deactivate == 1) {
                check.setChecked(true);
            } else if (listitem.get(position).Deactivate == 0) {
                check.setChecked(false);
            }
            Button edit = v1.findViewById(R.id.edititem);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String updateprice = price.getText().toString();
                    int Deactivate;
                    if (check.isChecked()) {
                        Deactivate = 1;
                    } else {
                        Deactivate = 0;
                    }
                    String item = nameitem.getText().toString();
                    myRef.child("Menu").child(cafename).child(item).child("price").setValue(updateprice);
                    myRef.child("Menu").child(cafename).child(item).child("deactivate").setValue(Deactivate);
                }
            });
            return v1;
        }
    }

}