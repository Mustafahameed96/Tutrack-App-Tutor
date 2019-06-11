package com.example.seekm.uitrial;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Feedback extends AppCompatActivity {

    ImageView back;
    Button btnFeedback;
    EditText text;
    FirebaseAuth mAuth;
    String current_userUid;
    private String TAG = "FEEDBACK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        current_userUid = currentFirebaseUser.getUid();

        btnFeedback = (Button) findViewById(R.id.btnFeedback);
        btnFeedback.setClickable(false);
        btnFeedback.setEnabled(false);

        back = (ImageView)findViewById(R.id.back);
        text = (EditText)findViewById(R.id.feedback);


        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Feedback.this, Drawer.class));

            }
        });

        text.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                btnFeedback.setClickable(true);
                btnFeedback.setEnabled(true);

            }
        });

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CShowProgress cShowProgress = CShowProgress.getInstance();
                cShowProgress.showProgress(Feedback.this);
                // Create a new user with a first and last name
                Map<String, Object> user = new HashMap<>();
                user.put("UserId", current_userUid);
                user.put("FeedText", text.getText().toString());

                // Add a new document with a generated ID
                db.collection("Feedback")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                Toast.makeText(Feedback.this, "Thankyou for providing your feedback", Toast.LENGTH_LONG).show();
                                cShowProgress.hideProgress();
                                startActivity(new Intent(Feedback.this, Drawer.class));

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });

            }
        });
    }
}
