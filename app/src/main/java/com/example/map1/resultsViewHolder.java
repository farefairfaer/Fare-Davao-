package com.example.map1;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class resultsViewHolder extends RecyclerView.ViewHolder {
    ToggleButton showBTN;
    TextView routeNameView, colorView;
    public resultsViewHolder(@NonNull View itemView) {
        super(itemView);
        showBTN = itemView.findViewById(R.id.showBTN);
        routeNameView = itemView.findViewById(R.id.routeName);
        colorView = itemView.findViewById(R.id.routeColor);
    }
}
