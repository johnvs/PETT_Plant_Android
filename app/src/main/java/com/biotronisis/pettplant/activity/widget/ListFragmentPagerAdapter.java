package com.biotronisis.pettplant.activity.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ListFragmentPagerAdapter extends FragmentPagerAdapter implements OnPageChangeListener {
	
    public static final String TAG = "ListFragmentPagerAdapter";
    
    private Context context;
    
	private List<TabItem> items = new ArrayList<TabItem>();
	private ArrayList<WeakReference<Fragment>> fragments = new ArrayList<WeakReference<Fragment>>();
	private int currentIndex;
	
    public ListFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    public void add(TabItem tabItem) {
    	items.add(tabItem);
    	fragments.add(null);
    	
    	// Initialize current after adding the first tab to the list 
    	if (getCount() == 1) {
            currentIndex = 0;
    	}
    }
    
    @Override
    public Fragment getItem(int position) {
    	TabItem item = items.get(position);
    	Fragment frag = Fragment.instantiate(context, item.fragmentClass, item.args);
    	fragments.set(position, new WeakReference<Fragment>(frag));
        return frag;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return items.get(position).title;
    }

    @Override
    public int getCount() {
      return items.size();
    }
    
    @Override
	public void onPageScrollStateChanged(int state) {/*ignore*/}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {/*ignore*/}

	@Override
	public void onPageSelected(int position) {
		selectFragment(position);
	}
	
	private void selectFragment(int position) {
		WeakReference<Fragment> currentRef = fragments.get(currentIndex);
		if (currentRef != null && currentRef.get() != null) {
			Fragment unselected = currentRef.get();
			if (unselected instanceof OnFragmentSelectedListener) {
				((OnFragmentSelectedListener)unselected).onFragmentUnselected();
			}
		}
		
		currentIndex = position;
		
		WeakReference<Fragment> newRef = fragments.get(position);
		if (newRef != null && newRef.get() != null) {
			Fragment selected = newRef.get();
			if (selected instanceof OnFragmentSelectedListener) {
				((OnFragmentSelectedListener)selected).onFragmentSelected();
			}
		}
	}
    
    public interface OnFragmentSelectedListener {
		void onFragmentSelected();
		void onFragmentUnselected();
	}
    
    public static class TabItem {
    	private String title;
    	private String fragmentClass;
    	private Bundle args;
    	
    	public TabItem(String title, Class<? extends Fragment> fragmentClass) {
    		this(title, fragmentClass, null);
    	}
    	
    	public TabItem(String title, Class<? extends Fragment> fragmentClass, Bundle args) {
    		this.title = title;
    		this.fragmentClass = fragmentClass.getName();
    		this.args = args;
    	}
    }
}


