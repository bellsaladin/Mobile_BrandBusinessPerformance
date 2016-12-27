package com.bse.daisybuzz.main;

import java.util.ArrayList;
import java.util.List;
import com.bse.daisybuzz.helper.Common;
import com.bse.daisybuzz.helper.SqliteDatabaseHelper;
import com.bse.daisybuzz.helper.Preferences;
import com.bse.daisybuzz.helper.Statics;
import com.bse.daisybuzz.helper.Utils;
import com.bse.daizybuzz.model.Localisation;
import com.bse.daizybuzz.model.Pdv;
import com.bse.daizybuzz.model.Superviseur;
import com.loopj.android.http.RequestParams;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Fragment1 extends Fragment implements LocationListener {
	SqliteDatabaseHelper db;

	Localisation localisation; // model created on save method, used to be saved
								// in local store
	private static final int CAMERA_REQUEST = 1888;
	LocationManager locationManager;
	String provider;

	private TextView txt_GPSLocationGathering;
	private ImageView imageView;
	private Button btn_takePhoto, btn_save;
	private EditText txt_licenceProgramme, txt_licenceRemplacee, txt_motif;
	private Spinner spinner_pdv, spinner_superviseur, spinner_ville, spinner_secteur;

	ProgressDialog prgDialog;
	String encodedString;
	String imgPath = null, imageFileName = null;
	Bitmap bitmap;
	private static int RESULT_LOAD_IMG = 1;
	View view;
	
	
	List<Superviseur> superviseursList;
	List<String> villesList, secteursList;
	List<Pdv> pdvsList;

	private String imageRealPath;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		prgDialog = new ProgressDialog(this.getActivity());
		// Set Cancelable as False
		prgDialog.setCancelable(false);
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.fragment1, container, false);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}

		/* ****************************************************************************************************************
		 * Finding views and implemeting listeners
		 * ******************************
		 * ****************************************
		 * *****************************************
		 */
		txt_GPSLocationGathering = (TextView)view.findViewById(R.id.txt_GPSLocationGathering);
		imageView = (ImageView) view.findViewById(R.id.iv_photo);
		spinner_pdv = (Spinner) view.findViewById(R.id.spinner_pdv);
		spinner_ville = (Spinner) view.findViewById(R.id.spinner_ville);
		spinner_secteur = (Spinner) view.findViewById(R.id.spinner_secteur);
		spinner_superviseur = (Spinner) view
				.findViewById(R.id.spinner_superviseur);
		btn_save = (Button) view.findViewById(R.id.btn_save);
		// btn_takePhoto = (Button) view.findViewById(R.id.btn_takePhoto);
		txt_licenceProgramme = (EditText) view
				.findViewById(R.id.txt_licenceProgrammee);
		txt_licenceProgramme.setEnabled(false);
		txt_licenceRemplacee = (EditText) view
				.findViewById(R.id.txt_licenceRemplacee);
		txt_motif = (EditText) view.findViewById(R.id.txt_motif);

		// listeners *****************

		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				takephoto(v);
			}
		});

		btn_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(btn_save.getText().equals("En cours...")) return;
				SaveDataAsyncTaskRunner runner = new SaveDataAsyncTaskRunner();
				runner.execute();
			}
		});
		
		spinner_ville.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {

				String ville = villesList.get(position);

				List<String> secteursArray = new ArrayList<String>();
				for (String secteur : db.getAllSecteurs(ville)) {
					secteursArray.add(String.valueOf(secteur));
				}

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(Fragment1.this.getActivity(),
						android.R.layout.simple_spinner_item, secteursArray);

				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				spinner_secteur.setAdapter(adapter);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		spinner_secteur.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
									   View selectedItemView, int position, long id) {


				String ville = spinner_ville.getSelectedItem().toString();
				String secteur = spinner_secteur.getSelectedItem().toString();
				pdvsList = db.getPDVsBySecteur(ville,secteur);

				List<String> pdvsArray = new ArrayList<String>();
				for (Pdv pdv : pdvsList) {
					pdvsArray.add(String.valueOf(pdv.getNom()));
				}

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(Fragment1.this.getActivity(),
						android.R.layout.simple_spinner_item, pdvsArray);

				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner_pdv.setAdapter(adapter);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		spinner_pdv.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
									   View selectedItemView, int position, long id) {

				if (pdvsList.size() > 0) {
					txt_licenceProgramme.setText(String.valueOf(pdvsList.get(
							position).getNom()));
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		/* ****************************************************************************************************************
		 * Load data from sqlite
		 * ************************************************
		 * ***************************************************************
		 */

		db = new SqliteDatabaseHelper(this.getActivity()
				.getApplicationContext());
		superviseursList = db.getAllSuperviseurs();
		
		villesList = db.getAllVilles();
		secteursList = null;
		pdvsList = null;

		/* ****************************************************************************************************************
		 * Form controls populating
		 * *********************************************
		 * ******************************************************************
		 */

		// ##### superviseurs
		List<String> superviseursArray = new ArrayList<String>();

		for (Superviseur superviseur : superviseursList) {
			superviseursArray.add(superviseur.getPrenom() + " "
					+ superviseur.getNom());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this.getActivity(), android.R.layout.simple_spinner_item,
				superviseursArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner_superviseur.setAdapter(adapter);

		// ##### villes
		List<String> villesArray = new ArrayList<String>();
		for (String ville : db.getAllVilles()) {
			villesArray.add(String.valueOf(ville));
		}

		adapter = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_spinner_item, villesArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner_ville.setAdapter(adapter);
		
		// ##### secteurs

		List<String> secteursArray = new ArrayList<String>();
		if(villesArray.size() > 0) {
			for (String secteur : db.getAllSecteurs(villesArray.get(0))) {
				secteursArray.add(String.valueOf(secteur));
			}

			adapter = new ArrayAdapter<String>(this.getActivity(),
					android.R.layout.simple_spinner_item, secteursArray);

			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			spinner_secteur.setAdapter(adapter);
		}
		// ##### points de ventes
		if(villesArray.size() > 0 && secteursArray.size() > 0) {
			String ville = villesArray.get(0);
			String secteur = secteursArray.get(0);
			pdvsList = db.getPDVsBySecteur(ville,secteur);
		}
		List<String> pdvsArray = new ArrayList<String>();
		if(pdvsList != null && pdvsList.size() >0){
			for (Pdv pdv : pdvsList) {
				pdvsArray.add(String.valueOf(pdv.getLicence()));
			}
		}		

		adapter = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_spinner_item, pdvsArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner_pdv.setAdapter(adapter);

		if (pdvsList != null && pdvsList.size() > 0)
			txt_licenceProgramme.setText(String.valueOf(pdvsList.get(0)
					.getLicence()));

		/* ****************************************************************************************************************
		 * LOCALISATION
		 * *********************************************************
		 * ******************************************************
		 */

		// Getting LocationManager object
		locationManager = (LocationManager) this.getActivity()
				.getSystemService(Context.LOCATION_SERVICE);

		// Creating an empty criteria object
		Criteria criteria = new Criteria();

		// Getting the name of the provider that meets the criteria
		provider = locationManager.getBestProvider(criteria, false);

		if (provider != null && !provider.equals("")) {

			// Get the location from the given provider
			// Location location = Common.getLastKnownLocation(provider);

			locationManager.requestLocationUpdates(provider, 20000, 1, this);
						
			Location location = Common.getLocation(this.getActivity());


			/* mMap = ((SupportMapFragment) this.getActivity()
					.getSupportFragmentManager().findFragmentById(R.id.map))
					.getMap();*/
			// mMap.setMyLocationEnabled(true);

			/*if (location != null)
				onLocationChanged(location);
			else
				Toast.makeText(this.getActivity().getBaseContext(),
						"Location can't be retrieved", Toast.LENGTH_SHORT)
						.show();*/

		} else {
			Toast.makeText(this.getActivity().getBaseContext(),
					"No Provider Found", Toast.LENGTH_SHORT).show();
		}

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == CAMERA_REQUEST) {

			if (null != intent) {
				// Get the Image from data

				Uri selectedImage = intent.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				// Get the cursor
				Cursor cursor = this.getActivity().getContentResolver()
						.query(selectedImage, filePathColumn, null, null, null);
				// Move to first row
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				imgPath = cursor.getString(columnIndex);
				cursor.close();

				// Set the Image in ImageView
				Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
				imageView.setImageBitmap(bitmap);
				// Get the Image's file name
				String fileNameSegments[] = imgPath.split("/");
				imageFileName = fileNameSegments[fileNameSegments.length - 1];
				// Put file name in Async Http Post Param which will used in Php
				// web app

				Uri uri = intent.getData();
				imageRealPath = Utils.getRealPathFromURI(this.getActivity()
						.getApplicationContext(), uri);
				/*
				 * Toast.makeText(this.getActivity(),
				 * Utils.getRealPathFromURI(this.getActivity()
				 * .getApplicationContext(), uri), Toast.LENGTH_LONG).show();
				 */
			} else {
				Toast.makeText(this.getActivity(),
						"Vous n'avez pas pris de photo", Toast.LENGTH_LONG)
						.show();
			}

		}
	}

	public void takephoto(View v) {
		Intent cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, CAMERA_REQUEST);
	}


	@Override
	public void onLocationChanged(Location location) {
		// Getting reference to TextView tv_longitude
		

		// Getting reference to TextView tv_latitude
		// TextView tvLatitude = (TextView)findViewById(R.id.tv_latitude);
		if (location != null) {
			txt_GPSLocationGathering.setText("Emplacement gps récupéré");
			// Setting Current Longitude
			// Toast.makeText(this.getActivity().getBaseContext(),
			// location.getLongitude() + "," + location.getLatitude() ,
			// Toast.LENGTH_SHORT).show();

			/*CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
					location.getLatitude(), location.getLongitude()));
			CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
			mMap.moveCamera(center);
			mMap.animateCamera(zoom);*/
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}
	
	class SaveDataAsyncTaskRunner extends AsyncTask<String, String, String> {

		private String resp;

		@Override
		protected String doInBackground(String... params) {
			//publishProgress("Sleeping..."); // Calls onProgressUpdate()
			try {
				// getting location first
				Location location = Common.getLocation(Fragment1.this.getActivity());
				if (location == null) {
					return "LOCATION_NOT_FOUND";
				}
				if (pdvsList.size() == 0){
					return "NO_PDV_SELECTED";
				}
				// ********* saving
				Pdv pdv = pdvsList.get(spinner_pdv.getSelectedItemPosition());

				Preferences preferences = new Preferences(Fragment1.this.getActivity());
				// setting parameters for HttpRequest
				String animateurId = preferences.getStringValue("ANIMATEUR_ID");
				String superviseurId = "1";
				String pdvId = String.valueOf(pdv.getId());
				String longitude = String.valueOf(location.getLongitude());
				String latitude = String.valueOf(location.getLatitude());
				String licenceRemplacee = txt_licenceRemplacee.getText().toString();
				String motif = txt_motif.getText().toString();
				RequestParams reqParams = new RequestParams();
				reqParams.put("animateurId", animateurId);
				reqParams.put("superviseurId", superviseurId);
				reqParams.put("pdvId", pdvId);
				reqParams.put("longitude", longitude);
				reqParams.put("latitude", latitude);
				reqParams.put("licenceRemplacee", licenceRemplacee);
				reqParams.put("motif", motif);
				// params.put("imageFileName", imageFileName);
				reqParams.put("imageFileName", imgPath);
				Log.e("Debug", "" + imgPath);
				Log.e("Debug", "" + imageRealPath);

				localisation = new Localisation(animateurId, imgPath, superviseurId,
						pdvId, longitude, latitude, licenceRemplacee, motif, Utils.now());
				
				long localisationId = db.createLocalisation(localisation);
				Statics.localisationDone = true;
				Statics.lastLocalisationId = (int) localisationId;
				return "DATA_SAVED";
			} catch (Exception e) {
				e.printStackTrace();
				resp = e.getMessage();
			}
			return resp;
		}

		
		@Override
		protected void onPostExecute(String result) {
			Fragment1.this.btn_save.setText("Enregistrer");
			
			if(result.equals("LOCATION_NOT_FOUND")){
				Toast.makeText(
						Fragment1.this.getActivity().getApplicationContext(),
						"Erreur : Impossible de récupérer la dernière localisation (GPS)! Le cache de la dernière position a peut être été vider.",
						Toast.LENGTH_LONG).show();
				return;
			}
			if(result.equals("NO_PDV_SELECTED")){
				Toast.makeText(
						Fragment1.this.getActivity().getApplicationContext(),
						"Vous devez indiquer un point de vente.",
						Toast.LENGTH_LONG).show();
				return;
			}
			if(result.equals("DATA_SAVED")){
				MainActivity.addRapportTab();
				Toast.makeText(Fragment1.this.getActivity().getApplicationContext(),
						"Localisation enregistrée sur la mémoire locale.",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			// Things to be done before execution of long running operation. For
			// example showing ProgessDialog
			Fragment1.this.btn_save.setText("En cours...");
		}

		@Override
		protected void onProgressUpdate(String... text) {
			//finalResult.setText(text[0]);
			// Things to be done while execution of long running operation is in
			// progress. For example updating ProgessDialog
		}
	}

}