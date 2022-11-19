package com.example.xget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeekSpending extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView totalweekamt;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    private WeekspendingAdapter weekspendingAdapter;
    private List<Data>mydatalist;

    private FirebaseAuth mauth;
    private String  onlineuserid="";
    private DatabaseReference expensesRef;

    private String type="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_spending);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Week spents");

                    totalweekamt=findViewById(R.id.totalweekamt);
                    progressBar=findViewById(R.id.progressbar1);
                    recyclerView=findViewById(R.id.recycleview);

                    LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
                    linearLayoutManager.setStackFromEnd(true);
                    linearLayoutManager.setReverseLayout(true);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(linearLayoutManager);

                    mauth=FirebaseAuth.getInstance();
                    onlineuserid=mauth.getCurrentUser().getUid();


                    mydatalist=new ArrayList<>();
                    weekspendingAdapter=new WeekspendingAdapter(WeekSpending.this,mydatalist);
                    recyclerView.setAdapter(weekspendingAdapter);

                    if (getIntent().getExtras()!=null){
                        type=getIntent().getStringExtra("type");
                        if (type.equals("week")){
                            readweekspendingitem();
                        }
                        else if (type.equals("month")){
                            readmonthspendingitem();
                        }

                      }
                  }

                            private void readmonthspendingitem() {

                                toolbar=findViewById(R.id.toolbar);
                                setSupportActionBar(toolbar);
                                getSupportActionBar().setTitle("Month spents");


                                MutableDateTime epoch= new MutableDateTime();
                                epoch.setDate(0);

                                DateTime now= new DateTime();
                                Months months= Months.monthsBetween(epoch,now);

                                expensesRef= FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
                                Query query=expensesRef.orderByChild("month").equalTo(months.getMonths());
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        mydatalist.clear();
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            Data data = dataSnapshot.getValue(Data.class);
                                            mydatalist.add(data);
                                        }
                                        weekspendingAdapter.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);


                                        int Totalamt = 0;
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                            Object total = map.get("amount");
                                            int pTotal = Integer.parseInt(String.valueOf(total));
                                            Totalamt += pTotal;
                                            totalweekamt.setText("Total Months spending : Rs  " + Totalamt);
                                        }

                                        if (Totalamt==0){
                                            Toast.makeText(WeekSpending.this, "No budget items found ", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                          }
                                     });

                                }


        private void readweekspendingitem() {

            MutableDateTime epoch= new MutableDateTime();
            epoch.setDate(0);

            DateTime now= new DateTime();
            Weeks weeks= Weeks.weeksBetween(epoch,now);

            expensesRef= FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
            Query query=expensesRef.orderByChild("week").equalTo(weeks.getWeeks());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    mydatalist.clear();
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Data data =dataSnapshot.getValue(Data.class);
                        mydatalist.add(data);
                    }
                    weekspendingAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);


                    int Totalamt=0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        Totalamt += pTotal;
                        totalweekamt.setText("Total Week spending : Rs  " + Totalamt);
                    }
                    if (Totalamt==0){
                        Toast.makeText(WeekSpending.this, "No budget items found ", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
}