package com.bse.daisybuzz.main;

import java.util.ArrayList;
import java.util.List;

import com.bse.daisybuzz.helper.Common;
import com.bse.daisybuzz.helper.SqliteDatabaseHelper;
import com.bse.daisybuzz.helper.Preferences;
import com.bse.daisybuzz.helper.Statics;
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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

	Rapport rapport; // model created on save method, used to be saved in local
						// store

	SqliteDatabaseHelper db;

	RequestParams params = new RequestParams();

	Spinner spinner_achete, spinner_raisonAchat, spinner_raisonRefus,
			spinner_ageClient, spinner_sexe, spinner_fidelite,
			spinner_marqueHabituelle, spinner_marqueAchetee,
			spinner_marqueHabituelle2;
	EditText txt_marqueHabituelleQte, txt_marqueAcheteeQte,
			txt_marqueHabituelleQte2, txt_commentaire, txt_cadeauxIds;
	LinearLayout linearLayout1;
	LinearLayout linearLayout2;
	CheckBox cb_tombola;
	private Button btn_choixCadeaux, btn_save;
	ProgressDialog prgDialog;

	List<Marque> marquesList;
	List<Cadeau> cadeauxList;
	List<RaisonAchat> raisonsAchatList;
	List<RaisonRefus> raisonsRefusList;
	List<TrancheAge> tranchesAgeList;
	View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		prgDialog = new ProgressDialog(this.getActivity());
		// Set Cancelable as False
		prgDialog.setCancelable(false);
		view = inflater.inflate(R.layout.fragment2, null);

		return view;
	}

	public void populateFields() {
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
		spinner_raisonRefus = (Spinner) view
				.findViewById(R.id.spinner_raisonRefus);
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
		txt_cadeauxIds = (EditText) view.findViewById(R.id.txt_cadeaux_ids);
		cb_tombola = (CheckBox) view.findViewById(R.id.cb_tombola);

		btn_save = (Button) view.findViewById(R.id.btn_rapport_save);
		btn_choixCadeaux = (Button) view.findViewById(R.id.btn_choixCadeaux);

		// listeners *****************
		
		
		btn_choixCadeaux.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openSelectCadeauxDialog();
			}
		});
		
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
		 * ************************************************
		 * ***************************************************************
		 */

		db = new SqliteDatabaseHelper(this.getActivity()
				.getApplicationContext());
		marquesList = db.getAllMarques();
		cadeauxList = db.getAllCadeaux();
		
		raisonsAchatList = db.getAllRaisonsAchat();
		raisonsRefusList = db.getAllRaisonsRefus();
		tranchesAgeList = db.getAllTranchesAge();

		/* ****************************************************************************************************************
		 * Form controls populating
		 * *********************************************
		 * ******************************************************************
		 */

		// ##### marques
		List<String> marquesArray = new ArrayList<String>();

		for (Marque marque : marquesList) {
			marquesArray.add(marque.getLibelle());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this.getActivity(), android.R.layout.simple_spinner_item,
				marquesArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner_marqueAchetee.setAdapter(adapter);
		spinner_marqueHabituelle.setAdapter(adapter);
		spinner_marqueHabituelle2.setAdapter(adapter);

		// ##### raisonsAchat
		List<String> raisonsAchatArray = new ArrayList<String>();

		for (RaisonAchat raisonAchat : raisonsAchatList) {
			raisonsAchatArray.add(raisonAchat.getLibelle());
		}

		adapter = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_spinner_item, raisonsAchatArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner_raisonAchat.setAdapter(adapter);

		// ##### raisonRefus
		List<String> raisonsRefusArray = new ArrayList<String>();

		for (RaisonRefus raisonRefus : raisonsRefusList) {
			raisonsRefusArray.add(raisonRefus.getLibelle());
		}

		adapter = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_spinner_item, raisonsRefusArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner_raisonRefus.setAdapter(adapter);

		// ##### tranches d'age
		List<String> tranchesAgeArray = new ArrayList<String>();

		for (TrancheAge trancheAge : tranchesAgeList) {
			tranchesAgeArray.add(trancheAge.getLibelle());
		}

		adapter = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_spinner_item, tranchesAgeArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner_ageClient.setAdapter(adapter);

		/* ****************************************************************************************************************
		 * Checking preferences for global parameters like tombola
		 * **************
		 * ********************************************************
		 * *****************************************
		 */

		Preferences preferences = new Preferences(this.getActivity());
		if (!preferences.getStringValue("PARAM_TOMBOLA_ENABLED").isEmpty()) {
			int tombolaEnabled = Integer.valueOf(preferences
					.getStringValue("PARAM_TOMBOLA_ENABLED"));
			if (tombolaEnabled != 1) {
				cb_tombola.setVisibility(CheckBox.GONE);
			}
		}
	}

	@Override
	public void onResume() {
		Log.e("DEBUG", "onResume of LoginFragment");
		super.onResume();
		populateFields();
	}

	public void save(View v) {

		if (spinner_achete.getSelectedItemPosition() == 0) {
			// getting values
			String achete = "1";
			String sexe = spinner_sexe.getSelectedItem().toString();
			String trancheAgeId = String.valueOf(tranchesAgeList.get(
					spinner_ageClient.getSelectedItemPosition()).getId());
			String raisonAchatId = String.valueOf(raisonsAchatList.get(
					spinner_raisonAchat.getSelectedItemPosition()).getId());
			String fidelite = spinner_fidelite.getSelectedItem().toString();
			String marqueHabituelleId = String
					.valueOf(marquesList.get(
							spinner_marqueHabituelle.getSelectedItemPosition())
							.getId());
			String marqueHabituelleQte = txt_marqueHabituelleQte.getText()
					.toString();
			String marqueAcheteeId = String.valueOf(marquesList.get(
					spinner_marqueAchetee.getSelectedItemPosition()).getId());
			String marqueAcheteeQte = txt_marqueAcheteeQte.getText().toString();
			String cadeauxIds = txt_cadeauxIds.getText().toString();
			String tombola = (cb_tombola.isChecked()) ? "1" : "0";
			String localisationId = String.valueOf(Statics.lastLocalisationId);

			// setting parameters
			params.put("achete", achete);
			params.put("trancheAgeId", trancheAgeId);
			params.put("sexe", sexe);
			params.put("raisonAchatId", raisonAchatId);
			params.put("fidelite", fidelite);
			params.put("marqueHabituelleId", marqueHabituelleId);
			params.put("marqueHabituelleQte", marqueHabituelleQte);
			params.put("marqueAcheteeId", marqueAcheteeId);
			params.put("marqueAcheteeQte", marqueAcheteeQte);
			params.put("cadeauxIds", cadeauxIds);
			params.put("tombola", tombola);
			params.put("localisationId", localisationId);

			rapport = new Rapport(achete, trancheAgeId, sexe, fidelite,
					raisonAchatId, marqueHabituelleId, marqueHabituelleQte,
					marqueAcheteeId, marqueAcheteeQte, cadeauxIds, tombola,
					localisationId);
		}

		if (spinner_achete.getSelectedItemPosition() == 1) {
			String achete = "0";
			String trancheAgeId = String.valueOf(tranchesAgeList.get(
					spinner_ageClient.getSelectedItemPosition()).getId());
			String raisonRefusId = String.valueOf(raisonsRefusList.get(
					spinner_raisonRefus.getSelectedItemPosition()).getId());
			String sexe = spinner_sexe.getSelectedItem().toString();
			String marqueHabituelleId = String.valueOf(marquesList.get(
					spinner_marqueHabituelle2.getSelectedItemPosition())
					.getId());
			String marqueHabituelleQte = txt_marqueHabituelleQte2.getText()
					.toString();
			String commentaire = txt_commentaire.getText().toString();
			String localisationId = String.valueOf(Statics.lastLocalisationId);

			// setting parameters
			params.put("achete", achete);
			params.put("trancheAgeId", trancheAgeId);
			params.put("sexe", sexe);
			params.put("raisonRefusId", raisonRefusId);
			params.put("marqueHabituelleId", marqueHabituelleId);
			params.put("marqueHabituelleQte", marqueHabituelleQte);
			params.put("commentaire", commentaire);
			params.put("localisationId", localisationId);

			rapport = new Rapport(achete, trancheAgeId, sexe, raisonRefusId,
					marqueHabituelleId, marqueHabituelleQte, commentaire,
					localisationId);
		}

		// ********* saving

		if (Common.isNetworkAvailable(this.getActivity())) {
			// passer http -> webservice
			makeHTTPCall();
		} else {
			storeDataOnLocalStorage();
		}
		// start upload of localisation data
	}

	private void storeDataOnLocalStorage() {
		db.createRapport(rapport);

		// change static localisation flag to done
		Statics.localisationDone = true;

		Toast.makeText(
				Fragment2.this.getActivity().getApplicationContext(),
				"Rapport enregistré sur la mémoire locale.", Toast.LENGTH_SHORT)
				.show();
	}

	/*public void askUserIfWantToSaveToLocalStorage() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					prgDialog
							.setMessage("Enregistrement des informations du rapport en local...");
					prgDialog.show();

					storeDataOnLocalStorage();
					Toast.makeText(
							Fragment2.this.getActivity()
									.getApplicationContext(),
							"Rapport enregistré !", Toast.LENGTH_SHORT).show();
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
	}*/

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
									"Erreur : Un problème de connexion ou peut être que le serveur distant n'est pas fonctionnel"
											+ statusCode, Toast.LENGTH_LONG)
									.show();
						}

						// askUserIfWantToSaveToLocalStorage();

					}
				});
	}

	private void openSelectCadeauxDialog() {
		final String[] items = new String[cadeauxList.size()];
		for(int i=0; i < cadeauxList.size(); i++){
			items[i] = cadeauxList.get(i).toString();
		}
		// arraylist to keep the selected items
		final ArrayList<Integer> seletedItems = new ArrayList<Integer>();

		AlertDialog.Builder builder = new AlertDialog.Builder(
				this.getActivity());
		builder.setTitle("Choix des cadeaux");
		builder.setMultiChoiceItems(items, null,
				new DialogInterface.OnMultiChoiceClickListener() {
					// indexSelected contains the index of item (of which
					// checkbox checked)
					@Override
					public void onClick(DialogInterface dialog,
							int indexSelected, boolean isChecked) {
						if (isChecked) {
							// If the user checked the item, add it to the
							// selected items
							// write your code when user checked the checkbox
							seletedItems.add(indexSelected);
						} else if (seletedItems.contains(indexSelected)) {
							// Else, if the item is already in the array, remove
							// it
							// write your code when user Uchecked the checkbox
							seletedItems.remove(Integer.valueOf(indexSelected));
						}
					}
				})
				// Set the action buttons
				.setPositiveButton("Valider",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								String ids = "", msg ="Cadeaux : ";
								for (int i = 0; i < seletedItems.size(); i++) {
									Integer itemIndex = seletedItems.get(i);
									Cadeau cadeau = cadeauxList.get(itemIndex);
									msg += (i < seletedItems.size() -1)?cadeau.getLibelle() + ", ": cadeau.getLibelle();
									ids += (i < seletedItems.size() -1)?cadeau.getId() + ", ": cadeau.getId();
								}
								txt_cadeauxIds.setText(ids);
								btn_choixCadeaux.setText(msg);
							}
						})
				.setNegativeButton("Annuler",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// Your code when user clicked on Cancel

							}
						});

		AlertDialog dialog = builder.create();// AlertDialog dialog; create like
												// this outside onClick
		dialog.show();
	}
}
