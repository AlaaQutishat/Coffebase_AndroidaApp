package com.example.coffebasemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity implements View.OnClickListener {
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
EditText emaill,passwordd,coffeenamee;
    private FirebaseAuth mAuth;
    String latitude;
    String longitude;
Button pinlocation,signup;
    ImageView showpass;
    TextView tx1;
    int l=0;
    private static  final int REQUEST_LOCATION=1;
ProgressBar progress;
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        ActivityCompat.requestPermissions(this,new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        mAuth = FirebaseAuth.getInstance();
 emaill=(EditText)findViewById(R.id.email);
        passwordd=(EditText)findViewById(R.id.password);
        coffeenamee=(EditText)findViewById(R.id.coffeename);
        pinlocation=(Button)findViewById(R.id.pinlocation);
        showpass=(ImageView)findViewById(R.id.showpass);
        showpass.setOnClickListener(this);
        progress=(ProgressBar)findViewById(R.id.progress);
        pinlocation =(Button)findViewById(R.id.pinlocation);
        pinlocation.setOnClickListener(this);
        signup=findViewById(R.id.signup);
        signup.setOnClickListener(this);
       tx1=(TextView)findViewById(R.id.login);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
       tx1.setOnClickListener(this);
        progress.setVisibility(View.INVISIBLE);
    }

 public void creatuser(){

      final String email =emaill.getText().toString().trim();
     final String coffeename = coffeenamee.getText().toString().trim();
     final String password = passwordd.getText().toString().trim();


     if (email.isEmpty()) {
         emaill.setError("Email required");
         emaill.requestFocus();
         return;
     }
     else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
         emaill.setError("please enter Valid Email");
         emaill.requestFocus();
         return;
     }
     else  if (password.isEmpty()) {
         passwordd.setError("password required");
         passwordd.requestFocus();
         return;
     }

     else if (password.length() < 6) {
         passwordd.setError("password should be at least 6 characters long");
         passwordd.requestFocus();
         return;
     }


     else if (coffeename.isEmpty()) {
         coffeenamee.setError("Coffeename required");
         coffeenamee.requestFocus();
         return;
     }

     else if (l==0 )  {
         Toast.makeText(getApplicationContext(),"Click in pin your location ",Toast.LENGTH_LONG).show();
pinlocation.requestFocus();
return;
     }


     progress.setVisibility(View.VISIBLE);






     mAuth.createUserWithEmailAndPassword(email, password)
             .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                     progress.setVisibility(View.INVISIBLE);
                     if (task.isSuccessful()) {
                         mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                 if(task.isSuccessful()){
                                 Toast.makeText(getApplicationContext(),"please check email to verification",Toast.LENGTH_LONG).show();
                             }
                                  else {
                                         Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();

                                     }
                                 }});
                         coffee user = new coffee(
                                 email,coffeename,latitude,longitude
                         );

                         FirebaseDatabase.getInstance().getReference("Coffee")
                                 .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                 .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {

                                 if (task.isSuccessful()) {
                                     FirebaseAuth.getInstance().signOut();
                                     Intent move = new Intent(getApplicationContext(), MainActivity.class);
                                     startActivity(move);

                                 }
                                     else {
                                         Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                     }
                                 } }
                         );

             } else {
                         if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                             Toast.makeText(getApplicationContext(), "You are already register", Toast.LENGTH_LONG).show();

                         }
                         else {
                             Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                         }}}});}




    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();

                                    latitude=location.getLatitude()+"";
                                    longitude=location.getLongitude()+"";
                                    Toast.makeText(getApplicationContext(),"Your Location Has Been Pinned",Toast.LENGTH_LONG).show();

                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    public void pinlocationn(){
        getLastLocation();
        l=1;
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login :
                Intent login =new Intent(getApplicationContext(),MainActivity.class);
                startActivity(login);
                break;
            case R.id.signup :
                creatuser();
                break;
            case R.id.pinlocation :
                pinlocationn();
                break;
            case R.id.showpass:
                if(passwordd.getTransformationMethod()== HideReturnsTransformationMethod.getInstance())
                    passwordd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                else
                    passwordd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                break;
        }
    }
}
