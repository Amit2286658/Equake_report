package com.example.lenovo.earthquakesampleappwithinternet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class settings extends AppCompatActivity {

    private static int preferenceChangeIdentifier=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new preferences()).commit();
        setUpActionbar();
    }

    private void setUpActionbar(){
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
        actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        if (preferenceChangeIdentifier!=0){
            NavUtils.navigateUpFromSameTask(this);
            main obj = new main();
            WeakReference<main> var = new WeakReference<>(obj);
            obj = var.get();
            obj.desiredMag = -1;
            obj.time_identifier = -1;
            obj.magnitude_identifier = -1;
            obj.base_quake_id = -1;
            obj = null;

            full_map_fragment obj1 = new full_map_fragment();
            WeakReference<full_map_fragment> var1 = new WeakReference<>(obj1);
            obj1 = var1.get();
            obj1.desiredMag = -1;
            obj1.desiredtime = -1;
            obj1.time_identifier = -1;
            obj1.mag_identifier = -1;
            obj1 = null;

            preferenceChangeIdentifier=0;
            return;
        }
        super.onBackPressed();
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
        if (preferenceChangeIdentifier!=0){
            NavUtils.navigateUpFromSameTask(this);
            main obj = new main();
            WeakReference<main> var = new WeakReference<>(obj);
            obj = var.get();
            obj.desiredMag = -1;
            obj.time_identifier = -1;
            obj.magnitude_identifier = -1;
            obj.base_quake_id = -1;
            obj = null;

            full_map_fragment obj1 = new full_map_fragment();
            WeakReference<full_map_fragment> var1 = new WeakReference<>(obj1);
            obj1 = var1.get();
            obj1.desiredMag = -1;
            obj1.desiredtime = -1;
            obj1.time_identifier = -1;
            obj1.mag_identifier = -1;
            obj1 = null;

            preferenceChangeIdentifier=0;
            return true;
        }else {
            onBackPressed();
            finish();
            return true;
        }
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

    public static class preferences extends PreferenceFragment {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            final Preference mag_preferences=findPreference(getString(R.string.settings_min_magnitude_key));
            setSharedSummaryFirstTime(mag_preferences);
            mag_preferences.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    setSharedSummary(preference,o.toString());
                    settings.preferenceChangeIdentifier=1;
                    return true;
                }
            });

            Preference limit_preference=findPreference(getString(R.string.settings_min_limit_key));
            setSharedSummaryFirstTime(limit_preference);
            limit_preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    setSharedSummary(preference,o.toString());
                    settings.preferenceChangeIdentifier=1;
                    return true;
                }
            });

            Preference orderBy_preference=findPreference(getString(R.string.order_by_key));
            setSharedSummaryFirstTime(orderBy_preference);
            orderBy_preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    setSharedSummary(preference, o.toString());
                    settings.preferenceChangeIdentifier=1;
                    return true;
                }
            });
            Preference map_type_preference=findPreference(getString(R.string.map_key));
            setSharedSummaryFirstTime(map_type_preference);
            map_type_preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    setSharedSummary(preference, o.toString());
                    settings.preferenceChangeIdentifier=1;
                    return true;
                }
            });
            Preference inbuiltBrowser_preference = findPreference(getString(R.string.browser_preference_key));
            inbuiltBrowser_preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    settings.preferenceChangeIdentifier = 1;
                    return true;
                }
            });
            Preference clusterMarker_preference = findPreference(getString(R.string.cluster_map_key));
            clusterMarker_preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    settings.preferenceChangeIdentifier = 1;
                    return true;
                }
            });
        }
        private void setSharedSummaryFirstTime(Preference preference){
            SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String changed_mag=sharedPreferences.getString(preference.getKey(),"");
            preference.setSummary(changed_mag);
        }
        private void setSharedSummary(Preference preference, String preference_value){
            preference.setSummary(preference_value);
        }
    }
}
