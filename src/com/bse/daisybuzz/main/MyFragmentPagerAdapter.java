package com.bse.daisybuzz.main;

import com.bse.daisybuzz.helper.Statics;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.ViewGroup;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter{
	
	private SparseArray<Fragment> map = new SparseArray<Fragment>();
	final int PAGE_COUNT = 3;
	
	/** Constructor of the class */
	public MyFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		
	}

	/** This method will be invoked when a page is requested to create */
	@Override
	public Fragment getItem(int position) {
		Bundle data = new Bundle();
		switch(position){
		
			/** tab1 is selected */
			case 0:
				Fragment1 fragment1 = new Fragment1();
				map.put(position, fragment1);
				return fragment1;
				
			/** tab2 is selected */
			case 1:
					Fragment2 fragment2 = new Fragment2();
					map.put(position, fragment2);
					return fragment2;
			/** tab3 is selected */
			case 2:
				Fragment3 fragment3 = new Fragment3();
				map.put(position, fragment3);
				return fragment3;
		}
		
		return null;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
	    map.remove(position);
	    super.destroyItem(container, position, object);
	}
	
	public Fragment getFragment(int pos) {
	    return map.get(pos);
	}

	/** Returns the number of pages */
	@Override
	public int getCount() {		
		return PAGE_COUNT;
	}
	
}
