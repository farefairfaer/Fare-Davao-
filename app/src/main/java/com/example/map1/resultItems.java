package com.example.map1;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class resultItems implements Parcelable {
    List<String> DRList = new ArrayList<>();
    List<String> FRList = new ArrayList<>();
    List<String> SRList = new ArrayList<>();
    List<GeoPoint> FRPath = new ArrayList<>();
    List<GeoPoint> SRPath = new ArrayList<>();
    double FRregFare, SRregFare, FRdiscFare, SRdiscFare, FRdistance, SRdistance;
    List<GeoPoint> DRPath = new ArrayList<>();
    double DRregFare, DRdiscFare, DRdistance;
    GeoPoint transferPoint;
    int viewType;

    public resultItems(int viewType, List<String> directRoutes, List<GeoPoint> pathLine, double fare,
                       double discFare, double distance) {
        this.viewType = viewType;
        this.DRList = directRoutes;
        this.DRPath = pathLine;
        this.DRregFare = fare;
        this.DRdiscFare = discFare;
        this.DRdistance = distance;
    }

    public resultItems(int viewType, List<String> firstRoutes, List<String> secondRoutes, List<GeoPoint> FRPath, List<GeoPoint> SRPath,
                       GeoPoint transferPoint, double regFirstFare, double regSecondFare, double discFirstFare,
                       double discSecondFare, double firstDistance, double secondDistance) {
        this.viewType = viewType;
        this.FRList = firstRoutes;
        this.SRList = secondRoutes;
        this.FRPath = FRPath;
        this.SRPath = SRPath;
        this.transferPoint = transferPoint;
        this.FRregFare = regFirstFare;
        this.SRregFare = regSecondFare;
        this.FRdiscFare = discFirstFare;
        this.SRdiscFare = discSecondFare;
        this.FRdistance = firstDistance;
        this.SRdistance = secondDistance;
    }

    public GeoPoint getTransferPoint() {
        return transferPoint;
    }

    public void setTransferPoint(GeoPoint transferPoint) {
        this.transferPoint = transferPoint;
    }

    protected resultItems(Parcel in) {
        DRList = in.createStringArrayList();
        FRList = in.createStringArrayList();
        SRList = in.createStringArrayList();

        FRPath = new ArrayList<>();
        in.readList(FRPath, GeoPoint.class.getClassLoader());
        SRPath = new ArrayList<>();
        in.readList(SRPath, GeoPoint.class.getClassLoader());

        DRPath = new ArrayList<>();
        in.readList(DRPath, GeoPoint.class.getClassLoader());

        FRregFare = in.readDouble();
        SRregFare = in.readDouble();
        FRdiscFare = in.readDouble();
        SRdiscFare = in.readDouble();
        FRdistance = in.readDouble();
        SRdistance = in.readDouble();
        DRregFare = in.readDouble();
        DRdiscFare = in.readDouble();
        DRdistance = in.readDouble();

        viewType = in.readInt();
    }

    public static final Creator<resultItems> CREATOR = new Creator<resultItems>() {
        @Override
        public resultItems createFromParcel(Parcel in) {
            return new resultItems(in);
        }

        @Override
        public resultItems[] newArray(int size) {
            return new resultItems[size];
        }
    };

    public int getViewType() {
        return viewType;
    }

    public List<GeoPoint> getSRPath() {
        return SRPath;
    }

    public void setSRPath(List<GeoPoint> SRPath) {
        this.SRPath = SRPath;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public List<String> getFRList() {
        return FRList;
    }

    public void setFRList(List<String> FRList) {
        this.FRList = FRList;
    }

    public List<String> getSRList() {
        return SRList;
    }

    public void setSRList(List<String> SRList) {
        this.SRList = SRList;
    }

    public List<GeoPoint> getFRPath() {
        return FRPath;
    }

    public void setFRPath(List<GeoPoint> FRPath) {
        this.FRPath = FRPath;
    }

    public Double getSRregFare() {
        return SRregFare;
    }

    public void setSRregFare(Double SRregFare) {
        this.SRregFare = SRregFare;
    }

    public Double getFRregFare() {
        return FRregFare;
    }

    public void setFRregFare(Double FRregFare) {
        this.FRregFare = FRregFare;
    }

    public Double getFRdiscFare() {
        return FRdiscFare;
    }

    public void setFRdiscFare(Double FRdiscFare) {
        this.FRdiscFare = FRdiscFare;
    }

    public Double getSRdiscFare() {
        return SRdiscFare;
    }

    public void setSRdiscFare(Double SRdiscFare) {
        this.SRdiscFare = SRdiscFare;
    }

    public Double getFRdistance() {
        return FRdistance;
    }

    public void setFRdistance(Double FRdistance) {
        this.FRdistance = FRdistance;
    }

    public Double getSRdistance() {
        return SRdistance;
    }

    public void setSRdistance(Double SRdistance) {
        this.SRdistance = SRdistance;
    }

    public Double getDRdiscFare() {
        return DRdiscFare;
    }

    public void setDRdiscFare(Double DRdiscFare) {
        this.DRdiscFare = DRdiscFare;
    }

    public Double getDRregFare() {
        return DRregFare;
    }

    public void setDRregFare(Double DRregFare) {
        this.DRregFare = DRregFare;
    }

    public List<String> getDRList() {
        return DRList;
    }

    public void setDRList(List<String> DRList) {
        this.DRList = DRList;
    }

    public List<GeoPoint> getDRPath() {
        return DRPath;
    }

    public void setDRPath(List<GeoPoint> DRPath) {
        this.DRPath = DRPath;
    }

    public Double getFares() {
        return DRregFare;
    }

    public void setFares(Double fare) {
        this.DRregFare = fare;
    }

    public Double getDiscFares() {
        return DRdiscFare;
    }

    public void setDiscFares(Double discFare) {
        this.DRdiscFare = discFare;
    }

    public Double getDRdistance() {
        return DRdistance;
    }

    public void setDRdistance(Double distance) {
        this.DRdistance = distance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeStringList(DRList);
        dest.writeStringList(FRList);
        dest.writeStringList(SRList);

        dest.writeList(FRPath);
        dest.writeList(SRPath);
        dest.writeList(DRPath);

        dest.writeDouble(FRregFare);
        dest.writeDouble(SRregFare);
        dest.writeDouble(FRdiscFare);
        dest.writeDouble(SRdiscFare);
        dest.writeDouble(FRdistance);
        dest.writeDouble(SRdistance);
        dest.writeDouble(DRregFare);
        dest.writeDouble(DRdiscFare);
        dest.writeDouble(DRdistance);

        dest.writeInt(viewType);
    }
}
