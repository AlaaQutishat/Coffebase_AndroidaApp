package com.example.coffeeuser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListCoffee extends AppCompatActivity {
    ListView l1;
    int childcount = 0;
    int test;
    String[] cafename;
    String[] image;
    Button fillter, serachbutton;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ArrayList<Model> list;
    String path = "";
    String[] avgrate;
    EditText search;
    DecimalFormat precision = new DecimalFormat("0.0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_coffee);
        l1 = (ListView) findViewById(R.id.cafelist);
        fillter = (Button) findViewById(R.id.fillterbutton);
        test = 0;
        search = findViewById(R.id.serch);
        serachbutton = findViewById(R.id.serachbutton);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("Coffee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                childcount = (int) dataSnapshot.getChildrenCount();
                image = new String[childcount];
                cafename = new String[childcount];
                avgrate = new String[childcount];
                double ratingTotal = 0;
                double ratingSum = 0;
                String ratingAvg = "";


                int i = 0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    if( dataSnapshot1.child("Rate").getChildrenCount()!=0){
                        for (DataSnapshot child : dataSnapshot1.child("Rate").getChildren()) {
                            String ra = (child.getValue().toString());
                            ratingSum = ratingSum + Double.valueOf(ra);
                            ratingTotal= dataSnapshot1.child("Rate").getChildrenCount();

                        }

                        ratingAvg = precision.format (ratingSum / ratingTotal);
                    }
                    else {
                        ratingAvg= precision.format(0);
                    }
                    ratingSum = 0;

                    avgrate[i]=ratingAvg;
                    String cafeename = String.valueOf(dataSnapshot1.child("coffeename").getValue());
                    cafename[i] = cafeename;
                    String url = String.valueOf(dataSnapshot1.child("image").child("imageurl").getValue());
                    image[i] = url;
                    i++;

                }

                list= new ArrayList<Model>();
                for(int j=0 ;j<cafename.length;j++){
                    list.add(new Model(cafename[j],image[j],avgrate[j]));

                }
                Listadapter adapter = new Listadapter(list);
                l1.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fillter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showd();
            }
        });
        serachbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

    }

    private void search() {

        ArrayList<Model> listt = new ArrayList<Model>();
        for (int j = 0; j < list.size(); j++) {
            if (list.get(j).Name.contains(search.getText().toString()))
                listt.add(new Model(list.get(j).Name, list.get(j).Image,list.get(j).AvarageRate));
        }
        Listadapter adapter = new Listadapter(listt);
        l1.setAdapter(adapter);

    }

    public void btn_logout(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(ListCoffee.this);
        alert.setMessage("Are You Sure You Want to exit");
        alert.setCancelable(true);
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent list = new Intent(ListCoffee.this, UserLogIn.class);
                list.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                list.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(list);
            }
        });
        AlertDialog alertt = alert.create();
        alertt.show();

    }


    class Listadapter extends BaseAdapter {
        ArrayList<Model> listitem = new <Model>ArrayList();

        Listadapter(ArrayList<Model> listadap) {
            this.listitem = listadap;

        }

        @Override
        public int getCount() {
            return listitem.size();

        }


        @Override
        public Object getItem(int position) {
            return listitem.get(position).Name;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater layot = getLayoutInflater();
            View v1 = layot.inflate(R.layout.coffee, null);
            CircleImageView image = v1.findViewById(R.id.profile_image);
            Button star = v1.findViewById(R.id.star);

            TextView avg = v1.findViewById(R.id.show_avg);
            if(listitem.get(position).AvarageRate!="0"){
            avg.setText(listitem.get(position).AvarageRate);}
            else {
                avg.setText(null);
            }
            final TextView namecofe = v1.findViewById(R.id.nameCoffee);
            namecofe.setText(listitem.get(position).Name);
            String imagee = listitem.get(position).Image;
            if (imagee != null) {
                Uri imageUri = Uri.parse(imagee);
                Uri u1 = imageUri;
                Glide.with(getApplicationContext()).load(u1).circleCrop().into(image);
            }

            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    showRate(listitem.get(position).Name);
                }
            });
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    showRate(listitem.get(position).Name);
                }
            });
            namecofe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TextView namecofe = v.findViewById(R.id.nameCoffee);
                    final Calendar rightNow = Calendar.getInstance();
                    int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
                    int minutes = rightNow.get(Calendar.MINUTE);
                    String firsttime = currentHourIn24Format + ":" + minutes;
                    SharedPreferences time = getSharedPreferences("entertime", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edittime = time.edit();
                    edittime.putString("time", firsttime);
                    edittime.apply();
                    Intent move = new Intent(getApplicationContext(), CoffeHome.class);
                  move.putExtra("cafename", namecofe.getText().toString());
                    startActivity(move);

                }
            });

            return v1;
        }


    }

    public void showd() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.activity_dialog, null);
        final AlertDialog alertD = new AlertDialog.Builder(this).create();
        Button bt2 = (Button) promptView.findViewById(R.id.okbutton);
        final CheckBox r1 = promptView.findViewById(R.id.smokecheck);
        final CheckBox r3 = promptView.findViewById(R.id.studycheck);
        final CheckBox r4 = promptView.findViewById(R.id.ratecheck);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (r1.isChecked() == true && r3.isChecked() == false && r4.isChecked() == false) {
                    nolsmoke();
                    alertD.cancel();
                } else if (r1.isChecked() == false && r3.isChecked() == true && r4.isChecked() == false) {
                    study();
                    alertD.cancel();
                } else if (r1.isChecked() == true && r3.isChecked() == true && r4.isChecked() == false) {
                    studyAndSmoke();
                    alertD.cancel();
                }
                else if (r1.isChecked() == false && r3.isChecked() == false && r4.isChecked() == true) {
                   sortrate();
                    alertD.cancel();
                }
                else if (r1.isChecked() == true && r3.isChecked() == false && r4.isChecked() == true) {
                    smokesort();
                    alertD.cancel();
                }
                else if (r1.isChecked() == false && r3.isChecked() == true && r4.isChecked() == true) {
                    studysort();
                    alertD.cancel();
                }
                else if (r1.isChecked() == true && r3.isChecked() == true && r4.isChecked() == true) {
                    allsort();
                    alertD.cancel();
                }

            }
        });
        alertD.setView(promptView);
        alertD.show();
        alertD.getWindow().setLayout(800, 800);


    }

    private void allsort() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("Coffee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                childcount= (int) dataSnapshot.getChildrenCount();
                image = new String[childcount];
                avgrate = new String[childcount];
                cafename = new String[childcount];
                int i=0;
                double ratingTotal = 0;
                double ratingSum = 0;
                String ratingAvg = "";

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if( dataSnapshot1.child("Rate").getChildrenCount()!=0){
                        for (DataSnapshot child : dataSnapshot1.child("Rate").getChildren()) {
                            String ra = (child.getValue().toString());
                            ratingSum = ratingSum + Double.valueOf(ra);
                            ratingTotal= dataSnapshot1.child("Rate").getChildrenCount();

                        }

                        ratingAvg = precision.format (ratingSum / ratingTotal);
                    }
                    else {
                        ratingAvg= precision.format(0);
                    }
                    ratingSum = 0;

                    avgrate[i]=ratingAvg;
                    if (dataSnapshot1.child("Aboutus").child("smoke").getValue().toString().equalsIgnoreCase("yes")&& dataSnapshot1.child("Aboutus").child("study").getValue().toString().equalsIgnoreCase("yes")){
                        String cafeename = String.valueOf(dataSnapshot1.child("coffeename").getValue());
                        cafename[i]=cafeename;
                        String url = String.valueOf(dataSnapshot1.child("image").child("imageurl").getValue());
                        image[i]=url;
                        i++;
                    }

                }

                list= new ArrayList<Model>();
                for(int j=0 ;j<i;j++){
                    list.add(new Model(cafename[j],image[j],avgrate[j]));

                }
                Collections.sort(list);
                Collections.reverse(list);
                Listadapter   adapter=new Listadapter(list);
                l1.setAdapter(adapter);}





            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }   });
    }

    private void studysort() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("Coffee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                childcount= (int) dataSnapshot.getChildrenCount();
                image = new String[childcount];
                cafename = new String[childcount];
                avgrate = new String[childcount];
                int i=0;
                double ratingTotal = 0;
                double ratingSum = 0;
                String ratingAvg = "";

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if( dataSnapshot1.child("Rate").getChildrenCount()!=0){
                        for (DataSnapshot child : dataSnapshot1.child("Rate").getChildren()) {
                            String ra = (child.getValue().toString());
                            ratingSum = ratingSum + Double.valueOf(ra);
                            ratingTotal= dataSnapshot1.child("Rate").getChildrenCount();

                        }
                        ratingAvg = precision.format (ratingSum / ratingTotal);
                    }
                    else {
                        ratingAvg= precision.format(0);
                    }

                    ratingSum = 0;
                    ratingTotal=0;

                    avgrate[i]=ratingAvg;

                    if (dataSnapshot1.child("Aboutus").child("study").getValue().toString().equalsIgnoreCase("yes")){
                        String cafeename = String.valueOf(dataSnapshot1.child("coffeename").getValue());
                        cafename[i]=cafeename;
                        String url = String.valueOf(dataSnapshot1.child("image").child("imageurl").getValue());
                        image[i]=url;
                        i++;
                    }

                }
                if(i!=0){
                    ArrayList<Model> list= new ArrayList<Model>();
                    for(int j=0 ;j<i;j++){
                        list.add(new Model(cafename[j],image[j],avgrate[j]));

                    }
                    Collections.sort(list);
                    Collections.reverse(list);
                    Listadapter   adapter=new Listadapter(list);
                    l1.setAdapter(adapter);}

            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }   });
    }

    private void smokesort() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("Coffee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                childcount= (int) dataSnapshot.getChildrenCount();
                image = new String[childcount];
                cafename = new String[childcount];
                avgrate = new String[childcount];
                int i=0;
                double ratingTotal = 0;
                double ratingSum = 0;
                String ratingAvg = "";

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){

                    if( dataSnapshot1.child("Rate").getChildrenCount()!=0){
                        for (DataSnapshot child : dataSnapshot1.child("Rate").getChildren()) {
                            String ra = (child.getValue().toString());
                            ratingSum = ratingSum + Double.valueOf(ra);
                            ratingTotal= dataSnapshot1.child("Rate").getChildrenCount();

                        }
                        ratingAvg = precision.format (ratingSum / ratingTotal);
                    }
                    else {
                        ratingAvg= precision.format(0);
                    }

                    ratingSum = 0;
                    ratingTotal=0;

                    avgrate[i]=ratingAvg;

                    if (dataSnapshot1.child("Aboutus").child("smoke").getValue().toString().equalsIgnoreCase("yes") ){
                        String cafeename = String.valueOf(dataSnapshot1.child("coffeename").getValue());
                        cafename[i]=cafeename;
                        String url = String.valueOf(dataSnapshot1.child("image").child("imageurl").getValue());
                        image[i]=url;
                        i++;
                    }

                }
                if(i!=0){
                    ArrayList<Model> list= new ArrayList<Model>();
                    for(int j=0 ;j<i;j++){
                        list.add(new Model(cafename[j],image[j],avgrate[j]));

                    }
                    Collections.sort(list);
                    Collections.reverse(list);
                    Listadapter   adapter=new Listadapter(list);
                    l1.setAdapter(adapter);}
            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }   });

    }

    private void sortrate() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("Coffee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                childcount= (int) dataSnapshot.getChildrenCount();
                image = new String[childcount];
                avgrate = new String[childcount];
                cafename = new String[childcount];
                int i=0;
                double ratingTotal = 0;
                double ratingSum = 0;
                String ratingAvg = "";

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if( dataSnapshot1.child("Rate").getChildrenCount()!=0){
                        for (DataSnapshot child : dataSnapshot1.child("Rate").getChildren()) {
                            String ra = (child.getValue().toString());
                            ratingSum = ratingSum + Double.valueOf(ra);
                            ratingTotal= dataSnapshot1.child("Rate").getChildrenCount();

                        }

                        ratingAvg = precision.format (ratingSum / ratingTotal);
                    }
                    else {
                        ratingAvg= precision.format(0);
                    }
                    ratingSum = 0;
                    avgrate[i]=ratingAvg;
                        String cafeename = String.valueOf(dataSnapshot1.child("coffeename").getValue());
                        cafename[i]=cafeename;
                        String url = String.valueOf(dataSnapshot1.child("image").child("imageurl").getValue());
                        image[i]=url;
                        i++;


                }

                list= new ArrayList<Model>();
                for(int j=0 ;j<i;j++){
                    list.add(new Model(cafename[j],image[j],avgrate[j]));

                }
                Collections.sort(list);
                Collections.reverse(list);
                Listadapter   adapter=new Listadapter(list);
                l1.setAdapter(adapter);}





            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }   });

    }

    public void showRate(final String name) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.show_rate, null);
        final AlertDialog alertD = new AlertDialog.Builder(this).create();
        final RatingBar bar = (RatingBar) promptView.findViewById(R.id.ratingBar_coffe);
        final TextView showRate = (TextView) promptView.findViewById(R.id.show_rate);
        path = "";
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("Coffee").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.child("coffeename").getValue().toString().equalsIgnoreCase(name)) {

                        path = dataSnapshot1.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Coffee");
                myRef.child(path).child("Rate").child(userid).setValue(rating);
            }
        });
        alertD.setView(promptView);
        alertD.show();


    }


    private void studyAndSmoke() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("Coffee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                childcount= (int) dataSnapshot.getChildrenCount();
                image = new String[childcount];
                avgrate = new String[childcount];
                cafename = new String[childcount];
                int i=0;
                double ratingTotal = 0;
                double ratingSum = 0;
                String ratingAvg = "";

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if( dataSnapshot1.child("Rate").getChildrenCount()!=0){
                        for (DataSnapshot child : dataSnapshot1.child("Rate").getChildren()) {
                            String ra = (child.getValue().toString());
                            ratingSum = ratingSum + Double.valueOf(ra);
                            ratingTotal= dataSnapshot1.child("Rate").getChildrenCount();

                        }

                        ratingAvg = precision.format (ratingSum / ratingTotal);
                    }
                    else {
                        ratingAvg= precision.format(0);
                    }
                    ratingSum = 0;

                    avgrate[i]=ratingAvg;
                    if (dataSnapshot1.child("Aboutus").child("smoke").getValue().toString().equalsIgnoreCase("yes")&& dataSnapshot1.child("Aboutus").child("study").getValue().toString().equalsIgnoreCase("yes")){
                        String cafeename = String.valueOf(dataSnapshot1.child("coffeename").getValue());
                        cafename[i]=cafeename;
                        String url = String.valueOf(dataSnapshot1.child("image").child("imageurl").getValue());
                        image[i]=url;
                        i++;
                    }

                }

                list= new ArrayList<Model>();
                for(int j=0 ;j<i;j++){
                    list.add(new Model(cafename[j],image[j],avgrate[j]));

                }
                    Listadapter   adapter=new Listadapter(list);
                    l1.setAdapter(adapter);}





            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }   });

    }

    public void nolsmoke() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("Coffee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                childcount= (int) dataSnapshot.getChildrenCount();
                image = new String[childcount];
                cafename = new String[childcount];
                avgrate = new String[childcount];
                int i=0;
                double ratingTotal = 0;
                double ratingSum = 0;
                String ratingAvg = "";

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){

                    if( dataSnapshot1.child("Rate").getChildrenCount()!=0){
                        for (DataSnapshot child : dataSnapshot1.child("Rate").getChildren()) {
                            String ra = (child.getValue().toString());
                            ratingSum = ratingSum + Double.valueOf(ra);
                            ratingTotal= dataSnapshot1.child("Rate").getChildrenCount();

                        }
                        ratingAvg = precision.format (ratingSum / ratingTotal);
                    }
                    else {
                        ratingAvg= precision.format(0);
                    }

                    ratingSum = 0;
                    ratingTotal=0;

                    avgrate[i]=ratingAvg;

                    if (dataSnapshot1.child("Aboutus").child("smoke").getValue().toString().equalsIgnoreCase("yes") ){
                        String cafeename = String.valueOf(dataSnapshot1.child("coffeename").getValue());
                        cafename[i]=cafeename;
                        String url = String.valueOf(dataSnapshot1.child("image").child("imageurl").getValue());
                        image[i]=url;
                        i++;
                    }

                }
                if(i!=0){
                    ArrayList<Model> list= new ArrayList<Model>();
                    for(int j=0 ;j<i;j++){
                        list.add(new Model(cafename[j],image[j],avgrate[j]));

                    }
                    Listadapter   adapter=new Listadapter(list);
                    l1.setAdapter(adapter);}
            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }   });

    }

    public void study() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("Coffee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                childcount= (int) dataSnapshot.getChildrenCount();
                image = new String[childcount];
                cafename = new String[childcount];
                avgrate = new String[childcount];
                int i=0;
                double ratingTotal = 0;
                double ratingSum = 0;
                String ratingAvg = "";

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if( dataSnapshot1.child("Rate").getChildrenCount()!=0){
                        for (DataSnapshot child : dataSnapshot1.child("Rate").getChildren()) {
                            String ra = (child.getValue().toString());
                            ratingSum = ratingSum + Double.valueOf(ra);
                            ratingTotal= dataSnapshot1.child("Rate").getChildrenCount();

                        }
                        ratingAvg = precision.format (ratingSum / ratingTotal);
                    }
                    else {
                        ratingAvg= precision.format(0);
                    }

                    ratingSum = 0;
                    ratingTotal=0;

                    avgrate[i]=ratingAvg;

                    if (dataSnapshot1.child("Aboutus").child("study").getValue().toString().equalsIgnoreCase("yes")){
                        String cafeename = String.valueOf(dataSnapshot1.child("coffeename").getValue());
                        cafename[i]=cafeename;
                        String url = String.valueOf(dataSnapshot1.child("image").child("imageurl").getValue());
                        image[i]=url;
                        i++;
                    }

                }
                if(i!=0){
                    ArrayList<Model> list= new ArrayList<Model>();
                    for(int j=0 ;j<i;j++){
                        list.add(new Model(cafename[j],image[j],avgrate[j]));

                    }
                    Listadapter   adapter=new Listadapter(list);
                    l1.setAdapter(adapter);}

            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }   });

    }

}
