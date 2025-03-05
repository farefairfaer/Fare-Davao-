package com.example.map1;

import static com.example.map1.MainActivity.routeColors;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.KmlFeature;
import org.osmdroid.bonuspack.kml.KmlPlacemark;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class routeData {

    public Map<String, List<GeoPoint>> loadJeepneyRoutes(Context context, String[] geoJsonFileNames) {
        Map<String, List<GeoPoint>> jeepneyRoutes = new HashMap<>();
        KmlDocument kmlDocument = new KmlDocument();

        for (String fileName : geoJsonFileNames) {
            try {
                String geoJson = readFileFromAssets(context, fileName);
                kmlDocument.parseGeoJSON(geoJson);
                String routeName = fileName.replace(".geojson", "").replace("_", " ");
                for (KmlFeature feature : kmlDocument.mKmlRoot.mItems) {
                    if (feature instanceof KmlPlacemark placemark) {
                        if (placemark.mGeometry != null) {
                            List<GeoPoint> coordinates = placemark.mGeometry.mCoordinates;
                            jeepneyRoutes.put(routeName, coordinates);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("loadJeepneyRoutes", "Could not load geoJSON file: " + fileName, e);
            }
        }
        Log.d("loadJeepneyRoutes", jeepneyRoutes.toString());
        return jeepneyRoutes;
    }
    public String readFileFromAssets(Context context, String fileName) throws IOException {
        InputStream is = context.getAssets().open(fileName);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        is.close();
        return new String(buffer);
    }

    public Map<String, Polyline> loadLines(Map<String, List<GeoPoint>> parsedRoutes) {
        Map<String, Polyline> routeLines = new HashMap<>();

        for(String routeName : parsedRoutes.keySet()) {
            List<GeoPoint> coordinates = parsedRoutes.get(routeName);
            Polyline line = new Polyline();
            line.setPoints(coordinates);
            int color = routeColors.getOrDefault(routeName, Color.BLACK);
            line.getOutlinePaint().setColor(color);
            line.getOutlinePaint().setStrokeWidth(5.0f);
            routeLines.put(routeName, line);
        }

        if (routeLines.get("MatinaCrossing") == null) {
            Log.d("loadJeepneyRoutes", "Matina polyline doesnt exist" + " " + parsedRoutes.toString());
        }
        return routeLines;
    }
}
