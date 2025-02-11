package com.example.map1;

import static com.example.map1.MainActivity.displayPathLine;
import static com.example.map1.MainActivity.routesList;
import android.util.Log;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class routeRecommender {
    public GeoPoint startPoint;
    public GeoPoint endPoint;
    public ArrayList<String> directRoutes = new ArrayList<>();
    public ArrayList<String> firstLegRoutes = new ArrayList<>();
    public ArrayList<String> secondLegRoutes = new ArrayList<>();
    private Polyline firstLegLine = new Polyline();
    private Polyline secondLegLine = new Polyline();
    private Polyline directLine = new Polyline();
    private GeoPoint transferPoint;
    private double firstLegFare;
    private double secondLegFare;
    private double directFare;
    private double directDistance;
    private double firstLegDistance;
    private double secondLegDistance;
    routeRecommender(GeoPoint startPoint, GeoPoint endPoint)  {
        this.startPoint= startPoint;
        this.endPoint = endPoint;
    }
    public boolean tripType() {
        boolean isDirect = false;
        List<String> startRoutes = findRoutes(routesList, startPoint); //names of routes with a
        List<String> endRoutes = findRoutes(routesList, endPoint); //names of routes with b
        Log.d("STARTROUTES", startRoutes.toString());
        Log.d("ENDROUTES", endRoutes.toString());

        if(!startRoutes.isEmpty() && !endRoutes.isEmpty()) {
            List<String> directRoutesList = findDirectRoutes(startRoutes, endRoutes);
            if(!directRoutesList.isEmpty()) {
                List<GeoPoint> shortestDirectPath = findDirectPath(directRoutesList); //returns shortest path from A to B
                setDirectFare(shortestDirectPath);
                directLine.setPoints(shortestDirectPath);
                displayPathLine(directLine);
                isDirect = true;
            } else {
                Map<String, String> doubleRoutePairs = findDoubleRoutes(startRoutes, endRoutes);
                Log.d("PAIRS", doubleRoutePairs.toString());
                setFirstLegRoutes(doubleRoutePairs);
                setSecondLegRoutes(doubleRoutePairs);
                displayPathLine(firstLegLine);
                displayPathLine(secondLegLine);
            }
        }
        return isDirect;
    }

    private Map<String, String> findDoubleRoutes(List<String> startRoutes, List<String> endRoutes) {
        Map<String, String> doubleRoutePairs = new HashMap<>();
        double pathDistance = Double.MAX_VALUE;
        List<GeoPoint> firstLeg;
        List<GeoPoint> secondLeg;

        for(String startRoute : startRoutes) {
            for (String endRoute : endRoutes) {
                if(!Objects.equals(startRoute, endRoute)) {
                    int startPointL1 = 0;
                    int transferPointA = 0;
                    int startPointL2 = 0;
                    int endPointL2 = 0;

                    List<GeoPoint> startRoutePoints = routesList.get(startRoute);
                    List<GeoPoint> endRoutePoints = routesList.get(endRoute);

                    List<Integer> startIndices = findIndices(startRoutePoints, startPoint);
                    List<Integer> endIndexes = findIndices(endRoutePoints, endPoint);

                    List<Integer> transferPointsA = findClosePoints(startRoute, endRoute); //points on routeA that are close to routeB
                    List<Integer> transferPointsB = findClosePoints(endRoute, startRoute); //points on routeB that are close to routeA

                    Log.d("START INDICES", startIndices.toString());
                    Log.d("END INDICES", endIndexes.toString());

                    if(!transferPointsA.isEmpty() && !transferPointsB.isEmpty()) { // if the routes are close enough
                        double transferDistance = Double.MAX_VALUE;

                        for (Integer transferPoint : transferPointsA) {
                            for (Integer startPoint1 : startIndices) {
                                if(startPoint1<transferPoint) {
                                  double legDistance = findDistance(startRoutePoints.subList(startPoint1, transferPoint));
                                  if(legDistance < transferDistance) {
                                      transferDistance = legDistance;
                                      transferPointA = transferPoint;
                                      startPointL1 = startPoint1;
                                  }
                                }
                            }
                        }
                        double minDistance = Double.MAX_VALUE;
                        double endPathDistance = Double.MAX_VALUE;
                        for (Integer transferPoint : transferPointsB) {
                            for (Integer endPoint1 : endIndexes) {
                                if (transferPoint < endPoint1 ) {
                                    double distance = endRoutePoints.get(transferPoint).distanceToAsDouble(startRoutePoints.get(transferPointA));
                                    double distance2 = findDistance(endRoutePoints.subList(transferPoint, endPoint1 + 1));
                                    if (distance < minDistance && distance2 < endPathDistance) {
                                        minDistance = distance;
                                        endPathDistance = distance2;
                                        endPointL2 = endPoint1;
                                        startPointL2 = transferPoint;
                                    }
                                }
                            }
                        }
                        firstLeg = startRoutePoints.subList(startPointL1, transferPointA);
                        secondLeg = endRoutePoints.subList(startPointL2, endPointL2);


                        if (!firstLeg.isEmpty() && !secondLeg.isEmpty()) {
                            double firstLegD = findDistance(firstLeg);
                            double secondLegD = findDistance(secondLeg);
                            if(firstLegD+secondLegD < pathDistance + 400) {
                                pathDistance = firstLegD+secondLegD;
                                String startKey = "StartRoute";
                                String endKey = "EndRoute";
                                doubleRoutePairs.put(startKey.concat(startRoute), startRoute);
                                doubleRoutePairs.put(endKey.concat(endRoute), endRoute);

                                setFirstLegLine(firstLeg);
                                setFirstLegFare(firstLeg);
                                setSecondLegLine(secondLeg);
                                setSecondLegFare(secondLeg);
                                setTransferPoint(transferPointA, startRoute);
                            }
                            Log.d("FINDING PAIRS", startRoute + endRoute);
                            Log.d("DOUBLEROUTEPAIRS", doubleRoutePairs.toString());
                            Log.d("POINTS", " startPointL1: " + startPointL1 + " transferPointA: " + transferPointA + " startPointL2: " +  startPointL2 +  " endPointL2: " + endPointL2);
                        }
                    }
                }
            }
        }
        return doubleRoutePairs;
    }

    private List<Integer> findClosePoints(String routeA, String routeB) {
        List<Integer> closePoints = new ArrayList<>();
        List<GeoPoint> routeAPoints = routesList.get(routeA);
        List<GeoPoint> routeBPoints = routesList.get(routeB);

        for(int i = 0; i < routeAPoints.size(); i++) {
            for(int j = 0; j < routeBPoints.size(); j++) {
                if(routeAPoints.get(i).distanceToAsDouble(routeBPoints.get(j)) < 100) {
                    if(!closePoints.contains(i)) {
                        closePoints.add(i);
                    }
                }
            }
        }

        Log.d("FINDING CLOSE POINTS", routeA + " indexes relative to " + routeB + " is close to:" + closePoints.toString());

        return closePoints; // relative to routeA
    }
    private List<Integer> findIndices(List<GeoPoint> routePoints, GeoPoint givPoint) {
        List<Integer> indices1 = new ArrayList<>();
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < routePoints.size(); i++) {
            GeoPoint point = routePoints.get(i);
            double distance = point.distanceToAsDouble(givPoint);
            if (distance < minDistance) {
                minDistance = distance;
                indices1.clear(); // Clear previous indices since we found a new minimum
                indices1.add(i);
            } else if (distance == minDistance) {
                indices1.add(i); // Add multiple closest indices if they exist
            }
        }

        List<Integer> indices = new ArrayList<>(indices1);

        for(int index: indices1) {
            GeoPoint referencePoint = routePoints.get(index);
            for(int i = 0; i < routePoints.size(); i++) {
                if(referencePoint.distanceToAsDouble(routePoints.get(i)) < 20 && !indices.contains(i)) {
                    indices.add(i);
                }
            }
        }

        return indices;
    }
    private List<GeoPoint> findDirectPath(List<String> directRoutesList) {
        List<GeoPoint> directPath = new ArrayList<>();
        List<String> fastestRoutes = new ArrayList<>();
        double globalMinDistance = Double.MAX_VALUE;

        Map<String, Double> routeDistances = new HashMap<>();
        Map<String, List<GeoPoint>> routePaths = new HashMap<>();

        for (String route : directRoutesList) {
            List<GeoPoint> routePoints = routesList.get(route);
            List<Integer> startIndexes = findIndices(routePoints, startPoint);
            List<Integer> endIndexes = findIndices(routePoints, endPoint);
            double minPathDistance = Double.MAX_VALUE;
            List<GeoPoint> bestPath = new ArrayList<>();

            Log.d("closestPoint", "Start indexes: " + startIndexes);
            Log.d("closestPoint", "End indexes: " + endIndexes);

            for (int startIndex : startIndexes) {
                for (int endIndex : endIndexes) {
                    List<GeoPoint> currentPath;
                    double currentDistance;

                    if (startIndex < endIndex) {
                        currentPath = new ArrayList<>(routePoints.subList(startIndex, endIndex +1));
                    } else {
                        currentPath = new ArrayList<>();
                        int size = routePoints.size();

                        for (int i = startIndex; i < size; i++) {
                            currentPath.add(routePoints.get(i));
                        }
                        for (int i = 0; i < endIndex; i++) {
                            currentPath.add(routePoints.get(i));
                        }
                    }
                    currentDistance = findDistance(currentPath);
                    Log.d("Path Distance", "Route: " + route + ", Distance: " + currentDistance);

                    Log.d("POINTS", " startPoint: " + startIndex + " endPoint: " + endIndex);

                    if (currentDistance < minPathDistance) {
                        minPathDistance = currentDistance;
                        bestPath = new ArrayList<>(currentPath);
                         Log.d("POINTS2", currentPath.toString());

                    }
                }
            }
            routeDistances.put(route, minPathDistance);
            routePaths.put(route, bestPath);
            globalMinDistance = Math.min(globalMinDistance, minPathDistance);
        }

        for (String route : routeDistances.keySet()) {
            if (routeDistances.get(route) == globalMinDistance) {
                fastestRoutes.add(route);
            }
        }
        if (!fastestRoutes.isEmpty()) {
            directPath = routePaths.get(fastestRoutes.get(0));
        }
        setDirectRoutes(fastestRoutes);
        return directPath;
    }


    public List<String> findRoutes(Map<String, List<GeoPoint>> routesList, GeoPoint point) {
        List<String> closeRoutes = new ArrayList<>();
        for(String routeName : routesList.keySet()) {
            List<GeoPoint> routePoints = routesList.get(routeName);
            for(GeoPoint pointOnRoute : routePoints) {
                if (pointOnRoute.distanceToAsDouble(point) < 400) {
                    if(!closeRoutes.contains(routeName)) {
                        closeRoutes.add(routeName);
                    }
                }
            }
        }
        return closeRoutes;
    }

    public List<String> findDirectRoutes(List<String> startRoutes, List<String> endRoutes) {
        List<String> directRoutesList = new ArrayList<>();
        for (String routeName : startRoutes) {
            if (endRoutes.contains(routeName)) {
                if(!directRoutesList.contains(routeName)) {
                    directRoutesList.add(routeName);
                }
            }
        }
        Log.d("FINDING DIRECT ROUTES", directRoutesList.toString());
       return directRoutesList;
    }

    public double calcFare(List<GeoPoint> shortestPath) {
        double fare = 0.0;
        double baseFare = 13;
        double roundedFare =0.0;
        double rate=0.0018;
        double distance = findDistance(shortestPath);
        Log.d("DISTANCE", String.valueOf(distance));

        if(distance > 4000) {
            fare = baseFare + (distance - 4000) * rate;
            roundedFare = (double) Math.round(fare * 4) / 4;

            fare = roundedFare;
        } else {
            fare = baseFare;
        }

        return fare;
    }
    public double findDistance(List<GeoPoint> points) {
        double distance =0;
        for(int i=0 ; i<points.size()-1; i++) {
            distance += points.get(i).distanceToAsDouble(points.get(i+1));
        }
        return distance;
    }
    private void setTransferPoint(int transferPointA, String routeName) {
        List<GeoPoint> routePoints = routesList.get(routeName);
        transferPoint = routePoints.get(transferPointA);
    }

    private void setDirectDistance(double directDistance) {
        this.directDistance = directDistance;
    }

    public double getDirectDistance() {
        return directDistance;
    }

    private void setFirstLegDistance(double firstLegDistance) {
        this.firstLegDistance = firstLegDistance;
    }
    public double getFirstLegDistance() {
        return firstLegDistance;
    }
    private void setSecondLegDistance(double secondLegDistance) {
        this.secondLegDistance = secondLegDistance;
    }
    public double getSecondLegDistance() {
        return secondLegDistance;
    }
    private void setDirectRoutes(List<String> directRoutesList) {
        directRoutes.addAll(directRoutesList);
    }
    public ArrayList<String> getDirectRoutes() { //returns fare and route
        return directRoutes;
    }
    public GeoPoint getTransferPoint() {
        return transferPoint;
    }
    public void setFirstLegLine(List<GeoPoint> pathPoints) {
        firstLegLine.setPoints(pathPoints);
    }
    public void setFirstLegFare(List<GeoPoint> pathPoints) {
        firstLegFare = calcFare(pathPoints);
    }
    public void setSecondLegFare(List<GeoPoint> pathPoints) {
        secondLegFare = calcFare(pathPoints);
    }
    public void setDirectFare(List<GeoPoint> pathPoints) {
        directFare = calcFare(pathPoints);
        Log.d("DIRECT FARE", String.valueOf(directFare));
    }
    public double getDirectFare() {
        return directFare;
    }
    public void setSecondLegLine(List<GeoPoint> pathPoints) {
        secondLegLine.setPoints(pathPoints);
    }
    public void setFirstLegRoutes(Map<String, String> pairs) {
        for(String startRoute : pairs.keySet()) {
            if(startRoute.contains("StartRoute".concat(pairs.get(startRoute)))) {
                firstLegRoutes.add(pairs.get(startRoute));
            }
        }
    }
    public void setSecondLegRoutes(Map<String, String> pairs) {
        for(String endRoute : pairs.keySet()) {
            if(endRoute.contains("EndRoute".concat(pairs.get(endRoute)))) {
                secondLegRoutes.add(pairs.get(endRoute));
                Log.d("SECONDLEGROUTES", secondLegRoutes.toString());
            }
        }
    }

    public ArrayList<String> getFirstLegRoutes() {
        return firstLegRoutes;
    }
    public ArrayList<String> getSecondLegRoutes() { //route and fare of secondleg
        return secondLegRoutes;
    }
    public double getFirstLegFare() {
        return firstLegFare;
    }
    public double getSecondLegFare() {
        return secondLegFare;
    }
}



