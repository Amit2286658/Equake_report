package com.example.lenovo.earthquakesampleappwithinternet;


import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.*;
import com.example.lenovo.earthquakesampleappwithinternet.timeChecks.timecheck;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import equake_report.contract.constantValues;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class full_map_fragment extends Fragment implements OnMapReadyCallback,
        LoaderManager.LoaderCallbacks<List<content_class>>, ClusterManager.OnClusterItemInfoWindowClickListener, timecheck{

    //creating na identifier to identify if the map itemSelection is changed or not
    private static int map_filter_itemSelection_identifier = -1;

    //creating an identifier to identify if the time itemSelection is changed or not
    private static int time_filter_itemSelection_identifier = -1;

    //creating an string to represent the desireed desiredtime or date as selected by the filter dialog
    public static int desiredtime = -1;
    //creating an string to represent the desired magnitude selected from the filter
    public static int desiredMag = -1;
    //creating an identifer to identify if there's a change in the filter dialog,
    //regarding the time select
    public static int time_identifier=-1;
    private static int filterMagnitudeClickIdentifier = -1;

    private final String JSON_URL="https://earthquake.usgs.gov/fdsnws/event/1/query";

    private MapView mapView;
    private GoogleMap mMap;

    //creating a identifier number to keep track of the spinner map item clicks
    public static int mag_identifier = -1;

    private static final int MYLOCATION_IDENTIFIER=147;
    /*Location location=getLastKnownLocation();*/

    private HashMap<Marker, Bundle> markerMap = new HashMap<>();

    public full_map_fragment() {
        // Required empty public constructor
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process is
     * restarted.  If a new instance of the fragment later needs to be
     * created, the data you place in the Bundle here will be available
     * in the Bundle given to {@link #onCreate(Bundle)},
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, and
     * {@link #onActivityCreated(Bundle)}.
     *
     * <p>This corresponds to {@link Activity#onSaveInstanceState(Bundle)
     * Activity.onSaveInstanceState(Bundle)} and most of the discussion there
     * applies here as well.  Note however: <em>this method may be called
     * at any time before {@link #onDestroy()}</em>.  There are many situations
     * where a fragment may be mostly torn down (such as when placed on the
     * back stack with no UI showing), but its state will not be saved until
     * its owning activity actually needs to save its state.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(constantValues.DESIRED_MAG_MAP_KEY, desiredMag);
        outState.putInt(constantValues.DESIRED_TIME_MAP_KEY, desiredtime);
        outState.putInt(constantValues.TIME_IDENTIFIER_MAP_KEY, time_identifier);
        outState.putInt(constantValues.MAG_IDENTIFIER_MAP_KEY, mag_identifier);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.full_map, container, false);
    }

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * {@link #setRetainInstance(boolean)} to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after {@link #onCreateView}
     * and before {@link #onViewStateRestored(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();

        try {
            MapsInitializer.initialize(this.getActivity());
        }catch (Exception e){
            e.printStackTrace();
        }

        mapView = (MapView)view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        if (mapView!=null)
            mapView.getMapAsync(this);

        if (savedInstanceState != null){
            desiredtime = savedInstanceState.getInt(constantValues.DESIRED_TIME_MAP_KEY);
            desiredMag = savedInstanceState.getInt(constantValues.DESIRED_MAG_MAP_KEY);
            time_identifier = savedInstanceState.getInt(constantValues.TIME_IDENTIFIER_MAP_KEY);
            mag_identifier = savedInstanceState.getInt(constantValues.MAG_IDENTIFIER_MAP_KEY);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(MapTypeByPreference());
        /*mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.maps_styling_1)));*/
        mMap.setBuildingsEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getLocationPermission();
        }else {
            /*if (location!=null){
                double latitude=location.getLatitude();
                double longitude=location.getLongitude();
                CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLng(new LatLng(latitude,longitude));
                mMap.animateCamera(cameraUpdate);
            }else*/
            updateUI();
        }

        LoaderManager loadermanager=getLoaderManager();
        loadermanager.initLoader(813,null, this);

    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void updateUI() throws SecurityException{
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MYLOCATION_IDENTIFIER:
                if (checkAllPermission(grantResults)
                        /*grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED*/)
                    updateUI();
                else {
                    AlertDialog.Builder permissionDeniedUponRequest_dialog = new AlertDialog.Builder(getActivity());
                    permissionDeniedUponRequest_dialog.setMessage("location permission is used to enable the mylocation layer on map");
                    permissionDeniedUponRequest_dialog.setPositiveButton("OK", null);
                }
                break;
        }
    }

    /**
     * Initialize the contents of the Fragment host's standard options menu.  You
     * should place your menu items in to <var>menu</var>.  For this method
     * to be called, you must have first called {@link #setHasOptionsMenu}.  See
     * {@link Activity#onCreateOptionsMenu(Menu) Activity.onCreateOptionsMenu}
     * for more information.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater
     * @see #setHasOptionsMenu
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.full_map_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     *
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent=new Intent(getActivity(),settings.class);
                startActivity(intent);
                break;
            case R.id.filter_map_menu:
                map_filter_dialog();
                break;
            case R.id.filter_map_about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void getLocationPermission(){
        if (ActivityCompat.checkSelfPermission(
                getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            if (showPermissionRationale()){
                /*
                * the rationale method has all the required dialogs to explain the user,
                * no need to create any dialog here
                * see {@Link #showPermissionRationale}
                * */
                return;
            }
            else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]
                                {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                        MYLOCATION_IDENTIFIER
                );
            }
        }
        else {
            updateUI();
        }
    }

    private boolean showPermissionRationale(){
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
            AlertDialog.Builder permissionRationale_dialog = new AlertDialog.Builder(getActivity());
            permissionRationale_dialog.setMessage("Fine location permission is required to enable the my location layer on map");
            permissionRationale_dialog.setPositiveButton("OK", null);
            return true;
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)){
            AlertDialog.Builder permissionRationale_dialog = new AlertDialog.Builder(getActivity());
            permissionRationale_dialog.setMessage("Coarse location permission is useful when enabling the my location on map");
            permissionRationale_dialog.setPositiveButton("OK", null);
            return true;
        }
        else return false;
    }

    private boolean checkAllPermission(int[] permissions){
        if (permissions.length < 1){
            return false;
        }
        for (int each : permissions){
            if (each != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    /*private Location getLastKnownLocation() throws SecurityException, NullPointerException{
        LocationManager locationManager=(LocationManager)getContext().getSystemService
(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(locationManager.getBestProvider(
                new Criteria(), true), 600000, 15f,
                this);
        return locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
    }*/

    /*
     * creating a method to draw circle,
     * notice that the CircleOptions method is in chain call,
     * don't get confused*/
    private void drawCircle(content_class position, LatLng points, CircleOptions circleOptions){
        circleOptions
                .radius((Math.exp((int)Double.parseDouble(position.getMagnitude())/1.01-0.13))*1000)
                .strokeColor(getColorResouceId(position.getMagnitude()))
                .fillColor(getColorResouceId(position.getMagnitude()))
                .strokeWidth(2f)
                .center(points)
        ;
        mMap.addCircle(circleOptions);
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<List<content_class>> onCreateLoader(int id, Bundle args) {

        final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        //default start time
        String TodaysDate=sdf.format(new Date());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String minmag = prefs.getString(
                getString(R.string.settings_min_magnitude_key),
                "4");

        String minLimit=prefs.getString(
                getString(R.string.settings_min_limit_key),
                getString(R.string.settings_min_limit_default));

        Uri baseUri = Uri.parse(JSON_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("minmag", desiredMag == -1 ? minmag : String.valueOf(desiredMag));
        if (desiredMag != -1 && desiredtime != -1) uriBuilder.appendQueryParameter("starttime", getDesiredTime(time_identifier));
        if (desiredMag == -1 && desiredtime == -1) uriBuilder.appendQueryParameter("limit", minLimit);

        /*uriBuilder.appendQueryParameter("orderby", orderBy);
        uriBuilder.appendQueryParameter("limit", minLimit);*/
        return new Loader_Class(getActivity(),uriBuilder.toString());
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See {@link FragmentManager#beginTransaction()
     * FragmentManager.openTransaction()} for further discussion on this.
     *
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     *
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link Cursor}
     * and you place it in a {@link CursorAdapter}, use
     * the {@link CursorAdapter#//CursorAdapter(Context, * Cursor, int)} constructor <em>without</em> passing
     * in either {@link CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<List<content_class>> loader, List<content_class> data) {

        mMap.clear();
        //Setting up the cluster manager
        ClusterManager<MarkerCluster> clusterManager = new ClusterManager<>(getActivity(), mMap);
        clusterManager.setOnClusterItemInfoWindowClickListener(this);

        CircleOptions circleOptions = new CircleOptions();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean clusterBoolean = sp.getBoolean(
                getString(R.string.cluster_map_key),
                true
        );
        content_class data_position;

        if (clusterBoolean) {
            for (content_class aData : data) {
                data_position = aData;
                switch (desiredtime) {
                    case 0:
                        int j = earthquakeMagCheck(data_position.getMagnitude());
                        try {
                            if (yesterdayCheck(data_position.getDate()) && j > desiredMag) {
                                LatLng points = new LatLng(
                                        Double.parseDouble(data_position.getLatitude()),
                                        Double.parseDouble(data_position.getLongitude())
                                );
                                Bundle bundle = bundleMethodForMarkerClick(data_position);
                                MarkerCluster markerCluster = new MarkerCluster(points, data_position.getTitle(),
                                        data_position.getMagnitude(), bundle);
                                clusterManager.addItem(markerCluster);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        int j1 = earthquakeMagCheck(data_position.getMagnitude());
                        try {
                            if (last2DaysCheck(data_position.getDate()) && j1 > desiredMag) {
                                LatLng points = new LatLng(
                                        Double.parseDouble(data_position.getLatitude()),
                                        Double.parseDouble(data_position.getLongitude())
                                );
                                Bundle bundle = bundleMethodForMarkerClick(data_position);
                                MarkerCluster markerCluster = new MarkerCluster(points, data_position.getTitle(),
                                        data_position.getMagnitude(), bundle);
                                clusterManager.addItem(markerCluster);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        int j2 = earthquakeMagCheck(data_position.getMagnitude());
                        try {
                            if (last4DaysCheck(data_position.getDate()) && j2 > desiredMag) {
                                LatLng points = new LatLng(
                                        Double.parseDouble(data_position.getLatitude()),
                                        Double.parseDouble(data_position.getLongitude())
                                );
                                Bundle bundle = bundleMethodForMarkerClick(data_position);
                                MarkerCluster markerCluster = new MarkerCluster(points, data_position.getTitle(),
                                        data_position.getMagnitude(), bundle);
                                clusterManager.addItem(markerCluster);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        int j3 = earthquakeMagCheck(data_position.getMagnitude());
                        try {
                            if (thisWeekCheck(data_position.getDate()) && j3 > desiredMag) {
                                LatLng points = new LatLng(
                                        Double.parseDouble(data_position.getLatitude()),
                                        Double.parseDouble(data_position.getLongitude())
                                );
                                Bundle bundle = bundleMethodForMarkerClick(data_position);
                                MarkerCluster markerCluster = new MarkerCluster(points, data_position.getTitle(),
                                        data_position.getMagnitude(), bundle);
                                clusterManager.addItem(markerCluster);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        LatLng points = new LatLng(
                                Double.parseDouble(data_position.getLatitude()),
                                Double.parseDouble(data_position.getLongitude())
                        );
                        Bundle bundle = bundleMethodForMarkerClick(data_position);
                        MarkerCluster markerCluster = new MarkerCluster(points, data_position.getTitle(),
                                data_position.getMagnitude(), bundle);
                        clusterManager.addItem(markerCluster);
                        break;
                }
            }
                if (clusterManager.getMarkerCollection() == null)
                    Toast.makeText(getContext(), "No information with given filter", Toast.LENGTH_LONG).show();
            mMap.setOnCameraIdleListener(clusterManager);
            mMap.setOnMarkerClickListener(clusterManager);
            mMap.setOnInfoWindowClickListener(clusterManager);
        } else {
            for (int i = 0; i < data.size(); i++) {
                data_position = data.get(i);
                switch (desiredtime) {
                    case 0:
                        int j = earthquakeMagCheck(data_position.getMagnitude());
                        try {
                            if (yesterdayCheck(data_position.getDate()) && j > desiredMag ){
                                LatLng points = new LatLng(
                                        Double.parseDouble(data_position.getLatitude()),
                                        Double.parseDouble(data_position.getLongitude())
                                );
                                Bundle bundle = bundleMethodForMarkerClick(data_position);
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(points);
                                markerOptions.title(data_position.getTitle());
                                Marker marker = mMap.addMarker(markerOptions);
                                markerMap.put(marker, bundle);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        int j1 = earthquakeMagCheck(data_position.getMagnitude());
                        try {
                            if (last2DaysCheck(data_position.getDate()) && j1 > desiredMag){
                                LatLng points = new LatLng(
                                        Double.parseDouble(data_position.getLatitude()),
                                        Double.parseDouble(data_position.getLongitude())
                                );
                                Bundle bundle = bundleMethodForMarkerClick(data_position);
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(points);
                                markerOptions.title(data_position.getTitle());
                                Marker marker = mMap.addMarker(markerOptions);
                                markerMap.put(marker, bundle);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        int j2 = earthquakeMagCheck(data_position.getMagnitude());
                        try {
                            if (last4DaysCheck(data_position.getDate()) && j2 > desiredMag){
                                LatLng points = new LatLng(
                                        Double.parseDouble(data_position.getLatitude()),
                                        Double.parseDouble(data_position.getLongitude())
                                );
                                Bundle bundle = bundleMethodForMarkerClick(data_position);
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(points);
                                markerOptions.title(data_position.getTitle());
                                Marker marker = mMap.addMarker(markerOptions);
                                markerMap.put(marker, bundle);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        int j3 = earthquakeMagCheck(data_position.getMagnitude());
                        try {
                            if (thisWeekCheck(data_position.getDate()) && j3 > desiredMag){
                                LatLng points = new LatLng(
                                        Double.parseDouble(data_position.getLatitude()),
                                        Double.parseDouble(data_position.getLongitude())
                                );
                                Bundle bundle = bundleMethodForMarkerClick(data_position);
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(points);
                                markerOptions.title(data_position.getTitle());
                                Marker marker = mMap.addMarker(markerOptions);
                                markerMap.put(marker, bundle);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        LatLng points = new LatLng(
                                Double.parseDouble(data_position.getLatitude()),
                                Double.parseDouble(data_position.getLongitude())
                        );
                        Bundle bundle = bundleMethodForMarkerClick(data_position);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(points);
                        markerOptions.title(data_position.getTitle());
                        Marker marker = mMap.addMarker(markerOptions);
                        markerMap.put(marker, bundle);
                        break;
                }
                /*drawCircle(data_position,points, circleOptions);*/
            }

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent intent = new Intent(getActivity(), detailsActivity.class);
                    Bundle bundle = markerMap.get(marker);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }

    private Bundle bundleMethodForMarkerClick(content_class position){
        Bundle bundle = new Bundle();
        if (position.getAlert().equalsIgnoreCase("null")) {
            bundle.putString(constantValues.ALERT, getString(R.string.no_alert));
        } else {
            bundle.putString(constantValues.ALERT, position.getAlert());
        }
        if (position.getTsunami_warning()==0) {
            bundle.putString(constantValues.TSUNAMIWARNING, getString(R.string.tsunami_warning_false));
        }else {
            bundle.putString(constantValues.TSUNAMIWARNING, getString(R.string.tsunami_warning_true));
        }
        bundle.putString(constantValues.STATUS, position.getStatus());

        if (position.getFelt().isEmpty() || position.getFelt().equalsIgnoreCase("null")){
            bundle.putString(constantValues.FELT,getString(R.string.not_available));
        }else{
            bundle.putString(constantValues.FELT, Integer.parseInt(position.getFelt())==1 ?
                    position.getFelt().concat(" person reported") : position.getFelt().concat(" peoples reported"));
        }
        if (Double.parseDouble(position.getLongitude())<0){
            bundle.putString(constantValues.LONGITUDE, position.getLongitude()+" °W");
        }else {
            bundle.putString(constantValues.LONGITUDE, position.getLongitude()+" °E");
        }
        if (Double.parseDouble(position.getLatitude())<0){
            bundle.putString(constantValues.LATITUDE, position.getLatitude()+" °S");
        }else bundle.putString(constantValues.LATITUDE, position.getLatitude()+" °N");

        bundle.putString(constantValues.DEPTH, position.getDepth()+" Km");
        bundle.putString(constantValues.MAGNITUDE, position.getMagnitude());
        bundle.putString(constantValues.TITLE, position.getTitle());
        bundle.putDouble(constantValues.LONGITUDE_DOUBLE, Double.parseDouble(position.getLongitude()));
        bundle.putDouble(constantValues.LATITUDE_DOUBLE, Double.parseDouble(position.getLatitude()));
        bundle.putString(constantValues.INTENSITY, position.getIntensity());
        bundle.putString(constantValues.SIGNIFICANCE, position.getSignificsnce());
        bundle.putString(constantValues.REPORTEDTIME, position.getUpdatedDate()+" "+position.getUpdatedTime());
        bundle.putString(constantValues.DATEANDTIME, position.getDate()+" "+position.getTime());
        bundle.putString(constantValues.LOCATIONPRIMARY_DETAIL, position.getLocationPrimary());
        bundle.putString(constantValues.LOCATIONSECONDARY_DETAIL, position.getLocationSecondary());
        bundle.putString(constantValues.USGSLINK_DETAIL, position.getLink());
        bundle.putInt(constantValues.ACTIVITY_IDENTIFIER, PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(
                getString(R.string.cluster_map_key),
                true
        ) ? 1 : 2);

        return bundle;
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<List<content_class>> loader) {
        //empty
    }

    private void map_filter_dialog(){

        final int[] localFilterMagIdentfier = {-1};
        final int[] localFilterTimeIdentifier = {-1};
        final int[] loaderId = new int[1];
        SharedPreferences sharedPrefs= PreferenceManager.getDefaultSharedPreferences(getActivity());
        Double settingsMagnitude = Double.parseDouble(sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default)
        ));

        final Dialog filter_dialog=new Dialog(getActivity());
        filter_dialog.setContentView(R.layout.filter_dialog_layout);
        filter_dialog.setTitle("Filter");
        filter_dialog.setCanceledOnTouchOutside(true);
        TextView mainText=(TextView)filter_dialog.findViewById(R.id.text_title_1);
        mainText.setText("Magnitude");

        //getting window of the dialog
        Window window=filter_dialog.getWindow();
        int width=ActionBar.LayoutParams.MATCH_PARENT;
        int height=ActionBar.LayoutParams.WRAP_CONTENT;
        if (window!=null)
            window.setLayout(width, height);

        /*creating ArrayList to provide data to the map filter adapter*/
        final ArrayList<String> map_filter=new ArrayList<>();
        map_filter.add(constantValues.MAG0);
        map_filter.add(constantValues.MAG1);
        map_filter.add(constantValues.MAG2);
        map_filter.add(constantValues.MAG3);
        map_filter.add(constantValues.MAG4);
        map_filter.add(constantValues.MAG5);
        map_filter.add(constantValues.MAG6);
        map_filter.add(constantValues.MAG7);


        //creating ArrayList to provide data to the desiredtime filter adapter
        ArrayList<String> time_filter=new ArrayList<>();
        time_filter.add(constantValues.YESTERDAY);
        time_filter.add(constantValues.Last2DAYS);
        time_filter.add(constantValues.Last4DAYS);
        time_filter.add(constantValues.THIS_WEEK);

        /*creating adapter for the map filter Spinner that uses the ArrayList @Link map_filter as the data source*/
        final ArrayAdapter<String> mag_filter_adapter=new ArrayAdapter<String>
                (getActivity(), R.layout.spinner_item, map_filter);
        mag_filter_adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);

        /*binding the adapter with the Spinner*/
        final Spinner filter_mag=(Spinner)filter_dialog.findViewById(R.id.filter_spinner);
        filter_mag.setAdapter(mag_filter_adapter);
        switch (mag_identifier){
            case 1:
                filter_mag.setSelection(mag_filter_adapter.getPosition(constantValues.MAG0));
                break;
            case 2:
                filter_mag.setSelection(mag_filter_adapter.getPosition(constantValues.MAG1));
                break;
            case 3:
                filter_mag.setSelection(mag_filter_adapter.getPosition(constantValues.MAG2));
                break;
            case 4:
                filter_mag.setSelection(mag_filter_adapter.getPosition(constantValues.MAG3));
                break;
            case 5:
                filter_mag.setSelection(mag_filter_adapter.getPosition(constantValues.MAG4));
                break;
            case 6:
                filter_mag.setSelection(mag_filter_adapter.getPosition(constantValues.MAG5));
                break;
            case 7:
                filter_mag.setSelection(mag_filter_adapter.getPosition(constantValues.MAG6));
                break;
            case 8:
                filter_mag.setSelection(mag_filter_adapter.getPosition(constantValues.MAG7));
                break;
            default:
                if (settingsMagnitude >= 0 && settingsMagnitude < 1)
                    filter_mag.setSelection(mag_filter_adapter.getPosition(constantValues.MAG0));

                else if (settingsMagnitude >= 1 && settingsMagnitude < 2)
                    filter_mag.setSelection(mag_filter_adapter.getPosition(constantValues.MAG1));

                else if (settingsMagnitude >= 2 && settingsMagnitude < 3)
                    filter_mag.setSelection(mag_filter_adapter.getPosition(constantValues.MAG2));

                else if (settingsMagnitude >= 3 && settingsMagnitude < 4)
                    filter_mag.setSelection(mag_filter_adapter.getPosition(constantValues.MAG3));

                else if (settingsMagnitude >= 4 && settingsMagnitude < 5)
                    filter_mag.setSelection(mag_filter_adapter.getPosition(constantValues.MAG4));

                else if (settingsMagnitude >= 5 && settingsMagnitude < 6)
                    filter_mag.setSelection(mag_filter_adapter.getPosition(constantValues.MAG5));

                else if (settingsMagnitude >= 6 && settingsMagnitude < 7)
                    filter_mag.setSelection(mag_filter_adapter.getPosition(constantValues.MAG6));

                else filter_mag.setSelection(mag_filter_adapter.getPosition(constantValues.MAG7));

                break;
        }
        //setting the spinner's background to null to use the custom dropdown icon
        //creating an arrayAdapter for the desiredTime filter
        final ArrayAdapter<String> time_filter_adapter=new ArrayAdapter<String>(getActivity(), R.layout.spinner_item,
                time_filter);
        time_filter_adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);

        //binding the adapter with the desiredTime filter spinner
        final Spinner filter_time=(Spinner)filter_dialog.findViewById(R.id.filter_spinner_time);
        filter_time.setAdapter(time_filter_adapter);
        switch (time_identifier){
            case 1:
                filter_time.setSelection(time_filter_adapter.getPosition(constantValues.YESTERDAY));
                break;
            case 2:
                filter_time.setSelection(time_filter_adapter.getPosition(constantValues.Last2DAYS));
                break;
            case 3:
                filter_time.setSelection(time_filter_adapter.getPosition(constantValues.Last4DAYS));
                break;
            case 4:
                filter_time.setSelection(time_filter_adapter.getPosition(constantValues.THIS_WEEK));
                break;
            default:
                break;
        }

        /*Attaching the listener with the map filter spinner*/
        filter_mag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, View view, final int i, long l) {
                final String getItem=adapterView.getItemAtPosition(i).toString();
                filter_mag.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                if (getItem.equalsIgnoreCase(constantValues.MAG0)) {
                                    localFilterMagIdentfier[0] = 0;
                                    filter_mag.setSelection(mag_filter_adapter.getPosition(getItem));
                                } else if (getItem.equalsIgnoreCase(constantValues.MAG1)){
                                    localFilterMagIdentfier[0] = 1;
                                    filter_mag.setSelection(mag_filter_adapter.getPosition(getItem));
                                } else if (getItem.equalsIgnoreCase(constantValues.MAG2)){
                                    localFilterMagIdentfier[0] = 2;
                                    filter_mag.setSelection(mag_filter_adapter.getPosition(getItem));
                                } else if (getItem.equalsIgnoreCase(constantValues.MAG3)){
                                    localFilterMagIdentfier[0] = 3;
                                    filter_mag.setSelection(mag_filter_adapter.getPosition(getItem));
                                } else if (getItem.equalsIgnoreCase(constantValues.MAG4)){
                                    localFilterMagIdentfier[0] = 4;
                                    filter_mag.setSelection(mag_filter_adapter.getPosition(getItem));
                                } else if (getItem.equalsIgnoreCase(constantValues.MAG5)){
                                    localFilterMagIdentfier[0] = 5;
                                    filter_mag.setSelection(mag_filter_adapter.getPosition(getItem));
                                } else if (getItem.equalsIgnoreCase(constantValues.MAG6)){
                                    localFilterMagIdentfier[0] = 6;
                                    filter_mag.setSelection(mag_filter_adapter.getPosition(getItem));
                                } else if (getItem.equalsIgnoreCase(constantValues.MAG7)){
                                    localFilterMagIdentfier[0] = 7;
                                    filter_mag.setSelection(mag_filter_adapter.getPosition(getItem));
                                }
                            }
                        }
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Empty
            }
        });

        filter_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, View view, final int i, long l) {
                final String getitem=adapterView.getItemAtPosition(i).toString();
                filter_time.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                if (getitem.equalsIgnoreCase(constantValues.YESTERDAY)) {
                                    localFilterTimeIdentifier[0] = 0;
                                    filter_time.setSelection(time_filter_adapter.getPosition(getitem));
                                }else if (getitem.equalsIgnoreCase(constantValues.Last2DAYS)){
                                    localFilterTimeIdentifier[0] = 1;
                                    filter_time.setSelection(time_filter_adapter.getPosition(getitem));
                                }else if (getitem.equalsIgnoreCase(constantValues.Last4DAYS)){
                                    localFilterTimeIdentifier[0] = 2;
                                    filter_time.setSelection(time_filter_adapter.getPosition(getitem));
                                }else if (getitem.equalsIgnoreCase(constantValues.THIS_WEEK)){
                                    localFilterTimeIdentifier[0] = 3;
                                    filter_time.setSelection(time_filter_adapter.getPosition(getitem));
                                }
                            }
                        }
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        TextView closingDialogButton=(TextView)filter_dialog.findViewById(R.id.filter_dialog_ok_button);
        closingDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (localFilterMagIdentfier[0]){
                    case 0:
                        mag_identifier = 1;
                        desiredMag = 0;
                        break;
                    case 1:
                        mag_identifier = 2;
                        desiredMag = 1;
                        break;
                    case 2:
                        mag_identifier = 3;
                        desiredMag = 2;
                        break;
                    case 3:
                        mag_identifier = 4;
                        desiredMag = 3;
                        break;
                    case 4:
                        mag_identifier = 5;
                        desiredMag = 4;
                        break;
                    case 5:
                        mag_identifier = 6;
                        desiredMag = 5;
                        break;
                    case 6:
                        mag_identifier = 7;
                        desiredMag = 6;
                        break;
                    case 7:
                        mag_identifier = 8;
                        desiredMag = 7;
                        break;
                    default:
                        break;
                }

                switch (localFilterTimeIdentifier[0]){
                    case 0:
                        mMap.clear();
                        loaderId[0] = 18032001;
                        time_identifier = 1;
                        desiredtime = 0;
                        getLoaderManager().initLoader(increementLoaderId(desiredMag,loaderId[0]),
                                null, full_map_fragment.this);
                        break;
                    case 1:
                        mMap.clear();
                        loaderId[0] = 18033001;
                        time_identifier = 2;
                        desiredtime = 1;
                        getLoaderManager().initLoader(increementLoaderId(desiredMag, loaderId[0]),
                                null, full_map_fragment.this);
                        break;
                    case 2:
                        mMap.clear();
                        loaderId[0] = 18034001;
                        time_identifier = 3;
                        desiredtime= 2;
                        getLoaderManager().initLoader(increementLoaderId(desiredMag, loaderId[0]),
                                null, full_map_fragment.this);
                        break;
                    case 3:
                        mMap.clear();
                        loaderId[0] = 18035001;
                        time_identifier = 4;
                        desiredtime = 3;
                        getLoaderManager().initLoader(increementLoaderId(desiredMag, loaderId[0]),
                                null, full_map_fragment.this);
                        break;
                    default:
                        break;
                }
                filter_dialog.dismiss();
            }
        });
        TextView cancelFilterButton = (TextView)filter_dialog.findViewById(R.id.filter_dialog_cancel_button);
        cancelFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter_dialog.dismiss();
            }
        });
        filter_dialog.show();

    }

    private int increementLoaderId(int desiredMAG, int magIdentifier){
        int returnLoaderId = -1;
        switch (desiredMAG){
            case 0:
                returnLoaderId = magIdentifier + 1;
                break;
            case 1:
                returnLoaderId = magIdentifier + 2;
                break;
            case 2:
                returnLoaderId = magIdentifier + 3;
                break;
            case 3:
                returnLoaderId = magIdentifier + 4;
                break;
            case 4:
                returnLoaderId = magIdentifier + 5;
                break;
            case 5:
                returnLoaderId = magIdentifier + 6;
                break;
            case 6:
                returnLoaderId = magIdentifier + 7;
                break;
            case 7:
                returnLoaderId = magIdentifier + 8;
                break;
                default: break;
        }
        return returnLoaderId;
    }

    /*this method returns the desired time*/
    @Deprecated
    private static String getDesiredTime(int timeIdentifier){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Calendar calendar=Calendar.getInstance();
        switch (timeIdentifier){
            case 1:
                calendar.add(Calendar.DATE, -1);
                Date date=calendar.getTime();
                String[] day1 = sdf.format(date).split(" ");
                return day1[0].concat("T").concat(day1[1]);
            case 2:
                calendar.add(Calendar.DATE, -2);
                Date date2=calendar.getTime();
                String[] day2 = sdf.format(date2).split(" ");
                return day2[0].concat("T").concat(day2[1]);
            case 3:
                calendar.add(Calendar.DATE, -4);
                Date date4=calendar.getTime();
                String[] day4 = sdf.format(date4).split(" ");
                return day4[0].concat("T").concat(day4[1]);
            case 4:
                calendar.add(Calendar.DATE, -7);
                Date date7=calendar.getTime();
                String[] day7 = sdf.format(date7).split(" ");
                return day7[0].concat("T").concat(day7[1]);

            default:
                return null;
        }
    }

    /*
     * returns the map type after checking the preference
     * returns map typ none if no preference is found
     * which happens to be the case for the first desiredtime
     */

    private int MapTypeByPreference(){
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getActivity());
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

    private int getColorResouceId(String magnitude){
        int color_resource_id;
        int color_id=(int)Double.parseDouble(magnitude);

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
        return  ContextCompat.getColor(getContext(),color_resource_id);
    }

    @Override
    public void onClusterItemInfoWindowClick(ClusterItem clusterItem) {
        MarkerCluster markerCluster = (MarkerCluster)clusterItem;
        Bundle bundle = markerCluster.getBundle();
        Intent intent = new Intent(getActivity(), detailsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean yesterdayCheck(String Date) throws ParseException {
        SimpleDateFormat sdf =  new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date yesterdayDate = sdf.parse(sdf.format(calendar.getTime()));
        Date date = sdf.parse(Date);
        return date.after(yesterdayDate) ? true : false;
    }

    @Override
    public boolean last2DaysCheck(String Date) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -2);
        Date last2Days = sdf.parse(sdf.format(calendar.getTime()));
        Date date = sdf.parse(Date);
        return date.after(last2Days);
    }

    @Override
    public boolean last4DaysCheck(String Date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -4);
        Date last4Days = sdf.parse(sdf.format(calendar.getTime()));
        Date earthquakeDate = sdf.parse(Date);
        return earthquakeDate.after(last4Days);
    }

    @Override
    public boolean thisWeekCheck(String Date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        Date thisWeek = sdf.parse(sdf.format(calendar.getTime()));
        Date earthquakeDate = sdf.parse(Date);
        return earthquakeDate.after(thisWeek);
    }

    private int earthquakeMagCheck(String magnitude){
        Double mMagnitude = Double.parseDouble(magnitude);
        if (mMagnitude >= 0 && mMagnitude <1)
            return 0;
        else if (mMagnitude >= 1 && mMagnitude < 2)
            return 1;
        else if (mMagnitude >= 2 && mMagnitude < 3)
            return 2;
        else if (mMagnitude >= 3 && mMagnitude < 4)
            return 3;
        else if (mMagnitude >= 4 && mMagnitude < 5)
            return 4;
        else if (mMagnitude >= 5 && mMagnitude < 6)
            return 5;
        else if (mMagnitude >= 6 && mMagnitude < 7)
            return 6;
        else if (mMagnitude >= 7)
            return 7;
        else return -1;
    }
}
