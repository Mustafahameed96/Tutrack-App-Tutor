package com.example.seekm.uitrial;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfile extends AppCompatActivity {

    FloatingActionButton FAB;
    EditText fName, lName, email, dob, gender;
    ImageView profileImg;
    private String TAG = "EDIT_PROFILE";
    private String URL;
    String ProfileImageUrl=null;
    String ProfileUrl=null;
    Uri uriProfileImage;
    private static final int CHOOSE_IMAGE = 101;
    StorageReference profileImageRef;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        fName = (EditText) findViewById(R.id.fName);
        lName = (EditText) findViewById(R.id.lName);
        email = (EditText) findViewById(R.id.email);
        dob = (EditText) findViewById(R.id.dob);
        profileImg = (ImageView)findViewById(R.id.profilePicture);
        gender = (EditText) findViewById(R.id.gender);

        FAB = (FloatingActionButton)findViewById(R.id.floatingActionButton3);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();


        final CShowProgress cShowProgress = CShowProgress.getInstance();
        cShowProgress.showProgress(EditProfile.this);
        disableEditText(email);
        disableEditText(gender);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Tutors")
                .whereEqualTo("User_uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    fName.setText(document.get("FirstName").toString());
                                    lName.setText(document.get("LastName").toString());
                                    email.setText(document.get("EmailAddress").toString());
                                    dob.setText(document.get("DateOfBirth").toString());
                                    gender.setText(document.get("Gender").toString());
                                    URL = document.get("ProfileImage_Url").toString();
                                    Glide
                                            .with(EditProfile.this)
                                            .load(URL)
                                            .into(profileImg);
                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onComplete: Exception" + e.getMessage());
                                }
                            }
                            cShowProgress.hideProgress();
                        }
                    }
                });

        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ProfileUrl!=null)
                    URL = ProfileUrl;
                cShowProgress.showProgress(EditProfile.this);
                FAB.setVisibility(View.INVISIBLE);
                final String upFname = fName.getText().toString();
                final String upLname = lName.getText().toString();
                final String upEmail = email.getText().toString();
                final String upDob = dob.getText().toString();
                final String upGender = gender.getText().toString();
                final String upURL = URL;

                db.collection("Tutors")
                        .document(uid)
                        .update(
                                "FirstName", upFname,
                                "LastName", upLname,
                                "EmailAddress", upEmail,
                                "DateOfBirth", upDob,
                                "Gender", upGender,
                                "ProfileImage_Url", upURL
                        );
                cShowProgress.hideProgress();
                Toast.makeText(EditProfile.this, "Changes saved!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent (EditProfile.this,Drawer.class));
            }

        });


        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

    }



    private void showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Image"),CHOOSE_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CHOOSE_IMAGE && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            uriProfileImage  = data.getData();
            profileImg.setImageDrawable(null);
            profileImg.setBackgroundResource(0);
            profileImg.setImageURI(uriProfileImage);
            uploadImageToFirebase();
        }
    }

    @SuppressLint("RestrictedApi")
    private void uploadImageToFirebase() {
        CShowProgress cShowProgress = CShowProgress.getInstance();
        cShowProgress.showProgress(EditProfile.this);
        profileImageRef= FirebaseStorage.getInstance().getReference().child("ProfilePicture/"+System.currentTimeMillis()+".jpg");
        if(uriProfileImage!=null){
            profileImageRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ProfileImageUrl=uri.toString();
                            ProfileUrl=ProfileImageUrl;
                            Toast.makeText(EditProfile.this, "Profile picture updated", Toast.LENGTH_LONG).show();
                            FAB.setVisibility(View.VISIBLE);

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });
            cShowProgress.hideProgress();
        }

    }
    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }
}

