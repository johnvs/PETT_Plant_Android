package com.biotronisis.pettplant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
//import android.support.v7.appcompat.BuildConfig;

import com.biotronisis.pettplant.BuildConfig;
import com.biotronisis.pettplant.R;
//import com.biotronisis.pettplant.plant.processor.PlantState;

public class AboutActivity extends AbstractBaseActivity {

    private static final String TAG = "AboutActivity";

    private TextView versionNumberTV;

    @Override
    public String getActivityName() {
        return TAG;
    }

    @Override
    public String getHelpKey() {
        return "about";
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
//      intent.putExtra(EXTRA_PLANT_STATE, plantState);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceSate) {
        super.onCreate(savedInstanceSate);
        setContentView(R.layout.activity_about);

        versionNumberTV = (TextView) findViewById(R.id.versionNumber);

//      int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        // Get the version number from the system
        versionNumberTV.setText(versionName);
//      versionNumberTV.setText("test");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
