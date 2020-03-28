package com.example.lenovo.earthquakesampleappwithinternet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import equake_report.contract.constantValues;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView spannedFirstPara = (TextView)findViewById(R.id.spanned_first_para);
        spannedFirstPara.setText(spanningTheText(getString(R.string.activity_about_content_first_para)));

        /*TextView flaticon = (TextView)findViewById(R.id.flaticon_link);
        flaticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPrefs=PreferenceManager.getDefaultSharedPreferences(AboutActivity.this);
                boolean browser_switch=sharedPrefs.getBoolean(getString(R.string.browser_preference_key),
                        true);
                if(browser_switch) {
                    Intent intent = new Intent(AboutActivity.this, inbuilt_browser.class);
                    intent.putExtra(constantValues.URL_KEY, getString(R.string.about_activity_content_flaticon_link));
                    intent.putExtra(constantValues.BROWSER_TITLE_KEY, "Flaticon");
                    startActivity(intent);
                }
                if(!browser_switch){
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(getString(R.string.about_activity_content_flaticon_link)));
                    startActivity(intent);
                }
            }
        });

        TextView freepik = (TextView)findViewById(R.id.freepik_link);
        freepik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPrefs=PreferenceManager.getDefaultSharedPreferences(AboutActivity.this);
                boolean browser_switch=sharedPrefs.getBoolean(getString(R.string.browser_preference_key),
                        true);
                if(browser_switch) {
                    Intent intent = new Intent(AboutActivity.this, inbuilt_browser.class);
                    intent.putExtra(constantValues.URL_KEY, getString(R.string.about_activity_content_icon_author_link));
                    intent.putExtra(constantValues.BROWSER_TITLE_KEY, "Freepik");
                    startActivity(intent);
                }
                if(!browser_switch){
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(getString(R.string.about_activity_content_icon_author_link)));
                    startActivity(intent);
                }
            }
        });*/

        TextView usgsLink = (TextView)findViewById(R.id.activity_about_content_second_para_usgs_link);
        usgsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPrefs=PreferenceManager.getDefaultSharedPreferences(AboutActivity.this);
                boolean browser_switch=sharedPrefs.getBoolean(getString(R.string.browser_preference_key),
                        true);
                if(browser_switch) {
                    Intent intent = new Intent(AboutActivity.this, inbuilt_browser.class);
                    intent.putExtra(constantValues.URL_KEY, getString(R.string.activity_about_content_second_para_link));
                    intent.putExtra(constantValues.BROWSER_TITLE_KEY, "USGS.gov");
                    startActivity(intent);
                }
                if(!browser_switch){
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(getString(R.string.activity_about_content_second_para_link)));
                    startActivity(intent);
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, "Amitkumar13234353@gmail.com");

                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivity(intent);
                }
                else {
                    Snackbar.make(view, "Email is disabled or Uninstalled", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int position = item.getItemId();
        switch (position){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private SpannableStringBuilder spanningTheText(String StringToSpan){
        final SpannableStringBuilder SSB=new SpannableStringBuilder(StringToSpan);
        final ForegroundColorSpan FCS = new ForegroundColorSpan(Color.BLACK);
        final StyleSpan SS = new StyleSpan(Typeface.BOLD);
        SSB.setSpan(FCS, 0, 13, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        SSB.setSpan(SS, 0, 13, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return SSB;
    }
}
