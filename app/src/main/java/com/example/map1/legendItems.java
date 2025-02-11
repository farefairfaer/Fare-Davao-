package com.example.map1;

import java.util.List;

public class legendItems {
    String routeName;
    int colors;

    legendItems(String routeName, int colors) {
        this.routeName = routeName;
        this.colors = colors;
    }

    public int getColors() {
        return colors;
    }

    public void setColors(int colors) {
        this.colors = colors;
    }

    public String getRoutes() {
        return routeName;
    }

    public void setRoutes(String routes) {
        this.routeName = routeName;
    }
}
