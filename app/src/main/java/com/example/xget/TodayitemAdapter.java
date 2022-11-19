package com.example.xget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TodayitemAdapter extends RecyclerView.Adapter<TodayitemAdapter.Viewholder> {

    private Context context;
    private List<Data> mydatalist;

    private String post_key = "";
    private String item = "";
    private String notes = "";
    private int amount;

    private TextView mitem;
    private EditText mnotes, mamount;

    private ProgressDialog loader;

    public TodayitemAdapter(Context context, List<Data> mydatalist) {
        this.context = context;
        this.mydatalist = mydatalist;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.retrieve_layout, parent, false);

        return new TodayitemAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {


        final Data data = mydatalist.get(position);

        holder.items.setText("Item : " + data.getItem());
        holder.amount.setText("Amount : " + data.getAmount());
        holder.notes.setText("Notes : " + data.getNotes());
        holder.date.setText("On : " + data.getDate());

        switch (data.getItem()) {
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_key = data.getId();
                item = data.getItem();
                amount = data.getAmount();
                updateData();
            }
        });

    }



    private void updateData() {


        AlertDialog.Builder mydialogs=new AlertDialog.Builder(context);
        LayoutInflater inflater= LayoutInflater.from(context);
        View mview= inflater.inflate(R.layout.update_layout,null);

        mydialogs.setView(mview);
        final AlertDialog dialog= mydialogs.create();
        mitem= mview.findViewById(R.id.itemname);
        mamount=mview.findViewById(R.id.amt);
        mnotes=mview.findViewById(R.id.note);

        mnotes.setText(notes);
        mnotes.setSelection(notes.length());


        mitem.setText(item);

        mamount.setText(String.valueOf(amount));
        mamount.setSelection(String.valueOf(amount).length());

        Button deletebtn= mview.findViewById(R.id.deletebtn);
        Button updatebtn=mview.findViewById(R.id.updatebtn);

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount= Integer.parseInt(mamount.getText().toString());
                notes=mnotes.getText().toString();

                DateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal= Calendar.getInstance();
                String date=dateFormat.format(cal.getTime());

                MutableDateTime epoch= new MutableDateTime();
                epoch.setDate(0);

                DateTime now= new DateTime();

                Weeks weeks= Weeks.weeksBetween(epoch,now);
                Months months= Months.monthsBetween(epoch,now);

                String itemday= item+date;
                String itemweek=item+weeks.getWeeks() ;
                String itemmonth=item+months.getMonths();


                Data data=new Data(item,date,post_key,itemday,itemweek,itemmonth,notes,amount,months.getMonths(),weeks.getWeeks());

                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("expenses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


                databaseReference.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(context, "Error while updating", Toast.LENGTH_SHORT).show();
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

                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("expenses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                databaseReference.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Deleted item successfully", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(context, "Error while deleting item ", Toast.LENGTH_SHORT).show();
                        }
                        loader.dismiss();
                    }
                });
            }
        });

        dialog.show();

    }

    @Override
    public int getItemCount() {
        return mydatalist.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        public TextView items,amount,date,notes;
        public ImageView imageView;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            items=itemView.findViewById(R.id.item);
            amount=itemView.findViewById(R.id.rs);
            date=itemView.findViewById(R.id.date);
            notes=itemView.findViewById(R.id.note);

            imageView=itemView.findViewById(R.id.imageview);
        }
    }
}
