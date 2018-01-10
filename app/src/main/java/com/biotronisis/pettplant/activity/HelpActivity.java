package com.biotronisis.pettplant.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
//import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.biotronisis.pettplant.R;
import com.biotronisis.pettplant.debug.MyDebug;

public class HelpActivity extends AbstractBaseActivity {

    private static final String TAG = "HelpActivity";
    public static final String EXTRA_ACTIVITY_NAME = "activityName";
    public static final String EXTRA_FRAGMENT_NAME = "fragmentName";

    @Override
    public String getActivityName() {
        return TAG;
    }

    @Override
    public String getHelpKey() {
        return null;         // no help
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        WebView webView = findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultFontSize(18);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_background_help));

        try {
            //noinspection ConstantConditions
            String fragmentName = getIntent().getExtras().getString(EXTRA_FRAGMENT_NAME);
//         String helpStr;
//      int resID = 0;

//      if (fragmentName == null) {
//         helpStr = getString(R.string.cant_find_help_text);
////         resID = getResources().getIdentifier(activityName, "string", getPackageName());
//      } else {
            int resID = getResources().getIdentifier(fragmentName, "string", getPackageName());
//      }

//      if (resID == 0) {
//         helpStr = getString(R.string.cant_find_help_text);
//      } else {
//         String helpStr = getString(resID);
//      }

            webView.loadData(getString(resID), "text/html", "UTF8");
        } catch (Exception e) {
            if (MyDebug.LOG) {
                Log.e(TAG, "getIntent().getExtras().getString() returned null in HelpActivity#onCreate", e);
            }
        }

        ActionBar actionBar = getSupportActionBar();
        try {
            //noinspection ConstantConditions
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            if (MyDebug.LOG) {
                Log.e(TAG, "setDisplayHomeAsUpEnabled() returned null in HelpActivity#onCreate", e);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar.
//      getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Creates an Intent for this Activity.
     *
     * @param callee the calling activity
     * @return Intent
     */
//   public static Intent createIntent(Context callee, String fragmentName) {
    public static Intent createIntent(Context callee, String activityName, String fragmentName) {
        Intent intent = new Intent(callee, HelpActivity.class);
        intent.putExtra(EXTRA_ACTIVITY_NAME, activityName);
        intent.putExtra(EXTRA_FRAGMENT_NAME, fragmentName);
        return intent;
    }
}
