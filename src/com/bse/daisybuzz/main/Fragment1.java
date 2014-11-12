package com.bse.daisybuzz.main;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.bse.daisybuzz.helper.Common;
import com.bse.daisybuzz.helper.SqliteDatabaseHelper;
import com.bse.daisybuzz.helper.Preferences;
import com.bse.daisybuzz.helper.Statics;
import com.bse.daizybuzz.model.Localisation;
import com.bse.daizybuzz.model.PDV;
import com.bse.daizybuzz.model.Superviseur;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Fragment1 extends Fragment implements LocationListener {
	SqliteDatabaseHelper db;
	
	Localisation localisation; // model created on save method, used to be saved in local store
	private static final int CAMERA_REQUEST = 1888;
	LocationManager locationManager;
	String provider;

	GoogleMap mMap;

	private ImageView imageView;
	private Button btn_takePhoto, btn_save;
	private EditText txt_licenceProgramme, txt_licenceRemplacee, txt_motif;
	private Spinner spinner_pdv, spinner_superviseur;

	ProgressDialog prgDialog;
	String encodedString;
	RequestParams params = new RequestParams();
	String imgPath = null, imageFileName = null;
	Bitmap bitmap;
	private static int RESULT_LOAD_IMG = 1;
	View view;

	List<Superviseur> superviseursList;
	List<PDV> pdvsList;

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

		imageView = (ImageView) view.findViewById(R.id.iv_photo);
		spinner_pdv = (Spinner) view.findViewById(R.id.spinner_pdv);
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
				save();
			}
		});

		spinner_pdv.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {

				if (pdvsList.size() > 0) {
					txt_licenceProgramme.setText(String.valueOf(pdvsList.get(
							position).getLicence()));
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

		db = new SqliteDatabaseHelper(this.getActivity().getApplicationContext());
		superviseursList = db.getAllSuperviseurs();
		pdvsList = db.getAllPDV();

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

		// ##### points de ventes
		List<String> pdvsArray = new ArrayList<String>();
		for (PDV pdv : pdvsList) {
			pdvsArray.add(String.valueOf(pdv.getNom()));
		}

		adapter = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_spinner_item, pdvsArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner_pdv.setAdapter(adapter);

		if (pdvsList.size() > 0)
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
			Location location = Common.getLocation(this.getActivity());

			locationManager.requestLocationUpdates(provider, 20000, 1, this);

			mMap = ((SupportMapFragment) this.getActivity()
					.getSupportFragmentManager().findFragmentById(R.id.map))
					.getMap();
			mMap.setMyLocationEnabled(true);

			if (location != null)
				onLocationChanged(location);
			else
				Toast.makeText(this.getActivity().getBaseContext(),
						"Location can't be retrieved", Toast.LENGTH_SHORT)
						.show();

		} else {
			Toast.makeText(this.getActivity().getBaseContext(),
					"No Provider Found", Toast.LENGTH_SHORT).show();
		}

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_REQUEST) {

			if (null != data) {
				// Get the Image from data

				Uri selectedImage = data.getData();
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
				Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				imageView.setImageBitmap(bitmap);
				// Get the Image's file name
				String fileNameSegments[] = imgPath.split("/");
				imageFileName = fileNameSegments[fileNameSegments.length - 1];
				// Put file name in Async Http Post Param which will used in Php
				// web app
			} else {
				Toast.makeText(this.getActivity(), "Vous n'avez pas pris de photo",
						Toast.LENGTH_LONG).show();
			}

		}
	}

	public void takephoto(View v) {
		Intent cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, CAMERA_REQUEST);
	}

	public void save() {
		
		// getting location first
		Location location = Common.getLocation(this.getActivity());
		if(location == null){
			Toast.makeText(
					Fragment1.this.getActivity().getApplicationContext(),
					"Erreur : Impossible de récupérer la dernière localisation ! Le cache de la dernière position a peut être été vider.",
					Toast.LENGTH_LONG).show();
			return;
		}
		// valdiation
		if (pdvsList.size() == 0 || superviseursList.size() == 0)
			return;

		// ********* saving
		Superviseur superviseur = superviseursList.get(spinner_superviseur
				.getSelectedItemPosition());
		PDV pdv = pdvsList.get(spinner_pdv.getSelectedItemPosition());		

		// setting parameters for HttpRequest
		String animateurId = String.valueOf(Statics.animateurId);
		String superviseurId = String.valueOf(superviseur.getId());
		String pdvId = String.valueOf(pdv.getId());
		String longitude = String.valueOf(location.getLongitude());
		String latitude = String.valueOf(location.getLatitude());
		String licenceRemplacee = txt_licenceRemplacee.getText().toString();
		String motif = txt_motif.getText().toString();
		params.put("animateurId", animateurId);
		params.put("superviseurId", superviseurId);
		params.put("pdvId", pdvId);
		params.put("longitude", longitude);
		params.put("latitude", latitude);
		params.put("licenceRemplacee", licenceRemplacee);
		params.put("motif", motif);
		params.put("imageFileName", imageFileName);
		
		localisation = new Localisation(animateurId, imageFileName,
				superviseurId, pdvId, longitude, latitude,
				licenceRemplacee, motif);
		
		// save of Localisation data
		if (Common.isNetworkAvailable(this.getActivity())) {
			sendDataToServer();
		} else {			
			askUserIfWantToSaveToLocalStorage();
		}
	}

	private void storeDataOnLocalStorage() {
		long localisationId = db.createLocalisation(localisation);		
		
		Statics.localisationDone = true;
		Statics.lastLocalisationId = (int) localisationId;
		
		Toast.makeText(
				Fragment1.this.getActivity().getApplicationContext(),
				"Localisation enregistrée sur la mémoire locale.",
				Toast.LENGTH_SHORT).show();
	}

	// When Upload button is clicked
	public void sendDataToServer() {
		// When Image is selected from Gallery
		if (imgPath != null && !imgPath.isEmpty()) {
			//prgDialog.setMessage("Converting Image to Binary Data");
			//prgDialog.show();
			// Convert image to String using Base64
			ansychronous_encodeImagetoStringThenSendDataToServer();
			// When Image is not selected from Gallery
		} else {
			Toast.makeText(
					Fragment1.this.getActivity().getApplicationContext(),
					"Vous devez séléctionner une image avant de procéder à l'envoi !",
					Toast.LENGTH_LONG).show();
		}
	}

	// AsyncTask - To convert Image to String
	public void ansychronous_encodeImagetoStringThenSendDataToServer() {
		new AsyncTask<Void, Void, String>() {

			protected void onPreExecute() {

			};

			@Override
			protected String doInBackground(Void... params) {
				BitmapFactory.Options options = null;
				options = new BitmapFactory.Options();
				options.inSampleSize = 3;
				bitmap = BitmapFactory.decodeFile(imgPath, options);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				// Must compress the Image to reduce image size to make upload
				// easy
				bitmap.compress(Bitmap.CompressFormat.JPEG, 5, stream);
				byte[] byte_arr = stream.toByteArray();
				// Encode Image to String
				encodedString = Base64.encodeToString(byte_arr, 0);

				bitmap.recycle();
				bitmap = null;
				return "";
			}

			@Override
			protected void onPostExecute(String msg) {
				prgDialog.setMessage("Calling Upload");
				// Put converted Image string into Async Http Post param
				params.put("image", encodedString);

				// Trigger upload of data (data and image)
				uplaodDataToServer();
			}
		}.execute(null, null, null);
	}

	public void uplaodDataToServer() {
		if (Common.isNetworkAvailable(this.getActivity())) {
			// passer http -> webservice
			makeHTTPCall();
		} else {
			askUserIfWantToSaveToLocalStorage();
		}
	}

	// Make Http call to upload image/ data to Php server
	public void makeHTTPCall() {

		Preferences preferences = new Preferences(this.getActivity());
		String webserviceRootUrl = preferences
				.getStringValue("PARAM_WEBSERVICE_ROOT_URL");

		prgDialog.setMessage("Envoi des données au serveur...");
		prgDialog.show();
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(3000000); // 30 seconds
		// Don't forget to change the IP address to your LAN address. Port no as
		// well.
		client.post(webserviceRootUrl + "/save_localisation.php", params,
				new AsyncHttpResponseHandler() {
					// When the response returned by REST has Http
					// response code '200'
					@Override
					public void onSuccess(String response) {
						// change static localisation flag to done
						Statics.localisationDone = true;
						Statics.lastLocalisationId = Integer.valueOf(response);
						
						// Hide Progress Dialog
						prgDialog.hide();
						Toast.makeText(
								Fragment1.this.getActivity()
										.getApplicationContext(),
								"Les informations de localisation on été enregistrées !",
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
									Fragment1.this.getActivity()
											.getApplicationContext(),
									"Le webservice demandé n'est pas disponible !",
									Toast.LENGTH_LONG).show();
						}
						// When Http response code is '500'
						else if (statusCode == 500) {
							Toast.makeText(
									Fragment1.this.getActivity()
											.getApplicationContext(),
									"IL y a eu un problème au niveau du script !",
									Toast.LENGTH_LONG).show();
						}
						// When Http response code other than 404, 500
						else {
							Toast.makeText(
									Fragment1.this.getActivity()
											.getApplicationContext(),
									"Erreur : Un problème de connexion ou peut être que le serveur distant n'est pas fonctionnel"
									/*
									 * "Error Occured \n Most Common Error: \n1. Device not connected to Internet\n2. Web App is not deployed in App server\n3. App server is not running\n HTTP Status code : "
									 * + statusCode
									 */, Toast.LENGTH_LONG).show();
							askUserIfWantToSaveToLocalStorage();
						}

					}
				});
	}

	public void askUserIfWantToSaveToLocalStorage() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					
					prgDialog.setMessage("Enregistrement des informations de localisation en local...");
					prgDialog.show();
					
					storeDataOnLocalStorage();
					Toast.makeText(
							Fragment1.this.getActivity().getApplicationContext(),
							"( "  +db.getRecordsCount("localisation") + " Localisations stockées en local)",
							Toast.LENGTH_SHORT).show();
					prgDialog.hide();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					// No button clicked
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(
				this.getActivity());
		builder.setMessage(
				"Impossible de communiquer avec le serveur distant, la connexion est peut être très lente. Voulez vous enregistrer ces informations en local ?")
				.setPositiveButton("Oui", dialogClickListener)
				.setNegativeButton("Non", dialogClickListener).show();
	}

	@Override
	public void onLocationChanged(Location location) {
		// Getting reference to TextView tv_longitude
		// TextView tvLongitude = (TextView)findViewById(R.id.tv_longitude);

		// Getting reference to TextView tv_latitude
		// TextView tvLatitude = (TextView)findViewById(R.id.tv_latitude);
		if (location != null) {
			// Setting Current Longitude
			// Toast.makeText(this.getActivity().getBaseContext(),
			// location.getLongitude() + "," + location.getLatitude() ,
			// Toast.LENGTH_SHORT).show();

			CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
					location.getLatitude(), location.getLongitude()));
			CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
			mMap.moveCamera(center);
			mMap.animateCamera(zoom);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

}