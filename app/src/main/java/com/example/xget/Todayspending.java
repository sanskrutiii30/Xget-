package com.example.xget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.FocusFinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class Todayspending extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView totalamtspendon;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    private ProgressDialog loader;

    private FirebaseAuth mauth;
    private String  onlineuserid="";
    private DatabaseReference expensesRef;

    private TodayitemAdapter todayitemAdapter;
    private List<Data> mydatalist;
    String gotAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todayspending);

        gotAmount = getIntent().getStringExtra("amount");
//
//        toolbar=findViewById(R.id.toolbar1);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Today's spending");


        totalamtspendon=findViewById(R.id.totalamtspendon);
        progressBar=findViewById(R.id.progressbar);
        recyclerView=findViewById(R.id.recycleviewT);


        fab=findViewById(R.id.fab);
        loader= new ProgressDialog(this);

        mauth=FirebaseAuth.getInstance();
        onlineuserid=mauth.getCurrentUser().getUid();
        expensesRef= FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        mydatalist=new ArrayList<>();
        todayitemAdapter= new TodayitemAdapter(Todayspending.this,mydatalist);
        recyclerView.setAdapter(todayitemAdapter);

        readitem();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                additemspendon();

            }
        });



    }

    private void readitem() {


        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query = databaseReference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                mydatalist.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Data data = snapshot1.getValue(Data.class);
                    mydatalist.add(data);
                }

                todayitemAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int Totalamt = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    Totalamt += pTotal;
                    totalamtspendon.setText("Total Today's spending : Rs  " + Totalamt);
                }
                if (Totalamt == 0) {
                    Toast.makeText(Todayspending.this, "No budget items found ", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void additemspendon() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(view);


        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemspinner = view.findViewById(R.id.itemspinner);
        final EditText amount = view.findViewById(R.id.amount);
        final Button cancel = view.findViewById(R.id.cancel);
        final EditText note = view.findViewById(R.id.note);
        final Button save = view.findViewById(R.id.save);

        note.setVisibility(View.VISIBLE);

        if (gotAmount!=null){
            amount.setText(gotAmount);
            amount.setEnabled(false);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String amt = amount.getText().toString();
                String item = itemspinner.getSelectedItem().toString();
                String notes = note.getText().toString();

                if (TextUtils.isEmpty(amt)) {
                    amount.setError("Amount is required");
                }

                if (TextUtils.isEmpty(notes)) {
                    note.setError("Note is required");
                    return;
                }
                if (item.equals("Select item")) {
                    Toast.makeText(Todayspending.this, "Select a Valid item ", Toast.LENGTH_SHORT).show();
                } else {
                    loader.setMessage("Adding a expense");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = expensesRef.push().getKey();

                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = dateFormat.format(cal.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);

                    DateTime now = new DateTime();
                    Weeks weeks = Weeks.weeksBetween(epoch, now);
                    Months months = Months.monthsBetween(epoch, now);


                    String itemday = item + date;
                    String itemweek = item + weeks.getWeeks();
                    String itemmonth = item + months.getMonths();


                    Data data = new Data(item, date, id, itemday, itemweek, itemmonth, notes, Integer.parseInt(amt), months.getMonths(), weeks.getWeeks());


                    expensesRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Todayspending.this, "Expenses item added Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Todayspending.this, "Error while adding item", Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });
                }
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Todayspending.this, Dashboard.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }
}