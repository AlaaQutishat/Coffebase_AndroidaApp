package com.example.coffeeuser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CoffeHome extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffe_home);
        findViewById(R.id.babout).setOnClickListener(this);
        findViewById(R.id.bmenu).setOnClickListener(this);
  findViewById(R.id.boffers).setOnClickListener(this);
        findViewById(R.id.favourite).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.babout:
                String  name= getIntent().getStringExtra("cafename");

                Intent move1 = new Intent(this,AboutUs.class);
                move1.putExtra("CAFE",name);
                startActivity(move1);
                break;
            case R.id.bmenu:
                String name2= getIntent().getStringExtra("cafename");
                Intent move2 = new Intent(this, Slidemenu.class);
                 move2.putExtra("cafe",name2);
                 startActivity(move2);
                break;
            case R.id.boffers:
                String name3= getIntent().getStringExtra("cafename");
                Intent move3 = new Intent(this, OffersView.class);
                move3.putExtra("cafee",name3);
                startActivity(move3);
                break;
            case R.id.favourite:
                sendlist();

                break;
        }
    }

    private void sendlist() {
        final ArrayList<String> itemm=new ArrayList<>();
        final String cafename=getIntent().getStringExtra("cafename");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();
        myRef.child("favourite").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(cafename).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    itemm.add(dataSnapshot1.getValue().toString());
                }
                Intent move4 = new Intent(getApplicationContext(), favourite.class);
                move4.putExtra("favlist",itemm);
                move4.putExtra("cafename",cafename);
                startActivity(move4);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name =dataSnapshot.child("name").getValue().toString();
                SharedPreferences username =getSharedPreferences("usernamee", Context.MODE_PRIVATE);
                SharedPreferences.Editor editee = username.edit();
                editee.putString("nameuser", name);
                editee.apply();
                    }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }
}