package com.example.coffebasemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
 EditText emaill,passwordd;
 TextView signup,forgetpassword;
 Button signin;
 ProgressBar progress;
    FirebaseAuth auth;
ImageView showpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emaill = findViewById(R.id.Email);
        passwordd = findViewById(R.id.Password);
        signup = findViewById(R.id.signup);
        forgetpassword = findViewById(R.id.forgetpass);
        showpass=(ImageView)findViewById(R.id.showpass);
        showpass.setOnClickListener(this);
        progress=(ProgressBar)findViewById(R.id.progress);
        signin=(Button)findViewById(R.id.login);
        signup.setOnClickListener(this);
        signin.setOnClickListener(this);
        forgetpassword.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
        progress.setVisibility(View.INVISIBLE);


    }

    private void userlogin() {

        final String email = emaill.getText().toString().trim();
        final String password = passwordd.getText().toString().trim();



        if (email.isEmpty()) {
            emaill.setError("Email required");
            emaill.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emaill.setError("please enter Valid Email");
            emaill.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordd.setError("password required");
            passwordd.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordd.setError("password should be at least 6 characters long");
            passwordd.requestFocus();
            return;
        }
        progress.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progress.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()) {
                    if (auth.getCurrentUser().isEmailVerified()) {
                         FirebaseDatabase database ;
                         DatabaseReference myRef ;
                        database = FirebaseDatabase.getInstance();
                        myRef = database.getReference("Coffee");
                        myRef.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                               String cafenameee =dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("coffeename").getValue().toString()+"";
                                Intent move = new Intent(getApplicationContext(), menu.class);
                                SharedPreferences name = getApplicationContext().getSharedPreferences("cafename", Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = name.edit();
                                edit.putString("name",cafenameee);
                                edit.apply();
                                startActivity(move);
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value

                            }
                        });

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "please Verify your email address", Toast.LENGTH_LONG).show();

                    }
                }


                else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }


        );}
        public  void forget(){
        if (emaill.getText().toString().isEmpty()) {
                emaill.setError("Email required");
                emaill.requestFocus();
                return;
            }

           else if (!Patterns.EMAIL_ADDRESS.matcher(emaill.getText().toString()).matches()) {
                emaill.setError("please enter Valid Email");
                emaill.requestFocus();
                return;
            }
           else{
               auth.sendPasswordResetEmail(emaill.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
switch (v.getId()){
    case R.id.signup :
        Intent signup =new Intent(MainActivity.this,signup.class);
        startActivity(signup);
        break;
    case R.id.forgetpass:
        forget();
        break;
    case  R.id.login:
        userlogin();
        break;
    case R.id.showpass:
        if(passwordd.getTransformationMethod()==HideReturnsTransformationMethod.getInstance())
            passwordd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        else
            passwordd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        break;
}
    }
}
