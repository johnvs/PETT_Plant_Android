package com.biotronisis.pettplant.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.biotronisis.pettplant.R;
import com.biotronisis.pettplant.activity.fragment.PettPlantFragment;
import com.biotronisis.pettplant.communication.CommunicationErrorType;
import com.biotronisis.pettplant.communication.CommunicationManager;
import com.biotronisis.pettplant.debug.MyDebug;
import com.biotronisis.pettplant.file.ErrorHandler;
import com.biotronisis.pettplant.model.CommunicationParams;
import com.biotronisis.pettplant.plant.PettPlantService;
import com.biotronisis.pettplant.plant.processor.PlantState;

public class MainActivity extends AbstractBaseActivity {

    private static final String TAG = "MainActivity";

    private boolean illBeBack = false;

    private final MyCommunicationManagerListener myCommunicationManagerListener =
          new MyCommunicationManagerListener();

    @Override
    public String getActivityName() {
        return TAG;
    }

    @Override
    public String getHelpKey() {
        return "main";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a new Error Handler object in case the application was stopped
        // but not destroyed, in which case, MyApplication.onCreate will not be executed.
        ErrorHandler.getInstance(getApplicationContext());

        if (MyDebug.LOG) {
            Log.d(TAG, "---------- onCreate ----------");
        }

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            PettPlantFragment fragment = new PettPlantFragment();
            transaction.replace(R.id.pett_plant_fragment, fragment);
            transaction.commit();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (MyDebug.LOG) {
            Log.d(TAG, "---------- onResume ---------- ");
        }
        illBeBack = false;

        // During a configuration change (i.e. device rotation), the service is not stopped,
        // so check if the service is running before starting it.
        PettPlantService pettPlantService = PettPlantService.getInstance();
        if (pettPlantService == null) {

            Intent intent = PettPlantService.createIntent(this);
            startService(intent);

            if (MyDebug.LOG) {
                Log.d(TAG, "---------- Started PettPlantService ---------- ");
            }

        } else {
            updateActionBar();
            pettPlantService.addCommStatusListener(myCommunicationManagerListener);
        }

        LocalBroadcastManager.getInstance(this).
              registerReceiver(pettPlantServiceEventReceiver,
                    new IntentFilter(PettPlantService.SERVICE_EVENT));
    }

    @Override
    public void onPause() {
        if (MyDebug.LOG) {
            Log.d(TAG, "---------- onPause ---------- ");
        }
        super.onPause();
        PettPlantService pettPlantService = PettPlantService.getInstance();
        if (pettPlantService != null) {
            pettPlantService.removeCommStatusListener(myCommunicationManagerListener);
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(pettPlantServiceEventReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
//      super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle options menu item clicks here. The action bar will automatically handle clicks
        // on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings: {
                illBeBack = true;
                Intent intent = SettingsActivity.createIntent(this, null);
                startActivityForResult(intent, REQUEST_STATE);
                return true;
            }
            case R.id.action_help: {
                illBeBack = true;
                Intent intent = HelpActivity.createIntent(this, getActivityName(), fragmentName);
                startActivity(intent);
                return true;
            }
            case R.id.action_about: {
                illBeBack = true;
                Intent intent = AboutActivity.createIntent(this);
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_STATE) {
            if (resultCode == RESULT_OK) {
                PlantState returnedPlantState = (PlantState) data.getSerializableExtra(EXTRA_PLANT_STATE);

                if (returnedPlantState != null) {
                    PettPlantFragment pettPlantFragment = (PettPlantFragment) getSupportFragmentManager()
                          .findFragmentById(R.id.pett_plant_fragment);

                    pettPlantFragment.updateState(returnedPlantState);
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Don't stop the service if we are only rotating the device
        if (!isChangingConfigurations() && !illBeBack) {
            Intent intent = PettPlantService.createIntent(this);
            stopService(intent);
        }

    }

    /**
     * Creates an Intent for this Activity.
     *
     * @param callee the calling activity
     * @return Intent
     */
    public static Intent createIntent(Context callee) {
        if (MyDebug.LOG) {
            Log.d("MainActivity", "creating main activity intent");
        }
        return new Intent(callee, MainActivity.class);
    }

    private class MyCommunicationManagerListener implements CommunicationManager.CommunicationManagerListener {

        @Override
        public void onDataReceived() {
        }

        @Override
        public void onDataSent() {
        }

        @Override
        public void onConnecting() {
        }

        @Override
        public void onConnected() {
            updateActionBar();
        }

        @Override
        public void onDisconnected() {
            updateActionBar();
        }

        @Override
        public void onError(CommunicationErrorType type) {
            if (MyDebug.LOG) {
                Log.d(TAG, "Connection Manager - Error");
            }
        }
    }

    /**
     * Updates the status (sub title) on the action bar.
     */
    private void updateActionBar() {

        CommunicationParams communicationParams = new CommunicationParams(this);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        PettPlantService pettPlantService = PettPlantService.getInstance();
        if (pettPlantService != null) {
            String message;
            if (pettPlantService.isConnected()) {
                message = getString(R.string.title_connected_to, communicationParams.getName());
            } else {
                message = getString(R.string.title_not_connected);
            }
            actionBar.setSubtitle(message);
        }

    }

    private final BroadcastReceiver pettPlantServiceEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(PettPlantService.EXTRA_EVENT_MESSAGE);
            if (MyDebug.LOG) {
                Log.d("Service receiver", "Got message: " + message);
            }

            if (message.equals(PettPlantService.SERVICE_CREATED)) {
                PettPlantService pettPlantService = PettPlantService.getInstance();
                if (pettPlantService != null) {
                    pettPlantService.addCommStatusListener(myCommunicationManagerListener);
                    updateActionBar();
                    if (MyDebug.LOG) {
                        Log.d(TAG, "PettPlantServiceBroadcastReceiver$onReceive() - Got service.");
                    }
                } else {
                    if (MyDebug.LOG) {
                        Log.d(TAG, "PettPlantServiceBroadcastReceiver$onReceive() - service is null.");
                    }
                }
            }
        }
    };

}
