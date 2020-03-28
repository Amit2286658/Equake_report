package com.example.lenovo.earthquakesampleappwithinternet;


import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.*;
import android.widget.*;
import equake_report.contract.constantValues;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class main extends Fragment implements LoaderManager.LoaderCallbacks<List<content_class>> {

    //creating an string to represent the desired magnitude selected by the filter dialog
    static int desiredMag = -1;

    //creating an identifier to identify if there's a change in the filter dialog,
    //regarding the time select
    static int time_identifier=-1;

    //creating a identifier number to keep track of the spinner map item clicks
    static int magnitude_identifier =-1;

    private String JSON_URL="https://earthquake.usgs.gov/fdsnws/event/1/query";

    private SharedPreferences sharedPrefs;

    static int base_quake_id = -1;
    private static content_class_adapter customAdapter;


    private View mainView;


    public main() {
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
        super.onSaveInstanceState(outState);
        outState.putInt(constantValues.TIME_IDENTIFIER_RESTORE_KEY, time_identifier);
        outState.putInt(constantValues.MAG_IDENTIFIER_RESTORE_KEY, magnitude_identifier);
        outState.putInt(constantValues.EARTHQUAKE_LOADER_ID_KEY, base_quake_id);
        outState.putInt(constantValues.DESIRED_MAG_KEY, desiredMag);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        // enable the menu for this fragment
        setHasOptionsMenu(true);


        mainView=inflater.inflate(R.layout.fragment_main, container, false);

        if (savedInstanceState != null){
            base_quake_id = savedInstanceState.getInt(constantValues.EARTHQUAKE_LOADER_ID_KEY);
            time_identifier = savedInstanceState.getInt(constantValues.TIME_IDENTIFIER_RESTORE_KEY);
            magnitude_identifier = savedInstanceState.getInt(constantValues.MAG_IDENTIFIER_RESTORE_KEY);
            desiredMag = savedInstanceState.getInt(constantValues.DESIRED_MAG_KEY);
        }
        /*
        creating an empty arrayList to temporarily fill the custom adapter
        until original data completes fetching from the internet in the background
        if not used, ide will complain for a missing parameter, and since
        list is not available until fetching is complete, it will complicate the situation
        */
        ArrayList<content_class> dataList=new ArrayList<>();


        final ListView datalist=(ListView)mainView.findViewById(R.id.listview);

        /*inserting the empty arrayList temporarily to the adapter
         * once fetching is complete, loader will automatically override the current parameter*/

        customAdapter=new content_class_adapter(getActivity(),dataList);
        datalist.setAdapter(customAdapter);
        datalist.setDivider(null);
        datalist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                content_class position=customAdapter.getItem(i);
                /*gettingDialogWindow(position);*/
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
                bundle.putInt(constantValues.ACTIVITY_IDENTIFIER, 0);


                Intent intent = new Intent(getActivity(), detailsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

//                gettingDialogWindow(position);
            }
        });

        RefreshingMethod();

        final SwipeRefreshLayout swipeRefreshLayout=(SwipeRefreshLayout)mainView.findViewById
                (R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshingMethod();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return mainView;
    }

    /*using the comparator interface
     * to sort the list by magnitude in descending order,
     * sorting in ascending order is done by the USGS ServerSide by default,
     * sorting by time is also not necessary for the same reason*/
    private Comparator<content_class> orderByMagnitude = new Comparator<content_class>() {
        @Override
        public int compare(content_class content_class, content_class t1) {
            float mag1 = Float.parseFloat(content_class.getMagnitude());
            float mag2 = Float.parseFloat(t1.getMagnitude());
            return (int)(mag2 - mag1);
        }
    };

    @Override
    public Loader<List<content_class>> onCreateLoader(int i, Bundle bundle) {

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String minLimit=sharedPrefs.getString(
                getString(R.string.settings_min_limit_key),
                getString(R.string.settings_min_limit_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.order_by_key),
                getString(R.string.order_by_default));

        String magnitude = bundle.getString("Mag__loader_key");

        Uri baseUri = Uri.parse(JSON_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("minmag" , magnitude);
        uriBuilder.appendQueryParameter("limit", minLimit);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new Loader_Class(getActivity(),uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<content_class>> loader, List<content_class> content_class) {

        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(
                getString(R.string.order_by_key),
                getResources().getStringArray(R.array.order_by_values)[1]
        ).equalsIgnoreCase(getResources().getStringArray(R.array.order_by_values)[0])){
            /*Collections.sort(content_class, com.example.lenovo.earthquakesampleappwithinternet.content_class.sortByMagnitude);*/
            Collections.sort(content_class, orderByMagnitude);
        }
        TextView textView=(TextView)mainView.findViewById(R.id.textview);
        ProgressBar progressBar=(ProgressBar)mainView.findViewById(R.id.progressbar);
        ListView listView=(ListView)mainView.findViewById(R.id.listview);
        listView.setVisibility(View.VISIBLE);
        ImageView global_map_bg=(ImageView)mainView.findViewById(R.id.global_map_bg);
        listView.setVisibility(View.VISIBLE);

        global_map_bg.setVisibility(View.VISIBLE);
        customAdapter.clear();
        content_class position;
        if (content_class != null && !content_class.isEmpty()) {
            global_map_bg.setVisibility(View.GONE);
            switch (time_identifier) {
                case 1:
                    for (int i = 0; i < content_class.size(); i++) {
                        position = content_class.get(i);
                        try {
                            int j = magnitudeIdentifier(position.getMagnitude());
                            if (currentDateCheck(position.getDate())) {
                                if (lastHourCheck(position.getTime()) && j >= desiredMag) {
                                    customAdapter.add(position);
                                }
                            }
                        } catch (ParseException | IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 2:
                    for (int i = 0; i < content_class.size(); i++){
                        position = content_class.get(i);
                        try {
                            int j = magnitudeIdentifier(position.getMagnitude());
                            if (last12hourCheck(position.getDate(), position.getTime()) && j >= desiredMag){
                                customAdapter.add(position);
                            }
                        }catch (ParseException | IndexOutOfBoundsException e){
                            e.printStackTrace();
                        }
                    }
                    break;
                case 3:
                    for (int i = 0; i < content_class.size(); i++){
                        position = content_class.get(i);
                        try {
                            int j = magnitudeIdentifier(position.getMagnitude());
                            if (last24hourcheck(position.getDate(), position.getTime()) && j >= desiredMag){
                                customAdapter.add(position);
                            }
                        }catch (ParseException e){
                            e.printStackTrace();
                        }
                    }
                    break;
                case 4:
                    for (int i = 0; i < content_class.size(); i++){
                        position = content_class.get(i);
                        try {
                            int j = magnitudeIdentifier(position.getMagnitude());
                            if (last48hourcheck(position.getDate(), position.getTime()) && j >= desiredMag)
                                customAdapter.add(position);
                        }catch (ParseException | IndexOutOfBoundsException e){
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    customAdapter.addAll(content_class);
                    break;
            }
            Log.i("customAdapter_size", String.valueOf(customAdapter.getCount()));
        }
        /*if(content_class!=null && !content_class.isEmpty()){
            customAdapter.addAll(content_class);
            global_map_bg.setVisibility(View.GONE);
        }*/
        if(content_class.isEmpty() || content_class==null) {
            textView.setText(getString(R.string.no_earthquake_info));
            textView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
        }

        if (customAdapter.isEmpty()){
            textView.setText(getString(R.string.no_equake_info_with_filters));
            textView.setVisibility(View.VISIBLE);
            global_map_bg.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<content_class>> loader) {
        customAdapter.clear();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id_of_item=item.getItemId();
        switch (id_of_item){
            case R.id.action_settings:
                Intent intent=new Intent(getActivity(),settings.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            case R.id.refresh:
                RefreshingMethod();
                return true;
            case R.id.main_menu_filter:
                main_filter_dialog();
                return true;
            case R.id.main_menu_about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
    private int networkStatus(){
        final int NO_CONNECTION=2;
        ConnectivityManager connectivityManager=(ConnectivityManager)getActivity().getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if (networkInfo!=null && networkInfo.isConnected()){
            if (networkInfo.getType()== ConnectivityManager.TYPE_MOBILE)
                return ConnectivityManager.TYPE_MOBILE;
            if(networkInfo.getType()== ConnectivityManager.TYPE_WIFI)
                return ConnectivityManager.TYPE_WIFI;
        }
        return NO_CONNECTION;
    }
    private void RefreshingMethod(){
        int networkStatus=networkStatus();
        final TextView textView=(TextView)mainView.findViewById(R.id.textview);
        ProgressBar progressBar=mainView.findViewById(R.id.progressbar);
        ListView listView=mainView.findViewById(R.id.listview);
        ImageView globalMapBg = (ImageView)mainView.findViewById(R.id.global_map_bg);
        textView.setVisibility(View.GONE);
        listView.setVisibility(View.INVISIBLE);
        globalMapBg.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        /*performing background task*/
        if (networkStatus==0 || networkStatus ==1) {

            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

            Bundle bundle = new Bundle();
            bundle.putString("Mag__loader_key", desiredMag == -1 ? minMagnitude : String.valueOf(desiredMag));

            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader( base_quake_id == -1 ? 1 : base_quake_id, bundle, this);

        }else{
            Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            globalMapBg.setVisibility(View.VISIBLE);
            textView.setText(getString(R.string.NO_CONNECTION));
            textView.setVisibility(View.VISIBLE);
        }
    }

    /*TODO: delete this method, as it is useless*/
    private View getChildViewAtPositiion(int pos, ListView listView){
        final int firstlistItemPosition=listView.getFirstVisiblePosition();
        final int lastListItemPosition=firstlistItemPosition + listView.getChildCount() - 1;

        if (pos < firstlistItemPosition || pos > lastListItemPosition){
            return listView.getAdapter().getView(pos,null,listView);
        }else {
            final int childIndex= pos-firstlistItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    /*
    *the application doesn't display any details dialog anymore, therefore this method does not serve any purpose
    *TODO: Delete this method*/
    @Deprecated
    private void gettingDialogWindow(final content_class position){

        final Dialog details_dialog=new Dialog(getActivity());
        details_dialog.setContentView(R.layout.details_layout);
        details_dialog.setTitle(position.getTitle());
        details_dialog.setCanceledOnTouchOutside(true);

        //getting dialog window
        Window window=details_dialog.getWindow();
        int width=ActionBar.LayoutParams.MATCH_PARENT;
        int height=ActionBar.LayoutParams.WRAP_CONTENT;
        if (window!=null)
            window.setLayout(width, height);

        TextView Alert=(TextView)details_dialog.findViewById(R.id.alert_level);
        if (position.getAlert().equalsIgnoreCase("null")) {
            Alert.setText(R.string.no_alert);
        } else {
            Alert.setText(position.getAlert());
        }

        GradientDrawable gradientDrawable=(GradientDrawable)Alert.getBackground();
        int alert_color=getAlertColor(position.getAlert());
        gradientDrawable.setColor(alert_color);

        TextView Tsunami_warning=(TextView)details_dialog.findViewById(R.id.Tsunami_warning);
        if (position.getTsunami_warning()==0) {
            Tsunami_warning.setText(R.string.tsunami_warning_false);
        }else {
            Tsunami_warning.setText(R.string.tsunami_warning_true);
        }

        TextView Status=(TextView)details_dialog.findViewById(R.id.status);
        if (position.getStatus().equalsIgnoreCase("null")) {
            Status.setVisibility(View.GONE);
        }else {
            Status.setText(position.getStatus());
        }

        TextView felt = (TextView)details_dialog.findViewById(R.id.felt);
        if (position.getFelt().isEmpty() || position.getFelt().equalsIgnoreCase("null")){
            felt.setText(R.string.not_available);
        }else{
            felt.setText(String.format(Locale.getDefault(),"%s",position.getFelt().concat(" peoples reported")));
        }

        TextView Longitude=(TextView)details_dialog.findViewById(R.id.Longitude);
        if (Double.parseDouble(position.getLongitude())<0){
            Longitude.setText(position.getLongitude()+" °W");
        }else {
            Longitude.setText(position.getLongitude()+" °E");
        }

        TextView Latitude=(TextView)details_dialog.findViewById(R.id.Latitude);
        if (Double.parseDouble(position.getLatitude())<0){
            Latitude.setText(position.getLatitude()+" °S");
        }else Latitude.setText(position.getLatitude()+" °N");

        TextView Depth=(TextView)details_dialog.findViewById(R.id.depth);
        Depth.setText(position.getDepth()+" Km");

        TextView browser_link=(TextView)details_dialog.findViewById(R.id.browser_link);
        browser_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String itemUrl=position.getLink();

                sharedPrefs=PreferenceManager.getDefaultSharedPreferences(getActivity());
                boolean browser_switch=sharedPrefs.getBoolean(getString(R.string.browser_preference_key),
                        true);
                if(browser_switch) {
                    Intent intent = new Intent(getActivity(), inbuilt_browser.class);
                    intent.putExtra("itemurl", itemUrl);
                    startActivity(intent);
                }
                if(!browser_switch){
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(itemUrl));
                    startActivity(intent);
                }
            }
        });

        TextView maps_link=(TextView)details_dialog.findViewById(R.id.map_link);
        maps_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("latitude",Double.parseDouble(position.getLatitude()));
                intent.putExtra("longitude",Double.parseDouble(position.getLongitude()));
                intent.putExtra("place",position.getLocationPrimary()+" "+position.getLocationSecondary()
                        +"\nMagnitude : "+position.getMagnitude());
                intent.putExtra("mag_to_radius",position.getMagnitude());
                startActivity(intent);
            }
        });

        TextView ok=(TextView)details_dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                details_dialog.dismiss();
            }
        });
        details_dialog.show();
    }

    @Deprecated
    private int getAlertColor(String alert){
        int alert_color_resource;
        if (alert.equalsIgnoreCase("green"))
            alert_color_resource=R.color.alert_green;
        else if (alert.equalsIgnoreCase("yellow"))
            alert_color_resource=R.color.alert_yellow;
        else if (alert.equalsIgnoreCase("orange"))
            alert_color_resource=R.color.alert_orange;
        else if (alert.equalsIgnoreCase("red"))
            alert_color_resource=R.color.alert_red;
        else
            alert_color_resource=android.R.color.white;

        return ContextCompat.getColor(getActivity(),alert_color_resource);
    }

    private boolean currentDateCheck(@NonNull String currentEarthquakeDate) throws ParseException {
        final SimpleDateFormat sdf=new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.parse(sdf.format(new Date())).equals(sdf.parse(currentEarthquakeDate));

    }

    private boolean lastHourCheck(String currentEarthquakeTime) throws ParseException{
        final SimpleDateFormat sdf=new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.parse(currentEarthquakeTime)
                .after(sdf.parse(sdf.format(new Date(System.currentTimeMillis() - 1000 * 60 * 60))));

    }

    private boolean last12hourCheck(String earthquakeDate, String earthquakeTime) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        Date mEarthquakeDate = sdf.parse(earthquakeDate+" "+earthquakeTime);
        Date mLast12Hour = sdf.parse(sdf.format(new Date(System.currentTimeMillis()-1000*60*60*12)));
        return mEarthquakeDate.after(mLast12Hour);
    }

    private boolean last24hourcheck(String earthquakeDate, String earthquakeTime) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        Date mEarthquakeDate = sdf.parse(earthquakeDate+" "+earthquakeTime);
        Date mLast24Hour = sdf.parse(sdf.format(new Date(System.currentTimeMillis()-1000*60*60*24)));
        return mEarthquakeDate.after(mLast24Hour);
    }

    private boolean last48hourcheck(String earthquakeDate, String earthquakeTime) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        Date mEarthquakeDate = sdf.parse(earthquakeDate+" "+earthquakeTime);
        Date mLast48Hour = sdf.parse(sdf.format(new Date(System.currentTimeMillis()-1000*60*60*48)));
        return mEarthquakeDate.after(mLast48Hour);
    }

    private int magnitudeIdentifier(String magnitude) throws ParseException, IndexOutOfBoundsException{
        Double mMagnitude = Double.parseDouble(magnitude);
        if (mMagnitude >= 0 && mMagnitude < 1)
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

    private void main_filter_dialog(){
        final int[] localMagIdentifier = new int[1];
        final int[] localtimeIdentfier = new int[1];

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Double settingsMagnitude = Double.parseDouble(sharedPreferences.getString(
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
        time_filter.add(constantValues.LAST_HOUR);
        time_filter.add(constantValues.LAST_12HOUR);
        time_filter.add(constantValues.LAST_24HOUR);
        time_filter.add(constantValues.LAST_48HOUR);

        /*creating adapter for the map filter Spinner that uses the ArrayList @Link map_filter as the data source*/
        final ArrayAdapter<String> map_filter_adapter=new ArrayAdapter<String>
                (getActivity(), R.layout.spinner_item, map_filter);
        map_filter_adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);

        /*binding the adapter with the Spinner*/
        final Spinner filter_main_mag=(Spinner)filter_dialog.findViewById(R.id.filter_spinner);
        filter_main_mag.setAdapter(map_filter_adapter);
        switch (magnitude_identifier){
            case 1:
                filter_main_mag.setSelection(map_filter_adapter.getPosition(constantValues.MAG0));
                break;
            case 2:
                filter_main_mag.setSelection(map_filter_adapter.getPosition(constantValues.MAG1));
                break;
            case 3:
                filter_main_mag.setSelection(map_filter_adapter.getPosition(constantValues.MAG2));
                break;
            case 4:
                filter_main_mag.setSelection(map_filter_adapter.getPosition(constantValues.MAG3));
                break;
            case 5:
                filter_main_mag.setSelection(map_filter_adapter.getPosition(constantValues.MAG4));
                break;
            case 6:
                filter_main_mag.setSelection(map_filter_adapter.getPosition(constantValues.MAG5));
                break;
            case 7:
                filter_main_mag.setSelection(map_filter_adapter.getPosition(constantValues.MAG6));
                break;
            case 8:
                filter_main_mag.setSelection(map_filter_adapter.getPosition(constantValues.MAG7));
                break;
            default:
                if (settingsMagnitude >= 0 && settingsMagnitude < 1)
                    filter_main_mag.setSelection(map_filter_adapter.getPosition(constantValues.MAG0));

                else if (settingsMagnitude >= 1 && settingsMagnitude < 2)
                    filter_main_mag.setSelection(map_filter_adapter.getPosition(constantValues.MAG1));

                else if (settingsMagnitude >= 2 && settingsMagnitude < 3)
                    filter_main_mag.setSelection(map_filter_adapter.getPosition(constantValues.MAG2));

                else if (settingsMagnitude >= 3 && settingsMagnitude < 4)
                    filter_main_mag.setSelection(map_filter_adapter.getPosition(constantValues.MAG3));

                else if (settingsMagnitude >= 4 && settingsMagnitude < 5)
                    filter_main_mag.setSelection(map_filter_adapter.getPosition(constantValues.MAG4));

                else if (settingsMagnitude >= 5 && settingsMagnitude < 6)
                    filter_main_mag.setSelection(map_filter_adapter.getPosition(constantValues.MAG5));

                else if (settingsMagnitude >= 6 && settingsMagnitude < 7)
                    filter_main_mag.setSelection(map_filter_adapter.getPosition(constantValues.MAG6));

                else filter_main_mag.setSelection(map_filter_adapter.getPosition(constantValues.MAG7));
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
                filter_time.setSelection(time_filter_adapter.getPosition(constantValues.LAST_HOUR));
                break;
            case 2:
                filter_time.setSelection(time_filter_adapter.getPosition(constantValues.LAST_12HOUR));
                break;
            case 3:
                filter_time.setSelection(time_filter_adapter.getPosition(constantValues.LAST_24HOUR));
                break;
            case 4:
                filter_time.setSelection(time_filter_adapter.getPosition(constantValues.LAST_48HOUR));
                break;
            default:
                break;
        }

        /*Attaching the listener with the map filter spinner*/
        filter_main_mag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, View view, final int i, long l) {
                final String getItem=adapterView.getItemAtPosition(i).toString();
                filter_main_mag.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                if (getItem.equalsIgnoreCase(constantValues.MAG0)) {
                                    localMagIdentifier[0] = 0;
                                    filter_main_mag.setSelection(map_filter_adapter.getPosition(getItem));
                                }else if (getItem.equalsIgnoreCase(constantValues.MAG1)){
                                    localMagIdentifier[0] = 1;
                                    filter_main_mag.setSelection(map_filter_adapter.getPosition(getItem));
                                }else if (getItem.equalsIgnoreCase(constantValues.MAG2)){
                                    localMagIdentifier[0] = 2;
                                    filter_main_mag.setSelection(map_filter_adapter.getPosition(getItem));
                                }else if (getItem.equalsIgnoreCase(constantValues.MAG3)){
                                    localMagIdentifier[0] = 3;
                                    filter_main_mag.setSelection(map_filter_adapter.getPosition(getItem));
                                }else if (getItem.equalsIgnoreCase(constantValues.MAG4)){
                                    localMagIdentifier[0] = 4;
                                    filter_main_mag.setSelection(map_filter_adapter.getPosition(getItem));
                                }else if (getItem.equalsIgnoreCase(constantValues.MAG5)){
                                    localMagIdentifier[0] = 5;
                                    filter_main_mag.setSelection(map_filter_adapter.getPosition(getItem));
                                }else if (getItem.equalsIgnoreCase(constantValues.MAG6)){
                                    localMagIdentifier[0] = 6;
                                    filter_main_mag.setSelection(map_filter_adapter.getPosition(getItem));
                                }else if (getItem.equalsIgnoreCase(constantValues.MAG7)){
                                    localMagIdentifier[0] = 7;
                                    filter_main_mag.setSelection(map_filter_adapter.getPosition(getItem));
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
            public void onItemSelected(final AdapterView<?> adapterView, View view,final int i, long l) {
                final String getitem=adapterView.getItemAtPosition(i).toString();
                filter_time.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                if (getitem.equalsIgnoreCase(constantValues.LAST_HOUR)) {
                                    localtimeIdentfier[0] = 0;
                                    filter_time.setSelection(time_filter_adapter.getPosition(getitem));
                                }else if (getitem.equalsIgnoreCase(constantValues.LAST_12HOUR)){
                                    localtimeIdentfier[0] = 1;
                                    filter_time.setSelection(time_filter_adapter.getPosition(getitem));
                                }else if (getitem.equalsIgnoreCase(constantValues.LAST_24HOUR)){
                                    localtimeIdentfier[0] = 2;
                                    filter_time.setSelection(time_filter_adapter.getPosition(getitem));
                                }else if (getitem.equalsIgnoreCase(constantValues.LAST_48HOUR)){
                                    localtimeIdentfier[0] = 3;
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
                switch (localMagIdentifier[0]){
                    case 0:
                        magnitude_identifier = 1;
                        desiredMag=0;
                        break;
                    case 1:
                        magnitude_identifier = 2;
                        desiredMag=1;
                        break;
                    case 2:
                        magnitude_identifier = 3;
                        desiredMag=2;
                        break;
                    case 3:
                        magnitude_identifier = 4;
                        desiredMag=3;
                        break;
                    case 4:
                        magnitude_identifier = 5;
                        desiredMag=4;
                        break;
                    case 5:
                        magnitude_identifier = 6;
                        desiredMag=5;
                        break;
                    case 6:
                        magnitude_identifier = 7;
                        desiredMag=6;
                        break;
                    case 7:
                        magnitude_identifier = 8;
                        desiredMag=7;
                        break;
                    default:
                        break;
                }
                switch (localtimeIdentfier[0]){
                    case 0:
                        time_identifier = 1;
                        base_quake_id = 152;
                        if(desiredMag != -1){
                            increement_earthquake_loader_id(desiredMag, base_quake_id);
                        }
                        break;
                    case 1:
                        time_identifier = 2;
                        base_quake_id = 162;
                        if(desiredMag != -1){
                            increement_earthquake_loader_id(desiredMag, base_quake_id);
                        }
                        break;
                    case 2:
                        time_identifier = 3;
                        base_quake_id = 172;
                        if(desiredMag != -1){
                            increement_earthquake_loader_id(desiredMag, base_quake_id);
                        }
                        break;
                    case 3:
                        time_identifier = 4;
                        base_quake_id = 182;
                        if(desiredMag != -1){
                            increement_earthquake_loader_id(desiredMag, base_quake_id);
                        }
                        break;
                    default:
                        break;
                }

                RefreshingMethod();

                filter_dialog.dismiss();
            }
        });
        TextView cancelButton = (TextView)filter_dialog.findViewById(R.id.filter_dialog_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter_dialog.dismiss();
            }
        });
        filter_dialog.show();
    }

    private void increement_earthquake_loader_id(int mdesiredMag, int earthquakeLoader){
        switch (mdesiredMag){
            case 0:
                base_quake_id = earthquakeLoader + 1;
                break;
            case 1:
                base_quake_id = earthquakeLoader + 2;
                break;
            case 2:
                base_quake_id = earthquakeLoader + 3;
                break;
            case 3:
                base_quake_id = earthquakeLoader + 4;
                break;
            case 4:
                base_quake_id = earthquakeLoader + 5;
                break;
            case 5:
                base_quake_id = earthquakeLoader + 6;
                break;
            case 6:
                base_quake_id = earthquakeLoader + 7;
                break;
            case 7:
                base_quake_id = earthquakeLoader + 8;
                break;
            default: break;
        }
    }
}
