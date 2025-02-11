package com.example.map1;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class legendViewHolder extends RecyclerView.ViewHolder{
    TextView legendColor, legendRoute;
    public legendViewHolder(@NonNull View itemView) {
        super(itemView);
        legendColor = itemView.findViewById(R.id.legendColor);
        legendRoute = itemView.findViewById(R.id.legendRoute);
    }
}
