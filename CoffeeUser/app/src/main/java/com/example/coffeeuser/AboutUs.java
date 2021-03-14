package com.example.coffeeuser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutUs extends AppCompatActivity {
 String latitude ,longitude ;
 TextView phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        final CircleImageView profile_image = findViewById(R.id.profile_image);
        final TextView caffeename = findViewById(R.id.caffeename);
       phone =findViewById(R.id.phone);
        final TextView smoke =findViewById(R.id.textView4);
        final TextView study =findViewById(R.id.textView);
        final TextView parking =findViewById(R.id.textView6);
        final TextView from =findViewById(R.id.textView8);
        final TextView to =findViewById(R.id.textView10);
        TextView location = findViewById(R.id.location);
        final String  name= getIntent().getStringExtra("CAFE");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("Coffee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    if (dataSnapshot1.child("coffeename").getValue().toString().equalsIgnoreCase(name)) {
                   caffeename.setText(dataSnapshot1.child("coffeename").getValue().toString());
                   phone.setText( dataSnapshot1.child("Aboutus").child("phonee").getValue().toString());
                   smoke.setText(dataSnapshot1.child("Aboutus").child("smoke").getValue().toString());
                   study.setText(dataSnapshot1.child("Aboutus").child("study").getValue().toString());
                   parking.setText(dataSnapshot1.child("Aboutus").child("parking").getValue().toString());
                   String uri=dataSnapshot1.child("image").child("imageurl").getValue().toString();
                  Uri ur=Uri.parse(uri);
                        Glide.with(getApplicationContext()).load(ur).circleCrop().into(profile_image);
                        from.setText(dataSnapshot1.child("Aboutus").child("fromm").getValue().toString()+" "+
                                dataSnapshot1.child("Aboutus").child("fromtime").getValue().toString());
                        to.setText(dataSnapshot1.child("Aboutus").child("too").getValue().toString()+" "+
                                dataSnapshot1.child("Aboutus").child("totime").getValue().toString());
                        latitude =dataSnapshot1.child("latitude").getValue().toString();
                        longitude =dataSnapshot1.child("longitude").getValue().toString();
                    }

                }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});
   location.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        showlocation(v);
    }
    });
   phone.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           call(v);
       }
   });
    }
    public void showlocation(View view) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+latitude+","+longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
        }
    public void call(View view) {
        Intent call = new Intent(Intent.ACTION_DIAL);
        call.setData(Uri.parse("tel:"+phone.getText()));
        if(call.resolveActivity(getPackageManager())!=null){
            startActivity(call);        }
    }
}
