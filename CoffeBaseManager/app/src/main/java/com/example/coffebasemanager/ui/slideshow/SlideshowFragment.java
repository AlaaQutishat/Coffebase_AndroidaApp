package com.example.coffebasemanager.ui.slideshow;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.coffebasemanager.R;
import com.example.coffebasemanager.addmenuitem;
import com.example.coffebasemanager.imageinfo;
import com.example.coffebasemanager.menu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class SlideshowFragment extends Fragment {
    CircleImageView profile_image;
    String itemname,calories,categoryspinner,price;
    EditText pricee,itemnamee;
    NumberPicker caloriess;
    Spinner categoryspinnerr;
    Button addd,clear;
    Uri FilePathUri;
    int Image_Request_Code = 7;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    String Storage_Path = "Menu/";
    private SlideshowViewModel slideshowViewModel;
    String cafenamee;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        storageReference = FirebaseStorage.getInstance().getReference().child("Menu");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        SharedPreferences result = getActivity().getSharedPreferences("cafename", Context.MODE_PRIVATE);
        cafenamee=result.getString("name","datanotfound");
            profile_image= root.findViewById(R.id.profile_image);
        pricee= root.findViewById(R.id.price);
        itemnamee= root.findViewById(R.id.name);
        caloriess= root.findViewById(R.id.calories);
        clear =root.findViewById(R.id.clear);
        categoryspinnerr= root.findViewById(R.id.categoryspinner);
        addd= root.findViewById(R.id.add);
        caloriess.setMinValue(1);
        caloriess.setMaxValue(100000);
        caloriess.setWrapSelectorWheel(true);
        String[] category = new String[] {"Cold Drink","Hot Drink","Main Course","Deserts"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, category);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoryspinnerr.setAdapter(adapter);
      profile_image.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              // Creating intent.
              Intent intent = new Intent();

              // Setting intent type as image to select image from phone storage.
              intent.setType("image/*");
              intent.setAction(Intent.ACTION_GET_CONTENT);
              startActivityForResult(Intent.createChooser(intent, "Please Select Image"),  Image_Request_Code);

          }
      });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_image.setImageResource(R.drawable.nescafe);
                pricee.setText(null);
                caloriess.setValue(1);
                itemnamee.setText(null);
            }
        });
        addd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                price=pricee.getText().toString();
                itemname=itemnamee.getText().toString();
                calories=String.valueOf(caloriess.getValue());
               int spinnercount=categoryspinnerr.getSelectedItemPosition();

                if(profile_image.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.nescafe).getConstantState()){
                    Toast.makeText(getContext(),"please change the image",Toast.LENGTH_LONG).show();
                    profile_image.requestFocus();
                    return;
                }

              if(itemname.isEmpty()){
                   itemnamee.setError("the field can't be empty");
                   itemnamee.requestFocus();
                   return;
               }
//                if (spinnercount==-1){
//                   Toast.makeText(getContext(),"please select from category",Toast.LENGTH_LONG).show();
//                   categoryspinnerr.requestFocus();
//                   return;
//               }
                if (price.isEmpty()){
                   pricee.setError("the field can't be empty");
                   pricee.requestFocus();
                   return;
               }
                switch (spinnercount){
                    case 0:
                        categoryspinner="Cold Drink";
                        break;
                    case 1 :
                        categoryspinner="Hot Drink";
                        break;
                    case 2:
                        categoryspinner="Main Course";
                        break;
                    case 3:
                        categoryspinner="Deserts";
                        break;
                }
                price=pricee.getText().toString()+"$";
                addmenuitem item=new addmenuitem(itemname,calories,categoryspinner,price,0);
                FirebaseDatabase.getInstance().getReference("Menu").child(cafenamee)
                        .child(itemname)
                        .setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            UploadImageFileToFirebaseStorage();
                            Toast.makeText(getContext(),"The item added successfully",Toast.LENGTH_LONG).show();


                        }
                            else {
                                Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        } }
                );
            }
        });
        return root;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode ==Image_Request_Code && resultCode ==RESULT_OK &&data!= null && data.getData()!= null){

        FilePathUri = data.getData();

            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), FilePathUri);
                // Setting up bitmap selected image into ImageView.
                profile_image.setImageBitmap(bitmap);
            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContext().getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }
    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {

        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null) {

            // Creating second StorageReference.
            final StorageReference storageReference2nd = storageReference.child("image" + FilePathUri.getLastPathSegment() + GetFileExtension(FilePathUri));

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageReference2nd.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Menu").child(cafenamee).child(itemname).child("image");
                                    HashMap <String,String> hashMap=new HashMap<>();
                                    hashMap.put("imageurl", String.valueOf(uri));
                                    databaseReference.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(),"Completed",Toast.LENGTH_LONG).show();

                                        }
                                    });
                                }
                            });


                            // Showing toast message after done uploading.
                            Toast.makeText(getContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {


                            // Showing exception erro message.
                            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {


                        }
                    });
        }
        else {

            Toast.makeText(getContext(), "Please Select Image ", Toast.LENGTH_LONG).show();

        }
    }

}