package com.example.seekm.uitrial;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentsProfile extends AppCompatActivity {

    TextView fullName, email, gender, qualification, dob, board;
    ProgressBar mBar;
    String fname, lname;
    Button request;
    ImageButton cancel;
    String myUID, URL;
    ListView listView;
    ImageView dp;
    String user_id;
    Button decline;
    DatabaseReference mfriendReqReference;
    DatabaseReference mDatabaseReference;
    DatabaseReference mFriendDatabase;
    DatabaseReference mNotificationReference;
    DatabaseReference mRootReference;
    DatabaseReference getmDatabaseReference;
    private String mCurrent_state;
    FirebaseUser mFirebaseUser;

    private String TAG = "TUTORS_PROFILE";

    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_profile);
        dp = (ImageView) findViewById(R.id.profilePicture);
        fullName = (TextView) findViewById(R.id.name_student);
        email = (TextView) findViewById(R.id.email_student);
        gender= (TextView) findViewById(R.id.genderr);
        qualification = (TextView) findViewById(R.id.qualificationn);
        dob = (TextView) findViewById(R.id.dobb);
        board = (TextView) findViewById(R.id.boardd);

        cancel = findViewById(R.id.backBtn33);
        decline=findViewById(R.id.btn_cancel);
        request = (Button)findViewById(R.id.btn_request_student);
        URL = null;

        final CShowProgress cShowProgress = CShowProgress.getInstance();
        cShowProgress.showProgress(StudentsProfile.this);


        Intent intent = getIntent();
        myUID = intent.getStringExtra("doc_id");

        user_id =  intent.getStringExtra("doc_id");
        Toast.makeText(StudentsProfile.this, myUID,Toast.LENGTH_LONG).show();

        mfriendReqReference= FirebaseDatabase.getInstance().getReference().child("friend_request");
        mDatabaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(myUID);
        mFriendDatabase=FirebaseDatabase.getInstance().getReference().child("friends");
        mNotificationReference=FirebaseDatabase.getInstance().getReference().child("notifications");
        mRootReference=FirebaseDatabase.getInstance().getReference();
        mFirebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        mCurrent_state = "req_received";


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Students")
                .whereEqualTo("User_uid", myUID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {

                                    fname = document.get("FirstName").toString();
                                    lname = document.get("LastName").toString();
                                    fullName.setText(fname + " " + lname);
                                    cShowProgress.hideProgress();

                                    qualification.setText(document.get("LatestQualification").toString());
                                    board.setText(document.get("EducationBoard").toString());
                                    email.setText(document.get("EmailAddress").toString());
                                    gender.setText(document.get("Gender").toString());
                                    dob.setText(document.get("DateOfBirth").toString());
                                    URL = document.get("ProfileImage_Url").toString();
                                    Glide
                                            .with(StudentsProfile.this)
                                            .load(URL)
                                            .into(dp);

                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onComplete: Exception" + e.getMessage());
                                }
                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                String display_name=dataSnapshot.child("name").getValue().toString();
//                String display_status=dataSnapshot.child("status").getValue().toString();
//                String display_image=dataSnapshot.child("image").getValue().toString();
//                mProfileName.setText(display_name);
//                mProfileStatus.setText(display_status);

                //----ADDING TOTAL  NO OF FRIENDS---
                mFriendDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long len = dataSnapshot.getChildrenCount();
                        //  mprofileFriendCount.setText("TOTAL FRIENDS : "+len);

                        //----SEEING THE FRIEND STATE OF THE USER---
                        //----ADDING THE TWO BUTTON-----
                        mfriendReqReference.child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //----CHECKING IF FRIEND REQUEST IS SEND OR RECEIVED----
                                if(dataSnapshot.hasChild(user_id)){

                                    String request_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                                    if(request_type.equals("received")){
                                        mCurrent_state="req_received";
                                        request.setText("Accept Friend Request");

                                    }




                                }

                                else{

                                    mFriendDatabase.child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            decline.setVisibility(View.INVISIBLE);
                                            decline.setEnabled(false);

                                            if(dataSnapshot.hasChild(user_id)){
                                                mCurrent_state="friends";
                                                request.setText("Block Student");
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {


                                        }
                                    });

                                }

                                //---USER IS FRIEND----
                                else{

                                    mFriendDatabase.child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {



                                            if(dataSnapshot.hasChild(user_id)){
                                                mCurrent_state="friends";
                                                request.setText("Block Tutor");
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                            //mProgressDialog.dismiss();
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(StudentsProfile.this, "Error fetching Friend request data", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //  mProgressDialog.dismiss();
                    }

                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //   mProgressDialog.dismiss();
            }

        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentsProfile.this,RequestsActivity.class);
                startActivity(intent);
            }
        });

    }
}
