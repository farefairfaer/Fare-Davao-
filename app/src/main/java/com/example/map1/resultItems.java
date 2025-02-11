package com.example.map1;

public class resultItems {
    String RouteName;
    Integer Color;

    resultItems(String RouteName, Integer Color) {
        this.RouteName = RouteName;
        this.Color = Color;
    }

    public String getRouteName() {
        return RouteName;
    }

    public void setRouteName(String routeName) {
        RouteName = routeName;
    }

    public Integer getColor() {
        return Color;
    }

    public void setColor(Integer color) {
        Color = color;
    }
}
