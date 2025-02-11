package com.example.map1;
import static android.util.Log.d;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class searchAdapter extends RecyclerView.Adapter<searchAdapter.ViewHolder> {

    private final Context context;
    private List<resultData> fullList;      // Stores all results
    private List<resultData> filteredList; // Stores filtered results
    private final OnItemClickListener clickListener;

    public searchAdapter(Context context, List<resultData> fullList, OnItemClickListener clickListener) {
        this.context = context;
        this.fullList = new ArrayList<>(fullList);
        this.filteredList = new ArrayList<>(fullList);
        this.clickListener = clickListener;
    }

    public void updateList(List<resultData> newList) {
        this.fullList = new ArrayList<>(newList);
        this.filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
                    filteredList.addAll(fullList);

        } else {
            for (resultData result : fullList) {
                if (result.getDisplay_name().toLowerCase().contains(query.toLowerCase())  ) {
                    Log.d("FilterDebug", "display_name: " + result.getDisplay_name());
                    filteredList.add(result);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.result_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        resultData result = filteredList.get(position);
        String refined = result.getDisplay_name().split(", Davao City")[0].trim();

        holder.subHeadView.setText(result.getDisplay_name());
        holder.nameTextView.setText(refined);
        holder.coordinatesTextView.setText("Lat: " + result.getLat() + ", Lon: " + result.getLon());

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onItemClick(result);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, coordinatesTextView, subHeadView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            coordinatesTextView = itemView.findViewById(R.id.coordinatesTextView);
            subHeadView = itemView.findViewById(R.id.subHeadView);
        }
    }

    // Define an interface for click events
    public interface OnItemClickListener {
        void onItemClick(resultData data);
    }
}
