package com.bse.daisybuzz.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class Fragment2 extends Fragment {
	Spinner spinner;
	LinearLayout linearLayout1;
	LinearLayout linearLayout2;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment2, null);
	    spinner = (Spinner) view.findViewById(R.id.spinner1);
	    linearLayout1 = (LinearLayout)view.findViewById(R.id.layout_1);
	    linearLayout2 = (LinearLayout)view.findViewById(R.id.layout_2);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	if(position == 0){
		    		linearLayout1.setVisibility(LinearLayout.VISIBLE);
		    		linearLayout2.setVisibility(LinearLayout.GONE);		    		
		    	}
		    	if(position == 1){
		    		linearLayout1.setVisibility(LinearLayout.GONE);
		    		linearLayout2.setVisibility(LinearLayout.VISIBLE);
		    	}

		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});
		
		return view;
	}
}
