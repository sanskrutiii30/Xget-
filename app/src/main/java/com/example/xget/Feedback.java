package com.example.xget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Feedback extends AppCompatActivity {


    EditText feedbackGiven;
    Button submitFb;
    private String onlineuserid="";
    private FirebaseAuth mauth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        feedbackGiven=findViewById(R.id.feedback);
        submitFb=findViewById(R.id.submitbtn);

        mauth=FirebaseAuth.getInstance();
        onlineuserid=mauth.getCurrentUser().getUid();

        submitFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (feedbackGiven.getText().toString().isEmpty()){
                    Toast.makeText(Feedback.this, "Please enter your feedback ", Toast.LENGTH_SHORT).show();
                }else{
                    submitfeedback();
                }

            }
        });
    }

    private void submitfeedback() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("feedbacks").child(onlineuserid);
        String feedGiven = feedbackGiven.getText().toString();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currDate = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat currTime = new SimpleDateFormat("HH:mm:ss");
        String saveCurrentDate = currDate.format(calendar.getTime());
        String saveCurrentTime = currTime.format(calendar.getTime());

        HashMap<String,Object> feedback = new HashMap<>();

        feedback.put("feedback",feedGiven);
        feedback.put("username",onlineuserid);

        databaseReference.child(saveCurrentDate+""+saveCurrentTime).updateChildren(feedback).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Feedback.this, "feedback submited", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Feedback.this,Dashboard.class));
            }
        });

    }
}