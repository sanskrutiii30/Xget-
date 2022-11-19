package com.example.xget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeekspendingAdapter extends RecyclerView.Adapter<WeekspendingAdapter.Viewholder> {

    private Context context;
    private List<Data> mydatalist;

    public WeekspendingAdapter(Context context, List<Data> mydatalist) {
        this.context = context;
        this.mydatalist = mydatalist;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.retrieve_layout, parent, false);

        return new WeekspendingAdapter.Viewholder(view);
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
