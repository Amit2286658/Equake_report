package com.example.lenovo.earthquakesampleappwithinternet;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity/* implements LoaderManager.LoaderCallbacks<List<content_class>>*/ {

    private MyReceiver networkReceiver;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter networkAvailability = new IntentFilter();
        networkAvailability.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkReceiver = new MyReceiver();
        registerReceiver(networkReceiver, networkAvailability);

        final ViewPager viewPager= findViewById(R.id.view_pager);
        fragment_adapter fragment_adapter=new fragment_adapter(getSupportFragmentManager());

        viewPager.setAdapter(fragment_adapter);

        TabLayout tabLayout= findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        Toolbar mainActivity_toolbar= findViewById(R.id.mainActivity_tool_bar);
        if (mainActivity_toolbar!=null)
            setSupportActionBar(mainActivity_toolbar);

        if (mainActivity_toolbar != null) {
            mainActivity_toolbar.setTitle(R.string.app_name);
        }

        networkReceiver.setOnNetworkChangeListener(new MyReceiver.networkChangeListener() {
            @Override
            public void isNetworkAvailable(boolean networkAvailability) {
                if (!networkAvailability){
                    Snackbar snackbar = Snackbar.make(viewPager, "Connection lost", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            startActivity(intent);
                        }
                    });
                    snackbar.show();
                }
            }
        });
    }
}
