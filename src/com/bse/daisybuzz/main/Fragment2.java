package com.bse.daisybuzz.main;

import java.util.ArrayList;
import java.util.List;

import com.bse.daisybuzz.helper.Common;
import com.bse.daisybuzz.helper.Constants;
import com.bse.daisybuzz.helper.SqliteDatabaseHelper;
import com.bse.daisybuzz.helper.Preferences;
import com.bse.daisybuzz.helper.Statics;
import com.bse.daisybuzz.helper.Utils;
import com.bse.daisybuzz.test.QuestionnaireDisponibilite;
import com.bse.daisybuzz.test.QuestionnaireShelfShare;
import com.bse.daizybuzz.model.Cadeau;
import com.bse.daizybuzz.model.Categorie;
import com.bse.daizybuzz.model.Localisation;
import com.bse.daizybuzz.model.Marque;
import com.bse.daizybuzz.model.Pdv;
import com.bse.daizybuzz.model.Produit;
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
import android.graphics.Color;
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
	private Button btn_showQuestionnaire1, btn_showQuestionnaire2;
	private Button btn_increment, btn_decrement;
	public static EditText _currentlySelectedEditText;
	List<Marque> marquesList;
	List<Cadeau> cadeauxList;
	List<RaisonAchat> raisonsAchatList;
	List<Produit> produitsList;
	List<Categorie> categoriesList;
	List<RaisonRefus> raisonsRefusList;
	List<TrancheAge> tranchesAgeList;
	View view;
	LinearLayout layout_questionnaire_shelfShare, layout_questionnaire_disponibilite;	
	
	QuestionnaireShelfShare questionnaireShelfShareCreator;
	QuestionnaireDisponibilite questionnaireDisponibiliteCreator;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment2, null);
		
		layout_questionnaire_shelfShare = (LinearLayout) view.findViewById(R.id.layout_questionnaire_shelfShare);
		layout_questionnaire_disponibilite = (LinearLayout) view.findViewById(R.id.layout_questionnaire_disponibility);
		
		btn_showQuestionnaire1 =  (Button) view.findViewById(R.id.btn_showQuestionnaire1);
		btn_showQuestionnaire2 =  (Button) view.findViewById(R.id.btn_showQuestionnaire2);
		
		btn_increment =  (Button) view.findViewById(R.id.btn_increment);
		btn_decrement =  (Button) view.findViewById(R.id.btn_decrement);
		
		btn_showQuestionnaire1.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	layout_questionnaire_shelfShare.setVisibility(LinearLayout.VISIBLE);
		    	layout_questionnaire_disponibilite.setVisibility(LinearLayout.GONE);
		    	btn_showQuestionnaire1.setTextColor(Color.parseColor("#000000"));
		    	btn_showQuestionnaire2.setTextColor(Color.parseColor("#999999"));
		    	
		    	// start / stop time count elapsed on each of the forms (Questionnaires)
		    	questionnaireShelfShareCreator.startTempsRemlissageCount();
		    	questionnaireDisponibiliteCreator.stopTempsRemplissageCount();
		    }
		});
		
		btn_showQuestionnaire2.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	layout_questionnaire_disponibilite.setVisibility(LinearLayout.VISIBLE);
		    	layout_questionnaire_shelfShare.setVisibility(LinearLayout.GONE);
		    	btn_showQuestionnaire2.setTextColor(Color.parseColor("#000000"));
		    	btn_showQuestionnaire1.setTextColor(Color.parseColor("#999999"));
		    	
		    	// start / stop time count elapsed on each of the forms (Questionnaires) 
		    	questionnaireDisponibiliteCreator.startTempsRemlissageCount();
		    	questionnaireShelfShareCreator.stopTempsRemplissageCount();
		    }
		});
		
		btn_increment.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	if(_currentlySelectedEditText == null) return;
		    	int value = Integer.parseInt(_currentlySelectedEditText.getText().toString());
		    	_currentlySelectedEditText.setText(String.valueOf(value+1));
		    }
		});
		
		btn_decrement.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	if(_currentlySelectedEditText == null) return;
		    	int value = Integer.parseInt(_currentlySelectedEditText.getText().toString());
		    	_currentlySelectedEditText.setText(String.valueOf(value-1));
		    }
		});
		
		// create questionnaires forms *************************
		
		questionnaireShelfShareCreator = new QuestionnaireShelfShare();
		questionnaireShelfShareCreator.init(this.getActivity(), layout_questionnaire_shelfShare);
		questionnaireShelfShareCreator.startTempsRemlissageCount();
		questionnaireDisponibiliteCreator = new QuestionnaireDisponibilite();
		questionnaireDisponibiliteCreator.init(this.getActivity(), layout_questionnaire_disponibilite);
		
		return view;
	}

	@Override
	public void onResume() {
		Log.e("DEBUG", "onResume of LoginFragment");
		super.onResume();
		//populateFields();
	}

	public void populateFields() {
		
	}
	
}
