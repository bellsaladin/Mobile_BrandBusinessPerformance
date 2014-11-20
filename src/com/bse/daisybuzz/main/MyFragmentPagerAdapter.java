package com.bse.daisybuzz.main;

import com.bse.daisybuzz.helper.Statics;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.MenuItem;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter{
	
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
				return fragment1;
				
			/** tab2 is selected */
			case 1:
					Fragment2 fragment2 = new Fragment2();
					return fragment2;				
			/** tab3 is selected */
			case 2:
				Fragment3 fragment3 = new Fragment3();
				return fragment3;
		}
		
		return null;
	}

	/** Returns the number of pages */
	@Override
	public int getCount() {		
		return PAGE_COUNT;
	}
	
}
