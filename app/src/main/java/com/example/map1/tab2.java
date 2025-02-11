package com.example.map1;

import static com.example.map1.MainActivity.bottomSheet;
import static com.example.map1.MainActivity.routeColors;

import android.graphics.Color;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class tab2 extends Fragment {
    private TextView fareView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab2, container, false);

        fareView = view.findViewById(R.id.directFare);

        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(900);

        List<resultItems> resultItemsList = new ArrayList<>();
        if (getArguments() != null) {
            ArrayList<String> recommendedRoutes = getArguments().getStringArrayList("directRoutes");
            double fare = getArguments().getDouble("directFare");

            Log.d("CHECKING ARGUMENTS", recommendedRoutes.toString());

            fareView.setText(String.valueOf(fare));

            for(String routeName: recommendedRoutes) {
                resultItemsList.add(new resultItems(routeName, routeColors.get(routeName)));
                Log.d("CHECKING ARGUMENTS", "YES I AM PARSING THE MAP " + resultItemsList.toString());
            }
        }

        resultsAdapter adapter = new resultsAdapter(getContext(), resultItemsList);
        RecyclerView recyclerView = view.findViewById(R.id.resultsListView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button backBTN = view.findViewById(R.id.backBTN);
        backBTN.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, tab1.class, null)
                    .disallowAddToBackStack()
                    .commit();
            MainActivity.map.getOverlayManager().clear();
        });
        return view;
    }
}