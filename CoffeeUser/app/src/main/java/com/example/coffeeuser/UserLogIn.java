package com.example.coffeeuser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserLogIn extends AppCompatActivity implements View.OnClickListener{
    FirebaseAuth auth;

    EditText  editTextEmail, editTextPassword;
    ProgressBar progressBar;
    Intent listcoffee;
    TextView forget;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_log_in);
        auth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.EmailSignin);
        editTextPassword = findViewById(R.id.passwordSignin);
        listcoffee = new Intent(UserLogIn.this,ListCoffee.class);
        progressBar =  findViewById(R.id.progressBar2) ;
        progressBar.setVisibility(View.GONE);
        forget=findViewById(R.id.textView3);
        forget.setOnClickListener(this);
        findViewById(R.id.showpass).setOnClickListener(this);
        findViewById(R.id.startslogin).setOnClickListener(this);
        findViewById(R.id.startsignup).setOnClickListener(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                userlogin();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();

        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {

        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }


    private void userlogin() {

        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();



        if (email.isEmpty()) {
            editTextEmail.setError(getString(R.string.input_error_email));
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getString(R.string.input_error_email_invalid));
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError(getString(R.string.input_error_password));
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError(getString(R.string.input_error_password_length));
            editTextPassword.requestFocus();
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();




        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    if (auth.getCurrentUser().isEmailVerified()) {
                        SharedPreferences email =getSharedPreferences("useremail", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = email.edit();
                        edit.putString("useremail",editTextEmail.getText().toString());
                        edit.apply();
                    startActivity(listcoffee);}
                    else {
                        Toast.makeText(getApplicationContext(), "please Verify your email address", Toast.LENGTH_LONG).show();

                    }


                }
                else {
                    Toast.makeText(UserLogIn.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startslogin:
                userlogin();
                break;
            case R.id.startsignup:
                startActivity(new Intent(this, SignUp.class));
                break;
            case R.id.showpass:
                if(editTextPassword.getTransformationMethod()==HideReturnsTransformationMethod.getInstance())
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                else
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                break;
            case R.id.textView3:
                forget();
                break;

        }
    }
    public  void forget(){
        if (editTextEmail.getText().toString().isEmpty()) {
            editTextEmail.setError("Email required");
            editTextEmail.requestFocus();
            return;
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText().toString()).matches()) {
            editTextEmail.setError("please enter Valid Email");
            editTextEmail.requestFocus();
            return;
        }
        else{
            auth.sendPasswordResetEmail(editTextEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "please check your email account ..,if you want to reset password", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();

                    }
                }
            });
        }

    }

}
