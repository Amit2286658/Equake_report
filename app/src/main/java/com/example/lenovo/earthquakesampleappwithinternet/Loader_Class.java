package com.example.lenovo.earthquakesampleappwithinternet;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class Loader_Class extends AsyncTaskLoader<List<content_class>> {

    private String mURL;

    public Loader_Class(Context context, String string) {
        super(context);
        mURL=string;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<content_class> loadInBackground() {
        if (mURL==null)
            return null;
        return quakeUtils.fetchEarthQuakeData(mURL);
    }
}
