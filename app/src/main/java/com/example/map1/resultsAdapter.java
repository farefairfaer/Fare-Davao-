package com.example.map1;

import static com.example.map1.MainActivity.displayPolyline;
import static com.example.map1.MainActivity.removePolyline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class resultsAdapter extends RecyclerView.Adapter<resultsViewHolder>{
    @NonNull
    Context context;
    List<resultItems> resultsList;
    public resultsAdapter(@NonNull Context context, List<resultItems> resultsList) {
        this.resultsList = resultsList;
        this.context = context;
    }
    @Override
    public resultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new resultsViewHolder(LayoutInflater.from(context).inflate(R.layout.results_item,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull resultsViewHolder holder, int position) {
        holder.colorView.setBackgroundColor(resultsList.get(position).getColor());
        holder.routeNameView.setText(resultsList.get(position).getRouteName());

        holder.showBTN.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                displayPolyline(resultsList.get(position).getRouteName());
            } else {
                removePolyline(resultsList.get(position).getRouteName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }
}
