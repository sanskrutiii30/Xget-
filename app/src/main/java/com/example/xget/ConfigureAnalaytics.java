package com.example.xget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ConfigureAnalaytics extends AppCompatActivity {
    private CardView todaycardview, weekcardview,monthcardview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_analaytics);

        todaycardview=findViewById(R.id.todasycardview);
        weekcardview=findViewById(R.id.weekscardview);
        monthcardview=findViewById(R.id.monthscardview);


        todaycardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(ConfigureAnalaytics.this,DailyAnalytics.class);
                startActivity(intent);
            }
        });

        weekcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(ConfigureAnalaytics.this,WeekAnalytics.class);
                startActivity(intent);
            }
        });

        monthcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(ConfigureAnalaytics.this,MonthAnalytics.class);
                startActivity(intent);
            }
        });
    }
}