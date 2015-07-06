package com.biotronisis.pettplant.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.biotronisis.pettplant.R;


public class MainActivity extends AbstractBaseActivity {

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
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_main, menu);

      if (savedInstanceState == null) {
         FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
         PettPlantFragment fragment = new PettPlantFragment();
         transaction.replace(R.id.sample_content_fragment, fragment);
         transaction.commit();
      }

      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();

      //noinspection SimplifiableIfStatement
      if (id == R.id.action_settings) {
         return true;
      }

      return super.onOptionsItemSelected(item);
   }
}
