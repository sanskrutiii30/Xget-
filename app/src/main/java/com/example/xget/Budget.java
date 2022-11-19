package com.example.xget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import java.security.PrivateKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class Budget extends AppCompatActivity {

    private TextView totalbudgetamt;
    private RecyclerView recyclerView;
    private Toolbar toolbar;


    private FloatingActionButton fab;

    private DatabaseReference budgetref,personalref;
    private FirebaseAuth mauth;
    private ProgressDialog loader;

    private String post_key="";
    private String item="";
    private int amount;
    private String note="";

    private TextView mitem;
    private EditText mnotes,mamount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Set Budget");


        mauth=FirebaseAuth.getInstance();
        budgetref= FirebaseDatabase.getInstance().getReference().child("budget").child(mauth.getCurrentUser().getUid());
        personalref=FirebaseDatabase.getInstance().getReference("personal").child(mauth.getCurrentUser().getUid());
        loader= new ProgressDialog(this);

        fab=findViewById(R.id.fab);
        totalbudgetamt=findViewById(R.id.totalbudgetamt);
        recyclerView=findViewById(R.id.recycleview);

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        budgetref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalamount=0;
                for (DataSnapshot snap : snapshot.getChildren()){
                    Data data=snap.getValue(Data.class);
                    totalamount += data.getAmount();
                    String stotal=String.valueOf("Month Budget : Rs "+totalamount);
                    totalbudgetamt.setText(stotal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                additem();

            }
        });

        budgetref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    int totalamt=0;
                    for (DataSnapshot snap : snapshot.getChildren()){
                        Data data= snap.getValue(Data.class);
                        totalamt+=data.getAmount();
                        String stotal=String.valueOf("Month Budget : "+totalamt);
                        totalbudgetamt.setText(stotal);
                    }

                    int weeklybudget = totalamt/4;
                    int dailybudget = totalamt/30;
                    personalref.child("budget").setValue(totalamt   );
                    personalref.child("weeklybudget").setValue(weeklybudget);
                    personalref.child("dailybudget").setValue(dailybudget);
                }
                else{
                    personalref.child("budget").setValue(0);
                    personalref.child("weeklybudget").setValue(0);
                    personalref.child("dailybudget").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getmonthtransportbudgetratio();
        getmonthfoodbudgetratio();
        getmonthhousebudgetratio();
        getmonthenterbudgetratio();
        getmonthcharbudgetratio();
        getmonthedubudgetratio();
        getmonthappbudgetratio();
        getmonthhelbudgetratio();
        getmonthpersonalbudgetratio();
        getmonthotherbudgetratio();
    }

    private void additem() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater= LayoutInflater.from(this);
        View myview= inflater.inflate(R.layout.input_layout,null);
        myDialog.setView(myview);


        final AlertDialog dialog =myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemspinner= myview.findViewById(R.id.itemspinner);
        final EditText  amount=myview.findViewById(R.id.amount);
        final Button cancel=myview.findViewById(R.id.cancel);
        final Button save = myview.findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String  budgetamt= amount.getText().toString();
                String budgetitem=itemspinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(budgetamt)){
                    amount.setError("Amount is required");
                }
                if (budgetitem.equals("Select item")){
                    Toast.makeText(Budget.this, "Select a Valid item ", Toast.LENGTH_SHORT).show();
                }
                else{
                    loader.setMessage("Adding a budget item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String  id =budgetref.push().getKey();

                    DateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal= Calendar.getInstance();
                    String date=dateFormat.format(cal.getTime());

                    MutableDateTime epoch= new MutableDateTime();
                    epoch.setDate(0);

                    DateTime now= new DateTime();

                    Weeks weeks=Weeks.weeksBetween(epoch,now);
                    Months months= Months.monthsBetween(epoch,now);

                    String itemday=budgetitem+date;
                    String itemweek=budgetitem+weeks.getWeeks() ;
                    String itemmonth=budgetitem+months.getMonths();


                    Data data=new Data(budgetitem,date,id,itemday,itemweek,itemmonth,null,Integer.parseInt(budgetamt),months.getMonths(),weeks.getWeeks());


                    budgetref.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Budget.this, "Budget item added Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(Budget.this, "Error while adding item", Toast.LENGTH_SHORT).show();
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
                            Intent intent = new Intent(Budget.this,Dashboard.class);
                            startActivity(intent);
                            finish();
                        }
                    });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions <Data> options= new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(budgetref,Data.class)
                .build();

                FirebaseRecyclerAdapter<Data,MyViewholder> adapter= new FirebaseRecyclerAdapter<Data,MyViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MyViewholder holder, int position, @NonNull Data model) {

                        holder.setitemamt("Allocated amount : Rs "+model.getAmount());
                        holder.setdate("On : "+model.getDate());
                        holder.setitemname("BudgetItem : "+ model.getItem());
//                        holder.setNotes("Notes : " + model.getNotes());

                        holder.notes.setVisibility(View.GONE);

                            switch (model.getItem()){
                                case "Transport":
                                    holder.imageView.setImageResource(R.drawable.ic_transport);
                                    break;
                                case "Food":
                                    holder.imageView.setImageResource(R.drawable.ic_food);
                                    break;
                                case "House":
                                    holder.imageView.setImageResource(R.drawable.ic_house);
                                    break;
                                case "Entertainment":
                                    holder.imageView.setImageResource(R.drawable.ic_entertainment);
                                    break;
                                case "Charity":
                                    holder.imageView.setImageResource(R.drawable.ic_consultancy);
                                    break;
                                case "Education":
                                    holder.imageView.setImageResource(R.drawable.ic_education);
                                    break;
                                case "Apparel":
                                    holder.imageView.setImageResource(R.drawable.ic_shirt);
                                    break;
                                case "Health":
                                    holder.imageView.setImageResource(R.drawable.ic_health);
                                    break;
                                case "Personal":
                                    holder.imageView.setImageResource(R.drawable.ic_personalcare);
                                    break;
                                case "Other":
                                    holder.imageView.setImageResource(R.drawable.ic_other);
                                    break;
                            }

                            holder.mview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    post_key=getRef(holder.getAdapterPosition()).getKey();

                                    item=model.getItem();
                                    amount=model.getAmount();
                                    updateData();
                                }
                            });

                        }

                        @NonNull
                        @Override
                        public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout,parent,false);

                            return new MyViewholder(view);
                    }
                };

                        recyclerView.setAdapter(adapter);
                        adapter.startListening();

                    }

                public class MyViewholder extends RecyclerView.ViewHolder{
                    View mview;
                    public ImageView imageView;
                    public TextView notes;

                    public MyViewholder(@NonNull View itemView) {
                        super(itemView);
                        mview=itemView;
                        imageView=itemView.findViewById(R.id.imageview);
                        notes=itemView.findViewById(R.id.note);
                    }
                    public void setitemname(String itemname){
                        TextView item= mview.findViewById(R.id.item);
                        item.setText(itemname);
                    }
                    public void setitemamt(String itemamt){
                        TextView amount=mview.findViewById(R.id.rs);
                        amount.setText(itemamt);
                    }
                    public void setdate(String itemdate){
                        TextView date=mview.findViewById(R.id.date);
                        date.setText(itemdate);
                    }
//                    public void setNotes(String itemnote){
//                        TextView note=mview.findViewById(R.id.note);
//                        note.setText(itemnote);
//                    }

                }

                    public void updateData(){

                        AlertDialog.Builder mydialogs=new AlertDialog.Builder(this);
                        LayoutInflater inflater= LayoutInflater.from(this);
                        View mview= inflater.inflate(R.layout.update_layout,null);

                        mydialogs.setView(mview);
                         final AlertDialog dialog= mydialogs.create();
                         mitem= mview.findViewById(R.id.itemname);
                         mamount=mview.findViewById(R.id.amt);
                         mnotes=mview.findViewById(R.id.note);

                        mitem.setVisibility(View.GONE);
                        mitem.setText(item);

                        mnotes.setText(note);
                        mnotes.setSelection(note.length());

                        mamount.setText(String.valueOf(amount));
                        mamount.setSelection(String.valueOf(amount).length());

                        Button deletebtn= mview.findViewById(R.id.deletebtn);
                        Button updatebtn=mview.findViewById(R.id.updatebtn);

                        updatebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                amount= Integer.parseInt(mamount.getText().toString());
                                note=mnotes.getText().toString();


                                DateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
                                Calendar cal= Calendar.getInstance();
                                String date=dateFormat.format(cal.getTime());

                                MutableDateTime epoch= new MutableDateTime();
                                epoch.setDate(0);

                                DateTime now= new DateTime();

                                Weeks weeks=Weeks.weeksBetween(epoch,now);
                                Months months= Months.monthsBetween(epoch,now);

                                String itemday=item+date;
                                String itemweek=item+weeks.getWeeks() ;
                                String itemmonth=item+months.getMonths();


                                Data data=new Data(item,date,post_key,itemday,itemweek,itemmonth,null,amount,months.getMonths(),weeks.getWeeks());


                                budgetref.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(Budget.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(Budget.this, "Error while updating", Toast.LENGTH_SHORT).show();
                                        }
                                        loader.dismiss();
                                    }
                                });

                                dialog.dismiss();
                            }
                        });

                                deletebtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        budgetref.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                dialog.dismiss();
                                                if (task.isSuccessful()){
                                                    Toast.makeText(Budget.this, "Deleted item successfully", Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    Toast.makeText(Budget.this, "Error while deleting item ", Toast.LENGTH_SHORT).show();
                                                }
                                                loader.dismiss();
                                            }
                                        });
                                    }
                                });
                        dialog.show();
                    }

                    private  void  getmonthtransportbudgetratio(){

                        Query  query = budgetref.orderByChild("item").equalTo("Transport");
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()){

                                    int totalamt=0;

                                    for (DataSnapshot ds : snapshot.getChildren()){
                                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                        Object total = map.get("amount");
                                        int ptotal=Integer.parseInt(String.valueOf(total));
                                        totalamt=ptotal;
                                    }
                                    int daytransratio = totalamt/30;
                                    int weektransratio= totalamt/4;
                                    int monthtransratio = totalamt;

                                    personalref.child("daytransratio").setValue(daytransratio);
                                    personalref.child("weektransratio").setValue(weektransratio);
                                    personalref.child("monthtransratio").setValue(monthtransratio);
                                }
                                else{
                                    personalref.child("daytransratio").setValue(0);
                                    personalref.child("weektransratio").setValue(0);
                                    personalref.child("monthtransratio").setValue(0);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                    private void  getmonthfoodbudgetratio(){
                        Query  query = budgetref.orderByChild("item").equalTo("Transport");
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()) {

                                    int totalamt = 0;

                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                        Object total = map.get("amount");
                                        int ptotal = Integer.parseInt(String.valueOf(total));
                                        totalamt = ptotal;
                                    }
                                    int dayfoodratio = totalamt / 30;
                                    int weekfoodratio = totalamt / 4;
                                    int monthfoodratio = totalamt;

                                    personalref.child("dayfoodratio").setValue(dayfoodratio);
                                    personalref.child("weekfoodratio").setValue(weekfoodratio);
                                    personalref.child("monthfoodratio").setValue(monthfoodratio);
                                }
                                else{
                                    personalref.child("dayfoodratio").setValue(0);
                                    personalref.child("weekfoodratio").setValue(0);
                                    personalref.child("monthfoodratio").setValue(0);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                    private void  getmonthhousebudgetratio(){
                        Query  query = budgetref.orderByChild("item").equalTo("House");
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()) {

                                    int totalamt = 0;

                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                        Object total = map.get("amount");
                                        int ptotal = Integer.parseInt(String.valueOf(total));
                                        totalamt = ptotal;
                                    }
                                    int dayhouseratio = totalamt/30;
                                    int weekhouseratio= totalamt/4;
                                    int monthhouseratio = totalamt;

                                    personalref.child("dayhouseratio").setValue(dayhouseratio);
                                    personalref.child("weekhouseratio").setValue(weekhouseratio);
                                    personalref.child("monthhouseratio").setValue(monthhouseratio);
                                }
                                else{
                                    personalref.child("dayhouseratio").setValue(0);
                                    personalref.child("weekhouseratio").setValue(0);
                                    personalref.child("monthhouseratio").setValue(0);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                    }
                    private void  getmonthenterbudgetratio(){

                        Query  query = budgetref.orderByChild("item").equalTo("Entertainment");
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {

                                    int totalamt = 0;

                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                        Object total = map.get("amount");
                                        int ptotal = Integer.parseInt(String.valueOf(total));
                                        totalamt = ptotal;
                                    }
                                    int dayenterratio = totalamt/30;
                                    int weekenterratio= totalamt/4;
                                    int monthenterratio = totalamt;

                                    personalref.child("dayenterratio").setValue(dayenterratio);
                                    personalref.child("weekenterratio").setValue(weekenterratio);
                                    personalref.child("monthenterratio").setValue(monthenterratio);
                                }
                                else{
                                    personalref.child("dayenterratio").setValue(0);
                                    personalref.child("weekenterratio").setValue(0);
                                    personalref.child("monthenterratio").setValue(0);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                    private void  getmonthcharbudgetratio(){

                        Query  query = budgetref.orderByChild("item").equalTo("Charity");
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){

                                    int totalamt=0;

                                    for (DataSnapshot ds : snapshot.getChildren()){
                                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                        Object total = map.get("amount");
                                        int ptotal=Integer.parseInt(String.valueOf(total));
                                        totalamt=ptotal;
                                    }
                                    int daycharratio = totalamt/30;
                                    int weekcharratio= totalamt/4;
                                    int monthcharratio = totalamt;

                                    personalref.child("daycharratio").setValue(daycharratio);
                                    personalref.child("weekcharratio").setValue(weekcharratio);
                                    personalref.child("monthcharratio").setValue(monthcharratio);
                                }else {
                                    personalref.child("daycharratio").setValue(0);
                                    personalref.child("weekcharratio").setValue(0);
                                    personalref.child("monthcharratio").setValue(0);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    private void  getmonthedubudgetratio(){
                        Query  query = budgetref.orderByChild("item").equalTo("Education");
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){

                                    int totalamt=0;

                                    for (DataSnapshot ds : snapshot.getChildren()){
                                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                        Object total = map.get("amount");
                                        int ptotal=Integer.parseInt(String.valueOf(total));
                                        totalamt=ptotal;
                                    }
                                    int dayeduratio = totalamt/30;
                                    int weekeduratio= totalamt/4;
                                    int montheduratio = totalamt;

                                    personalref.child("dayeduratio").setValue(dayeduratio);
                                    personalref.child("weekeduratio").setValue(weekeduratio);
                                    personalref.child("montheduratio").setValue(montheduratio);
                                }
                                else{
                                    personalref.child("dayeduratio").setValue(0);
                                    personalref.child("weekeduratio").setValue(0);
                                    personalref.child("montheduratio").setValue(0);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    private void getmonthappbudgetratio(){
                        Query  query = budgetref.orderByChild("item").equalTo("Apparel");
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){

                                    int totalamt=0;

                                    for (DataSnapshot ds : snapshot.getChildren()){
                                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                        Object total = map.get("amount");
                                        int ptotal=Integer.parseInt(String.valueOf(total));
                                        totalamt=ptotal;
                                    }
                                    int dayappratio = totalamt/30;
                                    int weekappratio= totalamt/4;
                                    int monthappratio = totalamt;

                                    personalref.child("dayappratio").setValue(dayappratio);
                                    personalref.child("weekappratio").setValue(weekappratio);
                                    personalref.child("monthappratio").setValue(monthappratio);
                                }
                                else{
                                    personalref.child("dayappratio").setValue(0);
                                    personalref.child("weekappratio").setValue(0);
                                    personalref.child("monthappratio").setValue(0);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    private void  getmonthhelbudgetratio(){

                        Query  query = budgetref.orderByChild("item").equalTo("Health");
                        query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){

                            int totalamt=0;

                            for (DataSnapshot ds : snapshot.getChildren()){
                                Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                Object total = map.get("amount");
                                int ptotal=Integer.parseInt(String.valueOf(total));
                                totalamt=ptotal;
                            }
                            int dayhealratio = totalamt/30;
                            int weekhealratio= totalamt/4;
                            int monthhealratio = totalamt;

                            personalref.child("dayhealratio").setValue(dayhealratio);
                            personalref.child("weekhealratio").setValue(weekhealratio);
                            personalref.child("monthhealratio").setValue(monthhealratio);
                        }else {
                            personalref.child("dayhealratio").setValue(0);
                            personalref.child("weekhealratio").setValue(0);
                            personalref.child("monthhealratio").setValue(0);
                        }

                    }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
                    private void getmonthpersonalbudgetratio(){
                        Query  query = budgetref.orderByChild("item").equalTo("Personal");
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()){

                                    int totalamt=0;

                                    for (DataSnapshot ds : snapshot.getChildren()){
                                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                        Object total = map.get("amount");
                                        int ptotal=Integer.parseInt(String.valueOf(total));
                                        totalamt=ptotal;
                                    }
                                    int daypersonalratio = totalamt/30;
                                    int weekpersonalratio= totalamt/4;
                                    int monthpersonalratio = totalamt;

                                    personalref.child("daypersonalratio").setValue(daypersonalratio);
                                    personalref.child("weekpersonalratio").setValue(weekpersonalratio);
                                    personalref.child("monthpersonalratio").setValue(monthpersonalratio);
                                }
                                else{

                                    personalref.child("daypersonalratio").setValue(0);
                                    personalref.child("weekpersonalratio").setValue(0);
                                    personalref.child("monthpersonalratio").setValue(0);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    private void getmonthotherbudgetratio(){
                        Query  query = budgetref.orderByChild("item").equalTo("Other");
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()){

                                    int totalamt=0;

                                    for (DataSnapshot ds : snapshot.getChildren()){
                                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                        Object total = map.get("amount");
                                        int ptotal=Integer.parseInt(String.valueOf(total));
                                        totalamt=ptotal;
                                    }
                                    int dayotherratio = totalamt/30;
                                    int weekotherratio= totalamt/4;
                                    int monthotherratio = totalamt;

                                    personalref.child("dayotherratio").setValue(dayotherratio);
                                    personalref.child("weekotherratio").setValue(weekotherratio);
                                    personalref.child("monthotherratio").setValue(monthotherratio);
                                }
                                else{
                                    personalref.child("dayotherratio").setValue(0);
                                    personalref.child("weekotherratio").setValue(0);
                                    personalref.child("monthotherratio").setValue(0);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

            }