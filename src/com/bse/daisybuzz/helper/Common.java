package com.bse.daisybuzz.helper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import com.bse.daizybuzz.model.Cadeau;
import com.bse.daizybuzz.model.Marque;
import com.bse.daizybuzz.model.PDV;
import com.bse.daizybuzz.model.RaisonAchat;
import com.bse.daizybuzz.model.RaisonRefus;
import com.bse.daizybuzz.model.Superviseur;
import com.bse.daizybuzz.model.TrancheAge;

public class Common {

	static InputStream is = null;
	static String result = null;
	static String line = null;
	
	/* *************************************************************************************
	 * 						Synchronizes all used data from database
	 * *********************************************************************************** */
	public static void synchronizeAll(Activity activity) {
		synchronizeAll(activity,"");
	}
	public static void synchronizeAll(Activity activity,String webserviceRootUrl) {
		
		Preferences preferences = new Preferences(activity);
		if(webserviceRootUrl.isEmpty())
			webserviceRootUrl = preferences.getStringValue("PARAM_WEBSERVICE_ROOT_URL");
	    
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
			Toast.makeText(activity.getApplicationContext(),
					"L'url du webservice n'est pas accessible !", Toast.LENGTH_LONG).show();
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
		DatabaseHelper db = new DatabaseHelper(activity.getApplicationContext());
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
			        int id = jsonas.getInt("id");
			        String libelle = jsonas.getString("libelle");			        
			        // Creating marque
					Marque marque = new Marque(id, libelle);
					// Inserting marque in db
					long marque_id = db.createMarque(marque);										
			    }
			    Log.e("Synchronization", "Marque : " + rows.length() + " records added to sqllite database");
			    Log.e("Synchronization", "Marque : " + db.getRecordsCount("marque") + " records existing at sqllite database");			    
			    // INSERTING PDVS ###################################################
			    rows= json_data.getJSONArray("pdvs");
			    Log.d("Rows",rows.toString());
			    for(int i=0; i< rows.length(); i++){
			        JSONObject jsonas = rows.getJSONObject(i);
			        int id = jsonas.getInt("id");
			        String nom = jsonas.getString("nom");
			        int licence = jsonas.getInt("licence");
			        // Creating pdv
					PDV pdv = new PDV(id, nom,licence);
					// Inserting pdv in db
					long pdv_id = db.createPDV(pdv);										
			    }
			    Log.e("Synchronization", "PDV : " + rows.length() + " records added to sqllite database");
			    Log.e("Synchronization", "PDV : " + db.getRecordsCount("pdv") + " records existing at sqllite database");
			    
			 // INSERTING CADEAUX ###################################################
			    rows= json_data.getJSONArray("cadeaux");
			    Log.d("Rows",rows.toString());
			    for(int i=0; i< rows.length(); i++){
			        JSONObject jsonas = rows.getJSONObject(i);
			        int id = jsonas.getInt("id");
			        String libelle = jsonas.getString("libelle");			        
			        // Creating cadeau
					Cadeau cadeau = new Cadeau(id,libelle);
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
			        int id = jsonas.getInt("id");
			        String nom = jsonas.getString("nom");
			        String prenom = jsonas.getString("prenom");;
			        // Creating marque
					Superviseur superviseur = new Superviseur(nom,prenom);
					// Inserting marque in db
					long superviseur_id = db.createSuperviseur(superviseur);										
			    }
			    
			    // INSERTING RAISONSACHAT ###################################################
			    rows= json_data.getJSONArray("raisonsAchat");
			    Log.d("Rows",rows.toString());
			    for(int i=0; i< rows.length(); i++){
			        JSONObject jsonas = rows.getJSONObject(i);
			        int id = jsonas.getInt("id");
			        String libelle = jsonas.getString("libelle");
					RaisonAchat raisonAchat = new RaisonAchat(id, libelle);
					db.createRaisonAchat(raisonAchat);										
			    }
			    Log.e("Synchronization", "RaisonsAchat : " + rows.length() + " records added to sqllite database");
			    Log.e("Synchronization", "RaisonsAchat : " + db.getRecordsCount("raisonachat") + " records existing at sqllite database");
			    
			 // INSERTING RAISONSREFU ###################################################
			    rows= json_data.getJSONArray("raisonsRefus");
			    Log.d("Rows",rows.toString());
			    for(int i=0; i< rows.length(); i++){
			        JSONObject jsonas = rows.getJSONObject(i);
			        int id = jsonas.getInt("id");
			        String libelle = jsonas.getString("libelle");			        
			        RaisonRefus raisonRefus = new RaisonRefus(id, libelle);
					db.createRaisonRefus(raisonRefus);										
			    }
			    Log.e("Synchronization", "RaisonsRefu : " + rows.length() + " records added to sqllite database");
			    Log.e("Synchronization", "RaisonsRefu : " + db.getRecordsCount("raisonrefus") + " records existing at sqllite database");
			    
			 // INSERTING tranchesAge ###################################################
			    rows= json_data.getJSONArray("tranchesAge");
			    Log.d("Rows",rows.toString());
			    for(int i=0; i< rows.length(); i++){
			        JSONObject jsonas = rows.getJSONObject(i);
			        int id = jsonas.getInt("id");
			        String libelle = jsonas.getString("libelle");			        
					TrancheAge trancheAge = new TrancheAge(id, libelle);
					db.createTrancheAge(trancheAge);										
			    }
			    Log.e("Synchronization", "RaisonsAchat : " + rows.length() + " records added to sqllite database");
			    Log.e("Synchronization", "RaisonsAchat : " + db.getRecordsCount("raisonachat") + " records existing at sqllite database");
			    
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
				Toast.makeText(activity.getBaseContext(),
						"L'application a été synchronisée correctement ! ", Toast.LENGTH_SHORT).show();

				
			} catch (JSONException e) {
				Toast.makeText(activity.getBaseContext(),
						"Erreur lors de la synchronization : " + e.getMessage(), Toast.LENGTH_LONG).show();
			    e.printStackTrace();
			}			
			
		} catch (Exception e) {
			Toast.makeText(activity.getBaseContext(),
					"Erreur lors de la synchronization : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	
	public static Location getLocation(Activity activity) {
        LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);  
        List<String> providers = lm.getProviders(true);

        /* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
        Location l = null;
        
        for (int i=providers.size()-1; i>=0; i--) {
                l = lm.getLastKnownLocation(providers.get(i));
                if (l != null) break;
        }        
        return l;
	}
}
