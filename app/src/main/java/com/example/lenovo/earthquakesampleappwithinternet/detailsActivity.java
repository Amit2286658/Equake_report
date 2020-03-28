package com.example.lenovo.earthquakesampleappwithinternet;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import equake_report.contract.constantValues;

public class detailsActivity extends AppCompatActivity {

    private Bundle bundle;
    private MapView mapView;
    private GoogleMap mMap;
    private MyReceiver myReceiver;
    private int activity_identifier = -1;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
            NavUtils.navigateUpFromSameTask(this);
    }

    /**
     * This method is called whenever the user chooses to navigate Up within your application's
     * activity hierarchy from the action bar.
     *
     * <p>If a parent was specified in the manifest for this activity or an activity-alias to it,
     * default Up navigation will be handled automatically. See
     * {@link #getSupportParentActivityIntent()} for how to specify the parent. If any activity
     * along the parent chain requires extra Intent arguments, the Activity subclass
     * should override the method {@link #onPrepareSupportNavigateUpTaskStack(TaskStackBuilder)}
     * to supply those arguments.</p>
     *
     * <p>See <a href="{@docRoot}guide/topics/fundamentals/tasks-and-back-stack.html">Tasks and
     * Back Stack</a> from the developer guide and
     * <a href="{@docRoot}design/patterns/navigation.html">Navigation</a> from the design guide
     * for more information about navigating within your app.</p>
     *
     * <p>See the {@link TaskStackBuilder} class and the Activity methods
     * {@link #getSupportParentActivityIntent()}, {@link #supportShouldUpRecreateTask(Intent)}, and
     * {@link #supportNavigateUpTo(Intent)} for help implementing custom Up navigation.</p>
     *
     * @return true if Up navigation completed successfully and this Activity was finished,
     * false otherwise.
     */
    @Override
    public boolean onSupportNavigateUp() {
            NavUtils.navigateUpFromSameTask(this);
            return true;
    }

    /**
     * Obtain an {@link Intent} that will launch an explicit target activity
     * specified by sourceActivity's {@link NavUtils#PARENT_ACTIVITY} &lt;meta-data&gt;
     * element in the application's manifest. If the device is running
     * Jellybean or newer, the android:parentActivityName attribute will be preferred
     * if it is present.
     *
     * @return a new Intent targeting the defined parent activity of sourceActivity
     */
    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        return new Intent(this, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        bundle = getIntent().getExtras();
        detailsMethod();
        activity_identifier = bundle.getInt(constantValues.ACTIVITY_IDENTIFIER);

        mapView=findViewById(R.id.details_mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(MapTypeByPreference());
                LatLng sydney = new LatLng(bundle.getDouble(constantValues.LATITUDE_DOUBLE)/*-34*/
                        , bundle.getDouble(constantValues.LONGITUDE_DOUBLE)/*151*/);
                mMap.addMarker(new MarkerOptions().position(sydney).title(bundle.getString(constantValues.TITLE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                mMap.getUiSettings().setZoomControlsEnabled(true);
                drawCircle(new LatLng(bundle.getDouble(constantValues.LATITUDE_DOUBLE)
                        , bundle.getDouble(constantValues.LONGITUDE_DOUBLE)));
            }
        });
        ImageView fullMapButton = (ImageView) findViewById(R.id.details_map_full_screen_button);
        fullMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(detailsActivity.this, MapsActivity.class);
                intent.putExtra("latitude",bundle.getDouble(constantValues.LATITUDE_DOUBLE));
                intent.putExtra("longitude",bundle.getDouble(constantValues.LONGITUDE_DOUBLE));
                intent.putExtra("place",bundle.getString(constantValues.TITLE));
                intent.putExtra("mag_to_radius",bundle.getString(constantValues.MAGNITUDE));
                startActivity(intent);
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        /*setting the custom network change listener*/
        IntentFilter networkAvailability = new IntentFilter();
        networkAvailability.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, networkAvailability);

        myReceiver.setOnNetworkChangeListener(new MyReceiver.networkChangeListener() {
            @Override
            public void isNetworkAvailable(boolean networkAvailability) {
                if (!networkAvailability){
                    Snackbar.make(mapView, "Connection lost", Snackbar.LENGTH_LONG).setAction(
                            "Settings", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                    startActivity(intent);
                                }
                            }
                    ).show();
                }
            }
        });
    }

    private void detailsMethod(){
        TextView alert=(TextView)findViewById(R.id.alert_detail);
        alert.setText(bundle.getString(constantValues.ALERT));

        TextView tsunami_warning = (TextView)findViewById(R.id.tsunami_detail);
        tsunami_warning.setText(bundle.getString(constantValues.TSUNAMIWARNING));

        TextView status = (TextView)findViewById(R.id.status_detail);
        status.setText(bundle.getString(constantValues.STATUS));

        TextView felt = (TextView)findViewById(R.id.felt_detail);
        felt.setText(bundle.getString(constantValues.FELT));

        TextView latlong = (TextView)findViewById(R.id.latlon_detail);
        String latlongText=bundle.getString(constantValues.LATITUDE)+", "+bundle.getString(constantValues.LONGITUDE);
        latlong.setText(latlongText);

        TextView depth = (TextView)findViewById(R.id.depth_detail);
        depth.setText(bundle.getString(constantValues.DEPTH));

        TextView intensity  = (TextView)findViewById(R.id.intensity_detail);
        if (bundle.getString(constantValues.INTENSITY).equalsIgnoreCase("null"))
            intensity.setText("n/a");
        else intensity.setText(bundle.getString(constantValues.INTENSITY));

        TextView significance = (TextView)findViewById(R.id.significance_detail);
        if (bundle.getString(constantValues.SIGNIFICANCE).equalsIgnoreCase("null"))
            significance.setText("n/a");
        else significance.setText(bundle.getString(constantValues.SIGNIFICANCE));

        TextView reported = (TextView)findViewById(R.id.updated_detail);
        reported.setText(bundle.getString(constantValues.REPORTEDTIME));

        TextView date = (TextView)findViewById(R.id.date_detail);
        date.setText(bundle.getString(constantValues.DATEANDTIME));

        TextView magnitude = (TextView)findViewById(R.id.magnitude_detail);
        magnitude.setText(bundle.getString(constantValues.MAGNITUDE));

        GradientDrawable magnitudeDrawable = (GradientDrawable)magnitude.getBackground();
        magnitudeDrawable.setColor(getSolidColorResourceId(bundle.getString(constantValues.MAGNITUDE)));

        TextView locationPrimary = (TextView)findViewById(R.id.locationPrimary_detail);
        locationPrimary.setText(bundle.getString(constantValues.LOCATIONPRIMARY_DETAIL));

        TextView locationSecondary = (TextView)findViewById(R.id.locationSecondary_detail);
        locationSecondary.setText(bundle.getString(constantValues.LOCATIONSECONDARY_DETAIL));

        TextView regionText=(TextView)findViewById(R.id.region_detail);
        if (bundle.getString(constantValues.LOCATIONSECONDARY_DETAIL).contains(",")) {
            String regionArray[] = bundle.getString(constantValues.LOCATIONSECONDARY_DETAIL).split(", ");
            regionText.setText(regionArray[1].equalsIgnoreCase("CA") ? "California" : regionArray[1]);
        }else
            regionText.setText(bundle.getString(constantValues.LOCATIONSECONDARY_DETAIL));
    }

    private int returnMapType(){
        SharedPreferences sharedPrefs=PreferenceManager.getDefaultSharedPreferences(this);
        String mapType=sharedPrefs.getString(
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
            return GoogleMap.MAP_TYPE_NORMAL;
        }
    }
    private int getZoomLevel(Circle circle, int radiusDivisor){
        int zoomLevel=11;
        if (circle!=null){
            double radius=circle.getRadius()+circle.getRadius()/2;
            double scale = radius/radiusDivisor;
            zoomLevel = (int)(16-Math.log(scale)/Math.log(2));
        }
        return zoomLevel;
    }

    private void drawCircle(LatLng points){
        CircleOptions circleOptions=new CircleOptions();
        circleOptions.center(points);
        circleOptions.radius(getCircleRadiusFromMagnitude());
        int circleAndStroke_color= getColorResouceId(Double.parseDouble(bundle.getString(constantValues.MAGNITUDE)));
        circleOptions.strokeColor(circleAndStroke_color);
        circleOptions.strokeWidth(2f);
        circleOptions.fillColor(circleAndStroke_color);
        Circle circle = mMap.addCircle(circleOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(circleOptions.getCenter(), getZoomLevel(circle,
                Double.parseDouble(bundle.getString(constantValues.MAGNITUDE))>3 ? 200 : 150)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int position=item.getItemId();
        switch (position){
            case R.id.SeeOnUSSGS :
                String itemUrl=bundle.getString(constantValues.USGSLINK_DETAIL);
                String itemTitle=bundle.getString(constantValues.TITLE);

                SharedPreferences sharedPrefs=PreferenceManager.getDefaultSharedPreferences(detailsActivity.this);
                boolean browser_switch=sharedPrefs.getBoolean(getString(R.string.browser_preference_key),
                        true);
                if(browser_switch) {
                    Intent intent = new Intent(this, inbuilt_browser.class);
                    intent.putExtra(constantValues.URL_KEY, itemUrl);
                    intent.putExtra(constantValues.BROWSER_TITLE_KEY, itemTitle);
                    startActivity(intent);
                }
                if(!browser_switch){
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(itemUrl));
                    startActivity(intent);
                }
                break;
            case R.id.details_share_button :
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                String USGSLink = bundle.getString(constantValues.USGSLINK_DETAIL);
                String title = bundle.getString(constantValues.TITLE);
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, title );
                intent.putExtra(android.content.Intent.EXTRA_TEXT,  USGSLink );
                startActivity(Intent.createChooser(intent, "share via" ));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private double getCircleRadiusFromMagnitude(){
        String mag= bundle.getString(constantValues.MAGNITUDE);
        return (Math.exp((int)Double.parseDouble(mag)/1.01-0.13))*1000;
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
        return  ContextCompat.getColor(this,color_resource_id);
    }
    /*creating a method to get the colors resource id, and it returns an int data type*/
    private int getSolidColorResourceId(String magnitude) {
        int color_resource_id;
        int color_id = (int) Double.parseDouble(magnitude);

        /*switch statement to return the correct id of color based on the value of magnitude*/
        switch (color_id) {
            case 0:
                color_resource_id = R.color.magnitude0;
                break;
            case 1:
                color_resource_id = R.color.magnitude1;
                break;
            case 2:
                color_resource_id = R.color.magnitude2;
                break;
            case 3:
                color_resource_id = R.color.magnitude3;
                break;
            case 4:
                color_resource_id = R.color.magnitude4;
                break;
            case 5:
                color_resource_id = R.color.magnitude5;
                break;
            case 6:
                color_resource_id = R.color.magnitude6;
                break;
            case 7:
                color_resource_id = R.color.magnitude7;
                break;
            case 8:
                color_resource_id = R.color.magnitude8;
                break;
            case 9:
                color_resource_id = R.color.magnitude9;
                break;
            case 10:
                color_resource_id = R.color.magnitude10;
                break;
            default:
                color_resource_id = R.color.magnitude11;
                break;
        }
        return ContextCompat.getColor(this, color_resource_id);
    }

    private int MapTypeByPreference(){
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        String mapType=sharedPreferences.getString(
                getString(R.string.map_key),
                getString(R.string.map_default));
        if (mapType.equalsIgnoreCase(constantValues.MAPTYPE_NORMAL))
            return GoogleMap.MAP_TYPE_NORMAL;
        else if (mapType.equalsIgnoreCase(constantValues.MAPTYPE_HYBRID))
            return GoogleMap.MAP_TYPE_HYBRID;
        else if (mapType.equalsIgnoreCase(constantValues.MAPTYPE_SATELLITE))
            return GoogleMap.MAP_TYPE_SATELLITE;
        else if (mapType.equalsIgnoreCase(constantValues.MAPTYPE_TERRAIN))
            return GoogleMap.MAP_TYPE_TERRAIN;
        else
            return GoogleMap.MAP_TYPE_NORMAL;
    }

}
