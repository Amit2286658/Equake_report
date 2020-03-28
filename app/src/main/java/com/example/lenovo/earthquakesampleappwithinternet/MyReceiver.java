package com.example.lenovo.earthquakesampleappwithinternet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MyReceiver extends BroadcastReceiver {

    private networkChangeListener mNetworkChangeListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        if (!isOnline(context)){
            mNetworkChangeListener.isNetworkAvailable(false);
        }
        else mNetworkChangeListener.isNetworkAvailable(true);
    }

    private static boolean isOnline(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =  connectivityManager.getActiveNetworkInfo();
        return networkInfo != null;
    }

    void setOnNetworkChangeListener(networkChangeListener listener){
        this.mNetworkChangeListener = listener;
    }

    public interface networkChangeListener{
        void isNetworkAvailable(boolean networkAvailability);
    }
}
