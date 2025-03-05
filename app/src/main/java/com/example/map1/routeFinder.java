package com.example.map1;

import static com.example.map1.MainActivity.routesList;
import android.util.Log;
import org.osmdroid.util.GeoPoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class routeFinder {
    public GeoPoint startPoint;
    public GeoPoint endPoint;
    routeFinder(GeoPoint startPoint, GeoPoint endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }
    public List<resultItems> results() {
        List<resultItems> resultItemsList = new ArrayList<>();
        List<String> startRoutes = findRoutes(startPoint);
        List<String> endRoutes = findRoutes(endPoint);
        Log.d("STARTROUTES", startRoutes.toString());
        Log.d("ENDROUTES", endRoutes.toString());

        if (!startRoutes.isEmpty() && !endRoutes.isEmpty()) {
            List<resultItems> directRoutes = findDirectResults(startRoutes, endRoutes);
            List<resultItems> doubleRoutes = findIndirectRideResults(startRoutes, endRoutes);

            resultItemsList.addAll(directRoutes);
            resultItemsList.addAll(doubleRoutes);
        }
        return resultItemsList;
    }
    private List<resultItems> findDirectResults(List<String> startRoutes, List<String> endRoutes) {
        List<resultItems> directRideItems = new ArrayList<>();

        List<String> directRoutesList = new ArrayList<>();
        for (String routeName : startRoutes) {
            if (endRoutes.contains(routeName)) {
                if (!directRoutesList.contains(routeName)) {
                    directRoutesList.add(routeName);
                }
            }
        }

        List<GeoPoint> bestPath = new ArrayList<>();

        for (String route : directRoutesList) {
            List<GeoPoint> routePoints = routesList.get(route);
            assert routePoints != null;
            List<Integer> startIndexes = findIndices(routePoints, startPoint);
            List<Integer> endIndexes = findIndices(routePoints, endPoint);
            double minPathDistance = Double.MAX_VALUE;

            Log.d("closestPoint", "Start indexes: " + startIndexes);
            Log.d("closestPoint", "End indexes: " + endIndexes);

            for (int startIndex : startIndexes) {
                for (int endIndex : endIndexes) {
                    List<GeoPoint> currentPath;
                    double currentDistance;
                    if (startIndex < endIndex) {
                        currentPath = new ArrayList<>(routePoints.subList(startIndex, endIndex + 1));
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

            double fare = calcFare(bestPath);
            double discountFare = calcDiscFare(bestPath);
            resultItems matchingObject = null;
            for (resultItems existingObject : directRideItems) {
                if (arePathsSimilar(existingObject.getDRPath(), bestPath)) {
                    matchingObject = existingObject;
                    break;
                }
            }
            double distance = Math.round((minPathDistance / 1000.0) * 10.0) / 10.0;

            if (matchingObject != null) {
                matchingObject.getDRList().add(route);
            } else if (distance < 20000){
                List<String> routeList = new ArrayList<>();
                routeList.add(route);
                directRideItems.add(new resultItems(1, routeList, bestPath, fare, discountFare, distance));
            }
        }
        Log.d("DIRECTROUTES", directRideItems.toString());

        return directRideItems;
    }
    private List<resultItems> findIndirectRideResults(List<String> startRoutes, List<String> endRoutes) {
        List<resultItems> indirectRideItems = new ArrayList<>();

        List<GeoPoint> firstLeg;
        List<GeoPoint> secondLeg;

        for(String startRoute : startRoutes) {
            for (String endRoute : endRoutes) {
                if(!Objects.equals(startRoute, endRoute)) {
                    double transferDistance = Double.MAX_VALUE;
                    double transferDistance1 = Double.MAX_VALUE;
                    int startPointL1 = 0;
                    int transferPointA = 0;
                    int startPointL2 = 0;
                    int endPointL2 = 0;

                    List<GeoPoint> startRoutePoints = routesList.get(startRoute);
                    List<GeoPoint> endRoutePoints = routesList.get(endRoute);

                    List<Integer> startIndices = findIndices(startRoutePoints, startPoint);
                    List<Integer> endIndices = findIndices(endRoutePoints, endPoint);

                    List<Integer> transferPointsA = findTransferPoints(startRoute, endRoute); //points on routeA that are close to routeB
                    List<Integer> transferPointsB = findTransferPoints(endRoute, startRoute); //points on routeB that are close to routeA

                    Log.d("START INDICES", startIndices.toString());
                    Log.d("END INDICES", endIndices.toString());
                    Log.d("TRANSFER POINTS A", transferPointsA.toString());
                    Log.d("TRANSFER POINTS B", transferPointsB.toString());

                    if(!transferPointsA.isEmpty() && !transferPointsB.isEmpty() && !startIndices.isEmpty() && !endIndices.isEmpty()) { // if the routes are close enough
                        for (Integer transferPoint : transferPointsA) {
                            for (Integer startPoint1 : startIndices) {
                                if(startPoint1<transferPoint) {
                                    double legDistance = findDistance(startRoutePoints.subList(startPoint1, transferPoint+1));
                                    if(legDistance < transferDistance) {
                                        transferDistance = legDistance;
                                        transferPointA = transferPoint;
                                        startPointL1 = startPoint1;
                                    }
                                }
                            }
                        }
                        for (Integer endPoint1 : endIndices) {
                            for (Integer transferPoint : transferPointsB) {
                                if (transferPoint < endPoint1) {
                                    double distance = endRoutePoints.get(transferPoint).distanceToAsDouble(startRoutePoints.get(transferPointA));
                                    if (distance < transferDistance1) {
                                        transferDistance1 = distance;
                                        endPointL2 = endPoint1;
                                        startPointL2 = transferPoint;
                                    }
                                }
                            }
                        }
                            Log.d("TRANSFER POINT B", endRoute + " " + startPointL2 + " " + endPointL2);
                            firstLeg = startRoutePoints.subList(startPointL1, transferPointA);
                            secondLeg = endRoutePoints.subList(startPointL2, endPointL2);

                            if (!firstLeg.isEmpty() && !secondLeg.isEmpty()) {
                                double FRdistance = Math.round((findDistance(firstLeg) / 1000.0) * 10.0) / 10.0;
                                double SRdistance = Math.round((findDistance(secondLeg) / 1000.0) * 10.0) / 10.0;
                                double SRregFare = calcFare(secondLeg);
                                double SRdiscFare = calcDiscFare(secondLeg);
                                double FRregFare = calcFare(firstLeg);
                                double FRdiscFare = calcDiscFare(firstLeg);
                                double distancebtn = startRoutePoints.get(transferPointA).distanceToAsDouble(endRoutePoints.get(startPointL2));

                                resultItems matchingObject = null;
                                for (resultItems existingObject : indirectRideItems) {
                                    if (arePathsSimilar(existingObject.getFRPath(), firstLeg) && arePathsSimilar(existingObject.getSRPath(), secondLeg)) {
                                        matchingObject = existingObject;
                                        break;
                                    }
                                }

                                if (matchingObject != null) {
                                    if (!matchingObject.getFRList().contains(startRoute)) {
                                        matchingObject.getFRList().add(startRoute);
                                    }
                                    if (!matchingObject.getSRList().contains(endRoute)) {
                                        matchingObject.getSRList().add(endRoute);
                                    }
                                } else {
                                    if (FRdistance != 0 && SRdistance >= 0.5 && distancebtn < 100) {
                                        List<String> FRList = new ArrayList<>();
                                        List<String> SRList = new ArrayList<>();
                                        FRList.add(startRoute);
                                        SRList.add(endRoute);
                                        indirectRideItems.add(new resultItems(2, FRList, SRList, firstLeg, secondLeg,
                                                endRoutePoints.get(startPointL2), FRregFare, SRregFare, FRdiscFare, SRdiscFare, FRdistance, SRdistance));
                                    }
                                }
                                Log.d("FINDING PAIRS", startRoute + endRoute);
                                Log.d("POINTS", " startPointL1: " + startPointL1 + " transferPointA: " + transferPointA + " startPointL2: " + startPointL2 + " endPointL2: " + endPointL2);
                        }
                    }
                }
            }
        }
        return indirectRideItems;
    }
    private boolean arePathsSimilar(List<GeoPoint> path1, List<GeoPoint> path2) {
        if (path1.isEmpty() || path2.isEmpty()) return false;

        int matchCount = 0;
        int totalPoints = Math.min(path1.size(), path2.size()); // Compare based on the smaller list

        for (GeoPoint point1 : path1) {
            for (GeoPoint point2 : path2) {
                if (point1.equals(point2)) { // EXACT point match
                    matchCount++;
                    break; // Move to the next point in path1 after finding a match
                }
            }
        }

        double similarity = (double) matchCount / totalPoints;
        return similarity >= 0.9; // Require at least 90% identical points
    }

    public List<String> findRoutes(GeoPoint point) {
        List<String> closeRoutes = new ArrayList<>();
        for (String routeName : routesList.keySet()) {
            List<GeoPoint> routePoints = routesList.get(routeName);
            for (GeoPoint pointOnRoute : routePoints) {
                if (pointOnRoute.distanceToAsDouble(point) < 400) {
                    if (!closeRoutes.contains(routeName)) {
                        closeRoutes.add(routeName);
                    }
                }
            }
        }
        return closeRoutes;
    }
    public double calcFare(List<GeoPoint> shortestPath) {
        double fare = 0.0;
        double baseFare = 13;
        double roundedFare = 0.0;
        double rate = 0.0018;
        double distance = findDistance(shortestPath);
        Log.d("DISTANCE", String.valueOf(distance));

        if (distance > 4000) {
            fare = baseFare + (distance - 4000) * rate;
            roundedFare = (double) Math.round(fare * 4) / 4;
            fare = roundedFare;
        } else {
            fare = baseFare;
        }

        return fare;
    }

    public double calcDiscFare(List<GeoPoint> shortestPath) {
        double fare = 0.0;
        double baseFare = 9.6;
        double roundedFare = 0.0;
        double rate = 0.00144;
        double distance = findDistance(shortestPath);
        Log.d("DISTANCE", String.valueOf(distance));

        if (distance > 4000) {
            fare = baseFare + (distance - 4000) * rate;
            roundedFare = (double) Math.round(fare * 4) / 4;
            fare = roundedFare;
        } else {
            fare = baseFare;
        }

        return fare;
    }
    private List<Integer> findTransferPoints(String routeA, String routeB) {
        List<Integer> closePoints = new ArrayList<>();
        List<GeoPoint> routeAPoints = routesList.get(routeA);
        List<GeoPoint> routeBPoints = routesList.get(routeB);

        for (int i = 0; i < routeAPoints.size(); i++) {
            for (int j = 0; j < routeBPoints.size(); j++) {
                if (routeAPoints.get(i).distanceToAsDouble(routeBPoints.get(j)) < 100) {
                    if (!closePoints.contains(i)) {
                        closePoints.add(i);
                    }
                }
            }
        }

        Log.d("FINDING CLOSE POINTS", routeA + " indexes relative to " + routeB + " is close to:" + closePoints.toString());

        return closePoints;
    }
    private List<Integer> findIndices(List<GeoPoint> routePoints, GeoPoint givPoint) {
        List<Integer> indices1 = new ArrayList<>();
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < routePoints.size(); i++) {
            GeoPoint point = routePoints.get(i);
            double distance = point.distanceToAsDouble(givPoint);
            if (distance < minDistance) {
                minDistance = distance;
                indices1.clear();
                indices1.add(i);
            } else if (distance == minDistance) {
                indices1.add(i);
            }
        }

        List<Integer> indices = new ArrayList<>(indices1);

        for (int index : indices1) {
            GeoPoint referencePoint = routePoints.get(index);
            for (int i = 0; i < routePoints.size(); i++) {
                if (referencePoint.distanceToAsDouble(routePoints.get(i)) < 20 && !indices.contains(i)) {
                    indices.add(i);
                }
            }
        }
        return indices;
    }
    public double findDistance(List<GeoPoint> points) {
        double distance = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            distance += points.get(i).distanceToAsDouble(points.get(i + 1));
        }
        return Math.round(distance * 10.0) / 10.0;
    }
}