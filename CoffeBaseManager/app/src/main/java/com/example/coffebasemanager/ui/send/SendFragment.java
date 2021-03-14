package com.example.coffebasemanager.ui.send;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.coffebasemanager.AllOrder;
import com.example.coffebasemanager.Allhistory;
import com.example.coffebasemanager.R;
import com.example.coffebasemanager.menulistitem;
import com.example.coffebasemanager.orderinfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class SendFragment extends Fragment {
    ListView l1;
    String[] name, time, seatid;
    String cafenameee = " ";
    int childcount = 0;
    String date;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private SendViewModel sendViewModel;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ArrayList<orderinfo> list;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModel =
                ViewModelProviders.of(this).get(SendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_send, container, false);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Order");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        final SharedPreferences namee = getActivity().getSharedPreferences("cafename", Context.MODE_PRIVATE);
        cafenameee = namee.getString("name", "datanotfound");
        l1 = (ListView) root.findViewById(R.id.Orderlist);
        filllist(cafenameee);
        l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final SharedPreferences namee = getActivity().getSharedPreferences("cafename", Context.MODE_PRIVATE);
                cafenameee = namee.getString("name", "datanotfound");
                final TextView username = (TextView) view.findViewById(R.id.customname);
                final TextView seatid = (TextView) view.findViewById(R.id.seatid);
                myRef.child(cafenameee).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (dataSnapshot1.child("username").getValue().equals(username.getText().toString()) && dataSnapshot1.child("seatnumber").getValue().equals(Long.parseLong(seatid.getText().toString())) && dataSnapshot1.child("deactivate").getValue().toString().equals("0")) {
                                SharedPreferences child = getActivity().getSharedPreferences("childname", Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = child.edit();
                                edit.putString("path", dataSnapshot1.getKey());
                                edit.putString("seat", seatid.getText().toString());
                                edit.putString("username", username.getText().toString());
                                edit.apply();
                                Intent move = new Intent(getContext(), AllOrder.class);
                                startActivity(move);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }
        });
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                System.out.println("onChildAdded:" + dataSnapshot.getKey());
//                String value = dataSnapshot.child("username").getValue().toString();
//                Long value2 = (Long) (dataSnapshot.child("seatnumber").getValue());
//                String value3 = dataSnapshot.child("OrderTime").getValue().toString();
//                list.add(new orderinfo(value, value2 + "", value3));
//                Listadapter adapter = new Listadapter(list);
//                l1.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
               // System.out.println("onChildAdded:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getContext(), "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        myRef.child(cafenameee).addChildEventListener(childEventListener);

        return root;
    }

    public void filllist(String cafename) {

        myRef.child(cafenameee).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                childcount = (int) dataSnapshot.getChildrenCount();
                name = new String[childcount];
                time = new String[childcount];
                seatid = new String[childcount];
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.child("deactivate").getValue().toString().equals("0")) {
                        String value = dataSnapshot1.child("username").getValue().toString();
                        name[i] = value;
                        Long value2 = (Long) (dataSnapshot1.child("seatnumber").getValue());
                        seatid[i] = "" + value2;
                        String value3 = dataSnapshot1.child("OrderTime").getValue().toString();
                        time[i] = value3;
                        i++;
                    }
                    list = new ArrayList<orderinfo>();
                    for (int j = 0; j < i; j++) {
                        list.add(new orderinfo(name[j], seatid[j], time[j]));

                    }

                    Listadapter adapter = new Listadapter(list);
                    l1.setAdapter(adapter);

                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    class Listadapter extends BaseAdapter {
        ArrayList<orderinfo> listitem = new <orderinfo>ArrayList();

        Listadapter(ArrayList<orderinfo> listadap) {
            this.listitem = listadap;

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
            View v1 = layot.inflate(R.layout.orderinfo, null);
            TextView customname = v1.findViewById(R.id.customname);
            TextView time = v1.findViewById(R.id.time);
            TextView seatid = v1.findViewById(R.id.seatid);
            customname.setText(listitem.get(position).name);
            time.setText(listitem.get(position).time + "");
            seatid.setText(listitem.get(position).seatid);
            return v1;
        }
    }

}