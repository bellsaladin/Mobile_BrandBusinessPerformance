package com.bse.daisybuzz.main;

import com.bse.daisybuzz.helper.Common;
import com.bse.daisybuzz.helper.Preferences;
import com.bse.daisybuzz.helper.Statics;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private ViewPager mPager;

	static ActionBar mActionbar;
	static Tab tab1, tab2, tab3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		/** Getting a reference to action bar of this activity */
		mActionbar = getSupportActionBar();

		/** Set tab navigation mode */
		mActionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mActionbar.setDisplayShowTitleEnabled(false);
		mActionbar.setDisplayShowHomeEnabled(false);
		// mActionbar.setDisplayShowTitleEnabled(false);

		/** Getting a reference to ViewPager from the layout */
		mPager = (ViewPager) findViewById(R.id.pager);

		/** Getting a reference to FragmentManager */
		FragmentManager fm = getSupportFragmentManager();

		/** Defining a listener for pageChange */
		ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				mActionbar.setSelectedNavigationItem(position);
			}

		};

		/** Setting the pageChange listener to the viewPager */
		mPager.setOnPageChangeListener(pageChangeListener);

		/** Creating an instance of FragmentPagerAdapter */
		MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(
				fm);

		/** Setting the FragmentPagerAdapter object to the viewPager object */
		mPager.setAdapter(fragmentPagerAdapter);

		/** Defining tab listener */
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				switch (tab.getPosition()) {
				case 0:
					tab.setIcon(R.drawable.icon_localisation);
					break;
				case 1:
					tab.setIcon(R.drawable.icon_rapport);
					break;
				case 2:
					tab.setIcon(R.drawable.icon_options);
					break;
				}
			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				
				switch (tab.getPosition()) {
				case 0:
					tab.setIcon(R.drawable.icon_localisation_selected);
					break;
				case 1:
					tab.setIcon(R.drawable.icon_rapport_selected);
					break;
				case 2:
					tab.setIcon(R.drawable.icon_options_selected);
					break;
				}
				
				mPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
			}
		};

		/** Creating fragment1 Tab */
		tab1 = mActionbar.newTab()//.setText("Localisation")
				// .setIcon(R.drawable.ic_launcher)
				.setIcon(R.drawable.icon_localisation_selected)
				// .setCustomView(R.layout.tab_custom_view)
				.setTabListener(tabListener);

		mActionbar.addTab(tab1);

		/** Creating fragment2 Tab */
		tab2 = mActionbar.newTab()//.setText("Rapport")
				// .setCustomView(R.layout.tab_custom_view)
				.setIcon(R.drawable.icon_rapport).setTabListener(tabListener);

		//mActionbar.addTab(tab2);

		tab3 = mActionbar.newTab()// .setText("Options")
				// .setCustomView(R.layout.tab_custom_view)
				.setIcon(R.drawable.icon_options)
				.setTabListener(tabListener);

		mActionbar.addTab(tab3);		

	}
	
	static void addReportTab(){
		mActionbar.addTab(tab2, 1);
	}

}
