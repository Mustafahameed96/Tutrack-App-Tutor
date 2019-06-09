package com.example.seekm.uitrial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity {


    private RecyclerView mReqList;

    private List<String> requestList = new ArrayList<>();

    private DatabaseReference mDatabaseReference;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mMessageDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    private RequestAdapter mRequestAdapter;


    protected void onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
//         Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.activity_requests, container, false);
        mReqList = (RecyclerView)mMainView.findViewById(R.id.recyclerViewRequestList);
        mAuth= FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friend_request");
        mDatabaseReference.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mReqList.setHasFixedSize(true);
        mReqList.setLayoutManager(linearLayoutManager);

        requestList.clear();
        mRequestAdapter = new RequestAdapter(requestList);
        mReqList.setAdapter(mRequestAdapter);

        mDatabaseReference.child(mCurrent_user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String userId = dataSnapshot.getKey();
                requestList.add(userId);
                mRequestAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
