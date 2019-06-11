
package com.example.seekm.uitrial;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private double latitude, longitude ;
    private  String First_Name ,Last_Name ;
    private static final String TAG = "MapActivity";
    ListView listView;

    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> StudentId = new ArrayList<>();



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        listView =  findViewById(R.id.list_view_11);

        mAuth= FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friend_request");


        mDatabaseReference.child(mCurrent_user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String userId = dataSnapshot.getKey();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Students").document(userId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();

                                    try {

                                        First_Name = document.get("FirstName").toString();
                                        Last_Name = document.get("LastName").toString();
                                        StudentId.add(document.getId());
                                        arrayList.add(First_Name + " " + Last_Name);

                                        arrayAdapter = new ArrayAdapter<String>(RequestsActivity.this, android.R.layout.simple_list_item_1, arrayList);
                                        listView.setAdapter(arrayAdapter);



                                    } catch (NullPointerException e) {

                                    }


                                } else {
                                    Log.w(TAG, "Error getting document.", task.getException());
                                }
                            }

                        });



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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String user_doc_id =  StudentId.get(position);
                Toast.makeText(RequestsActivity.this,user_doc_id,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RequestsActivity.this, StudentsProfile.class);
                intent.putExtra("doc_id", user_doc_id);
                startActivity(intent);
            }
        });


    }

}