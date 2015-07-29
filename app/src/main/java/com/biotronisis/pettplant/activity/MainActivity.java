package com.biotronisis.pettplant.activity;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.biotronisis.pettplant.R;
import com.biotronisis.pettplant.activity.fragment.PettPlantFragment;
import com.biotronisis.pettplant.debug.MyDebug;
import com.biotronisis.pettplant.file.ErrorHandler;
import com.biotronisis.pettplant.service.PettPlantService;

public class MainActivity extends AbstractBaseActivity {

   // This was added on the develop branch.

   private static String TAG = "MainActivity";

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
      ErrorHandler.getInstance(this.getApplicationContext());

      Intent intent = PettPlantService.createIntent(this);
      startService(intent);
      if (MyDebug.LOG) {
         Log.d(TAG, "Started PettPlantService.");
      }

      if (savedInstanceState == null) {
         FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
         PettPlantFragment fragment = new PettPlantFragment();
         transaction.replace(R.id.pett_plant_fragment, fragment);
         transaction.commit();
      }
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
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.

      switch (item.getItemId()) {
         case R.id.action_help: {
            Intent intent = HelpActivity.createIntent(this, getActivityName(), fragmentName);
            startActivity(intent);
            return true;
         }
      }

      return super.onOptionsItemSelected(item);
   }
}
