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
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeekAnalytics extends AppCompatActivity {

    private FirebaseAuth mauth;
    private String onlineuserid="";
    private DatabaseReference expensesref, personalref;

    private Toolbar toolbar;

    private TextView totalbudgtamt, anltrasnamt,anlfoodamt,anlhouseamt,analenteramt,anlchramt,anleduamt,anlappamt,anlhealamt,anlprsnlamt,anlothamt,monthspentamt;

    private RelativeLayout relativetrans, relativefod,relativehouse,relativeenter,relativechr,relativeedu,relativeapp,relativeheal,relativeprsnl,relativeoth,relativeanlaysis;

    private AnyChartView anyChartView;

    private ImageView transp_stat,fod_stat,house_stat,enter_stat,chr_state,edu_stat,app_stat,heal_stat,prsnl_stat,oth_stat,weekratiospend_img;

    private TextView  prog_trans,prog_food,prog_hou,prog_enter,prog_chr,prog_edu,prog_app,prog_heal,prog_prsnl,prog_oth,monthratiospending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_analytics);

        mauth = FirebaseAuth.getInstance();
        onlineuserid = mauth.getCurrentUser().getUid();
        expensesref = FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        personalref = FirebaseDatabase.getInstance().getReference("personal").child(onlineuserid);

        totalbudgtamt = findViewById(R.id.totalbudamtspendon);


        toolbar=findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Week Analytics");


        //anychart
        anyChartView = findViewById(R.id.anychart);

        //imageview
        transp_stat = findViewById(R.id.trasnport_status);
        fod_stat = findViewById(R.id.food_status);
        house_stat = findViewById(R.id.house_status);
        enter_stat = findViewById(R.id.entertain_status);
        chr_state = findViewById(R.id.charity_status);
        edu_stat = findViewById(R.id.edu_status);
        app_stat = findViewById(R.id.app_status);
        heal_stat = findViewById(R.id.heal_status);
        prsnl_stat = findViewById(R.id.per_status);
        oth_stat = findViewById(R.id.oth_status);
        weekratiospend_img = findViewById(R.id.monthratiospending_img);

        //textview
        anltrasnamt = findViewById(R.id.analytictrasnportamt);
        anlfoodamt = findViewById(R.id.analyticfoodamt);
        anlhouseamt = findViewById(R.id.analytichouseamt);
        analenteramt = findViewById(R.id.analyticentertainamt);
        anlchramt = findViewById(R.id.analyticcharityamt);
        anleduamt = findViewById(R.id.analyticeduamt);
        anlappamt = findViewById(R.id.analyticappamt);
        anlhealamt = findViewById(R.id.analytichealamt);
        anlprsnlamt = findViewById(R.id.analyticperamt);
        anlothamt = findViewById(R.id.analyticothamt);
        monthspentamt = findViewById(R.id.monthspentamt);

        //relativelayout
        relativetrans = findViewById(R.id.relativelaytransport);
        relativefod = findViewById(R.id.relativelayfood2);
        relativehouse = findViewById(R.id.relativelayhouse3);
        relativeenter = findViewById(R.id.relativelayentertanmt4);
        relativechr = findViewById(R.id.relativelaychrity5);
        relativeedu = findViewById(R.id.relativelayedu6);
        relativeapp = findViewById(R.id.relativelayapp7);
        relativeheal = findViewById(R.id.relativelayheal8);
        relativeprsnl = findViewById(R.id.relativelayper9);
        relativeoth = findViewById(R.id.relativelayoth10);
        relativeanlaysis = findViewById(R.id.linearlytanalysis);


        //textview
        prog_trans = findViewById(R.id.prog_trans);
        prog_food = findViewById(R.id.prog_fod);
        prog_hou = findViewById(R.id.prog_house);
        prog_enter = findViewById(R.id.prog_enter);
        prog_chr = findViewById(R.id.prog_char);
        prog_edu = findViewById(R.id.prog_edu);
        prog_app = findViewById(R.id.prog_app);
        prog_heal = findViewById(R.id.prog_heal);
        prog_prsnl = findViewById(R.id.prog_prsnl);
        prog_oth = findViewById(R.id.prog_oth);
        monthratiospending = findViewById(R.id.monthratiospending);

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
        gettotalweekspending();
        loadgraph();
        setstatusandimageresource();
    }

    private void gettotalweektransexp()  {
        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now= new DateTime();
        Weeks weeks= Weeks.weeksBetween(epoch,now);

        String itemweek ="Transport"+ weeks.getWeeks();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemweek").equalTo(itemweek);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int Totalamt = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        Totalamt += pTotal;
                        anltrasnamt.setText("spent :  " + Totalamt);
                    }
                    personalref.child("weekTrans").setValue(Totalamt);
                }
                else{
                    relativetrans.setVisibility(View.GONE);
                    personalref.child("weekTrans").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void gettotalweekfoodexp() {
        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now= new DateTime();

        Weeks weeks= Weeks.weeksBetween(epoch,now);
        String itemweek ="Food"+ weeks.getWeeks();

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemweek").equalTo(itemweek);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int Totalamt = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        Totalamt += pTotal;
                        anlfoodamt.setText("spent :  " + Totalamt);
                    }
                    personalref.child("weekFood").setValue(Totalamt);
                }
                else{
                    relativefod.setVisibility(View.GONE);
                    personalref.child("weekFood").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void gettotalweekhouseexp() {

        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now= new DateTime();

        Weeks weeks= Weeks.weeksBetween(epoch,now);
        String itemweek ="House"+ weeks.getWeeks();

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemweek").equalTo(itemweek);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int Totalamt = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        Totalamt += pTotal;
                        anlhouseamt.setText("spent :  " + Totalamt);
                    }
                    personalref.child("weekHouse").setValue(Totalamt);
                }
                else{
                    relativehouse.setVisibility(View.GONE);
                    personalref.child("weekHouse").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void gettotalweekenterexp() {

        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now= new DateTime();

        Weeks weeks= Weeks.weeksBetween(epoch,now);
        String itemweek ="Entertainment"+ weeks.getWeeks();

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemweek").equalTo(itemweek);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int Totalamt = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        Totalamt += pTotal;
                        analenteramt.setText("spent :  " + Totalamt);
                    }
                    personalref.child("weekEntertainment").setValue(Totalamt);
                }
                else{
                    relativeenter.setVisibility(View.GONE);
                    personalref.child("weekEntertainment").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void gettotalweekcharexp() {

        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now= new DateTime();

        Weeks weeks= Weeks.weeksBetween(epoch,now);
        String itemweek ="Charity"+ weeks.getWeeks();

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemweek").equalTo(itemweek);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int Totalamt = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        Totalamt += pTotal;
                        anlchramt.setText("spent :  " + Totalamt);
                    }
                    personalref.child("weekCharity").setValue(Totalamt);
                }
                else{
                    relativechr.setVisibility(View.GONE);
                    personalref.child("weekCharity").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void gettotalweekeduexp() {


        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now= new DateTime();

        Weeks weeks= Weeks.weeksBetween(epoch,now);
        String itemweek ="Education"+ weeks.getWeeks();

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemweek").equalTo(itemweek);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int Totalamt = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        Totalamt += pTotal;
                        anleduamt.setText("spent :  " + Totalamt);
                    }
                    personalref.child("weekEducation").setValue(Totalamt);
                }
                else{
                    relativeedu.setVisibility(View.GONE);
                    personalref.child("weekEducation").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void gettotalweekappexp() {

        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now= new DateTime();

        Weeks weeks= Weeks.weeksBetween(epoch,now);
        String itemweek ="Apparel"+ weeks.getWeeks();

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemweek").equalTo(itemweek);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int Totalamt = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        Totalamt += pTotal;
                        anlappamt.setText("spent :  " + Totalamt);
                    }
                    personalref.child("weekApparel").setValue(Totalamt);
                }
                else{
                    relativeapp.setVisibility(View.GONE);
                    personalref.child("weekApparel").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void gettotalweekhealexp() {
        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now= new DateTime();

        Weeks weeks= Weeks.weeksBetween(epoch,now);
        String itemweek ="Health"+ weeks.getWeeks();

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemweek").equalTo(itemweek);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int Totalamt = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        Totalamt += pTotal;
                        anlhealamt.setText("spent :  " + Totalamt);
                    }
                    personalref.child("weekHealth").setValue(Totalamt);
                }
                else{
                    relativeheal.setVisibility(View.GONE);
                    personalref.child("weekHealth").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void gettotalweekpersnlexp() {

        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now= new DateTime();

        Weeks weeks= Weeks.weeksBetween(epoch,now);
        String itemweek ="Personal"+ weeks.getWeeks();

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemweek").equalTo(itemweek);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int Totalamt = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        Totalamt += pTotal;
                        anlprsnlamt.setText("spent :  " + Totalamt);
                    }
                    personalref.child("weekPersonal").setValue(Totalamt);
                }
                else{
                    relativeprsnl.setVisibility(View.GONE);
                    personalref.child("weekPersonal").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void gettotalweekothexp() {

        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now= new DateTime();

        Weeks weeks= Weeks.weeksBetween(epoch,now);
        String itemweek ="Other"+ weeks.getWeeks();

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("itemweek").equalTo(itemweek);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int Totalamt = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        Totalamt += pTotal;
                        anlothamt.setText("spent :  " + Totalamt);
                    }
                    personalref.child("weekOther").setValue(Totalamt);
                }
                else{
                    relativeoth.setVisibility(View.GONE);
                    personalref.child("weekOther").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void gettotalweekspending() {

        MutableDateTime epoch= new MutableDateTime();
        epoch.setDate(0);
        DateTime now= new DateTime();
        Weeks weeks= Weeks.weeksBetween(epoch,now);

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserid);
        Query query=databaseReference.orderByChild("week").equalTo(weeks.getWeeks());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0) {

                    int Totalamt = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        Totalamt += pTotal;
                    }
                    totalbudgtamt.setText("Week's spending "+Totalamt);
                    monthspentamt.setText("Total spent : "+ Totalamt);
                }else{
                    anyChartView.setVisibility(View.GONE);
                    Toast.makeText(WeekAnalytics.this, "You've not spent in this week ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void loadgraph() {

        personalref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int transtotal;
                    if (snapshot.hasChild("weekTrans")){
                        transtotal=Integer.parseInt(snapshot.child("weekTrans").getValue().toString());
                    }else{
                        transtotal=0;
                    }
                    int foodtotal;
                    if (snapshot.hasChild("weekFood")){
                        foodtotal=Integer.parseInt(snapshot.child("weekFood").getValue().toString());
                    }else{
                        foodtotal=0;
                    }
                    int houseototal;
                    if (snapshot.hasChild("weekHouse")){
                        houseototal=Integer.parseInt(snapshot.child("weekHouse").getValue().toString());
                    }else{
                        houseototal=0;
                    }
                    int entertotal;
                    if (snapshot.hasChild("weekEntertainment")){
                        entertotal=Integer.parseInt(snapshot.child("weekEntertainment").getValue().toString());
                    }else{
                        entertotal=0;
                    }
                    int chartotal;
                    if (snapshot.hasChild("weekCharity")){
                        chartotal=Integer.parseInt(snapshot.child("weekCharity").getValue().toString());
                    }else{
                        chartotal=0;
                    }
                    int edutotal;
                    if (snapshot.hasChild("weekEducation")){
                        edutotal=Integer.parseInt(snapshot.child("weekEducation").getValue().toString());
                    }else{
                        edutotal=0;
                    }
                    int apptotal;
                    if (snapshot.hasChild("weekApparel")){
                        apptotal=Integer.parseInt(snapshot.child("weekApparel").getValue().toString());
                    }else{
                        apptotal=0;
                    }
                    int heltotal;
                    if (snapshot.hasChild("weekHealth")){
                        heltotal=Integer.parseInt(snapshot.child("weekHealth").getValue().toString());
                    }else{
                        heltotal=0;
                    }
                    int pertotal;
                    if (snapshot.hasChild("weekPersonal")){
                        pertotal=Integer.parseInt(snapshot.child("weekPersonal").getValue().toString());
                    }else{
                        pertotal=0;
                    }
                    int othtotal;
                    if (snapshot.hasChild("weekOther")){
                        othtotal=Integer.parseInt(snapshot.child("weekOther").getValue().toString());
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
                    pie.title("Weekly Analytics");
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
                    Toast.makeText(WeekAnalytics.this, "Child doesn't exists", Toast.LENGTH_SHORT).show();
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
                if (snapshot.hasChild("weekTrans")) {
                    transtotal = Integer.parseInt(snapshot.child("weekTrans").getValue().toString());
                } else {
                    transtotal = 0;
                }
                int foodtotal;
                if (snapshot.hasChild("weekFood")) {
                    foodtotal = Integer.parseInt(snapshot.child("weekFood").getValue().toString());
                } else {
                    foodtotal = 0;
                }
                int houseototal;
                if (snapshot.hasChild("weekHouse")) {
                    houseototal = Integer.parseInt(snapshot.child("weekHouse").getValue().toString());
                } else {
                    houseototal = 0;
                }
                int entertotal;
                if (snapshot.hasChild("weekEntertainment")) {
                    entertotal = Integer.parseInt(snapshot.child("weekEntertainment").getValue().toString());
                } else {
                    entertotal = 0;
                }
                int chartotal;
                if (snapshot.hasChild("weekCharity")) {
                    chartotal = Integer.parseInt(snapshot.child("weekCharity").getValue().toString());
                } else {
                    chartotal = 0;
                }
                int edutotal;
                if (snapshot.hasChild("weekEducation")) {
                    edutotal = Integer.parseInt(snapshot.child("weekEducation").getValue().toString());
                } else {
                    edutotal = 0;
                }
                int apptotal;
                if (snapshot.hasChild("weekApparel")) {
                    apptotal = Integer.parseInt(snapshot.child("weekApparel").getValue().toString());
                } else {
                    apptotal = 0;
                }
                int heltotal;
                if (snapshot.hasChild("weekHealth")) {
                    heltotal = Integer.parseInt(snapshot.child("weekHealth").getValue().toString());
                } else {
                    heltotal = 0;
                }
                int pertotal;
                if (snapshot.hasChild("weekPersonal")) {
                    pertotal = Integer.parseInt(snapshot.child("weekPersonal").getValue().toString());
                } else {
                    pertotal = 0;
                }
                int othtotal;
                if (snapshot.hasChild("weekOther")) {
                    othtotal = Integer.parseInt(snapshot.child("weekOther").getValue().toString());
                } else {
                    othtotal = 0;
                }

                float monthtotalspentamt;
                if (snapshot.hasChild("weeklybudget")){
                    monthtotalspentamt=Integer.parseInt(snapshot.child("weeklybudget").getValue().toString());
                }else{
                    monthtotalspentamt=0;
                }

                //ratios

                float traratio;
                if (snapshot.hasChild("weektransratio")){
                    traratio=Integer.parseInt(snapshot.child("weektransratio").getValue().toString());
                }else{
                    traratio=0;
                }
                float fodratio;
                if (snapshot.hasChild("weekfoodratio")){
                    fodratio=Integer.parseInt(snapshot.child("weekfoodratio").getValue().toString());
                }else{
                    fodratio=0;
                }
                float houratio;
                if (snapshot.hasChild("weekhouseratio")){
                    houratio=Integer.parseInt(snapshot.child("weekhouseratio").getValue().toString());
                }else{
                    houratio=0;
                }
                float enterratio;
                if (snapshot.hasChild("weekenterratio")){
                    enterratio=Integer.parseInt(snapshot.child("weekenterratio").getValue().toString());
                }else{
                    enterratio=0;
                }
                float charratio;
                if (snapshot.hasChild("weekcharratio")){
                    charratio=Integer.parseInt(snapshot.child("weekcharratio").getValue().toString());
                }else{
                    charratio=0;
                }
                float eduratio;
                if (snapshot.hasChild("weekeduratio")){
                    eduratio=Integer.parseInt(snapshot.child("weekeduratio").getValue().toString());
                }else{
                    eduratio=0;
                }
                float appratio;
                if (snapshot.hasChild("weekappratio")){
                    appratio=Integer.parseInt(snapshot.child("weekappratio").getValue().toString());
                }else{
                    appratio=0;
                }

                float helratio;
                if (snapshot.hasChild("weekhealratio")){
                    helratio=Integer.parseInt(snapshot.child("weekhealratio").getValue().toString());
                }else{
                    helratio=0;
                }
                float perratio;
                if (snapshot.hasChild("weekpersonalratio")){
                    perratio=Integer.parseInt(snapshot.child("weekpersonalratio").getValue().toString());
                }else{
                    perratio=0;
                }
                float othratio;
                if (snapshot.hasChild("weekotherratio")){
                    othratio=Integer.parseInt(snapshot.child("weekotherratio").getValue().toString());
                }else{
                    othratio=0;
                }
                float monthtotalspentamtratio;
                if (snapshot.hasChild("weeklybudget")){
                    monthtotalspentamtratio=Integer.parseInt(snapshot.child("weeklybudget").getValue().toString());
                }
                else{
                    monthtotalspentamtratio=0;
                }

                float monthpercent=( monthtotalspentamt/monthtotalspentamtratio)/100;
                if (monthpercent<50){
                    monthratiospending.setText(monthpercent+"%" + "used of "+monthtotalspentamtratio+"   Status  ");
                    weekratiospend_img.setImageResource(R.drawable.green);
                }else if (monthpercent>=50 && monthpercent<100){
                    monthratiospending.setText(monthpercent+"%" + "used of "+monthtotalspentamtratio+"   Status  ");
                    weekratiospend_img.setImageResource(R.drawable.brown);
                }else {
                    monthratiospending.setText(monthpercent+"%" + "used of "+monthtotalspentamtratio+"   Status  ");
                    weekratiospend_img.setImageResource(R.drawable.red);
                }

                float Transpercent=(transtotal/traratio)*100;
                if (Transpercent<50){
                    prog_trans.setText(Transpercent+"%"+ "used of "+traratio+" Status ");
                    transp_stat.setImageResource(R.drawable.green);
                }else if (Transpercent>=50 && Transpercent<100){
                    prog_trans.setText(Transpercent+"%"+ "used of "+traratio+" Status ");
                    transp_stat.setImageResource(R.drawable.brown);
                }else{
                    prog_trans.setText(Transpercent+"%" + "used of "+traratio+" Status ");
                    transp_stat.setImageResource(R.drawable.red);
                }

                float foodpercent=(foodtotal/fodratio)*100;

                if (foodpercent<50){
                    prog_food.setText(foodtotal+"%" + "used of "+fodratio+" Status ");
                    fod_stat.setImageResource(R.drawable.green);
                }else if (foodpercent>=50 && foodpercent<100){
                    prog_food.setText(foodtotal+"%" + "used of "+fodratio+" Status ");
                    fod_stat.setImageResource(R.drawable.brown);
                }else{
                    prog_food.setText(foodtotal+"%" + "used of "+fodratio+" Status ");
                    fod_stat.setImageResource(R.drawable.red);
                }


                float housepercent=(houseototal/houratio)*100;

                if (housepercent<50){
                    prog_hou.setText(houseototal+"%" + "used of "+houratio+" Status ");
                    house_stat.setImageResource(R.drawable.green);
                }else if (housepercent>=50 && housepercent<100){
                    prog_hou.setText(houseototal+"%" + "used of "+houratio+" Status ");
                    house_stat.setImageResource(R.drawable.brown);
                }else{
                    prog_hou.setText(houseototal+"%" + "used of "+houratio+" Status ");
                    house_stat.setImageResource(R.drawable.red);
                }

                float enterpecent=(entertotal/enterratio)*100;

                if (enterpecent<50){
                    prog_enter.setText(entertotal+"%" + "used of "+enterratio+" Status ");
                    enter_stat.setImageResource(R.drawable.green);
                }else if (enterpecent>=50 && enterpecent<100){
                    prog_enter.setText(entertotal+"%" + "used of "+enterratio+" Status ");
                    enter_stat.setImageResource(R.drawable.brown);
                }else{
                    prog_enter.setText(entertotal+"%" + "used of "+enterratio+" Status ");
                    enter_stat.setImageResource(R.drawable.red);
                }


                float charpercent=(chartotal/charratio)*100;

                if (charpercent<50){
                    prog_chr.setText(chartotal+"%" + "used of "+charratio+" Status ");
                    chr_state.setImageResource(R.drawable.green);
                }else if (enterpecent>=50 && enterpecent<100){
                    prog_chr.setText(chartotal+"%" + "used of "+charratio+" Status ");
                    chr_state.setImageResource(R.drawable.brown);
                }else{
                    prog_chr.setText(chartotal+"%" + "used of "+charratio+" Status ");
                    chr_state.setImageResource(R.drawable.red);
                }


                float edupercent=(edutotal/eduratio)*100;

                if (edupercent<50){
                    prog_edu.setText(edutotal+"%" + "used of "+eduratio+" Status ");
                    edu_stat.setImageResource(R.drawable.green);
                }else if (edupercent>=50 && edupercent<100){
                    prog_edu.setText(edutotal+"%" + "used of "+eduratio+" Status ");
                    edu_stat.setImageResource(R.drawable.brown);
                }else{
                    prog_edu.setText(edutotal+"%" + "used of "+eduratio+" Status ");
                    edu_stat.setImageResource(R.drawable.red);
                }

                float apppercent=(apptotal/appratio)*100;

                if (apppercent<50){
                    prog_app.setText(apptotal+"%" + "used of "+appratio+" Status ");
                    app_stat.setImageResource(R.drawable.green);
                }else if (apppercent>=50 && apppercent<100){
                    prog_app.setText(apptotal+"%" + "used of "+appratio+" Status ");
                    app_stat.setImageResource(R.drawable.brown);
                }else{
                    prog_app.setText(apptotal+"%" + "used of "+appratio+" Status ");
                    app_stat.setImageResource(R.drawable.red);;
                }

                float helpercent=(heltotal/helratio)*100;

                if (helpercent<50){
                    prog_heal.setText(heltotal+"%" + "used of "+helratio+" Status ");
                    heal_stat.setImageResource(R.drawable.green);
                }else if (helpercent>=50 && helpercent<100){
                    prog_heal.setText(heltotal+"%" + "used of "+helratio+" Status ");
                    heal_stat.setImageResource(R.drawable.brown);
                }else{
                    prog_heal.setText(heltotal+"%" + "used of "+helratio+" Status ");
                    heal_stat.setImageResource(R.drawable.red);
                }


                float perpercent=(pertotal/perratio)*100;

                if (perpercent<50){
                    prog_prsnl.setText(pertotal+"%" + "used of "+perratio+" Status ");
                    prsnl_stat.setImageResource(R.drawable.green);
                }else if (perpercent>=50 && perpercent<100){
                    prog_prsnl.setText(pertotal+"%" + "used of "+perratio+" Status ");
                    prsnl_stat.setImageResource(R.drawable.brown);
                }else{
                    prog_prsnl.setText(pertotal+"%" + "used of "+perratio+" Status ");
                    prsnl_stat.setImageResource(R.drawable.red);
                }

                float othpercent=(othtotal/othratio)*100;

                if (othpercent<50){
                    prog_oth.setText(othtotal+"%" + "used of "+othratio+" Status ");
                    oth_stat.setImageResource(R.drawable.green);
                }else if (othpercent>=50 && othpercent<100){
                    prog_oth.setText(othtotal+"%" + "used of "+othratio+" Status ");
                    oth_stat.setImageResource(R.drawable.brown);
                }else{
                    prog_oth.setText(othtotal+"%" + "used of "+othratio+" Status ");
                    oth_stat.setImageResource(R.drawable.red);
                }

            }

            else{
                Toast.makeText(WeekAnalytics.this, "setting status and image error", Toast.LENGTH_SHORT).show();
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

   }


}