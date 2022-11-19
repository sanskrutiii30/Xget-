package com.example.xget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.lang.UProperty;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.Executor;

public class Login extends AppCompatActivity {
    private EditText email,password;
    private Button loginbtn;
    private TextView loginqn,forgetpass;

    private FirebaseAuth mauth=FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;

    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkFingerPrint();

        email=findViewById(R.id.email);
        password=findViewById(R.id.pass);
        loginbtn=findViewById(R.id.loginbtn);
        loginqn=findViewById(R.id.loginqn);
        forgetpass=findViewById(R.id.foregetpass);

        progressDialog =new ProgressDialog(this);

                    loginqn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent= new Intent(Login.this,SignUP.class);
                            startActivity(intent);
                        }
                    });


                    loginbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String stringemail=email.getText().toString();
                            String stringpass=password.getText().toString();

                                    if (TextUtils.isEmpty(stringemail)){

                                        email.setError("Email is required");
                                    }
                                    if (TextUtils.isEmpty(stringpass)){

                                        password.setError("password is required");

                                    }
                                    else{

                                        progressDialog.setMessage(" Logging In ");
                                        progressDialog.setCanceledOnTouchOutside(false);
                                        progressDialog.show();
                                        mauth.signInWithEmailAndPassword(stringemail,stringpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                    if(task.isSuccessful()){
                                                        Intent intent = new Intent(Login.this,Dashboard.class);
                                                        startActivity(intent);
                                                        Toast.makeText(Login.this, "Log In successfully ", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }else{
                                                        Toast.makeText(Login.this,"Please enter valid details", Toast.LENGTH_SHORT).show();
                                                    }
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                }
                         });

                    forgetpass.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final EditText edittext = new EditText(Login.this);
//                            String useremail=edittext.getText().toString();

                            AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);

//                            alert.setMessage("Enter Your Message");
                            alert.setTitle("Enter Your Email");

                            alert.setView(edittext);

                            alert.setPositiveButton("Send now", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    if (TextUtils.isEmpty(edittext.getText().toString())) {
                                        Toast.makeText(Login.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
//                                        FirebaseAuth mauth = FirebaseAuth.getInstance();
                                        mauth.sendPasswordResetEmail(edittext.getText().toString())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(Login.this, "Password Resent link sent" , Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Login.this, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });

                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // what ever you want to do with No option.
                                    dialog.dismiss();
                                }
                            });
                            alert.show();
                        }
                    });
                    }

    private void checkFingerPrint() {

        BiometricManager biometricManager = BiometricManager.from(this);

        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(getApplicationContext(), "Devices doesn't have biometric", Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(getApplicationContext(), "Not Working", Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(getApplicationContext(), "No fingerprint assigned", Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                Toast.makeText(getApplicationContext(), "security update required", Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                Toast.makeText(getApplicationContext(), "unsupported", Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                Toast.makeText(getApplicationContext(), "status unknown", Toast.LENGTH_SHORT).show();
                break;

        }

        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new androidx.biometric.BiometricPrompt(Login.this, executor, new androidx.biometric.BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull androidx.biometric.BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                FirebaseUser user = mauth.getCurrentUser();
                if (user!=null){
                    Intent intent=new Intent(Login.this,Dashboard.class);
                    startActivity(intent);
                    Toast.makeText(Login.this, "Welcome back", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Unlock Xget").setDescription("Use fingerprint to login").setDeviceCredentialAllowed(true).build();
        biometricPrompt.authenticate(promptInfo);
    }

}