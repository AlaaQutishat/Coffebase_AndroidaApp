package com.example.coffebasemanager.ui.share;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.coffebasemanager.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class ShareFragment extends Fragment {

    private ShareViewModel shareViewModel;
 Button add;
 ImageView sale;
    Uri FilePathUri;
    int Image_Request_Code = 7;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel =
                ViewModelProviders.of(this).get(ShareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);
       add=root.findViewById(R.id.addbutton);
       sale=root.findViewById(R.id.saleimage);
        storageReference = FirebaseStorage.getInstance().getReference().child("Offers");
       add.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               addimage();
           }
       });
        sale.setOnClickListener(new View.OnClickListener() {
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
       return  root;
    }
    public void addimage(){
        if(sale.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.sale).getConstantState()){
            Toast.makeText(getContext(),"please change the image",Toast.LENGTH_LONG).show();
            sale.requestFocus();
         return;
        }
        UploadImageFileToFirebaseStorage();
        Toast.makeText(getContext(),"The item added successfully",Toast.LENGTH_LONG).show();
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
                sale.setImageBitmap(bitmap);
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
    public void UploadImageFileToFirebaseStorage() {
        SharedPreferences result = getActivity().getSharedPreferences("cafename", Context.MODE_PRIVATE);
       final String cafenamee=result.getString("name","datanotfound");

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
                                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Offers").child(cafenamee).child("image" + FilePathUri.getLastPathSegment() + GetFileExtension(FilePathUri));
                                    HashMap<String,String> hashMap=new HashMap<>();
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