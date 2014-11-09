package com.bse.daisybuzz.main;

import com.bse.daisybuzz.helper.Preferences;
import com.bse.daizybuzz.model.PDV;
import com.bse.daizybuzz.model.Superviseur;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class Fragment2 extends Fragment {
	
	RequestParams params = new RequestParams();
	
	Spinner spinner;
	LinearLayout linearLayout1;
	LinearLayout linearLayout2;
	CheckBox cb_tombola;
	private Button btn_takePhoto, btn_save;
	ProgressDialog prgDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		prgDialog = new ProgressDialog(this.getActivity());
		// Set Cancelable as False
		prgDialog.setCancelable(false);
		View view = inflater.inflate(R.layout.fragment2, null);
		
		/* ****************************************************************************************************************
		 * Finding views and implemeting listeners
		 * ******************************
		 * ****************************************
		 * *****************************************
		 */		
		
		btn_save = (Button) view.findViewById(R.id.btn_rapport_save);
		spinner = (Spinner) view.findViewById(R.id.spinner1);
		linearLayout1 = (LinearLayout) view.findViewById(R.id.layout_1);
		linearLayout2 = (LinearLayout) view.findViewById(R.id.layout_2);
		cb_tombola = (CheckBox) view.findViewById(R.id.cb_tombola);

		// listeners *****************

		btn_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				save(v);
			}
		});
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				if (position == 0) {
					linearLayout1.setVisibility(LinearLayout.VISIBLE);
					linearLayout2.setVisibility(LinearLayout.GONE);
				}
				if (position == 1) {
					linearLayout1.setVisibility(LinearLayout.GONE);
					linearLayout2.setVisibility(LinearLayout.VISIBLE);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		Preferences preferences = new Preferences(this.getActivity());
		if (!preferences.getStringValue("PARAM_TOMBOLA_ENABLED").isEmpty()) {
			int tombolaEnabled = Integer.valueOf(preferences
					.getStringValue("PARAM_TOMBOLA_ENABLED"));
			if (tombolaEnabled != 1) {
				cb_tombola.setVisibility(CheckBox.GONE);
			}
		}

		return view;
	}
	
	
	
	public void save(View v){		
		// valdiation
		if(1 == 2)
			return;		
					
		// ********* saving
		
		// set parameters
		/*String licenceRemplacee = txt_licenceRemplacee.getText().toString();
		String motif = txt_motif.getText().toString();
		Superviseur superviseur = superviseursList.get(spinner_superviseur.getSelectedItemPosition());
		PDV pdv = pdvsList.get(spinner_pdv.getSelectedItemPosition());
		params.put("superviseur", String.valueOf(superviseur.getId()));
		params.put("pdv", String.valueOf(pdv.getId()));
		Location location = locationManager.getLastKnownLocation(provider);
		params.put("longitude", String.valueOf(location.getLongitude()));
		params.put("latitude", String.valueOf(location.getLatitude()));*/
		
		
		// params.put("licenceRemplacee", licenceRemplacee);
		params.put("achete", "1");
		
		// start upload of localisation data
		makeHTTPCall();
	}

	// Make Http call to upload image/ data to Php server
	public void makeHTTPCall() {

		Preferences preferences = new Preferences(this.getActivity());
		String webserviceRootUrl = preferences
				.getStringValue("PARAM_WEBSERVICE_ROOT_URL");

		prgDialog.setMessage("Invoking Php");
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(3000000); // 30 seconds
		// Don't forget to change the IP address to your LAN address. Port no as
		// well.
		client.post(webserviceRootUrl + "/save_rapport.php", params,
				new AsyncHttpResponseHandler() {
					// When the response returned by REST has Http
					// response code '200'
					@Override
					public void onSuccess(String response) {
						// Hide Progress Dialog
						prgDialog.hide();
						Toast.makeText(
								Fragment2.this.getActivity()
										.getApplicationContext(), response,
								Toast.LENGTH_LONG).show();
					}

					// When the response returned by REST has Http
					// response code other than '200' such as '404',
					// '500' or '403' etc
					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// Hide Progress Dialog
						prgDialog.hide();
						// When Http response code is '404'
						if (statusCode == 404) {
							Toast.makeText(
									Fragment2.this.getActivity()
											.getApplicationContext(),
									"Le webservice demandé n'est pas disponible !",
									Toast.LENGTH_LONG).show();
						}
						// When Http response code is '500'
						else if (statusCode == 500) {
							Toast.makeText(
									Fragment2.this.getActivity()
											.getApplicationContext(),
									"IL y a eu un problème au niveau du script !",
									Toast.LENGTH_LONG).show();
						}
						// When Http response code other than 404, 500
						else {
							Toast.makeText(
									Fragment2.this.getActivity()
											.getApplicationContext(),
									"Error Occured \n Most Common Error: \n1. Device not connected to Internet\n2. Web App is not deployed in App server\n3. App server is not running\n HTTP Status code : "
											+ statusCode, Toast.LENGTH_LONG)
									.show();
						}

					}
				});
	}
}
