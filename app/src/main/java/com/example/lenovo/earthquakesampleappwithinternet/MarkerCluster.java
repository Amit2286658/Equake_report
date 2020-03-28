package com.example.lenovo.earthquakesampleappwithinternet;

import android.os.Bundle;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerCluster  implements ClusterItem {

    private LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private Bundle bundle;
    public MarkerCluster(LatLng points){
        mPosition = points;
    }

    public MarkerCluster(LatLng points, String title, String snippet, Bundle bundle){
        mPosition = points;
        mTitle = title;
        mSnippet = snippet;
        this.bundle = bundle;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public Bundle getBundle() {
        return bundle;
    }
}
