package com.example.map1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class legendAdapter extends RecyclerView.Adapter<legendViewHolder>{
    @NonNull
    Context context;
    List<legendItems> items;
    legendAdapter(Context context, List<legendItems> legendItems) {
        this.items = legendItems;
        this.context = context;
    }

    @NonNull
    @Override
    public legendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new legendViewHolder(LayoutInflater.from(context).inflate(R.layout.legend_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull legendViewHolder holder, int position) {
        holder.legendRoute.setText(items.get(position).getRoutes());
        holder.legendColor.setBackgroundColor(items.get(position).getColors());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
