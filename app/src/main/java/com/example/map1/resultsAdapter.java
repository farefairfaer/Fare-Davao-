package com.example.map1;

import static com.example.map1.MainActivity.displayPathLine;
import static com.example.map1.MainActivity.removePathLine;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class resultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    @NonNull
    Context context;
    List<resultItems> resultItemsList;
    public boolean[] checked;
    public resultsAdapter(@NonNull Context context, List<resultItems> resultItemsList) {
        this.resultItemsList = resultItemsList  ;
        this.context = context;
        this.checked = new boolean[resultItemsList.size()];
    }

    @Override
    public int getItemViewType(int position) {
        return resultItemsList.get(position).getViewType();
    }

    static class LayoutOneViewHolder extends RecyclerView.ViewHolder {
        private TextView DRListVIEW;
        private TextView DRregFareVIEW;
        private TextView DRdiscFareVIEW;
        private TextView DRdistanceVIEW;
        private LinearLayout directItemVIEW;

        public LayoutOneViewHolder(@NonNull View itemView) {
            super(itemView);
            DRListVIEW = itemView.findViewById(R.id.DRListVIEW);
            DRregFareVIEW = itemView.findViewById(R.id.DRregFareVIEW);
            DRdiscFareVIEW = itemView.findViewById(R.id.DRdiscFareVIEW);
            DRdistanceVIEW = itemView.findViewById(R.id.DRdistanceVIEW);
            directItemVIEW = itemView.findViewById(R.id.directITEM);
        }

        private void setView(List<String> DRList, Double DRregFare, Double DRdiscFare, Double DRdistance) {
            String DRListString = DRList.toString().replace("[", "").replace("]", "");
            String DRregFareString = DRregFare.toString().concat("P");
            String DRdiscFareString = DRdiscFare.toString().concat("P");
            String DRdistanceString = DRdistance.toString().concat("km");

            this.DRListVIEW.setText(DRListString);
            this.DRregFareVIEW.setText(DRregFareString);
            this.DRdiscFareVIEW.setText(DRdiscFareString);
            this.DRdistanceVIEW.setText(DRdistanceString);
        }
    }

    static class LayoutTwoViewHolder extends RecyclerView.ViewHolder {
        private TextView FRListVIEW;
        private TextView FRregFareVIEW;
        private TextView FRdiscFareVIEW;
        private TextView FRdistanceVIEW;
        private TextView SRListVIEW;
        private TextView SRregFareVIEW;
        private TextView SRdiscFareVIEW;
        private TextView SRdistanceVIEW;
        private LinearLayout indirectItemVIEW;

        public LayoutTwoViewHolder(@NonNull View itemView) {
            super(itemView);
            FRListVIEW = itemView.findViewById(R.id.FRListVIEW);
            FRregFareVIEW = itemView.findViewById(R.id.FRregfareVIEW);
            FRdiscFareVIEW = itemView.findViewById(R.id.FRdiscFareVIEW);
            FRdistanceVIEW = itemView.findViewById(R.id.FRdistanceVIEW);
            SRListVIEW = itemView.findViewById(R.id.SRListVIEW);
            SRregFareVIEW = itemView.findViewById(R.id.SRregfareVIEW);
            SRdiscFareVIEW = itemView.findViewById(R.id.SRdiscFareVIEW);
            SRdistanceVIEW = itemView.findViewById(R.id.SRdistanceVIEW);
            indirectItemVIEW = itemView.findViewById(R.id.indirectITEM);
        }

        private void setViews(List<String> FRList, Double FRregFare, Double FRdiscFare, Double FRdistance,
                              List<String> SRList, Double SRregFare, Double SRdiscFare, Double SRdistance) {
            String FRListString = FRList.toString().replace("[", "").replace("]", "");
            String FRregFareString = FRregFare.toString().concat("P");
            String FRdiscFareString = FRdiscFare.toString().concat("P");;
            String FRdistanceString = FRdistance.toString().concat("km");;

            this.FRListVIEW.setText(FRListString);
            this.FRregFareVIEW.setText(FRregFareString);
            this.FRdiscFareVIEW.setText(FRdiscFareString);
            this.FRdistanceVIEW.setText(FRdistanceString);

            String SRListString = SRList.toString().replace("[", "").replace("]", "");
            String SRregFareString = SRregFare.toString().concat("P");
            String SRdiscFareString = SRdiscFare.toString().concat("P");
            String SRdistanceString = SRdistance.toString().concat("km");

            this.SRListVIEW.setText(SRListString);
            this.SRregFareVIEW.setText(SRregFareString);
            this.SRdiscFareVIEW.setText(SRdiscFareString);
            this.SRdistanceVIEW.setText(SRdistanceString);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View layoutOne = LayoutInflater.from(parent.getContext()).inflate(R.layout.directresults_item, parent, false);
            return new LayoutOneViewHolder(layoutOne);
        } else if (viewType == 2) {
            View layoutTwo = LayoutInflater.from(parent.getContext()).inflate(R.layout.indirectresults_item, parent, false);
            return new LayoutTwoViewHolder(layoutTwo);
        }
        return null;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        resultItems item = resultItemsList.get(position);

        if (holder instanceof LayoutOneViewHolder) {
            LayoutOneViewHolder viewHolder = (LayoutOneViewHolder) holder;
            viewHolder.setView(
                    item.getDRList(),
                    item.getDRregFare(),
                    item.getDRdiscFare(),
                    item.getDRdistance()
            );
            viewHolder.directItemVIEW.setOnClickListener(view -> {
                if(!checked[position]) {
                    displayPathLine(resultItemsList.get(position).getDRPath(), resultItemsList.get(position).toString());
                    checked[position] = true;
                    viewHolder.directItemVIEW.setBackgroundColor(Color.parseColor("#f6f6f6"));
                    Toast.makeText(context, "Show Direct Route", Toast.LENGTH_SHORT).show();
                } else{
                    removePathLine(resultItemsList.get(position).toString());
                    Toast.makeText(context, "Hide Direct Route", Toast.LENGTH_SHORT).show();
                    viewHolder.directItemVIEW.setBackgroundColor(Color.WHITE);
                    checked[position] = false;
                }
            });

        } else if (holder instanceof LayoutTwoViewHolder) {
            LayoutTwoViewHolder viewHolder = (LayoutTwoViewHolder) holder;
            viewHolder.setViews(
                    item.getFRList(),
                    item.getFRregFare(),
                    item.getFRdiscFare(),
                    item.getFRdistance(),
                    item.getSRList(),
                    item.getSRregFare(),
                    item.getSRdiscFare(),
                    item.getSRdistance()
            );
            MainActivity mainActivity = new MainActivity();
            viewHolder.indirectItemVIEW.setOnClickListener(view -> {
                if (!checked[position]) {
                    displayPathLine(resultItemsList.get(position).getFRPath(), resultItemsList.get(position).toString());
                    displayPathLine(resultItemsList.get(position).getSRPath(), resultItemsList.get(position).toString() + " SR");
                    mainActivity.markLoc(resultItemsList.get(position).getTransferPoint(), "commonPoint", "", "");
                    viewHolder.indirectItemVIEW.setBackgroundColor(Color.parseColor("#f6f6f6"));
                    checked[position] = true;
                } else {
                    removePathLine(resultItemsList.get(position).toString());
                    removePathLine(resultItemsList.get(position).toString() + " SR");
                    mainActivity.removeExistingMarker("commonPoint");
                    viewHolder.indirectItemVIEW.setBackgroundColor(Color.WHITE);
                    checked[position] = false;
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return resultItemsList.size();
    }
}
