package com.example.coffeeuser.ui.share;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.coffeeuser.Order;
import com.example.coffeeuser.R;
import com.example.coffeeuser.favourite;
import com.example.coffeeuser.menuitemlist;
import com.example.coffeeuser.orderlistinfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShareFragment extends Fragment implements Serializable {
    public static ArrayList<orderlistinfo> order_n;
    String[] pricee;
    int[] Caloriess;
    String[] itemname;
    String[] image;
    ListView l1;
    int childcount = 0;
    int i;
    private ShareViewModel shareViewModel;
    Button OrderButton;
    DecimalFormat precision = new DecimalFormat("0.0");
    final int[] x = new int[1];
    String[] avgrate;
    HashSet<String> favv = new HashSet<String>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel =
                ViewModelProviders.of(this).get(ShareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);
        order_n = new ArrayList<>();
        l1 = root.findViewById(R.id.list1);
        OrderButton = root.findViewById(R.id.Order);
        String activity= getActivity().getIntent().getStringExtra("activity");
        if (activity!=null && activity.equalsIgnoreCase("favourite")){
            final String cafename = getActivity().getIntent().getStringExtra("cafe");
            fav(cafename);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();
            myRef.child("Menu").child(cafename).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    childcount = (int) dataSnapshot.getChildrenCount();
                    pricee = new String[childcount];
                    Caloriess = new int[childcount];
                    image = new String[childcount];
                    itemname = new String[childcount];
                    avgrate = new String[childcount];
                    double ratingTotal = 0;
                    double ratingSum = 0;
                    String ratingAvg = "";
                    i = 0;

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        if (dataSnapshot1.child("deactivate").getValue().toString().equals("0")) {
                            if (dataSnapshot1.child("category").getValue().toString().equalsIgnoreCase("Main Course")) {
                                if (dataSnapshot1.child("Rate").getChildrenCount() != 0) {
                                    for (DataSnapshot child : dataSnapshot1.child("Rate").getChildren()) {
                                        String ra = (child.getValue().toString());
                                        ratingSum = ratingSum + Double.valueOf(ra);
                                        ratingTotal = dataSnapshot1.child("Rate").getChildrenCount();

                                    }
                                    ratingAvg = precision.format(ratingSum / ratingTotal);
                                } else {
                                    ratingAvg = precision.format(0.0);
                                }
                                ratingTotal = 0;
                                ratingSum = 0;
                                avgrate[i] = ratingAvg;
                                String value = (String) dataSnapshot1.child("calories").getValue();
                                Caloriess[i] = Integer.parseInt(value);
                                String value2 = String.valueOf(dataSnapshot1.child("itemname").getValue());
                                itemname[i] = value2;
                                String value3 = String.valueOf(dataSnapshot1.child("price").getValue());
                                pricee[i] = value3;
                                String url = String.valueOf(dataSnapshot1.child("image").child("imageurl").getValue());
                                image[i] = url;
                                i++;
                            }
                        }
                    }
                    ArrayList<menuitemlist> list = new ArrayList<menuitemlist>();
                    for (int j = 0; j < i; j++) {
                        if (favv.contains(itemname[j]))
                            list.add(new menuitemlist(pricee[j], itemname[j], image[j], Caloriess[j], avgrate[j], 1));
                        else
                            list.add(new menuitemlist(pricee[j], itemname[j], image[j], Caloriess[j], avgrate[j], 0));

                    }
                    Listadapter adapter = new Listadapter(list);
                    l1.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            order_n=favourite.listitem;
        }
       else  if (activity!=null && activity.equalsIgnoreCase("Order")){
            ArrayList<orderlistinfo> listitem = favourite.listitem;
            final String cafename = getActivity().getIntent().getStringExtra("cafename");
            fav(cafename);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();
            myRef.child("Menu").child(cafename).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    childcount = (int) dataSnapshot.getChildrenCount();
                    pricee = new String[childcount];
                    Caloriess = new int[childcount];
                    image = new String[childcount];
                    itemname = new String[childcount];
                    avgrate = new String[childcount];
                    double ratingTotal = 0;
                    double ratingSum = 0;
                    String ratingAvg = "";
                    i = 0;

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        if (dataSnapshot1.child("deactivate").getValue().toString().equals("0")) {
                            if (dataSnapshot1.child("category").getValue().toString().equalsIgnoreCase("Main Course")) {
                                if (dataSnapshot1.child("Rate").getChildrenCount() != 0) {
                                    for (DataSnapshot child : dataSnapshot1.child("Rate").getChildren()) {
                                        String ra = (child.getValue().toString());
                                        ratingSum = ratingSum + Double.valueOf(ra);
                                        ratingTotal = dataSnapshot1.child("Rate").getChildrenCount();

                                    }
                                    ratingAvg = precision.format(ratingSum / ratingTotal);
                                } else {
                                    ratingAvg = precision.format(0.0);
                                }
                                ratingTotal = 0;
                                ratingSum = 0;
                                avgrate[i] = ratingAvg;
                                String value = (String) dataSnapshot1.child("calories").getValue();
                                Caloriess[i] = Integer.parseInt(value);
                                String value2 = String.valueOf(dataSnapshot1.child("itemname").getValue());
                                itemname[i] = value2;
                                String value3 = String.valueOf(dataSnapshot1.child("price").getValue());
                                pricee[i] = value3;
                                String url = String.valueOf(dataSnapshot1.child("image").child("imageurl").getValue());
                                image[i] = url;
                                i++;
                            }
                        }
                    }
                    ArrayList<menuitemlist> list = new ArrayList<menuitemlist>();
                    for (int j = 0; j < i; j++) {
                        if (favv.contains(itemname[j]))
                            list.add(new menuitemlist(pricee[j], itemname[j], image[j], Caloriess[j], avgrate[j], 1));
                        else
                            list.add(new menuitemlist(pricee[j], itemname[j], image[j], Caloriess[j], avgrate[j], 0));

                    }
                    Listadapter adapter = new Listadapter(list);
                    l1.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            order_n=Order.mainorder_n;
        }
        else {

            SharedPreferences result = getActivity().getSharedPreferences("cafename", Context.MODE_PRIVATE);
            final String cafename = result.getString("name", "datanotfound");
            fav(cafename);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();
            myRef.child("Menu").child(cafename).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    childcount = (int) dataSnapshot.getChildrenCount();
                    pricee = new String[childcount];
                    Caloriess = new int[childcount];
                    image = new String[childcount];
                    itemname = new String[childcount];
                    avgrate = new String[childcount];
                    double ratingTotal = 0;
                    double ratingSum = 0;
                    String ratingAvg = "";
                    i = 0;

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        if (dataSnapshot1.child("deactivate").getValue().toString().equals("0")) {
                            if (dataSnapshot1.child("category").getValue().toString().equalsIgnoreCase("Main Course")) {
                                if (dataSnapshot1.child("Rate").getChildrenCount() != 0) {
                                    for (DataSnapshot child : dataSnapshot1.child("Rate").getChildren()) {
                                        String ra = (child.getValue().toString());
                                        ratingSum = ratingSum + Double.valueOf(ra);
                                        ratingTotal = dataSnapshot1.child("Rate").getChildrenCount();

                                    }
                                    ratingAvg = precision.format(ratingSum / ratingTotal);
                                } else {
                                    ratingAvg = precision.format(0.0);
                                }
                                ratingTotal = 0;
                                ratingSum = 0;
                                avgrate[i] = ratingAvg;
                                String value = (String) dataSnapshot1.child("calories").getValue();
                                Caloriess[i] = Integer.parseInt(value);
                                String value2 = String.valueOf(dataSnapshot1.child("itemname").getValue());
                                itemname[i] = value2;
                                String value3 = String.valueOf(dataSnapshot1.child("price").getValue());
                                pricee[i] = value3;
                                String url = String.valueOf(dataSnapshot1.child("image").child("imageurl").getValue());
                                image[i] = url;
                                i++;
                            }
                        }
                    }
                    ArrayList<menuitemlist> list = new ArrayList<menuitemlist>();
                    for (int j = 0; j < i; j++) {
                        if (favv.contains(itemname[j]))
                            list.add(new menuitemlist(pricee[j], itemname[j], image[j], Caloriess[j], avgrate[j], 1));
                        else
                            list.add(new menuitemlist(pricee[j], itemname[j], image[j], Caloriess[j], avgrate[j], 0));

                    }
                    Listadapter adapter = new Listadapter(list);
                    l1.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        OrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String cafename;
                String activity= getActivity().getIntent().getStringExtra("activity");
                if (activity!=null && activity.equalsIgnoreCase("Order")){

                    cafename=getActivity().getIntent().getStringExtra("cafename");
                }
                else {
                 cafename = getActivity().getIntent().getStringExtra("cafe");
                }
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                FirebaseAuth AUTH = FirebaseAuth.getInstance();
                myRef.child("Users").child(AUTH.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String c = dataSnapshot.child("point").getValue().toString();
                        SharedPreferences point = getActivity().getSharedPreferences("point", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = point.edit();
                        edit.putString("pointt", c);
                        edit.apply();
                        Intent move = new Intent(getContext(), Order.class);
                        move.putExtra("activity", "ShareFragment");
                        move.putExtra("name", cafename);
                        startActivity(move);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {


                    }
                });


            }
        });
        return root;
    }


    public void fav(String cafename) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        FirebaseAuth AUTH = FirebaseAuth.getInstance();
        myRef.child("favourite").child(AUTH.getCurrentUser().getUid()).child(cafename).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashSet<String> a = new      HashSet<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    a.add(dataSnapshot1.getKey().toString());


                }

                fill(a);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void fill(     HashSet<String> a) {
        favv = a;
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
            return listitem.get(position).itemname;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater layot = getLayoutInflater();
            View v1 = layot.inflate(R.layout.menu_components, null);
            CircleImageView image = v1.findViewById(R.id.profile_image);
            final TextView nameitem = v1.findViewById(R.id.type);
            final TextView price = v1.findViewById(R.id.price);
            TextView Calories = v1.findViewById(R.id.Calories);
            final TextView avgrate = v1.findViewById(R.id.avg_rate);
            nameitem.setText(listitem.get(position).itemname);
            Calories.setText(listitem.get(position).calories + "");
            price.setText(listitem.get(position).price);
            final Button fav = v1.findViewById(R.id.fav);

            avgrate.setText(listitem.get(position).rate);


            Button star = v1.findViewById(R.id.star);
            final String name = listitem.get(position).itemname;
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRate(name);
                }
            });
            if (listitem.get(position).x == 1) {

                fav.setBackgroundResource(R.drawable.fav);
            }

            String imagee = listitem.get(position).image;
            if (imagee != null) {
                Uri imageUri = Uri.parse(imagee);
                Uri u1 = imageUri;
                Glide.with(getContext()).load(u1).circleCrop().into(image);
            }
            final NumberPicker count = v1.findViewById(R.id.cal);
            count.setMinValue(1);
            count.setMaxValue(20);
            count.setWrapSelectorWheel(true);
            Button add = v1.findViewById(R.id.add);

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String namee = nameitem.getText().toString();
                    String priceee = price.getText().toString();
                    String countt = String.valueOf(count.getValue());
                    boolean flag = false;
                    int c = 0;
                    for (int i = 0; i < order_n.size(); i++) {
                        if (namee.equalsIgnoreCase(order_n.get(i).name)) {
                            flag = true;
                            c = i;
                        }
                    }
                    if (flag) {
                        Toast.makeText(getContext(), "Item Added", Toast.LENGTH_SHORT).show();
                        order_n.get(c).quintity += Integer.parseInt(countt);
                        countt = "" + order_n.get(c).quintity;
                    } else {
                        Toast.makeText(getContext(), "Item Added", Toast.LENGTH_SHORT).show();
                        order_n.add(new orderlistinfo(namee, priceee, Integer.parseInt(countt)));
                    }
                }
            });

            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fav.getBackground().getConstantState() == getResources().getDrawable(R.drawable.fav).getConstantState()) {
                        String namee = nameitem.getText().toString();
                        SharedPreferences result = getActivity().getSharedPreferences("cafename", Context.MODE_PRIVATE);
                        String cafename = result.getString("name", "datanotfound");
                        fav.setBackgroundResource(R.drawable.fav2);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference();
                        myRef.child("favourite").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(cafename).child(namee).removeValue();
                        Toast.makeText(getContext(), "Item Remove", Toast.LENGTH_SHORT).show();
                    } else {
                        fav.setBackgroundResource(R.drawable.fav);
                        String namee = nameitem.getText().toString();
                        SharedPreferences result = getActivity().getSharedPreferences("cafename", Context.MODE_PRIVATE);
                        String cafename = result.getString("name", "datanotfound");
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference();
                        myRef.child("favourite").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(cafename).child(namee).setValue(namee);
                        Toast.makeText(getContext(), "Item Added", Toast.LENGTH_SHORT).show();

                    }

                }
            });


            return v1;
        }
    }

    public void showRate(final String name) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.show_rate, null);
        final AlertDialog alertD = new AlertDialog.Builder(getActivity()).create();

        final RatingBar bar = (RatingBar) promptView.findViewById(R.id.ratingBar_coffe);
        final TextView showRate = (TextView) promptView.findViewById(R.id.show_rate);


        bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                SharedPreferences result = getActivity().getSharedPreferences("cafename", Context.MODE_PRIVATE);
                String cafename = result.getString("name", "datanotfound");
                myRef.child("Menu").child(cafename).child(name).child("Rate").child(userid).setValue(rating);
            }
        });
        alertD.setView(promptView);
        alertD.show();


    }
}