package com.bse.daisybuzz.helper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.bse.daisybuzz.main.Fragment2;
import com.bse.daisybuzz.main.MainActivity;
import com.bse.daisybuzz.main.SynchronizerAlarmManagerBroadcastReceiver;
import com.bse.daisybuzz.main.SynchronizerService;
import com.bse.daizybuzz.model.Cadeau;
import com.bse.daizybuzz.model.Categorie;
import com.bse.daizybuzz.model.Localisation;
import com.bse.daizybuzz.model.Marque;
import com.bse.daizybuzz.model.MarqueCategorie;
import com.bse.daizybuzz.model.Pdv;
import com.bse.daizybuzz.model.PdvPoi;
import com.bse.daizybuzz.model.Poi;
import com.bse.daizybuzz.model.Produit;
import com.bse.daizybuzz.model.Questionnaire;
import com.bse.daizybuzz.model.RaisonAchat;
import com.bse.daizybuzz.model.RaisonRefus;
import com.bse.daizybuzz.model.Rapport;
import com.bse.daizybuzz.model.Superviseur;
import com.bse.daizybuzz.model.TrancheAge;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Common {

	static InputStream inputStream = null;
	static String result = null;
	static String line = null;

	/* *************************************************************************************
	 * Synchronizes all used data from database
	 * *********************************
	 * **************************************************
	 */
	public static void synchronizeAll(Activity activity) {
		synchronizeAll(activity, "");
	}

	public static boolean synchronizeAll(Activity activity,
			String webserviceRootUrl) {
		if (!Common.isNetworkAvailable(activity)) {
			Toast.makeText(
					activity.getApplicationContext(),
					"La connexion internet n'est pas disponible (vérifier que votre téléphone est bien configuré) !",
					Toast.LENGTH_LONG).show();
			return false;
		}
		Preferences preferences = new Preferences(activity);		

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("animateurId", String
				.valueOf(Statics.sfoId)));

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.DEFAULT_WEBSERVICE_URL_ROOT
					+ "/get_data.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
			Log.e("pass 1", "get data success ");
		} catch (Exception e) {
			Log.e("Fail 1", e.toString());
			Toast.makeText(
					activity.getApplicationContext(),
					"Impossible de communiquer avec le serveur distant ! Réessayer plus tard ...",
					Toast.LENGTH_LONG).show();
			return false;
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			inputStream.close();
			result = sb.toString();
			Log.e("pass 2", "synchronisation webservice sollicitation success ");
		} catch (Exception e) {
			Log.e("Fail 2", e.toString());
		}

		// preprate sqlLite database
		SqliteDatabaseHelper db = new SqliteDatabaseHelper(
				activity.getApplicationContext());

		// start JSON parsing
		try {
			Log.e("DEBUG", result);
			JSONObject json_data = new JSONObject(result);
			// get 'produit' created locally to keep them from deletion after the purge
			List<Produit> produitsCreatedLocally = new ArrayList<Produit>(); 
			for(Produit produit : db.getAllProduits()){
				if(produit.isAddedLocaly())
					produitsCreatedLocally.add(produit);
			}
			
			try {
				db.purgeServerFeedData(); // FIXME : I should make sure to have
											// no problem before purging the bd
											// !!
				// INSERTING MARQUES
				// ###################################################
				JSONArray rows = json_data.getJSONArray("marques");
				Log.d("Rows", rows.toString());
				for (int i = 0; i < rows.length(); i++) {
					JSONObject jsonas = rows.getJSONObject(i);
					int id = jsonas.getInt("id");
					String libelle = jsonas.getString("libelle");
					// Creating marque
					Marque marque = new Marque(id, libelle);
					// Inserting marque in db
					long marque_id = db.createMarque(marque);
				}
				Log.e("Synchronization", "Marque : " + rows.length()
						+ " records added to sqllite database");
				Log.e("Synchronization",
						"Marque : " + db.getRecordsCount("marque")
								+ " records existing at sqllite database");
				
				// INSERTING PRODUITS
				// ###################################################
				rows = json_data.getJSONArray("produits");
				Log.d("Rows", rows.toString());
				for (int i = 0; i < rows.length(); i++) {
					JSONObject jsonas = rows.getJSONObject(i);
					int id = jsonas.getInt("id");
					String sku = jsonas.getString("sku");
					String libelle = jsonas.getString("libelle");
					String categorieId = jsonas.getString("categorie_id");
					// Creating marque
					Produit produit = new Produit(id, sku, libelle, categorieId);
					// Inserting marque in db
					long produit_id = db.createProduit(produit);
				}
				// create also 'produits' created localy
				for(Produit produit: produitsCreatedLocally){
					long produit_id = db.createProduit(produit);
				}
				
				Log.e("Synchronization", "PRODUIT : " + rows.length()
						+ " records added to sqllite database");
				Log.e("Synchronization",
						"PRODUIT : " + db.getRecordsCount("produit")
								+ " records existing at sqllite database");
				
				// INSERTING CATEGORIES
				// ###################################################
				rows = json_data.getJSONArray("categories");
				Log.d("Rows", rows.toString());
				for (int i = 0; i < rows.length(); i++) {
					JSONObject jsonas = rows.getJSONObject(i);
					int id = jsonas.getInt("id");
					String context = jsonas.getString("context");
					String nom = jsonas.getString("nom");
					String parentId = jsonas.getString("parent_id");
					// Creating marque
					Categorie categorie = new Categorie(id, nom, context, parentId);
					// Inserting marque in db
					long categorie_id = db.createCategorie(categorie);
				}
				Log.e("Synchronization", "CATEGORIE : " + rows.length()
						+ " records added to sqllite database");
				Log.e("Synchronization",
						"CATEGORIE : " + db.getRecordsCount("categorie")
								+ " records existing at sqllite database");
				
				// INSERTING MARQUES_CATEGORIES
				// ###################################################
				rows = json_data.getJSONArray("marques_categories");
				Log.d("Rows", rows.toString());
				for (int i = 0; i < rows.length(); i++) {
					JSONObject jsonas = rows.getJSONObject(i);
					String marqueId = jsonas.getString("marque_id");
					String categorieId = jsonas.getString("categorie_id");
					// Creating marque
					MarqueCategorie marque_categorie = new MarqueCategorie(marqueId, categorieId);
					// Inserting marque in db
					long marque_categorie_id = db.createMarqueCategorie(marque_categorie);
				}
				Log.e("Synchronization", "MARQUE_CATEGORIE : " + rows.length()
						+ " records added to sqllite database");
				Log.e("Synchronization",
						"MARQUE_CATEGORIE : " + db.getRecordsCount("marque_categorie")
								+ " records existing at sqllite database");
				
				
				// INSERTING POIS
				// ###################################################
				rows = json_data.getJSONArray("pois");
				Log.d("Rows", rows.toString());
				for (int i = 0; i < rows.length(); i++) {
					JSONObject jsonas = rows.getJSONObject(i);
					int id = jsonas.getInt("id");
					String libelle = jsonas.getString("libelle");
					// Creating marque
					Poi poi = new Poi(id, libelle);
					// Inserting marque in db
					long poi_id = db.createPoi(poi);
				}
				Log.e("Synchronization", "POI : " + rows.length()
						+ " records added to sqllite database");
				Log.e("Synchronization",
						"POI : " + db.getRecordsCount("poi")
								+ " records existing at sqllite database");
				
				// INSERTING PDVS_POIS
				// ###################################################
				rows = json_data.getJSONArray("pdvs_pois");
				Log.d("Rows", rows.toString());
				for (int i = 0; i < rows.length(); i++) {
					JSONObject jsonas = rows.getJSONObject(i);
					String pdvId = jsonas.getString("pdv_id");
					String poiId = jsonas.getString("poi_id");
					// Creating marque
					PdvPoi pdv_poi = new PdvPoi(pdvId, poiId);
					// Inserting marque in db
					long pdv_poi_id = db.createPdvPoi(pdv_poi);
				}
				Log.e("Synchronization", "PDV_POI : " + rows.length()
						+ " records added to sqllite database");
				Log.e("Synchronization",
						"PDV_POI : " + db.getRecordsCount("pdv_poi")
								+ " records existing at sqllite database");
				
				// INSERTING PDVS
				// ###################################################
				rows = json_data.getJSONArray("pdvs");
				Log.d("Rows", rows.toString());
				for (int i = 0; i < rows.length(); i++) {
					JSONObject jsonas = rows.getJSONObject(i);
					int id = jsonas.getInt("id");
					String nom = jsonas.getString("nom");
					String licence = jsonas.getString("licence");
					String ville = jsonas.getString("ville");
					String secteur = jsonas.getString("secteur");
					// Creating pdv
					Pdv pdv = new Pdv(id, nom, licence, ville, secteur);
					// Inserting pdv in db
					long pdv_id = db.createPDV(pdv);
				}
				Log.e("Synchronization", "PDV : " + rows.length()
						+ " records added to sqllite database");
				Log.e("Synchronization", "PDV : " + db.getRecordsCount("pdv")
						+ " records existing at sqllite database");

				// INSERTING CADEAUX
				// ###################################################
				rows = json_data.getJSONArray("cadeaux");
				Log.d("Rows", rows.toString());
				for (int i = 0; i < rows.length(); i++) {
					JSONObject jsonas = rows.getJSONObject(i);
					int id = jsonas.getInt("id");
					String libelle = jsonas.getString("libelle");
					// Creating cadeau
					Cadeau cadeau = new Cadeau(id, libelle);
					// Inserting marque in db
					long cadeau_id = db.createCadeau(cadeau);
				}
				Log.e("Synchronization", "Cadeau : " + rows.length()
						+ " records added to sqllite database");
				Log.e("Synchronization",
						"Cadeau : " + db.getRecordsCount("cadeau")
								+ " records existing at sqllite database");

				// INSERTING PDVS
				// ###################################################
				rows = json_data.getJSONArray("superviseurs");
				Log.d("Rows", rows.toString());
				for (int i = 0; i < rows.length(); i++) {
					JSONObject jsonas = rows.getJSONObject(i);
					int id = jsonas.getInt("id");
					String nom = jsonas.getString("nom");
					String prenom = jsonas.getString("prenom");
					// Creating marque
					Superviseur superviseur = new Superviseur(id, nom, prenom);
					// Inserting marque in db
					long superviseur_id = db.createSuperviseur(superviseur);
				}

				// INSERTING RAISONSACHAT
				// ###################################################
				rows = json_data.getJSONArray("raisonsAchat");
				Log.d("Rows", rows.toString());
				for (int i = 0; i < rows.length(); i++) {
					JSONObject jsonas = rows.getJSONObject(i);
					int id = jsonas.getInt("id");
					String libelle = jsonas.getString("libelle");
					RaisonAchat raisonAchat = new RaisonAchat(id, libelle);
					db.createRaisonAchat(raisonAchat);
				}
				Log.e("Synchronization", "RaisonsAchat : " + rows.length()
						+ " records added to sqllite database");
				Log.e("Synchronization",
						"RaisonsAchat : " + db.getRecordsCount("raisonachat")
								+ " records existing at sqllite database");

				// INSERTING RAISONSREFU
				// ###################################################
				rows = json_data.getJSONArray("raisonsRefus");
				Log.d("Rows", rows.toString());
				for (int i = 0; i < rows.length(); i++) {
					JSONObject jsonas = rows.getJSONObject(i);
					int id = jsonas.getInt("id");
					String libelle = jsonas.getString("libelle");
					RaisonRefus raisonRefus = new RaisonRefus(id, libelle);
					db.createRaisonRefus(raisonRefus);
				}
				Log.e("Synchronization", "RaisonsRefu : " + rows.length()
						+ " records added to sqllite database");
				Log.e("Synchronization",
						"RaisonsRefu : " + db.getRecordsCount("raisonrefus")
								+ " records existing at sqllite database");

				// INSERTING tranchesAge
				// ###################################################
				rows = json_data.getJSONArray("tranchesAge");
				Log.d("Rows", rows.toString());
				for (int i = 0; i < rows.length(); i++) {
					JSONObject jsonas = rows.getJSONObject(i);
					int id = jsonas.getInt("id");
					String libelle = jsonas.getString("libelle");
					TrancheAge trancheAge = new TrancheAge(id, libelle);
					db.createTrancheAge(trancheAge);
				}
				Log.e("Synchronization", "RaisonsAchat : " + rows.length()
						+ " records added to sqllite database");
				Log.e("Synchronization",
						"RaisonsAchat : " + db.getRecordsCount("raisonachat")
								+ " records existing at sqllite database");

				rows = json_data.getJSONArray("parameters");
				for (int i = 0; i < rows.length(); i++) {
					JSONObject jsonas = rows.getJSONObject(i);
					String key = jsonas.getString("key");
					String value = jsonas.getString("value");
					;
					preferences.saveValue(key, value);
				}

				// try to repopulate rapport page
				// FIXME :
				// TODO : Régler ce problème
				if (MainActivity.fragmentPagerAdapter != null) {
					Fragment2 fragment2 = (Fragment2) MainActivity.fragmentPagerAdapter
							.getFragment(1);
					if (fragment2 != null)
						fragment2.populateFields();
				}
				// Info popup
				Toast.makeText(activity.getBaseContext(),
						"L'application a été synchronisée correctement ! ",
						Toast.LENGTH_SHORT).show();

			} catch (JSONException e) {
				Log.e("DEBUG", e.getMessage());
				Toast.makeText(activity.getBaseContext(),
						"Erreur lors de la synchronization ! ",
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			/*
			 * Toast.makeText(activity.getBaseContext(),
			 * "Erreur lors de la synchronization ! La connection est trop lente."
			 * , Toast.LENGTH_LONG).show()
			 */
		}
		return true;
	}


	public static void sendLocalisationToServer(final Localisation localisation,
			String webserviceRootUrl, SqliteDatabaseHelper db, Activity activity) {
		RequestParams params = new RequestParams();
		params.put("sfoId", localisation
				.getSfoId());
		params.put("superviseurId", localisation
				.getSuperviseurId());
		params.put("pdvId", localisation
				.getPdvId());
		params.put("imageFileName", localisation
				.getCheminImage());
		params.put("latitude", localisation
				.getLatitude());
		params.put("longitude", localisation
				.getLongitude());
		params.put("licenceRemplacee",
				localisation.getLicenceRemplacee());
		params.put("motif", localisation
				.getMotif());
		params.put("dateCreation", localisation
				.getDateCreation());
		
		// ******************* ENCODING IMAGE ********************** //
		Bitmap bitmap = Utils.getBitmapUsingRealPath(localisation
				.getCheminImage());
		BitmapFactory.Options options = null;
		options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		bitmap = BitmapFactory.decodeFile(localisation.getCheminImage(),
				options);
		// Log.e("Bitmap", "Bitmap" + localisation.getCheminImage());
		// Log.e("Bitmap", "Bitmap" + bitmap.getHeight());
		if (bitmap != null) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			// Must compress the Image to reduce image size to make upload
			// easy
			bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
			byte[] byte_arr = stream.toByteArray();
			// Encode Image to String
			String encodedString = Base64.encodeToString(byte_arr, 0);
			params.put("image", encodedString);
			// Log.e("Debug", "image : " + encodedString);
			bitmap.recycle();
			bitmap = null;
		}

		// ******************* ENCODING IMAGE ********************** //	

		try {			
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Constants.HTTP_REQUEST_TIMEOUT);
			client.post(Constants.DEFAULT_WEBSERVICE_URL_ROOT + "/save_localisation.php", params,
					new AsyncHttpResponseHandler() {
						// When the response returned by REST has Http
						// response code '200'
						@Override
						public void onSuccess(String response) {
							Log.e("Async response",response);
							// Common.result = response;
							String insertedLocalisationId = response.trim();
							localisation.setInsertedInServerWithId(insertedLocalisationId);
							//SynchronizerAlarmManagerBroadcastReceiver.db.updateLocalisation(localisation);
							SynchronizerService.db.updateLocalisation(localisation);
							Log.e("LOCALISATION","result : " + insertedLocalisationId);

						}
						// When the response returned by REST has Http
						// response code other than '200' such as '404',
						// '500' or '403' etc
						@Override
						public void onFailure(int statusCode, Throwable error,
								String content) {

						}
					}
			);
			
		} catch (Exception e) {
			Log.e("Fail 1", e.toString());
			/*
			 * Toast.makeText( activity.getApplicationContext(),
			 * "Impossible de communiquer avec le serveur distant ! Réessayer plus tard ..."
			 * + e.getMessage(), Toast.LENGTH_LONG).show();
			 */
		}

	}

	public static void sendQuestionnaireToServer(final Questionnaire questionnaire,
			String webserviceRootUrl, SqliteDatabaseHelper db, Activity activity) {
		RequestParams params = new RequestParams();		
		
		params.put("type", questionnaire.getType());
		params.put("quantitiesData", questionnaire.getQuantitiesData());
		params.put("localisationId", questionnaire.getLocalisationId());
		params.put("nbrLignesTraitees", String.valueOf(questionnaire.getNbrLignesTraitees()));
		params.put("tempsRemplissage", String.valueOf(questionnaire.getTempsRemplissage()));
		params.put("dateCreation", questionnaire.getDateCreation());

		try {
			
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Constants.HTTP_REQUEST_TIMEOUT);
			client.post(Constants.DEFAULT_WEBSERVICE_URL_ROOT + "/save_questionnaire.php", params,
					new AsyncHttpResponseHandler() {
						// When the response returned by REST has Http
						// response code '200'
						@Override
						public void onSuccess(String response) {
							//SynchronizerAlarmManagerBroadcastReceiver.db.deleteQuestionnaire(questionnaire);
							SynchronizerService.db.deleteQuestionnaire(questionnaire);
							Log.i("Send rapport ", response);
						}

						// When the response returned by REST has Http
						// response code other than '200' such as '404',
						// '500' or '403' etc
						@Override
						public void onFailure(int statusCode, Throwable error,
								String content) {
							Log.e("Error Send Questionnaire ", content);

						}
					}
			);			
		} catch (Exception e) {
			Log.e("Exception Send Rapport", e.toString());
			/*
			 * Toast.makeText( activity.getApplicationContext(),
			 * "Impossible de communiquer avec le serveur distant ! Réessayer plus tard ..."
			 * , Toast.LENGTH_LONG).show();
			 */
		}
	}

	public static Location getLocation(Activity activity) {
		LocationManager lm = (LocationManager) activity
				.getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = lm.getProviders(true);

		/*
		 * Loop over the array backwards, and if you get an accurate location,
		 * then break out the loop
		 */
		Location l = null;

		for (int i = providers.size() - 1; i >= 0; i--) {
			
			l = lm.getLastKnownLocation(providers.get(i));
			if (l != null)
				return l;
			
		}
		return l;
	}

	public static boolean isNetworkAvailable(Activity activity) {
		ConnectivityManager connectivityManager = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

}
