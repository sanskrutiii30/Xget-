package com.example.xget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MyProfile extends AppCompatActivity {

    private TextView useremail;
    private Toolbar setingtolbar;
    private Button logout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        useremail=findViewById(R.id.useremail);
        logout=findViewById(R.id.logoutbtn);


        setingtolbar=findViewById(R.id.toolbar1);
        setSupportActionBar(setingtolbar);
        getSupportActionBar().setTitle("My Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        useremail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MyProfile.this)
                        .setTitle("Xget")
                        .setMessage("Are you sure you want to logout ? ")
                        .setCancelable(false)
                        .setPositiveButton("Yes",(dialog,which)->{
                            FirebaseAuth.getInstance().signOut();
                            Intent intent=new Intent(MyProfile.this,Login.class);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("No",null )
                        .show();
                    }
                });
            }
        }