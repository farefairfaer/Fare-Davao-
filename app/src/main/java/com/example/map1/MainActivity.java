package com.example.map1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements tab1.tab1Listener, tab1.searchListener{
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
    static View bottomSheet;
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

        bottomSheet = findViewById(R.id.bottomSheet);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(750);
        bottomSheetBehavior.setMaxHeight(1500);
        bottomSheetBehavior.setDraggable(true);

        //buttons
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
    static void displayPolyline(String routeName) {
        if(routeLines.containsKey(routeName)) {
            Polyline routeLine = routeLines.get(routeName);
            if (routeLine != null) {
                map.getOverlayManager().add(0,routeLine);
                Log.d("POLYLINE OVERLAY", map.getOverlayManager().toString());
                map.invalidate();
            }
        }
    }

    static void removePolyline(String routeName) {
        if(routeLines.containsKey(routeName)) {
            Polyline routeLine = routeLines.get(routeName);
            if (routeLine != null) {
                map.getOverlayManager().remove(routeLine);
                map.invalidate();
            }
        }
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
    public static void displayPathLine(Polyline pathLine) {
        pathLine.getOutlinePaint().setStrokeWidth(10.0f);
        pathLine.getOutlinePaint().setColor(Color.BLACK);
        map.getOverlayManager().add(0,pathLine);
        map.invalidate();
    }
    @Override
    public void userInput(GeoPoint startPoint, GeoPoint endPoint) {
        routeRecommender recommender = new routeRecommender(startPoint, endPoint);
        boolean isDirect = recommender.tripType();
        Bundle bundle = new Bundle();

        if (isDirect) {
            ArrayList<String> directRoutes = recommender.getDirectRoutes(); // routes and fare
            double directFare = recommender.getDirectFare();

            if (!directRoutes.isEmpty()) {
                bundle.putStringArrayList("directRoutes", directRoutes);
                bundle.putDouble("directFare", directFare);

                tab2 tab2Fragment = new tab2();
                tab2Fragment.setArguments(bundle);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .disallowAddToBackStack()
                        .replace(R.id.fragmentContainer, tab2Fragment)
                        .commit();
            } else {
                Toast.makeText(this, "No routes found", Toast.LENGTH_SHORT).show();
                map.getOverlays().clear();
            }
        } else {
            ArrayList<String> firstRoutes = recommender.getFirstLegRoutes(); // route
            ArrayList<String> secondRoutes = recommender.getSecondLegRoutes();
            double firstLegFare = recommender.getFirstLegFare();
            double secondLegFare = recommender.getSecondLegFare();
            GeoPoint commonPoint = recommender.getTransferPoint();
            markLoc(commonPoint, "commonPoint");

            if (!firstRoutes.isEmpty() && !secondRoutes.isEmpty()) {
                bundle.putStringArrayList("firstLeg", firstRoutes);
                bundle.putStringArrayList("secondLeg", secondRoutes);
                bundle.putDouble("firstFare", firstLegFare);
                bundle.putDouble("secondFare", secondLegFare);

                tab3 tab3Fragment = new tab3();
                tab3Fragment.setArguments(bundle);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .disallowAddToBackStack()
                        .replace(R.id.fragmentContainer, tab3Fragment)
                        .commit();
            } else {
                Toast.makeText(this, "No routes found", Toast.LENGTH_SHORT).show();
                map.getOverlays().clear();
            }
        }
    }

    boolean startMarker = false;
    boolean endMarker = false;
    public void markLoc(GeoPoint location, String buttonType) {
        if(location!=null) {
            if(Objects.equals(buttonType, "start")) {
                Log.d("MainActivity", "Marking location: " + location);

                if(startMarker) {
                    for(int i=0;i<map.getOverlays().size();i++){
                        Overlay overlay=map.getOverlays().get(i);
                        if(overlay instanceof Marker&&((Marker) overlay).getId().equals("Start Marker")){
                            map.getOverlays().remove(overlay);
                        }
                    }
                }

                Marker marker = new Marker(map);
                marker.setId("Start Marker");
                marker.setPosition(location);
                marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_add_location_22, null));
                map.getOverlays().add(marker);
                map.getController().setCenter(location);
                map.invalidate();
                startMarker = true;
            }
            if (Objects.equals(buttonType, "destin")) {
                Log.d("MainActivity", "Marking location: " + location);

                if(endMarker) {
                    for(int i=0;i<map.getOverlays().size();i++){
                        Overlay overlay=map.getOverlays().get(i);
                        if(overlay instanceof Marker&&((Marker) overlay).getId().equals("End Marker")){
                            map.getOverlays().remove(overlay);
                        }
                    }
                }

                Marker marker = new Marker(map);
                marker.setId("End Marker");
                marker.setPosition(location);
                marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_marker_23, null));
                map.getOverlays().remove(marker);
                map.getOverlays().add(marker);
                map.getController().setCenter(location);
                map.invalidate();
                endMarker = true;
            }
            if (Objects.equals(buttonType, "commonPoint")) {
                Marker marker = new Marker(map);
                marker.setPosition(location);
                marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_add_location_24, null));
                map.getOverlays().add(marker);
                map.getController().setCenter(location);
                map.invalidate();
            }
        }
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