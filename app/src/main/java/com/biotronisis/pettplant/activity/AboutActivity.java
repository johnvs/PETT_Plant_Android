package com.biotronisis.pettplant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.TextView;

import com.biotronisis.pettplant.BuildConfig;
import com.biotronisis.pettplant.R;

public class AboutActivity extends AbstractBaseActivity {

    private static final String TAG = "AboutActivity";

    @Override
    public String getActivityName() {
        return TAG;
    }

    @Override
    public String getHelpKey() {
        return "about";
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, AboutActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceSate) {
        super.onCreate(savedInstanceSate);
        setContentView(R.layout.activity_about);

        TextView versionNumberTV = findViewById(R.id.versionNumber);

        // Get the version number from the system
        versionNumberTV.setText(BuildConfig.VERSION_NAME);

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
