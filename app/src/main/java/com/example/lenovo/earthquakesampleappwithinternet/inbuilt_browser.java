package com.example.lenovo.earthquakesampleappwithinternet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import equake_report.contract.constantValues;

public class inbuilt_browser extends AppCompatActivity{
    private WebView webView;
    private ProgressDialog progressDialog;
    private int LOADER_ID=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbuilt_browser);
        progressDialog=ProgressDialog.show(this,"Loading","Please wait...");
        progressDialog.setCancelable(false);
        String url=gettingUrlFromIntent();
        loadingWebsite(url);
        setUpActionbar();
    }

    private void loadingWebsite(String url){
        webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(url);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            /**
             * @param view
             * @param url
             * @deprecated
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progressDialog.show();
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();
            }
        });
    }


    private String gettingUrlFromIntent(){
        Intent intent=getIntent();
        return intent.getExtras().getString(constantValues.URL_KEY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browser_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
            progressDialog.show();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        WebView webView=(WebView)findViewById(R.id.webView);
        int position=item.getItemId();

        switch (position){
            case R.id.browser_backward:
                webView.goBack();
                progressDialog.show();
                return true;
            case R.id.browser_stop:
                return true;
            case R.id.browser_forward:
                webView.goForward();
                progressDialog.show();
                return true;
            case R.id.browser_refresh:
                loadingWebsite(gettingUrlFromIntent());
                progressDialog.show();
                return true;
            case R.id.browser_close:
                finish();
                progressDialog.show();
                return true;
            case android.R.id.home :
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void setUpActionbar(){
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getIntent().getExtras().getString(constantValues.BROWSER_TITLE_KEY));
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        invalidateOptionsMenu();
        if (!webView.canGoBack())
            menu.findItem(R.id.browser_backward).setEnabled(false);
        if (!webView.canGoForward())
            menu.findItem(R.id.browser_forward).setEnabled(false);
        menu.findItem(R.id.browser_stop).setEnabled(false);
        return super.onPrepareOptionsMenu(menu);
    }
}
