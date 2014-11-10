package com.bse.daisybuzz.main;

import java.util.ArrayList;
import java.util.List;

import com.bse.daisybuzz.helper.DatabaseHelper;
import com.bse.daisybuzz.helper.Preferences;
import com.bse.daizybuzz.model.Marque;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class Fragment2 extends Fragment {
	DatabaseHelper db;
	
	RequestParams params = new RequestParams();

	Spinner spinner_achete, spinner_raisonAchat, spinner_ageClient,
			spinner_sexe, spinner_fidelite, spinner_marqueHabituelle,
			spinner_marqueAchetee, spinner_marqueHabituelle2;
	EditText txt_marqueHabituelleQte, txt_marqueAcheteeQte,
			txt_marqueHabituelleQte2, txt_commentaire;
	LinearLayout linearLayout1;
	LinearLayout linearLayout2;
	CheckBox cb_tombola;
	private Button btn_takePhoto, btn_save;
	ProgressDialog prgDialog;

	
	List<Marque> marquesList;
	
	
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
		linearLayout1 = (LinearLayout) view.findViewById(R.id.layout_1);
		linearLayout2 = (LinearLayout) view.findViewById(R.id.layout_2);

		spinner_achete = (Spinner) view.findViewById(R.id.spinner_achete);
		spinner_raisonAchat = (Spinner) view
				.findViewById(R.id.spinner_raisonAchat);
		spinner_fidelite = (Spinner) view.findViewById(R.id.spinner_fidelite);
		spinner_ageClient = (Spinner) view.findViewById(R.id.spinner_ageClient);
		spinner_marqueAchetee = (Spinner) view
				.findViewById(R.id.spinner_marqueAchetee);
		spinner_marqueHabituelle = (Spinner) view
				.findViewById(R.id.spinner_marqueHabituelle);
		spinner_sexe = (Spinner) view.findViewById(R.id.spinner_sexe);
		txt_marqueHabituelleQte = (EditText) view
				.findViewById(R.id.txt_marqueHabituelleQte);
		txt_marqueAcheteeQte = (EditText) view
				.findViewById(R.id.txt_marqueAcheteeQte);

		spinner_marqueHabituelle2 = (Spinner) view
				.findViewById(R.id.spinner_marqueHabituelle2);
		txt_marqueHabituelleQte2 = (EditText) view
				.findViewById(R.id.txt_marqueHabituelleQte2);
		txt_commentaire = (EditText) view.findViewById(R.id.txt_commentaire);

		cb_tombola = (CheckBox) view.findViewById(R.id.cb_tombola);

		btn_save = (Button) view.findViewById(R.id.btn_rapport_save);

		// listeners *****************

		btn_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				save(v);
			}
		});

		spinner_achete.setOnItemSelectedListener(new OnItemSelectedListener() {
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
		
		/* ****************************************************************************************************************
		 * Load data from sqlite
		 * ****************************************************************************************************************/

		db = new DatabaseHelper(this.getActivity().getApplicationContext());
		marquesList = db.getAllMarques();
		
		/* ****************************************************************************************************************
		 * Form controls populating
		 * ****************************************************************************************************************/
		
		// ##### superviseurs
		List<String> marquesArray =  new ArrayList<String>();
		
		for(Marque marque : marquesList){
			marquesArray.add(marque.getLibelle());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
		    this.getActivity(), android.R.layout.simple_spinner_item, marquesArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinner_marqueAchetee.setAdapter(adapter);
		spinner_marqueHabituelle.setAdapter(adapter);
		spinner_marqueHabituelle2.setAdapter(adapter);
		
		
		/* ****************************************************************************************************************
		 * Checking preferences for global parameters like tombola
		 * ****************************************************************************************************************/


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

	public void save(View v) {
		// valdiation
		if (1 == 2)
			return;
		
		
		
		if (spinner_achete.getSelectedItemPosition() == 0) {
			// getting values
			String age = spinner_ageClient.getSelectedItem().toString();
			String sexe = spinner_sexe.getSelectedItem().toString();
			String raisonAchat = spinner_raisonAchat.getSelectedItem()
					.toString();
			String fidelite = spinner_fidelite.getSelectedItem().toString();
			String marqueHabituelle = String.valueOf(marquesList.get(spinner_marqueHabituelle
					.getSelectedItemPosition()).getId());
			String marqueHabituelleQte = txt_marqueHabituelleQte.getText()
					.toString();
			String marqueAchetee = String.valueOf(marquesList.get(spinner_marqueAchetee
					.getSelectedItemPosition()).getId());
			String marqueAcheteeQte = txt_marqueAcheteeQte.getText().toString();
			String tombola = (cb_tombola.isChecked())?"1":"0";

			// setting parameters
			params.put("achete", "1");
			params.put("age", age);
			params.put("sexe", sexe);
			params.put("raisonAchat", raisonAchat);
			params.put("fidelete", fidelite);
			params.put("marqueHabituelle", marqueHabituelle);
			params.put("marqueHabituelleQte", marqueHabituelleQte);
			params.put("marqueAchetee", marqueAchetee);
			params.put("marqueAcheteeQte", marqueAcheteeQte);
			params.put("tombola", tombola);
		}

		if (spinner_achete.getSelectedItemPosition() == 1) {
			String age = spinner_ageClient.getSelectedItem().toString();
			String sexe = spinner_sexe.getSelectedItem().toString();
			String raisonRefu = "";
			String marqueHabituelle = String.valueOf(marquesList.get(spinner_marqueHabituelle
					.getSelectedItemPosition()).getId());
			String marqueHabituelleQte = txt_marqueHabituelleQte.getText()
					.toString();			
			String marqueAcheteeQte = txt_marqueAcheteeQte.getText().toString();
			String commentaire = txt_commentaire.getText().toString();

			// setting parameters
			params.put("achete", "0");
			params.put("age", age);
			params.put("sexe", sexe);
			params.put("raisonRefu", raisonRefu);			
			params.put("marqueHabituelle", marqueHabituelle);
			params.put("marqueHabituelleQte", marqueHabituelleQte);
			params.put("commentaire", commentaire);
			params.put("marqueAcheteeQte", marqueAcheteeQte);
		}

		// ********* saving
		

		// start upload of localisation data
		makeHTTPCall();
	}

	// Make Http call to upload image/ data to Php server
	public void makeHTTPCall() {

		Preferences preferences = new Preferences(this.getActivity());
		String webserviceRootUrl = preferences
				.getStringValue("PARAM_WEBSERVICE_ROOT_URL");

		prgDialog.setMessage("Communication avec le serveur...");
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
