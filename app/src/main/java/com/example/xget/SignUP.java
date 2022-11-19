package com.example.xget;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUP extends AppCompatActivity {

    private EditText email,password,phnno,fulname;
    private Button reg_btn;
    private TextView reg_qn;

    private FirebaseAuth mauth;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email=findViewById(R.id.reg_email);
        password=findViewById(R.id.reg_pass);
        phnno=findViewById(R.id.phnno);
        fulname=findViewById(R.id.fulname);
        reg_btn=findViewById(R.id.reg_btn);
        reg_qn=findViewById(R.id.reg_qn);

        mauth=FirebaseAuth.getInstance();
        loading = new ProgressDialog(this);



        reg_qn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SignUP.this,Login.class);
                startActivity(intent);
            }
        });

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringemail=email.getText().toString();
                String stringpass=password.getText().toString();
                String fulnme=fulname.getText().toString();
                String phno=phnno.getText().toString();

                if (TextUtils.isEmpty(stringemail)||TextUtils.isEmpty(stringpass)){
                    Toast.makeText(SignUP.this, "Enter all details", Toast.LENGTH_SHORT).show();
                }else{
                    loading.setTitle("Verifying details");
                    loading.setMessage("PLease wait while we create an account for you...");
                    loading.show();
                    loading.setCancelable(false);
                    loading.setCanceledOnTouchOutside(false);
                    registeruser(stringemail,stringpass,fulnme,phno);
                }

            }
        });

    }
    public void registeruser(String stringemail, String stringpass,String fullNm,String phone){
        mauth.createUserWithEmailAndPassword(stringemail,stringpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    updateToRealtime(stringemail,stringpass,fullNm,phone);
                }else{
                    Toast.makeText(SignUP.this,"Please enter valid details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateToRealtime(String email, String pswd,String user_fname,String phone) {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");

        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("email",email);
        userMap.put("password",pswd);
        userMap.put("phone",phone);
        userMap.put("fullName",user_fname);


        dbRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loading.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(SignUP.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUP.this,Dashboard.class));
                }else{
                    Toast.makeText(SignUP.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
