package com.bse.daisybuzz.main;

import com.bse.daisybuzz.helper.Common;
import com.bse.daisybuzz.helper.Preferences;

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
import android.widget.EditText;

public class MainActivity extends ActionBarActivity {

	private ViewPager mPager;

	ActionBar mActionbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
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
			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				mPager.setCurrentItem(tab.getPosition());

			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
			}
		};

		/** Creating fragment1 Tab */
		Tab tab = mActionbar.newTab().setText("Localisation")
		// .setIcon(R.drawable.ic_launcher)
				.setTabListener(tabListener);

		mActionbar.addTab(tab);

		/** Creating fragment2 Tab */
		tab = mActionbar.newTab().setText("Rapport")
		// .setIcon(R.drawable.ic_launcher)
				.setTabListener(tabListener);

		mActionbar.addTab(tab);

		tab = mActionbar.newTab().setText("Options")
		// .setIcon(R.drawable.ic_launcher)
				.setTabListener(tabListener);

		mActionbar.addTab(tab);		

	}

	

}
