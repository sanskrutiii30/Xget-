package com.example.xget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class DailyAnalytics extends AppCompatActivity {

    private Toolbar setingtolbar;

    private FirebaseAuth mauth;
    private String onlineuserid="";
    private DatabaseReference expensesref, personalref;

    private TextView totalbudgtamt, anltrasnamt,anlfoodamt,anlhouseamt,analenteramt,anlchramt,anleduamt,anlappamt,anlhealamt,anlprsnlamt,anlothamt,dailyspentamt,monthspentamt;

    private RelativeLayout relativetrans, relativefod,relativehouse,relativeenter,relativechr,relativeedu,relativeapp,relativeheal,relativeprsnl,relativeoth,relativeanlaysis;

    private AnyChartView anyChartView;

    private ImageView transp_stat,fod_stat,house_stat,enter_stat,chr_state,edu_stat,app_stat,heal_stat,prsnl_stat,oth_stat,dailyrationspend_img,monthrationspending_img;

    private TextView  prog_trans,prog_food,prog_hou,prog_enter,prog_chr,prog_edu,prog_app,prog_heal,prog_prsnl,prog_oth,dailyrationspend,monthrationspending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_analytics);
//
        setingtolbar=findViewById(R.id.toolbar1);
        setSupportActionBar(setingtolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Today Analaysis");

        mauth=FirebaseAuth.getInstance();
        onlineuserid=mauth.getCurrentUser().getUid();
        expensesref= FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        personalref=FirebaseDatabase.getInstance().getReference("personal").child(onlineuserid);

        totalbudgtamt=findViewById(R.id.totalbudamtspendon);

        //anychart
        anyChartView=findViewById(R.id.anychart);

        //imageview
        transp_stat=findViewById(R.id.trasnport_status);
        fod_stat=findViewById(R.id.food_status);
        house_stat=findViewById(R.id.house_status);
        enter_stat=findViewById(R.id.entertain_status);
        chr_state=findViewById(R.id.charity_status);
        edu_stat=findViewById(R.id.edu_status);
        app_stat=findViewById(R.id.app_status);
        heal_stat=findViewById(R.id.heal_status);
        prsnl_stat=findViewById(R.id.per_status);
        oth_stat=findViewById(R.id.oth_status);
        dailyrationspend_img=findViewById(R.id.monthratiospending_img);


        //textview
        anltrasnamt=findViewById(R.id.analytictrasnportamt  );
        anlfoodamt=findViewById(R.id.analyticfoodamt);
        anlhouseamt=findViewById(R.id.analytichouseamt);
        analenteramt=findViewById(R.id.analyticentertainamt);
        anlchramt=findViewById(R.id.analyticcharityamt);
        anleduamt=findViewById(R.id.analyticeduamt);
        anlappamt=findViewById(R.id.analyticappamt);
        anlhealamt=findViewById(R.id.analytichealamt);
        anlprsnlamt=findViewById(R.id.analyticperamt);
        anlothamt=findViewById(R.id.analyticothamt);
        dailyspentamt=findViewById(R.id.monthspentamt);


        //relativelayout
        relativetrans=findViewById(R.id.relativelaytransport);
        relativefod=findViewById(R.id.relativelayfood2);
        relativehouse=findViewById(R.id.relativelayhouse3);
        relativeenter=findViewById(R.id.relativelayentertanmt4);
        relativechr=findViewById(R.id.relativelaychrity5);
        relativeedu=findViewById(R.id.relativelayedu6);
        relativeapp=findViewById(R.id.relativelayapp7);
        relativeheal=findViewById(R.id.relativelayheal8);
        relativeprsnl=findViewById(R.id.relativelayper9);
        relativeoth=findViewById(R.id.relativelayoth10);
        relativeanlaysis=findViewById(R.id.linearlytanalysis);

        //textview
        prog_trans=findViewById(R.id.prog_trans);
        prog_food=findViewById(R.id.prog_fod);
        prog_hou=findViewById(R.id.prog_house);
        prog_enter=findViewById(R.id.prog_enter);
        prog_chr=findViewById(R.id.prog_char);
        prog_edu=findViewById(R.id.prog_edu);
        prog_app=findViewById(R.id.prog_app);
        prog_heal=findViewById(R.id.prog_heal);
        prog_prsnl=findViewById(R.id.prog_prsnl);
        prog_oth=findViewById(R.id.prog_oth);
        dailyrationspend=findViewById(R.id.monthratiospending);


        gettotalweektransexp();
        gettotalweekfoodexp();
        gettotalweekhouseexp();
        gettotalweekenterexp();
        gettotalweekcharexp();
        gettotalweekeduexp();
        gettotalweekappexp();
        gettotalweekhealexp();
        gettotalweekpersnlexp();
        gettotalweekothexp();
        gettotaldayspending();

//        new java.util.Timer().schedule(
//                () = {
//
//                },
//
//                2000
//        );
        loadGraph();
        setstatusandimageresource();

    }
    private void gettotalweektransexp(){
        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now= new DateTime();

        DateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal= Calendar.getInstance();
        String date=dateFormat.format(cal.getTime());
        String itemday="Transport"+date;

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemday").equalTo(itemday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int Totalamt=0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map=(Map<String, Object>)ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        Totalamt +=pTotal;
                        anltrasnamt.setText("Today's spending : Rs  "+Totalamt);
                    }
                    personalref.child("dayTrans").setValue(Totalamt);
                }
                else{
                    relativetrans.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void gettotalweekfoodexp(){
        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now= new DateTime();

        DateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal= Calendar.getInstance();
        String date=dateFormat.format(cal.getTime());
        String itemday="Food"+date;

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemday").equalTo(itemday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int Totalamt=0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map=(Map<String, Object>)ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        Totalamt +=pTotal;
                        anlfoodamt.setText("Today's spending : Rs  "+Totalamt);
                    }
                    personalref.child("dayFood").setValue(Totalamt);
                }
                else{
                    relativefod.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void gettotalweekhouseexp(){
        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now= new DateTime();

        DateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal= Calendar.getInstance();
        String date=dateFormat.format(cal.getTime());
        String itemday="House"+date;

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemday").equalTo(itemday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int Totalamt=0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map=(Map<String, Object>)ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        Totalamt +=pTotal;
                        anlhouseamt.setText("Today's spending : Rs  "+Totalamt);
                    }
                    personalref.child("dayHouse").setValue(Totalamt);
                }
                else{
                    relativehouse.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void gettotalweekenterexp(){
        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal= Calendar.getInstance();
        String date=dateFormat.format(cal.getTime());
        String itemday="Entertainment"+date;

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemday").equalTo(itemday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int Totalamt=0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map=(Map<String, Object>)ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        Totalamt +=pTotal;
                        analenteramt.setText("Today's spending : Rs  "+Totalamt);
                    }
                    personalref.child("dayEntertainment").setValue(Totalamt);
                }
                else{
                    relativeenter.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void gettotalweekcharexp(){
        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal= Calendar.getInstance();
        String date=dateFormat.format(cal.getTime());
        String itemday="Charity"+date;

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemday").equalTo(itemday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int Totalamt=0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map=(Map<String, Object>)ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        Totalamt +=pTotal;
                        anlchramt.setText("Today's spending : Rs  "+Totalamt);
                    }
                    personalref.child("dayCharity").setValue(Totalamt);
                }
                else{
                    relativechr.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void gettotalweekeduexp(){
        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal= Calendar.getInstance();
        String date=dateFormat.format(cal.getTime());
        String itemday="Education"+date;

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemday").equalTo(itemday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int Totalamt=0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map=(Map<String, Object>)ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        Totalamt +=pTotal;
                        anleduamt.setText("Today's spending : Rs  "+Totalamt);
                    }
                    personalref.child("dayEducation").setValue(Totalamt);
                }
                else{
                    relativeedu.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void gettotalweekappexp(){
        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal= Calendar.getInstance();
        String date=dateFormat.format(cal.getTime());
        String itemday="Apparel"+date;

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemday").equalTo(itemday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int Totalamt=0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map=(Map<String, Object>)ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        Totalamt +=pTotal;
                        anlappamt.setText("Today's spending : Rs  "+Totalamt);
                    }
                    personalref.child("dayApparel").setValue(Totalamt);
                }
                else{
                    relativeapp.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void gettotalweekhealexp(){
        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal= Calendar.getInstance();
        String date=dateFormat.format(cal.getTime());
        String itemday="Health"+date;

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemday").equalTo(itemday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int Totalamt=0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map=(Map<String, Object>)ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        Totalamt +=pTotal;
                        anlhealamt.setText("Today's spending : Rs  "+Totalamt);
                    }
                    personalref.child("dayHealth").setValue(Totalamt);
                }
                else{
                    relativeheal.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void gettotalweekpersnlexp(){
        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal= Calendar.getInstance();
        String date=dateFormat.format(cal.getTime());
        String itemday="Personal"+date;

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemday").equalTo(itemday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int Totalamt=0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map=(Map<String, Object>)ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        Totalamt +=pTotal;
                        anlprsnlamt.setText("Today's spending : Rs  "+Totalamt);
                    }
                    personalref.child("dayPersonal").setValue(Totalamt);
                }
                else{
                    relativeprsnl.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void gettotalweekothexp(){
        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal= Calendar.getInstance();
        String date=dateFormat.format(cal.getTime());
        String itemday="Other"+date;

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemday").equalTo(itemday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int Totalamt=0;
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String,Object> map=(Map<String, Object>)ds.getValue();
                        Object total=map.get("amount");
                        int pTotal=Integer.parseInt(String.valueOf(total));
                        Totalamt +=pTotal;
                        anlothamt.setText("Total Today's spending : Rs  "+Totalamt);
                    }
                    personalref.child("dayOther").setValue(Totalamt);
                }
                else{
                    relativeoth.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void gettotaldayspending(){

        DateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal= Calendar.getInstance();
        String date=dateFormat.format(cal.getTime());
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){

                    int Totalamt=0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        Totalamt += pTotal;
                    }
                    totalbudgtamt.setText("Total spent : Rs "+Totalamt);
                    dailyspentamt.setText("Total spent : Rs "+Totalamt);
                }
                else {
//                    dailyspentamt.setText("you've not spent today ");
                    Toast.makeText(DailyAnalytics.this, "You've not spent today", Toast.LENGTH_SHORT).show();
                    anyChartView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadGraph(){
        personalref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int transtotal;
                    if (snapshot.hasChild("dayTrans")){
                        transtotal=Integer.parseInt(snapshot.child("dayTrans").getValue().toString());
                    }else{
                        transtotal=0;
                    }
                    int foodtotal;
                    if (snapshot.hasChild("dayFood")){
                        foodtotal=Integer.parseInt(snapshot.child("dayFood").getValue().toString());
                    }else{
                        foodtotal=0;
                    }
                    int houseototal;
                    if (snapshot.hasChild("dayHouse")){
                        houseototal=Integer.parseInt(snapshot.child("dayHouse").getValue().toString());
                    }else{
                        houseototal=0;
                    }
                    int entertotal;
                    if (snapshot.hasChild("dayEntertainment")){
                        entertotal=Integer.parseInt(snapshot.child("dayEntertainment").getValue().toString());
                    }else{
                        entertotal=0;
                    }
                    int chartotal;
                    if (snapshot.hasChild("dayCharity")){
                        chartotal=Integer.parseInt(snapshot.child("dayCharity").getValue().toString());
                    }else{
                        chartotal=0;
                    }
                    int edutotal;
                    if (snapshot.hasChild("dayEducation")){
                        edutotal=Integer.parseInt(snapshot.child("dayEducation").getValue().toString());
                    }else{
                        edutotal=0;
                    }
                    int apptotal;
                    if (snapshot.hasChild("dayApparel")){
                        apptotal=Integer.parseInt(snapshot.child("dayApparel").getValue().toString());
                    }else{
                        apptotal=0;
                    }
                    int heltotal;
                    if (snapshot.hasChild("dayHealth")){
                        heltotal=Integer.parseInt(snapshot.child("dayHealth").getValue().toString());
                    }else{
                        heltotal=0;
                    }
                    int pertotal;
                    if (snapshot.hasChild("dayPersonal")){
                        pertotal=Integer.parseInt(snapshot.child("dayPersonal").getValue().toString());
                    }else{
                        pertotal=0;
                    }
                    int othtotal;
                    if (snapshot.hasChild("dayOther")){
                        othtotal=Integer.parseInt(snapshot.child("dayOther").getValue().toString());
                    }else{
                        othtotal=0;
                    }

                    Pie pie= AnyChart.pie() ;
                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Transport",transtotal));
                    data.add(new ValueDataEntry("Food",foodtotal));
                    data.add(new ValueDataEntry("House",houseototal));
                    data.add(new ValueDataEntry("Entertainment",entertotal));
                    data.add(new ValueDataEntry("Charity",chartotal));
                    data.add(new ValueDataEntry("Education",edutotal));
                    data.add(new ValueDataEntry("Apparel",apptotal));
                    data.add(new ValueDataEntry("Health",heltotal));
                    data.add(new ValueDataEntry("Personal",pertotal));
                    data.add(new ValueDataEntry("Other",othtotal));

                    pie.data(data);
                    pie.title("Daily Analytics");
                    pie.labels().position("outside");
                    pie.legend().title().enabled(true);
                    pie.legend().title()
                            .text("Items spent on ")
                            .padding(0d,0d,10d,0d);
                    pie.legend()
                            .position("center-bottom")
                            .itemsLayout(LegendLayout.HORIZONTAL)
                            .align(Align.CENTER);

                    anyChartView.setChart(pie);
                }else{
                    
                    Toast.makeText(DailyAnalytics.this, "Child doesn't exists", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setstatusandimageresource(){
        personalref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {if (snapshot.exists()) {
                int transtotal;
                if (snapshot.hasChild("dayTrans")) {
                    transtotal = Integer.parseInt(snapshot.child("dayTrans").getValue().toString());
                } else {
                    transtotal = 0;
                }
                int foodtotal;
                if (snapshot.hasChild("dayFood")) {
                    foodtotal = Integer.parseInt(snapshot.child("dayFood").getValue().toString());
                } else {
                    foodtotal = 0;
                }
                int houseototal;
                if (snapshot.hasChild("dayHouse")) {
                    houseototal = Integer.parseInt(snapshot.child("dayHouse").getValue().toString());
                } else {
                    houseototal = 0;
                }
                int entertotal;
                if (snapshot.hasChild("dayEntertainment")) {
                    entertotal = Integer.parseInt(snapshot.child("dayEntertainment").getValue().toString());
                } else {
                    entertotal = 0;
                }
                int chartotal;
                if (snapshot.hasChild("dayCharity")) {
                    chartotal = Integer.parseInt(snapshot.child("dayCharity").getValue().toString());
                } else {
                    chartotal = 0;
                }
                int edutotal;
                if (snapshot.hasChild("dayEducation")) {
                    edutotal = Integer.parseInt(snapshot.child("dayEducation").getValue().toString());
                } else {
                    edutotal = 0;
                }
                int apptotal;
                if (snapshot.hasChild("dayApparel")) {
                    apptotal = Integer.parseInt(snapshot.child("dayApparel").getValue().toString());
                } else {
                    apptotal = 0;
                }
                int heltotal;
                if (snapshot.hasChild("dayHealth")) {
                    heltotal = Integer.parseInt(snapshot.child("dayHealth").getValue().toString());
                } else {
                    heltotal = 0;
                }
                int pertotal;
                if (snapshot.hasChild("dayPersonal")) {
                    pertotal = Integer.parseInt(snapshot.child("dayPersonal").getValue().toString());
                } else {
                    pertotal = 0;
                }
                int othtotal;
                if (snapshot.hasChild("dayOther")) {
                    othtotal = Integer.parseInt(snapshot.child("dayOther").getValue().toString());
                } else {
                    othtotal = 0;
                }

                float monthtotalspentamt;
                if (snapshot.hasChild("today")){
                    monthtotalspentamt=Integer.parseInt(snapshot.child("today").getValue().toString());
                }else{
                    monthtotalspentamt=0;
                }

                //ratios

                float traratio;
                if (snapshot.hasChild("daytransratio")){
                    traratio=Integer.parseInt(snapshot.child("daytransratio").getValue().toString());
                }else{
                    traratio=0;
                }
                float fodratio;
                if (snapshot.hasChild("dayfoodratio")){
                    fodratio=Integer.parseInt(snapshot.child("dayfoodratio").getValue().toString());
                }else{
                    fodratio=0;
                }
                float houratio;
                if (snapshot.hasChild("dayhouseratio")){
                    houratio=Integer.parseInt(snapshot.child("dayhouseratio").getValue().toString());
                }else{
                    houratio=0;
                }
                float enterratio;
                if (snapshot.hasChild("dayenterratio")){
                    enterratio=Integer.parseInt(snapshot.child("dayenterratio").getValue().toString());
                }else{
                    enterratio=0;
                }
                float charratio;
                if (snapshot.hasChild("daycharratio")){
                    charratio=Integer.parseInt(snapshot.child("daycharratio").getValue().toString());
                }else{
                    charratio=0;
                }
                float eduratio;
                if (snapshot.hasChild("dayeduratio")){
                    eduratio=Integer.parseInt(snapshot.child("dayeduratio").getValue().toString());
                }else{
                    eduratio=0;
                }
                float appratio;
                if (snapshot.hasChild("dayappratio")){
                    appratio=Integer.parseInt(snapshot.child("dayappratio").getValue().toString());
                }else{
                    appratio=0;
                }

                float helratio;
                if (snapshot.hasChild("dayhealratio")){
                    helratio=Integer.parseInt(snapshot.child("dayhealratio").getValue().toString());
                }else{
                    helratio=0;
                }
                float perratio;
                if (snapshot.hasChild("daypersonalratio")){
                    perratio=Integer.parseInt(snapshot.child("daypersonalratio").getValue().toString());
                }else{
                    perratio=0;
                }
                float othratio;
                if (snapshot.hasChild("dayotherratio")){
                    othratio=Integer.parseInt(snapshot.child("dayotherratio").getValue().toString());
                }else{
                    othratio=0;
                }
                float monthtotalspentamtratio;
                if (snapshot.hasChild("dailybudget")){
                    monthtotalspentamtratio=Integer.parseInt(snapshot.child("dailybudget").getValue().toString());
                }
                else{
                    monthtotalspentamtratio=0;
                }

                float monthpercent=( monthtotalspentamt/monthtotalspentamtratio)/100;
                if (monthpercent<50){
                   dailyrationspend.setText(monthpercent+"%"+ "used of "+monthtotalspentamtratio+"   Status  ");
                    dailyrationspend_img.setImageResource(R.drawable.green);
                }else if (monthpercent>=50 && monthpercent<100){
                    dailyrationspend.setText(monthpercent+"%"+ "used of "+monthtotalspentamtratio+"   Status  ");
                    dailyrationspend_img.setImageResource(R.drawable.brown);
                }else {
                    dailyrationspend.setText(monthpercent+"%"+ "used of "+monthtotalspentamtratio+"   Status  ");
                    dailyrationspend_img.setImageResource(R.drawable.red);
                }

                float Transpercent=(transtotal/traratio)*100;
                if (Transpercent<50){
                    prog_trans.setText(Transpercent+"%"+"used of "+traratio+" Status ");
                    transp_stat.setImageResource(R.drawable.green);
                }else if (Transpercent>=50 && Transpercent<100){
                    prog_trans.setText(Transpercent+"%"+"used of "+traratio+" Status ");
                    transp_stat.setImageResource(R.drawable.brown);
                }else{
                    prog_trans.setText(Transpercent+"%"+"used of "+traratio+" Status ");
                    transp_stat.setImageResource(R.drawable.red);
                }

                float foodpercent=(foodtotal/fodratio)*100;

                if (foodpercent<50){
                    prog_food.setText(foodtotal+"%"+"used of "+fodratio+" Status ");
                    fod_stat.setImageResource(R.drawable.green);
                }else if (foodpercent>=50 && foodpercent<100){
                    prog_food.setText(foodtotal+"%"+"used of "+fodratio+" Status ");
                    fod_stat.setImageResource(R.drawable.brown);
                }else{
                    prog_food.setText(foodtotal+"%"+"used of "+fodratio+" Status ");
                    fod_stat.setImageResource(R.drawable.red);
                }


                float housepercent=(houseototal/houratio)*100;

                if (housepercent<50){
                    prog_hou.setText(houseototal+"%"+"used of "+houratio+" Status ");
                    house_stat.setImageResource(R.drawable.green);
                }else if (housepercent>=50 && housepercent<100){
                    prog_hou.setText(houseototal+"%"+"used of "+houratio+" Status ");
                    house_stat.setImageResource(R.drawable.brown);
                }else{
                    prog_hou.setText(houseototal+"%"+"used of "+houratio+" Status ");
                    house_stat.setImageResource(R.drawable.red);
                }

                float enterpecent=(entertotal/enterratio)*100;

                if (enterpecent<50){
                    prog_enter.setText(entertotal+"%"+"used of "+enterratio+" Status ");
                    enter_stat.setImageResource(R.drawable.green);
                }else if (enterpecent>=50 && enterpecent<100){
                    prog_enter.setText(entertotal+"%"+"used of "+enterratio+" Status ");
                    enter_stat.setImageResource(R.drawable.brown);
                }else{
                    prog_enter.setText(entertotal+"%"+"used of "+enterratio+" Status ");
                    enter_stat.setImageResource(R.drawable.red);
                }


                float charpercent=(chartotal/charratio)*100;

                if (charpercent<50){
                    prog_chr.setText(chartotal+"%"+"used of "+charratio+" Status ");
                    chr_state.setImageResource(R.drawable.green);
                }else if (enterpecent>=50 && enterpecent<100){
                    prog_chr.setText(chartotal+"%"+"used of "+charratio+" Status ");
                    chr_state.setImageResource(R.drawable.brown);
                }else{
                    prog_chr.setText(chartotal+"%"+"used of "+charratio+" Status ");
                    chr_state.setImageResource(R.drawable.red);
                }


                float edupercent=(edutotal/eduratio)*100;

                if (edupercent<50){
                    prog_edu.setText(edutotal+"%"+"used of "+eduratio+" Status ");
                    edu_stat.setImageResource(R.drawable.green);
                }else if (edupercent>=50 && edupercent<100){
                    prog_edu.setText(edutotal+"%"+"used of "+eduratio+" Status ");
                    edu_stat.setImageResource(R.drawable.brown);
                }else{
                    prog_edu.setText(edutotal+"%"+"used of "+eduratio+" Status ");
                    edu_stat.setImageResource(R.drawable.red);
                }

                float apppercent=(apptotal/appratio)*100;

                if (apppercent<50){
                    prog_app.setText(apptotal+"%"+"used of "+appratio+" Status ");
                    app_stat.setImageResource(R.drawable.green);
                }else if (apppercent>=50 && apppercent<100){
                    prog_app.setText(apptotal+"%"+"used of "+appratio+" Status ");
                    app_stat.setImageResource(R.drawable.brown);
                }else{
                    prog_app.setText(apptotal+"%"+"used of "+appratio+" Status ");
                    app_stat.setImageResource(R.drawable.red);;
                }

                float helpercent=(heltotal/helratio)*100;

                if (helpercent<50){
                    prog_heal.setText(heltotal+"%"+"used of "+helratio+" Status ");
                    heal_stat.setImageResource(R.drawable.green);
                }else if (helpercent>=50 && helpercent<100){
                    prog_heal.setText(heltotal+"%"+"used of "+helratio+" Status ");
                    heal_stat.setImageResource(R.drawable.brown);
                }else{
                    prog_heal.setText(heltotal+"%"+"used of "+helratio+" Status ");
                    heal_stat.setImageResource(R.drawable.red);
                }


                float perpercent=(pertotal/perratio)*100;

                if (perpercent<50){
                    prog_prsnl.setText(pertotal+"%"+"used of "+perratio+" Status ");
                    prsnl_stat.setImageResource(R.drawable.green);
                }else if (perpercent>=50 && perpercent<100){
                    prog_prsnl.setText(pertotal+"%"+"used of "+perratio+" Status ");
                    prsnl_stat.setImageResource(R.drawable.brown);
                }else{
                    prog_prsnl.setText(pertotal+"%"+"used of "+perratio+" Status ");
                    prsnl_stat.setImageResource(R.drawable.red);
                }

                float othpercent=(othtotal/othratio)*100;

                if (othpercent<50){
                    prog_oth.setText(othtotal+"%"+"used of "+othratio+" Status ");
                    oth_stat.setImageResource(R.drawable.green);
                }else if (othpercent>=50 && othpercent<100){
                    prog_oth.setText(othtotal+"%"+"used of "+othratio+" Status ");
                    oth_stat.setImageResource(R.drawable.brown);
                }else{
                    prog_oth.setText(othtotal+"%"+"used of "+othratio+" Status ");
                    oth_stat.setImageResource(R.drawable.red);
                }

            }

            else{
                Toast.makeText(DailyAnalytics.this, "setting status and image error", Toast.LENGTH_SHORT).show();
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}