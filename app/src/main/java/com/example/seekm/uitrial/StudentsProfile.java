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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
                                if(dataSnapshot.hasChild(user_id))
                                {

                                    String request_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                                    if(request_type.equals("received")){
                                        mCurrent_state="req_received";
                                        request.setText("Accept Request");

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
                                                request.setText("Remove Student");
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {


                                        }
                                    });

                                }

                                //---USER IS FRIEND----

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


        //-------SEND REQUEST BUTTON IS PRESSED----
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mUserId= mFirebaseUser.getUid();





                //-------NOT FRIEND STATE--------
                if(mCurrent_state.equals("not_friends")){

                    DatabaseReference newNotificationReference = mRootReference.child("notifications").child(user_id).push();

//                    String newNotificationId = newNotificationReference.getKey();
//
//                    HashMap<String,String> notificationData=new HashMap<String, String>();
//                    notificationData.put("from",mFirebaseUser.getUid());
//                    notificationData.put("type","request");
//
//                    Map requestMap = new HashMap();
//                    requestMap.put("friend_request/"+mFirebaseUser.getUid()+ "/"+user_id + "/request_type","sent");
//                    requestMap.put("friend_request/"+user_id+"/"+mFirebaseUser.getUid()+"/request_type","received");
//                    requestMap.put("notifications/"+user_id+"/"+newNotificationId,notificationData);

                    //----FRIEND REQUEST IS SEND----
//                    mRootReference.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                            if(databaseError==null){
//
//                                Toast.makeText(ProfileActivityy.this, "Friend Request sent successfully", Toast.LENGTH_SHORT).show();
//
//                                mProfileSendReqButton.setEnabled(true);
//                                mCurrent_state= "req_sent";
//                                mProfileSendReqButton.setText("Cancel Friend Request");
//
//                            }
//                            else{
//                                mProfileSendReqButton.setEnabled(true);
//                                Toast.makeText(ProfileActivityy.this, "Some error in sending friend Request", Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
//                    });
                }

//                //-------CANCEL--FRIEND--REQUEST-----
//
//                if(mCurrent_state.equals("req_sent")){
//
//                    Map valueMap=new HashMap();
//                    valueMap.put("friend_request/"+mFirebaseUser.getUid()+"/"+user_id,null);
//                    valueMap.put("friend_request/"+user_id+"/"+mFirebaseUser.getUid(),null);
//
//                    //----FRIEND REQUEST IS CANCELLED----
//                    mRootReference.updateChildren(valueMap, new DatabaseReference.CompletionListener() {
//
//                        @Override
//                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                            if(databaseError == null){
//
//                                mCurrent_state = "not_friends";
//                                mProfileSendReqButton.setText("Send Friend Request");
//                                mProfileSendReqButton.setEnabled(true);
//                                Toast.makeText(ProfileActivityy.this, "Friend Request Cancelled Successfully...", Toast.LENGTH_SHORT).show();
//                            }
//                            else{
//
//                                mProfileSendReqButton.setEnabled(true);
//                                Toast.makeText(ProfileActivityy.this, "Cannot cancel friend request...", Toast.LENGTH_SHORT).show();
//
//                            }
//                        }
//
//                    });
//
//
//
//
//                }

                //-------ACCEPT FRIEND REQUEST------

                if(mCurrent_state.equals("req_received")){
                    //-----GETTING DATE-----
                    Date current_date=new Date(System.currentTimeMillis());

                    //Log.e("----Date---",current_date.toString());
                    String date[]=current_date.toString().split(" ");
                    final String todays_date=(date[1] + " " + date[2] + "," + date[date.length-1]+" "+date[3]);

                    Map friendMap=new HashMap();
                    friendMap.put("friends/"+mFirebaseUser.getUid()+"/"+user_id+"/date",todays_date);
                    friendMap.put("friends/"+user_id+"/"+mFirebaseUser.getUid()+"/date",todays_date);

                    friendMap.put("friend_request/"+mFirebaseUser.getUid()+"/"+user_id,null);
                    friendMap.put("friend_request/"+user_id+"/"+mFirebaseUser.getUid(),null);

                    //-------BECAME FRIENDS------
                    mRootReference.updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError==null){

                                request.setEnabled(true);
                                mCurrent_state = "friends";
                                request.setText("Unfriend this person");
//                                mProfileDeclineReqButton.setEnabled(false);
//                                mProfileDeclineReqButton.setVisibility(View.INVISIBLE);

                            }
                            else{
                                //mProfileSendReqButton.setEnabled(true);
                                Toast.makeText(StudentsProfile.this, "Error is " +databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }


                //----UNFRIEND---THIS---PERSON----
                if(mCurrent_state.equals("friends")){

                    Map valueMap=new HashMap();
                    valueMap.put("friends/"+mFirebaseUser.getUid()+"/"+user_id,null);
                    valueMap.put("friends/"+user_id+"/"+mFirebaseUser.getUid(),null);

                    //----UNFRIENDED THE PERSON---
                    mRootReference.updateChildren(valueMap, new DatabaseReference.CompletionListener() {

                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null){
                                mCurrent_state = "not_friends";
                                request.setText("Cant Send Request");
                                request.setEnabled(false);
                                //mProfileSendReqButton.setEnabled(true);
                                Toast.makeText(StudentsProfile.this, "Successfully Unfriended...", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                request.setEnabled(true);
                                Toast.makeText(StudentsProfile.this, "Error in unfriending", Toast.LENGTH_SHORT).show();

                            }
                        }

                    });


                }

            }
        });
        //-----DECLING THE FRIEND REQUEST-----
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map valueMap=new HashMap();
                valueMap.put("friend_request/"+mFirebaseUser.getUid()+"/"+user_id,null);
                valueMap.put("friend_request/"+user_id+"/"+mFirebaseUser.getUid(),null);

                mRootReference.updateChildren(valueMap, new DatabaseReference.CompletionListener() {

                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if(databaseError == null){

                            mCurrent_state = "not_friends";
                            request.setText("Send Friend Request");
                            request.setEnabled(false);
                            Toast.makeText(StudentsProfile.this, "Friend Request Declined Successfully...", Toast.LENGTH_SHORT).show();

                            decline.setEnabled(false);
                            decline.setVisibility(View.INVISIBLE);
                        }
                        else{

                            request.setEnabled(true);
                            Toast.makeText(StudentsProfile.this, "Cannot decline friend request...", Toast.LENGTH_SHORT).show();

                        }
                    }

                });


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
