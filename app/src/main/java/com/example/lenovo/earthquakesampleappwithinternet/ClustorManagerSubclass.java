package com.example.lenovo.earthquakesampleappwithinternet;

import android.content.Context;
import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.ClusterManager;

public class ClustorManagerSubclass extends ClusterManager<MarkerCluster> {

    public ClustorManagerSubclass(Context context, GoogleMap map) {
        super(context, map);
    }

}
