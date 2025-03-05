package com.example.map1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;


import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements InputFragment.tab1Listener, InputFragment.searchListener {
    public static MapView map;
    public Button clearAllBTN;
    private ImageButton fareMatrixListBTN;
    public ImageButton centerMapBTN;
    public RecyclerView legendView;
    public ImageButton zoomInBTN;
    public ImageButton zoomOutBTN;
    public static Map<String, Polyline> routeLines;
    public static Map<String, List<GeoPoint>> routesList;
    private Button routeListBTN;
    private ArrayList<Integer> dropDownList = new ArrayList<>();
    private LinearLayout legendLayout;
    private boolean[] selectedRoutes;
    private List<legendItems> legendItems = new ArrayList<>();
    private static Context mContext;
    private String[] geoJsonFileNames = {"Matina_Crossing.geojson", "Mintal.geojson",
            "Catalunan_Grande.geojson", "Maa.geojson", "Toril.geojson",
            "Sasa_via_JP_Laurel.geojson", "Calinan.geojson"};
    static String[] routeNames = {"Matina Crossing", "Mintal", "Catalunan Grande", "Maa",
            "Toril", "Sasa via JP Laurel", "Calinan"};
    static Map<String, Integer> routeColors = new HashMap<>(Map.of(
            "Matina Crossing", Color.RED,
            "Mintal", Color.BLUE,
            "Catalunan Grande", Color.DKGRAY,
            "Maa", Color.GREEN,
            "Toril", Color.MAGENTA,
            "Sasa via JP Laurel", Color.CYAN,
            "Calinan", Color.YELLOW,
            "Path", Color.BLACK
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mContext = this;
        clearAllBTN = findViewById(R.id.clearALLBTN);
        zoomInBTN = findViewById(R.id.zoomInBTN);
        zoomOutBTN = findViewById(R.id.zoomOutBTN);
        centerMapBTN = findViewById(R.id.centerMapBTN);
        routeListBTN = findViewById(R.id.routeListBTN);
        legendLayout = findViewById(R.id.legendLayout);
        legendView = findViewById(R.id.legendView);
        fareMatrixListBTN = findViewById(R.id.fareMatrixListBTN);

        fareMatrixListBTN.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, fareMatrixMenu.class);
            startActivity(intent);
        });

        legendAdapter adapter = new legendAdapter(getApplicationContext(), legendItems);
        legendView.setAdapter(adapter);
        legendView.setLayoutManager(new LinearLayoutManager(this));

        selectedRoutes = new boolean[routeNames.length];

        //make list of jeepneyroutes geopoints
        routeData routeData = new routeData();
        routesList = routeData.loadJeepneyRoutes(this, geoJsonFileNames); //list<gropoint> mostly for calcs

        //make polylines out of jeepneyroutes
        routeLines = routeData.loadLines(routesList);
        Log.d("Tag", routesList.keySet().toString());

        //create map
        Configuration.getInstance().setUserAgentValue(getPackageName());
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setZoomLevel(17);
        map.setMinZoomLevel(3.0);
        map.setMaxZoomLevel(18.0);
        map.setMultiTouchControls(true);
        GeoPoint center = new GeoPoint(7.056208, 125.577373);
        IMapController mapController = map.getController();
        mapController.setCenter(center);

        centerMapBTN.setOnClickListener((v -> {
            mapController.setCenter(center);
            map.setZoomLevel(17);
        }));

        zoomInBTN.setOnClickListener(v -> map.getController().zoomIn());
        zoomOutBTN.setOnClickListener(v -> map.getController().zoomOut());

        routeListBTN.setOnClickListener(v -> {
            showRoutesDialog(routeLines, adapter, legendItems);
        });
    }
    public static Context getContext(){
        return mContext;
    }
    private void showRoutesDialog(Map<String, Polyline> routeLines1, legendAdapter adapter, List<legendItems> legendItems) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select Route");
        builder.setCancelable(false);
        List<Overlay> routesDisplayed = new ArrayList<>();

        builder.setMultiChoiceItems(routeNames, selectedRoutes, (dialog, which, isChecked) -> {
            String routeName = routeNames[which];
            legendLayout.setVisibility(View.VISIBLE);

            if (isChecked) {
                if (!dropDownList.contains(which)) {
                    dropDownList.add(which);
                    boolean alreadyExists = false;
                    for (legendItems item : legendItems) {
                        if (item.getRoutes().equals(routeName)) {
                            alreadyExists = true;
                            break;
                        }
                    }
                    if (!alreadyExists) {
                        legendItems.add(new legendItems(routeName, routeColors.get(routeName)));
                        adapter.notifyItemInserted(legendItems.size() - 1);
                    }
                }
            } else {
                int indexInLegend = -1;
                for (int i = 0; i < legendItems.size(); i++) {
                    if (legendItems.get(i).getRoutes().equals(routeName)) {
                        indexInLegend = i;
                        break;
                    }
                }
                if (indexInLegend != -1) {
                    legendItems.remove(indexInLegend);
                    adapter.notifyItemRemoved(indexInLegend);
                }
                dropDownList.remove((Integer) which);
            }
        }).setPositiveButton("Ok", (dialog, which) -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < dropDownList.size(); i++) {
                if (dropDownList.get(i) < routeNames.length) {
                    stringBuilder.append(routeNames[dropDownList.get(i)]);
                    if (i != dropDownList.size() - 1) {
                        stringBuilder.append(", ");
                    }
                }
            }
            if (!dropDownList.isEmpty()) {
                for (int i = 0; i < dropDownList.size(); i++) {
                    if (dropDownList.get(i) < routeNames.length) {
                        Overlay overlay = routeLines1.get(routeNames[dropDownList.get(i)]);
                        if (overlay != null) {
                            map.getOverlays().add(0, overlay);
                            routesDisplayed.add(overlay);
                        }
                    }
                }
                clearAllBTN.setVisibility(View.VISIBLE);
                clearAllBTN.setOnClickListener(v -> {
                    legendItems.clear();
                    adapter.notifyDataSetChanged();
                    for (Overlay overlay : routesDisplayed) {
                        map.getOverlayManager().remove(overlay);
                    }
                    map.invalidate();
                    clearAllBTN.setVisibility(View.GONE);
                    legendLayout.setVisibility(View.GONE);
                });

            } else {
                clearAllBTN.setVisibility(View.GONE);
                map.getOverlays().clear();
                legendLayout.setVisibility(View.GONE);
            }
            Arrays.fill(selectedRoutes, false);
            dropDownList.clear();
            map.invalidate();
        }).setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            legendLayout.setVisibility(View.GONE);
            Arrays.fill(selectedRoutes, false);
            dropDownList.clear();
        }).setNeutralButton("Clear All", (dialog, which) -> {
            Arrays.fill(selectedRoutes, false);
            legendItems.clear();
            adapter.notifyDataSetChanged();
            legendLayout.setVisibility(View.GONE);
            Arrays.fill(selectedRoutes, false);
            dropDownList.clear();
        });
        builder.show();
    }
    static void displayPathLine(List<GeoPoint> pathLine, String position) {
        Polyline pathLine1 = new Polyline();
        pathLine1.setPoints(pathLine);
        pathLine1.setId(position);
        pathLine1.getOutlinePaint().setStrokeWidth(10.0f);
        pathLine1.getOutlinePaint().setColor(Color.BLACK);
        map.getOverlayManager().add(0, pathLine1);
        map.invalidate();
    }
    static void removePathLine(String position) {
        for(int i=0;i<map.getOverlays().size();i++){
            Overlay overlay=map.getOverlays().get(i);
            if(overlay instanceof Polyline&&((Polyline) overlay).getId().equals(position)){
                map.getOverlays().remove(overlay);
            }
        }
        map.invalidate();
    }
    public void userInput(GeoPoint startPoint, GeoPoint endPoint) {
        routeFinder routeFinder = new routeFinder(startPoint, endPoint);
        List<resultItems> resultItemsList = routeFinder.results();

        ResultsFragment tab2 = new ResultsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("resultItemsList", (ArrayList<resultItems>) resultItemsList);
        tab2.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, tab2).commit();
    }
    boolean startMarker = false;
    boolean endMarker = false;
    boolean commonPointMarker = false;
    public void markLoc(GeoPoint location, String buttonType, String startAddress, String destination) {
        if (location != null) {
            Log.d("MainActivity", "Marking location: " + location);

            Context context = MainActivity.getContext();
            if (context == null) {
                Log.e("MainActivity", "Context is null, cannot proceed.");
                return;
            }

            if (Objects.equals(buttonType, "start")) {
                removeExistingMarker("Start Marker");
                Marker marker = new Marker(map);
                marker.setId("Start Marker");
                marker.setTitle(startAddress);
                marker.setPosition(location);
                marker.setIcon(ResourcesCompat.getDrawable(context.getResources(), R.drawable.green_marker, context.getTheme()));
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                map.getOverlays().add(marker);
                map.getController().setCenter(location);
                map.invalidate();
                startMarker = true;
            }
            else if (Objects.equals(buttonType, "destin")) {
                removeExistingMarker("End Marker");

                Marker marker = new Marker(map);
                marker.setId("End Marker");
                marker.setTitle(destination);
                marker.setPosition(location);
                marker.setIcon(ResourcesCompat.getDrawable(context.getResources(), R.drawable.red_marker, context.getTheme()));
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                map.getController().setCenter(location);
                map.getOverlays().add(marker);
                map.invalidate();
                endMarker = true;
            }
            else if (Objects.equals(buttonType, "commonPoint")) {
                removeExistingMarker("commonPoint");

                Marker marker = new Marker(map);
                marker.setId("commonPoint");
                marker.setPosition(location);
                marker.setIcon(ResourcesCompat.getDrawable(context.getResources(), R.drawable.yellow_marker, context.getTheme()));
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                map.getOverlays().add(marker);
                map.invalidate();
                commonPointMarker = true;
            }
        }
    }

    public void removeExistingMarker(String markerId) {
        List<Overlay> toRemove = new ArrayList<>();

        for (Overlay overlay : map.getOverlays()) {
            if (overlay instanceof Marker && markerId.equals(((Marker) overlay).getId())) {
                toRemove.add(overlay);
            }
        }

        map.getOverlays().removeAll(toRemove);
        map.invalidate();
    }


    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
}