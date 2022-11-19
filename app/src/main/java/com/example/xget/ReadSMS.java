package com.example.xget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class ReadSMS extends AppCompatActivity {

    ListView listView;
    private static final int PERMISION_REQUEST_READ_CONTACTS=100;
    ArrayList<String> smslist;
    private static final String TAG=ReadSMS.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_sms);

        Toast.makeText(this, "Click items to manage.", Toast.LENGTH_SHORT).show();

        listView=(ListView) findViewById(R.id.idlist);

        int permissioncheck= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);

        if (permissioncheck == PackageManager.PERMISSION_GRANTED)
        {
            showcontacts();
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_SMS}, PERMISION_REQUEST_READ_CONTACTS);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISION_REQUEST_READ_CONTACTS) {
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                showcontacts();
            }else{
                Toast.makeText(this, "Until you grant permission ,we can not display the names", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showcontacts(){
        Uri inboxuri= Uri.parse("content://sms/inbox");
        smslist=new ArrayList<>();
        ContentResolver contentResolver= getContentResolver();

        Cursor cursor=contentResolver.query(inboxuri,null,null,null,null);

        while (cursor.moveToNext()){
            String number=cursor.getString(cursor.getColumnIndexOrThrow("address")).toString();
           String body=cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();

           if (body.toLowerCase().contains("spent")) {
               String[] amount = body.split(" ");
               smslist.add(body);
           }

        }
        cursor.close();
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,smslist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String clicked = (String) listView.getItemAtPosition(position);

                AlertDialog.Builder alert = new AlertDialog.Builder(ReadSMS.this);
                alert.setMessage("Did you make entry for this spent in your expense ?").setIcon(android.R.drawable.ic_dialog_alert);
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        smslist.remove(clicked);
                        adapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String[] fetch = clicked.split(" ");
                        Intent intent = new Intent(ReadSMS.this,Todayspending.class);
                        intent.putExtra("amount",fetch[1]);
                        startActivity(intent);
                    }
                });
                alert.show();
            }
        });
    }
}