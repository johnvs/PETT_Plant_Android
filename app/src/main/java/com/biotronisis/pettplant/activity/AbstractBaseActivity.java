package com.biotronisis.pettplant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.view.MenuItem;

public abstract class AbstractBaseActivity extends AppCompatActivity implements IntentParams {

    private boolean active;
    private boolean hasFragments;

    public static String fragmentName = null;

    /**
     * Returns the name of this Activity.
     *
     * @return activity name
     */
    protected abstract String getActivityName();

    /**
     * The key used to display the help for this activity.
     *
     * @return activity's help key
     */
    public abstract String getHelpKey();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//      ActionBar actionBar = getSupportActionBar();
//      actionBar.setDisplayHomeAsUpEnabled(true);

        // Set the size of the activity title (larger than default)
//      int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
//      TextView titleTextView = (TextView) findViewById(titleId);
//      titleTextView.setTextColor(getResources().getColor(android.R.color.black));
//      titleTextView.setTextSize((float) 24.0);

        // and increase the left margin
//    LinearLayout.LayoutParams layoutParams =
//          (LinearLayout.LayoutParams) titleTextView.getLayoutParams();
//    layoutParams.setMargins(10, 0, 0, 0);
//    titleTextView.setLayoutParams(layoutParams);
    }

    @Override
    protected void onResume() {
        super.onResume();
        active = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        active = false;
    }

//    public boolean isActive() {
//        return active;
//    }

//    private class HelpOnMenuItemClicked implements ActionMenuView.OnMenuItemClickListener {
//
//        @Override
//        public boolean onMenuItemClick(MenuItem item) {
//
//            if (!hasFragments) {
//                fragmentName = null;
//            }
//
//            Intent intent = HelpActivity.createIntent(AbstractBaseActivity.this, getActivityName(),
//                  fragmentName);
//            startActivity(intent);
//            return false;
//        }
//    }

    public interface MyOnClickListener {

        void onClick();
    }

}
