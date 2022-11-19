package com.example.xget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class History extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private RecyclerView recyclerView;
    private TodayitemAdapter todayitemAdapter;
        private List<Data> mydatalist;

        private FirebaseAuth mauth;
        private String onlinuserid="";
        private DatabaseReference expensesref,personalref;


        private Toolbar setingtolbar;
            private Button search;
            private TextView historytotalamtspent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        setingtolbar=findViewById(R.id.toolbar);
        setSupportActionBar(setingtolbar);
        getSupportActionBar().setTitle("History");


        search=findViewById(R.id.search);
        historytotalamtspent=findViewById(R.id.historytotalamtspent);

        mauth=FirebaseAuth.getInstance();
        onlinuserid=mauth.getCurrentUser().getUid();

        recyclerView=findViewById(R.id.recyclerview_idfeed);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mydatalist= new ArrayList<>();
        todayitemAdapter = new TodayitemAdapter(History.this,mydatalist);
        recyclerView.setAdapter(todayitemAdapter);

        search.setOnClickListener((view)->{
//            Toast.makeText(this, "Adapter is set", Toast.LENGTH_SHORT).show();
            showdatepickerDialog();
        });
    }
    private void showdatepickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,

                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
            datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

        int months =month+1;
        String date =dayOfMonth + "-" + months + "-" +year;
        Toast.makeText(this, date , Toast.LENGTH_SHORT).show();

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("expenses").child(onlinuserid);
        Query query=databaseReference.orderByChild("date").equalTo(date);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                mydatalist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    mydatalist.add(data);
                }

                recyclerView.setVisibility(View.VISIBLE);
                todayitemAdapter.notifyDataSetChanged();

                int Totalamt = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    Totalamt += pTotal;

                    if (Totalamt>0)
                    {
                        historytotalamtspent.setVisibility(View.VISIBLE);
                        historytotalamtspent.setText("This day you spent : " + Totalamt);

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}