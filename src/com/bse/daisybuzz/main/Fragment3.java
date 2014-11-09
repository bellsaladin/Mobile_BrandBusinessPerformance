package com.bse.daisybuzz.main;

import com.bse.daisybuzz.helper.Preferences;
import com.bse.daisybuzz.helper.DatabaseHelper;
import com.bse.daizybuzz.model.Cadeau;
import com.bse.daizybuzz.model.Marque;
import com.bse.daizybuzz.model.PDV;
import com.bse.daizybuzz.model.Superviseur;
import com.loopj.android.http.RequestParams;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Fragment3 extends Fragment {

	private Button btn_disconnect;
	private Button btn_synchronize;

	ProgressDialog prgDialog;

	RequestParams params = new RequestParams();
	String id;
	String name;
	InputStream is = null;
	String result = null;
	String line = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		/* prgDialog = new ProgressDialog(this.getActivity());
		// Set Cancelable as False
		prgDialog.setCancelable(false);*/
		View view = inflater.inflate(R.layout.fragment3, null);

		btn_disconnect = (Button) view.findViewById(R.id.btn_disconnect);
		btn_disconnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				disconnecte(v);
			}
		});
		btn_synchronize = (Button) view.findViewById(R.id.btn_synchronize);
		btn_synchronize.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				synchronize(v);
			}
		});
		
		if (android.os.Build.VERSION.SDK_INT > 9) { /* IMPORTANT FOR THE Webservice request to work */
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		
		return view;
	}

	public void synchronize(View v) {
		synchronizeAll();
	}

	public void disconnecte(View v) {

	}

	
	/* *************************************************************************************
	 * 						Synchronizes all used data from database
	 * *********************************************************************************** */
	
	public void synchronizeAll() {
		
		Preferences preferences = new Preferences(this.getActivity());
	    String webserviceRootUrl = preferences.getStringValue("PARAM_WEBSERVICE_ROOT_URL");
	    
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("id", "1"));
		
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(webserviceRootUrl + "/get_data.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			Log.e("pass 1", "connection success ");
		} catch (Exception e) {
			Log.e("Fail 1", e.toString());
			Toast.makeText(this.getActivity().getApplicationContext(),
					"Invalid IP Address", Toast.LENGTH_LONG).show();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
			Log.e("pass 2", "connection success ");
		} catch (Exception e) {
			Log.e("Fail 2", e.toString());
		}
		
		// preprate sqlLite database
		DatabaseHelper db = new DatabaseHelper(this.getActivity().getApplicationContext());
		db.onUpgrade(db.getWritableDatabase(), 0, 1); // force tables to delete		
		
		// start JSON parsing
		try {
			JSONObject json_data = new JSONObject(result);
			
			try {
				
				// INSERTING MARQUES ###################################################
			    JSONArray rows= json_data.getJSONArray("marques");
			    Log.d("Rows",rows.toString());
			    for(int i=0; i< rows.length(); i++){
			        JSONObject jsonas = rows.getJSONObject(i);
			        String libelle = jsonas.getString("libelle");			        
			        // Creating marque
					Marque marque1 = new Marque("libelle");
					// Inserting marque in db
					long marque_id = db.createMarque(marque1);										
			    }
			    Log.e("Synchronization", "Marque : " + rows.length() + " records added to sqllite database");
			    Log.e("Synchronization", "Marque : " + db.getRecordsCount("marque") + " records existing at sqllite database");			    
			    // INSERTING PDVS ###################################################
			    rows= json_data.getJSONArray("pdvs");
			    Log.d("Rows",rows.toString());
			    for(int i=0; i< rows.length(); i++){
			        JSONObject jsonas = rows.getJSONObject(i);
			        String nom = jsonas.getString("nom");
			        int licence = jsonas.getInt("licence");
			        // Creating marque
					PDV pdv = new PDV(nom,licence);
					// Inserting marque in db
					long pdv_id = db.createPDV(pdv);										
			    }
			    Log.e("Synchronization", "PDV : " + rows.length() + " records added to sqllite database");
			    Log.e("Synchronization", "PDV : " + db.getRecordsCount("pdv") + " records existing at sqllite database");
			    
			 // INSERTING CADEAUX ###################################################
			    rows= json_data.getJSONArray("cadeaux");
			    Log.d("Rows",rows.toString());
			    for(int i=0; i< rows.length(); i++){
			        JSONObject jsonas = rows.getJSONObject(i);
			        String libelle = jsonas.getString("libelle");			        
			        // Creating cadeau
					Cadeau cadeau = new Cadeau(libelle);
					// Inserting marque in db
					long cadeau_id = db.createCadeau(cadeau);										
			    }
			    Log.e("Synchronization", "Cadeau : " + rows.length() + " records added to sqllite database");
			    Log.e("Synchronization", "Cadeau : " + db.getRecordsCount("cadeau") + " records existing at sqllite database");
			    
			    // INSERTING PDVS ###################################################
			    rows= json_data.getJSONArray("superviseurs");
			    Log.d("Rows",rows.toString());
			    for(int i=0; i< rows.length(); i++){
			        JSONObject jsonas = rows.getJSONObject(i);
			        String nom = jsonas.getString("nom");
			        String prenom = jsonas.getString("prenom");;
			        // Creating marque
					Superviseur superviseur = new Superviseur(nom,prenom);
					// Inserting marque in db
					long superviseur_id = db.createSuperviseur(superviseur);										
			    }
			    Log.e("Synchronization", "Superviseur : " + rows.length() + " records added to sqllite database");
			    Log.e("Synchronization", "Superviseur : " + db.getRecordsCount("superviseur") + " records existing at sqllite database");
			    
			    rows= json_data.getJSONArray("parameters");			    
			    for(int i=0; i< rows.length(); i++){
			        JSONObject jsonas = rows.getJSONObject(i);
			        String key = jsonas.getString("key");
			        String value = jsonas.getString("value");;
			        preferences.saveValue(key, value);
			    }			    

			    // Save user authentification values
			    
			    preferences.saveValue("USER_PASSWORD","admin");
			    preferences.saveValue("USER_NAME","123");			    			    
			    
			    
			    // Info popup
				Toast.makeText(this.getActivity().getBaseContext(),
						"database synched corretly : " + preferences.getIntValue("TOMBOLA_ENABLED"), Toast.LENGTH_SHORT).show();

				
			} catch (JSONException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}			
			
		} catch (Exception e) {
			Log.e("Fail 3", e.toString());
		}
	}
	
	
}