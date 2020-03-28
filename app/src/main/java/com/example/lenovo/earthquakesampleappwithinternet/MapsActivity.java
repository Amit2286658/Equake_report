package com.example.lenovo.earthquakesampleappwithinternet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public MapsActivity() {
        /*
         * creating an empty constructor to call this fragmentActivity from the fragment adapter*/
    }

    private GoogleMap mMap;
    private Context mapsContext = MapsActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map_link is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map_link once available.
     * This callback is triggered when the map_link is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        /*seting the map type*/
        googleMap.setMapType(returnMapType());

        double longitude=gettingLongitude();
        double latitude=gettingLatitude();
        String place=getPlace();
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latitude/*-34*/, longitude/*151*/);
        mMap.addMarker(new MarkerOptions().position(sydney).title(place));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setBuildingsEnabled(true);

        /*setting a circle on the map*/
        drawCircle(new LatLng(latitude, longitude));
    }

    private double gettingLatitude(){
        Intent i=getIntent();
        return i.getExtras().getDouble("latitude",0);
    }
    private double gettingLongitude(){
        Intent i=getIntent();
        return i.getExtras().getDouble("longitude",0);
    }
    private String getPlace(){
        Intent i=getIntent();
        return i.getExtras().getString("place","");
    }
    private double getCircleRadiusFromMagnitude(){
        Intent i = getIntent();
        return (Math.exp((int)Double.parseDouble(i.getExtras().getString("mag_to_radius"))/1.01-0.13))*1000;
    }

    private int returnMapType(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String mapType= sharedPrefs.getString(
                getString(R.string.map_key),
                getString(R.string.map_default)
        );
        if (mapType.equalsIgnoreCase("normal")){
            return GoogleMap.MAP_TYPE_NORMAL;
        }else if (mapType.equalsIgnoreCase("hybrid")){
            return GoogleMap.MAP_TYPE_HYBRID;
        }else if (mapType.equalsIgnoreCase("satellite")){
            return GoogleMap.MAP_TYPE_SATELLITE;
        }else if (mapType.equalsIgnoreCase("terrain")){
            return GoogleMap.MAP_TYPE_TERRAIN;
        }else {
            return GoogleMap.MAP_TYPE_NONE;
        }
    }

    private int getZoomLevel(Circle circle){
        int zoomLevel=11;
        if (circle!=null){
            double radius=circle.getRadius()+circle.getRadius()/2;
            double scale = radius/500;
            zoomLevel = (int)(16-Math.log(scale)/Math.log(2));
        }
        return zoomLevel;
    }

    private void drawCircle(LatLng points){
        CircleOptions circleOptions=new CircleOptions();
        circleOptions.center(points);
        circleOptions.radius(getCircleRadiusFromMagnitude());
        int circleAndStroke_color= getColorResouceId(Double.parseDouble(getIntent().getExtras().getString("mag_to_radius")));
        circleOptions.strokeColor(circleAndStroke_color);
        circleOptions.strokeWidth(2f);
        circleOptions.fillColor(circleAndStroke_color);
        Circle circle = mMap.addCircle(circleOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(circleOptions.getCenter(), getZoomLevel(circle)));
    }

    private int getColorResouceId(double magnitude){
        int color_resource_id;
        int color_id=(int)magnitude;

        /*switch statement to return the correct id of color based on the value of magnitude*/
        switch (color_id){
            case 0:
                color_resource_id=R.color.magnitude0_transparent;
                break;
            case 1:
                color_resource_id=R.color.magnitude1_transparent;
                break;
            case 2:
                color_resource_id=R.color.magnitude2_transparent;
                break;
            case 3:
                color_resource_id=R.color.magnitude3_transparent;
                break;
            case 4:
                color_resource_id=R.color.magnitude4_transparent;
                break;
            case 5:
                color_resource_id=R.color.magnitude5_transparent;
                break;
            case 6:
                color_resource_id=R.color.magnitude6_transparent;
                break;
            case 7:
                color_resource_id=R.color.magnitude7_transparent;
                break;
            case 8:
                color_resource_id=R.color.magnitude8_transparent;
                break;
            case 9:
                color_resource_id=R.color.magnitude9_transparent;
                break;
            case 10:
                color_resource_id=R.color.magnitude10_transparent;
                break;
            default:
                color_resource_id=R.color.magnitude11_transparent;
                break;
        }
        return  ContextCompat.getColor(mapsContext,color_resource_id);
    }
}
