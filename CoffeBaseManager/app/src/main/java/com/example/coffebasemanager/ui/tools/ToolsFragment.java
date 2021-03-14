package com.example.coffebasemanager.ui.tools;

import android.content.ContentResolver;
import android.content.Intent;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.coffebasemanager.R;
import com.example.coffebasemanager.addaboutus;
import com.example.coffebasemanager.imageinfo;
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

public class ToolsFragment extends Fragment {
    FrameLayout fram;
    String url,fromtime,totime,studyy,parkingg,smokee,phonee;
    ImageView image,imagg;
    int fromm,too,seatt;
    Spinner openn,closee;
    Button edit,save;
    NumberPicker seat,open,close;
    EditText phone;
    RadioGroup rg1,rg2,rg3;
    RadioButton studyyes,studyno,smokeyes,smokeno,parkingyes,parkingno;
    private ToolsViewModel toolsViewModel;
    int idstudy,idsmokr,idpark;
    int spinnercount,spinnercount2;
    String Storage_Path = "Coffee/";
    Uri FilePathUri;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    int Image_Request_Code = 7;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools, container, false);
        storageReference = FirebaseStorage.getInstance().getReference();
        fram=root.findViewById(R.id.frame);
      save=(Button)root.findViewById(R.id.save);
        openn =(Spinner)root.findViewById(R.id.spinneropen);
        closee =(Spinner)root.findViewById(R.id.spinnerclose);
        image=(ImageView)root.findViewById(R.id.imagee);
        seat=(NumberPicker)root.findViewById(R.id.seat) ;
        open=(NumberPicker)root.findViewById(R.id.open) ;
        close=(NumberPicker)root.findViewById(R.id.close) ;
        phone=(EditText)root.findViewById(R.id.phone);
        studyyes=(RadioButton) root.findViewById(R.id.studyyes);
        studyno=(RadioButton) root.findViewById(R.id.studyno);
        smokeyes=(RadioButton) root.findViewById(R.id.smokeyes);
        smokeno=(RadioButton) root.findViewById(R.id.smokeno);
        parkingyes=(RadioButton) root.findViewById(R.id.parkingyes);
        parkingno=(RadioButton) root.findViewById(R.id.parkingno);
        rg1=(RadioGroup)root.findViewById(R.id.study);
        rg2=(RadioGroup)root.findViewById(R.id.smoke);
        rg3=(RadioGroup)root.findViewById(R.id.parking);
        imagg=root.findViewById(R.id.imagg);

 phonee =phone.getText().toString().trim();
 seat.setMinValue(1);
 seat.setMaxValue(200);
 seat.setWrapSelectorWheel(true);
        open.setMinValue(1);
        open.setMaxValue(12);
        open.setWrapSelectorWheel(true);

        close.setMinValue(1);
        close.setMaxValue(12);
        close.setWrapSelectorWheel(true);

        String[] category = new String[] {"Am","Pm"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, category);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        openn.setAdapter(adapter);
        closee.setAdapter(adapter);
        fram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating intent.
                Intent intent = new Intent();

                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                idstudy = rg1.getCheckedRadioButtonId();
                idsmokr = rg2.getCheckedRadioButtonId();
                idpark = rg3.getCheckedRadioButtonId();
                phonee=phone.getText().toString().trim();
                spinnercount=openn.getSelectedItemPosition();
                spinnercount2=closee.getSelectedItemPosition();
                if(imagg.getDrawable().getConstantState()==getResources().getDrawable(R.drawable.profile).getConstantState()){
                    Toast.makeText(getContext(),"please change the image",Toast.LENGTH_LONG).show();
                    imagg.requestFocus();
                    return;
                }
                if(phonee.isEmpty() ){
                    phone.setError("the phone can't be empty");
                    phone.requestFocus();
                    return;
                }
                 if(phonee.length()!=10){
                    phone.setError("the phone must be 10 number");
                    phone.requestFocus();
                    return;
                }
                if (studyyes.isChecked()==false && studyno.isChecked()==false){
                    Toast.makeText(getContext(),"Please Choose from study check",Toast.LENGTH_LONG).show();
                    rg1.requestFocus();
                    return;
                }
                 if (smokeyes.isChecked()==false && smokeno.isChecked()==false){
                    Toast.makeText(getContext(),"Please Choose from smoke check",Toast.LENGTH_LONG).show();
                    rg2.requestFocus();
                    return;
                }
                if (parkingyes.isChecked()==false && parkingno.isChecked()==false) {
                    Toast.makeText(getContext(), "Please Choose from park check", Toast.LENGTH_LONG).show();
                    rg3.requestFocus();
                    return;
                }

//                if (spinnercount==-1){
//                    Toast.makeText(getContext(),"please select from am or pm",Toast.LENGTH_LONG).show();
//                    openn.requestFocus();
//                    return;
//                }
//                 if (spinnercount2==-1){
//                    Toast.makeText(getContext(),"please select from am or pm",Toast.LENGTH_LONG).show();
//                    closee.requestFocus();
//                    return;
//                }
                fromm=open.getValue();
                seatt=seat.getValue();
                too=close.getValue();
                switch (idstudy){
                    case R.id.studyyes:
                        studyy="yes";
                        break;
                    case R.id.studyno:
                        studyy="No";
                        break;
                }

                switch (idsmokr){
                    case R.id.smokeyes:
                        smokee="yes";
                        break;
                    case R.id.smokeno:
                        smokee="No";

                        break;
                }

                switch (idpark){
                    case R.id.parkingyes:
                        parkingg="yes";
                        break;
                    case R.id.parkingno:
                        parkingg="No";

                        break;
                }

                switch (spinnercount){
                    case 0:
                        fromtime="Am";
                        break;
                    case 1 :
                        fromtime="Pm";
                        break;
                }
                switch (spinnercount2){
                    case 0:
                        totime="Am";
                        break;
                    case 1 :
                        totime="Pm";
                        break;
                }




                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                addaboutus Alaa = new addaboutus(url,phonee,fromtime,totime,studyy,parkingg,smokee,seatt,fromm,too);
                myRef.child("Coffee").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Aboutus").setValue(Alaa);
                UploadImageFileToFirebaseStorage();
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), FilePathUri);
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, 135, 135, true);
                // Setting up bitmap selected image into ImageView.
                imagg.setImageBitmap(resized);


            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }
    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }
    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {
        String Storage_Path = "Coffee/";


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
                                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Coffee").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("image");
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

