package com.example.map1;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.osmdroid.util.GeoPoint;

import java.util.Objects;

public class InputFragment extends Fragment {
    GeoPoint startLoc,endLoc;
    String address1, address2;

    public TextView startSearch, destSearch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.input_fragment, container, false);
        startSearch = view.findViewById(R.id.startSearch);
        destSearch = view.findViewById(R.id.destinationSearch);

        startSearch.setOnClickListener(v -> openSearch("start"));
        destSearch.setOnClickListener(v -> openSearch("destin"));

        Button findRoutesBTN = view.findViewById(R.id.findRoutesBTN);
        findRoutesBTN.setOnClickListener(v -> {
            if (startLoc==null || endLoc==null){
                Log.d("tab1", "start or end loc is null");
                Toast.makeText(getContext(), "Please select both start and destination", Toast.LENGTH_SHORT).show();
            } else {
                tab1Listener.userInput(startLoc, endLoc);
            }
        });
        return view;
    }

    private tab1Listener tab1Listener;
    private searchListener searchListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        tab1Listener= (tab1Listener) context;
        searchListener = (searchListener) context;
    }

    public interface tab1Listener{
        void userInput(GeoPoint startLoc, GeoPoint endLoc);
    }
    public interface searchListener {
        void markLoc(GeoPoint location, String buttonType, String startAddress, String destination);
    }
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Log.d("Launcher", "Data received: " + data.getStringExtra("display_name"));
                        String lat = data.getStringExtra("lat");
                        String lon = data.getStringExtra("lon");
                        GeoPoint location = new GeoPoint(Double.parseDouble(lat), Double.parseDouble(lon));
                        String bType = data.getStringExtra("buttonType");

                        if(Objects.equals(data.getStringExtra("buttonType"),"start")){
                            address1 = data.getStringExtra("display_name");
                            Log.d("tab1", "Updating startSearch with: " + address1);
                            startSearch.setText(address1);
                            searchListener.markLoc(location, bType,address1,address2);
                            startLoc=location;
                        }
                        else if(Objects.equals(data.getStringExtra("buttonType"),"destin")){
                            address2 = data.getStringExtra("display_name");
                            Log.d("tab1", "Updating startSearch with: " + address2);
                            destSearch.setText(address2);
                            searchListener.markLoc(location, bType,address1, address2);
                            endLoc=location;
                        }
                    }
                }
            }
    );
    public void openSearch(String buttonType){
        Intent getLoc = new Intent(InputFragment.this.getContext(), searchActivity.class);
        getLoc.putExtra("buttonType", buttonType);
        launcher.launch(getLoc);
    }
}