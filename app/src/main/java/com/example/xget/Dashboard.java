package com.example.xget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

public class Dashboard extends AppCompatActivity {

    private CardView budgetcardview, todayscardview;

    private TextView budgettv,todaytv,weektv,monthtv,savingtv;

    private ImageView budgetimg,todayimg,weekimg,monthimg,analyticimg,histroyimg;

    private FirebaseAuth mauth;
    private DatabaseReference budgetref,expensesref,personalref;
    private String  onlineuesrid="";

    private int totalamountmonth=0;
    private int totalamountbudget=0;
    private int totalamountbudgetb=0;
    private int totalamountbudgetc=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setTitle();
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        budgetcardview=findViewById(R.id.budgetcardview);
        todayscardview=findViewById(R.id.todaycardview);

        weekimg=findViewById(R.id.weekimg);
        monthimg=findViewById(R.id.monthimg);
        analyticimg=findViewById(R.id.analyticimg);
        histroyimg=findViewById(R.id.histroyimg);

        budgettv=findViewById(R.id.budgettv);
        todaytv=findViewById(R.id.todaytv);
        weektv=findViewById(R.id.weektv);
        monthtv=findViewById(R.id.monthtv);
        savingtv=findViewById(R.id.savingtv);

        mauth=FirebaseAuth.getInstance();
        onlineuesrid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        budgetref= FirebaseDatabase.getInstance().getReference("budget").child(onlineuesrid);
        expensesref= FirebaseDatabase.getInstance().getReference("expenses").child(onlineuesrid);
        personalref= FirebaseDatabase.getInstance().getReference("personal").child(onlineuesrid);




        budgetcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this,Budget.class);
                startActivity(intent);

            }
        });
        todayscardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this,Todayspending.class);
                startActivity(intent);

            }
        });
        weekimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this,WeekSpending.class);
                intent.putExtra("type","week");
                startActivity(intent);

            }
        });

        monthimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this,WeekSpending.class);
                intent.putExtra("type","month");
                startActivity(intent);

            }
        });

        analyticimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this,ConfigureAnalaytics.class);
                startActivity(intent);

            }
        });

        histroyimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this,History.class);
                startActivity(intent);

            }
        });

        budgetref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalamountbudget += pTotal;
                    }
                    totalamountbudgetc  = totalamountbudgetb;
                    personalref.child("budget").setValue(totalamountbudgetc);
                }else {
                    personalref.child("budget").setValue(0);
                    Toast.makeText(Dashboard.this, "Please set a Budget ", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getbudgetamount();
        gettodayspentamount();
        getweekspentamount();
        getmonthspentamount();
        getsaving();
    }

            private void getbudgetamount(){
                budgetref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getChildrenCount()>0){
                            for (DataSnapshot ds : snapshot.getChildren()){
                                Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                Object total = map.get("amount");
                                int pTotal = Integer.parseInt(String.valueOf(total));
                                totalamountbudget += pTotal;
                                budgettv.setText("Rs "+String.valueOf(totalamountbudget));

                            }
                        }else{
                            totalamountbudget=0;
                            budgettv.setText("Rs "+String.valueOf(0));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

                private  void   gettodayspentamount(){

                    DateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal= Calendar.getInstance();
                    String date=dateFormat.format(cal.getTime());

                    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineuesrid);
                    Query query=databaseReference.orderByChild("date").equalTo(date);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int Totalamt=0;
                            for (DataSnapshot ds : snapshot.getChildren()){
                                Map<String,Object>map=(Map<String, Object>)ds.getValue();
                                Object total=map.get("amount");
                                int pTotal=Integer.parseInt(String.valueOf(total));
                                Totalamt +=pTotal;
                                todaytv.setText("Rs  "+Totalamt);
                            }
                            personalref.child("today").setValue(Totalamt);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                    private  void getweekspentamount(){

                        MutableDateTime epoch= new MutableDateTime();
                        epoch.setDate(0);

                        DateTime now= new DateTime();
                        Months months= Months.monthsBetween(epoch,now);

                       DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference("expenses").child(onlineuesrid);
                       Query query=databaseReference.orderByChild("month").equalTo(months.getMonths());
                       query.addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {

                               int Totalamt = 0;
                               for (DataSnapshot ds : snapshot.getChildren()) {
                                   Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                   Object total = map.get("amount");
                                   int pTotal = Integer.parseInt(String.valueOf(total));
                                   Totalamt += pTotal;
                                   weektv.setText("Rs  " + Totalamt);

                               }

                               personalref.child("month").setValue(Totalamt);
                               totalamountmonth= Totalamt;
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {

                           }
                       });
                    }

                    private  void  getmonthspentamount(){

                        MutableDateTime epoch= new MutableDateTime();
                        epoch.setDate(0);

                        DateTime now= new DateTime();
                        Weeks weeks= Weeks.weeksBetween(epoch,now);

                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineuesrid);
                        Query query=databaseReference.orderByChild("week").equalTo(weeks.getWeeks());
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                int Totalamt = 0;
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                    Object total = map.get("amount");
                                    int pTotal = Integer.parseInt(String.valueOf(total));
                                    Totalamt += pTotal;
                                    monthtv.setText("Rs  " + Totalamt);
                                }

                                }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                    private void getsaving(){

                        personalref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()){
                                    int budget;
                                    if (snapshot.hasChild("budget")){
                                        budget=Integer.parseInt(snapshot.child("budget").getValue().toString());
                                    }
                                    else{
                                        budget=0;
                                    }
                                    int monthspend;
                                    if (snapshot.hasChild("month")){
                                        monthspend=Integer.parseInt(Objects.requireNonNull(snapshot.child("month").getValue().toString()));
                                    }
                                    else{
                                        monthspend=0;
                                    }
                                    int saving=budget-monthspend;
                                    savingtv.setText("Rs : "+saving);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(Dashboard.this, "Error while loading ", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.account){
            Intent intent =new Intent(Dashboard.this,MyProfile.class);
            startActivity(intent);
        }
        if (item.getItemId()==R.id.sms){
            Intent intent =new Intent(Dashboard.this,ReadSMS.class);
            startActivity(intent);
        }
        if (item.getItemId()==R.id.feedback){
            Intent intent =new Intent(Dashboard.this,Feedback.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}