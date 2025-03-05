package com.example.map1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;


public class ResultsFragment extends Fragment {
    private TextView startAddressVIEW, destinationAddressVIEW;
    private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.results_fragment, container, false);

    //    startAddressVIEW = view.findViewById(R.id.startAddressVIEW);
    //    destinationAddressVIEW = view.findViewById(R.id.destinationAddressVIEW);
        recyclerView = view.findViewById(R.id.resultsListView);

        if (getArguments() != null) {
            List<resultItems> results= getArguments().getParcelableArrayList("resultItemsList");
       //     String startAddress = getArguments().getString("startAddress");
        //    String destination = getArguments().getString("destination");

        //    startAddressVIEW.setText(startAddress);
        //    destinationAddressVIEW.setText(destination);

            Log.d("CHECKING ARGUMENTS", results.toString());
            resultsAdapter adapter = new resultsAdapter(getContext(), results);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        Button backBTN = view.findViewById(R.id.backBTN);
        backBTN.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, InputFragment.class, null)
                    .disallowAddToBackStack()
                    .commit();
            MainActivity.map.getOverlayManager().clear();
        });
        return view;
    }
}