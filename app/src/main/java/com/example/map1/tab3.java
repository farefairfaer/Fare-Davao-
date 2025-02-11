package com.example.map1;

import static com.example.map1.MainActivity.bottomSheet;
import static com.example.map1.MainActivity.routeColors;

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

public class tab3 extends Fragment {

    private TextView firstFareView;
    private TextView discFirstFareView;
    private TextView secondFareView;
    private TextView discSecondFareView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab3, container, false);

        firstFareView = view.findViewById(R.id.firstFare);
        discFirstFareView = view.findViewById(R.id.discountedfirstFare);
        secondFareView = view.findViewById(R.id.secondFare);
        discSecondFareView = view.findViewById(R.id.discountedSecondFare);

        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(1100);

        List<resultItems> resultItemsListL1 = new ArrayList<>();
        List<resultItems> resultItemsListL2 = new ArrayList<>();

        if(getArguments() != null) {
            List<String> recommendedFirstRoutes =  getArguments().getStringArrayList("firstLeg");
            List<String> recommendedSecondRoutes = getArguments().getStringArrayList("secondLeg");

            double firstLegFare = getArguments().getDouble("firstFare");
            double secondLegFare = getArguments().getDouble("secondFare");

            Log.d("CHECKING ARGUMENTS", recommendedFirstRoutes.toString() + " first fare: " + firstLegFare + " second fare: " + secondLegFare);

            secondFareView.setText(String.valueOf(secondLegFare));
            firstFareView.setText(String.valueOf(firstLegFare));

            for(String route : recommendedFirstRoutes) {
                resultItemsListL1.add(new resultItems(route, routeColors.get(route)));
            }

            for(String route: recommendedSecondRoutes) {
                resultItemsListL2.add(new resultItems(route, routeColors.get(route)));
            }
        }

        resultsAdapter adapterL1 = new resultsAdapter(getContext(), resultItemsListL1);
        RecyclerView recyclerViewL1 = view.findViewById(R.id.firstLegResults);
        recyclerViewL1.setAdapter(adapterL1);
        recyclerViewL1.setLayoutManager(new LinearLayoutManager(getContext()));

        resultsAdapter adapterL2 = new resultsAdapter(getContext(), resultItemsListL2);
        RecyclerView recyclerViewL2 = view.findViewById(R.id.secondLegResults);
        recyclerViewL2.setAdapter(adapterL2);
        recyclerViewL2.setLayoutManager(new LinearLayoutManager(getContext()));

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