package com.example.seekm.uitrial;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MAINACTIVITY";
    VideoView videoView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        nextActivity();


    }


    void nextActivity(){
        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        catch (Error err) {
            Log.d(TAG, "onCreate: Error");
        }
        setContentView(R.layout.activity_main);
//        getSupportActionBar().hide();

        try {
            videoView = findViewById(R.id.videoView);
            Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);
            videoView.setVideoURI(video);

            videoView.setOnCompletionListener(  new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (isFinishing())
                        return;
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                        final   String User_uid1 = currentFirebaseUser.getUid();


                        DocumentReference docRef = db.collection("Tutors").document(User_uid1);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {



                                        finishAffinity();
                                        startActivity(new Intent(MainActivity.this,Drawer.class));




                                    } else {



                                        finishAffinity();
                                        startActivity(new Intent(MainActivity.this,NextActivity.class));

                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });




                    } else {

                        Intent i = new Intent(MainActivity.this, OnBoarding_1.class);
                        startActivity(i);
                        finish();
                    }



                }
            });
            videoView.start();
        } catch (Error err) {
            //  Log.d(TAG, "onCreateInMainActivity: " + err.getMessage());
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                final   String User_uid1 = currentFirebaseUser.getUid();


                DocumentReference docRef = db.collection("Tutors").document(User_uid1);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {



                                finishAffinity();
                                startActivity(new Intent(MainActivity.this,Drawer.class));




                            } else {



                                finishAffinity();
                                startActivity(new Intent(MainActivity.this,NextActivity.class));

                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });




            } else {

                Intent i = new Intent(MainActivity.this, OnBoarding_1.class);
                startActivity(i);
                finish();
            }

        }

    }
}
