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

import com.bse.daisybuzz.main.Fragment1;
import com.bse.daisybuzz.main.Fragment2;
import com.bse.daisybuzz.main.MainActivity;
import com.bse.daizybuzz.model.Cadeau;
import com.bse.daizybuzz.model.Localisation;
import com.bse.daizybuzz.model.Marque;
import com.bse.daizybuzz.model.PDV;
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
		if(!Common.isNetworkAvailable(activity)){
			Toast.makeText(
					activity.getApplicationContext(), "La connexion internet n'est pas disponible (vérifier que votre téléphone est bien configuré) !",
					Toast.LENGTH_LONG).show();
			return false;		
		}
		Preferences preferences = new Preferences(activity);
		if (webserviceRootUrl.isEmpty())
			webserviceRootUrl = preferences
					.getStringValue("PARAM_WEBSERVICE_ROOT_URL");

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("animateurId",
				String.valueOf(Statics.animateurId)));

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(webserviceRootUrl
					+ "/get_data.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
			Log.e("pass 1", "connection success ");
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
			Log.e("pass 2", "connection success ");
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

			try {
				db.purgeServerFeedData(); // FIXME : I should make sure to have no problem before purging the bd !!
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
				// INSERTING PDVS
				// ###################################################
				rows = json_data.getJSONArray("pdvs");
				Log.d("Rows", rows.toString());
				for (int i = 0; i < rows.length(); i++) {
					JSONObject jsonas = rows.getJSONObject(i);
					int id = jsonas.getInt("id");
					String nom = jsonas.getString("nom");
					int licence = jsonas.getInt("licence");
					// Creating pdv
					PDV pdv = new PDV(id, nom, licence);
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
					;
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
				if(MainActivity.fragmentPagerAdapter != null){
					Fragment2 fragment2 = (Fragment2)MainActivity.fragmentPagerAdapter.getFragment(1);
					if(fragment2 != null)
						fragment2.populateFields();
				}
				// Info popup
				Toast.makeText(activity.getBaseContext(),
						"L'application a été synchronisée correctement ! ",
						Toast.LENGTH_SHORT).show();

			} catch (JSONException e) {
				Log.e("DEBUG", e.getMessage());
				Toast.makeText(
						activity.getBaseContext(),
						"Erreur lors de la synchronization ! ",
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			/*Toast.makeText(activity.getBaseContext(),
					"Erreur lors de la synchronization ! La connection est trop lente.",
					Toast.LENGTH_LONG).show()*/
		}
		return true;
	}

	public static void pushAllDataToServer(final Activity activity,
			final ProgressDialog prgDialog) {
		if (!Common.isNetworkAvailable(activity)) {
			Toast.makeText(activity.getApplicationContext(), "La connexion internet n'est pas disponible !",
					Toast.LENGTH_SHORT).show();
			return;
		}
		final SqliteDatabaseHelper db = new SqliteDatabaseHelper(
				activity.getApplicationContext());
		List<Localisation> localisationsList = db.getAllLocalisations();
		int localisationsCount = localisationsList.size();
		
		if(localisationsCount==0){
			Toast.makeText(activity.getApplicationContext(), "Aucune donnée stockée en local à traiter !",
					Toast.LENGTH_LONG).show();
			return;
		}
		
		int currentLocalisationNum = 1;
		for (final Localisation localisation : localisationsList) {
			if (!Common.isNetworkAvailable(activity)) {
				Toast.makeText(activity.getApplicationContext(), "La connexion internet n'est pas disponible !",
						Toast.LENGTH_SHORT).show();
				prgDialog.hide();
				return;
			}
			prgDialog.setMessage("Localisation " + currentLocalisationNum + "/ "
					+ localisationsCount);
			prgDialog.show();
			
			Preferences preferences = new Preferences(activity);
			String webserviceRootUrl = preferences
					.getStringValue("PARAM_WEBSERVICE_ROOT_URL");
			

			// send the current localisation to the server
			int insertedLocalisationId = sendLocalisationToServer(localisation,webserviceRootUrl,db, activity);
			if(insertedLocalisationId == -1){
				prgDialog.hide();
				Toast.makeText(activity.getApplicationContext(),
						"Synchronisation interrompue ! Reéssayez dans quelque instants...", Toast.LENGTH_SHORT).show();
				return; // stop if problem while send any localisation to the server
			}				
			
			List<Rapport> rapportsList = db.getAllRapports();
			// send all the rapports related to the current localisation  to server
			for (final Rapport rapport : rapportsList) {
				if(rapport.getLocalisationId().equals(String.valueOf(localisation.getId()))){
					// update localisationId of rapport by the last inserted one
					rapport.setLocalisationId(String.valueOf(insertedLocalisationId));
					// try send it to the server
					if(!sendRapportToServer(rapport,webserviceRootUrl, db, activity)){
						prgDialog.hide();
						Toast.makeText(activity.getApplicationContext(),
								"Synchronisation interrompue ! Reéssayez dans quelque instants...", Toast.LENGTH_SHORT).show();
						return; // avoid going to delete this localisation if any the reports are still not saved on the server
					}
					db.deleteRapport(rapport);
				}				
					
			}
			db.deleteLocalisation(localisation);
			
			/*Toast.makeText(activity.getApplicationContext(),
					"Localisation " + currentLocalisationNum + "/" + localisationsCount + " envoyée au serveur ...", Toast.LENGTH_SHORT).show();*/
			
			currentLocalisationNum++;
		}	
		prgDialog.hide();
		Toast.makeText(activity.getApplicationContext(),
				"Synchronisation terminée avec succès !", Toast.LENGTH_SHORT).show();
	}

	public static int sendLocalisationToServer(Localisation localisation, String webserviceRootUrl, SqliteDatabaseHelper db ,Activity activity) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("animateurId",
				localisation.getAnimateurId()));
		nameValuePairs.add(new BasicNameValuePair("superviseurId",
				localisation.getSuperviseurId()));
		nameValuePairs.add(new BasicNameValuePair("pdvId", localisation
				.getPdvId()));
		nameValuePairs.add(new BasicNameValuePair("imageFileName",
				localisation.getCheminImage()));
		nameValuePairs.add(new BasicNameValuePair("latitude", localisation
				.getLatitude()));
		nameValuePairs.add(new BasicNameValuePair("longitude", localisation
				.getLongitude()));
		nameValuePairs.add(new BasicNameValuePair("licenceRemplacee",
				localisation.getLicenceRemplacee()));
		nameValuePairs.add(new BasicNameValuePair("motif", localisation
				.getMotif()));

		// ******************* ENCODING IMAGE ********************** //
		
		Bitmap bitmap = Utils.getBitmapUsingRealPath(localisation.getCheminImage());
		BitmapFactory.Options options = null;
		options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		bitmap = BitmapFactory.decodeFile(localisation.getCheminImage(), options);
		//Log.e("Bitmap", "Bitmap" + localisation.getCheminImage());
		//Log.e("Bitmap", "Bitmap" + bitmap.getHeight());
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		// Must compress the Image to reduce image size to make upload
		// easy
		bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
		byte[] byte_arr = stream.toByteArray();
		// Encode Image to String
		String encodedString = Base64.encodeToString(byte_arr, 0);

		bitmap.recycle();
		bitmap = null;
		
		// ******************* ENCODING IMAGE ********************** //
		nameValuePairs.add(new BasicNameValuePair("image", encodedString));
		
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(webserviceRootUrl
					+ "/save_localisation.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
			Log.e("pass 1", "connection success");
			
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			inputStream.close();
			result = sb.toString();
			
			/*Toast.makeText(
					activity.getApplicationContext(),
					"Localisation envoyée au serveur !",
					Toast.LENGTH_LONG).show();*/
			return Integer.valueOf(result.trim());
		} catch (Exception e) {
			Log.e("Fail 1", e.toString());
			/*Toast.makeText(
					activity.getApplicationContext(),
					"Impossible de communiquer avec le serveur distant ! Réessayer plus tard ..." + e.getMessage(),
					Toast.LENGTH_LONG).show();*/
		}
		return -1;
		
	}
	
	public static boolean sendRapportToServer(Rapport rapport, String webserviceRootUrl, SqliteDatabaseHelper db, Activity activity) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("achete",
				rapport.getAchete()));
		nameValuePairs.add(new BasicNameValuePair("trancheAgeId",
				rapport.getTrancheAgeId()));
		nameValuePairs.add(new BasicNameValuePair("sexe", rapport
				.getSexe()));
		nameValuePairs.add(new BasicNameValuePair("raisonAchatId",
				rapport.getRaisonAchatId()));
		nameValuePairs.add(new BasicNameValuePair("raisonRefusId", rapport
				.getRaisonRefusId()));
		nameValuePairs.add(new BasicNameValuePair("fidelite", rapport
				.getFidelite()));
		nameValuePairs.add(new BasicNameValuePair("marqueHabituelleId",
				rapport.getMarqueHabituelleId()));
		nameValuePairs.add(new BasicNameValuePair("marqueHabituelleQte",
				rapport.getMarqueHabituelleQte()));
		nameValuePairs.add(new BasicNameValuePair("marqueAcheteeId",
				rapport.getMarqueAcheteeId()));
		nameValuePairs.add(new BasicNameValuePair("marqueAcheteeQte",
				rapport.getMarqueAcheteeQte()));
		nameValuePairs.add(new BasicNameValuePair("cadeauxIds",
				rapport.getCadeauId()));
		nameValuePairs.add(new BasicNameValuePair("tombola",
				rapport.getTombola()));
		nameValuePairs.add(new BasicNameValuePair("commentaire", rapport
				.getCommentaire()));
		nameValuePairs.add(new BasicNameValuePair("localisationId", rapport
				.getLocalisationId()));

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(webserviceRootUrl
					+ "/save_rapport.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
			Log.e("pass 1", "connection success");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			inputStream.close();
			result = sb.toString();
			Log.e("Debug", result);
			return true;
		} catch (Exception e) {
			Log.e("Fail 1", e.toString());
			/*Toast.makeText(
					activity.getApplicationContext(),
					"Impossible de communiquer avec le serveur distant ! Réessayer plus tard ...",
					Toast.LENGTH_LONG).show();*/
		}
		return false;		
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
				break;
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
