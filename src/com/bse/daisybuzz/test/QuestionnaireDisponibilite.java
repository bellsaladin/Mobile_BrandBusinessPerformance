package com.bse.daisybuzz.test;

import java.util.ArrayList;
import java.util.List;

import com.bse.daisybuzz.helper.SqliteDatabaseHelper;
import com.bse.daisybuzz.helper.Statics;
import com.bse.daisybuzz.helper.Utils;
import com.bse.daisybuzz.main.Fragment2;
import com.bse.daizybuzz.model.Cadeau;
import com.bse.daizybuzz.model.Categorie;
import com.bse.daizybuzz.model.Marque;
import com.bse.daizybuzz.model.Poi;
import com.bse.daizybuzz.model.Produit;
import com.bse.daizybuzz.model.Questionnaire;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class QuestionnaireDisponibilite {
	TableLayout _tableLayout;
	List<Produit> _produitsList;
	List<Poi> _poisList;
	List<Categorie> _categoriesProduitsList;
	List<Categorie> _segmentsOfSelectedCategorieProduitsList;
	
	private Activity _targetActivity;
	private int[][][] _quantitiesArray;
	private EditText[] _editTextsArray;
	private SqliteDatabaseHelper _db;
	
	Spinner _cb_categorie;
	Spinner _cb_segment;
	Spinner _cb_poi;
	
	
	public Questionnaire _questionnaire;
	
	public void init(final Activity activity, LinearLayout containerLayout){
		this._targetActivity = activity;
		
		_db = new SqliteDatabaseHelper(this._targetActivity.getApplicationContext());
				
		_categoriesProduitsList = _db.getAllCategoriesOfProduits(); 
		_poisList = _db.getAllPois();
		
		_produitsList = _db.getAllProduits();
		_segmentsOfSelectedCategorieProduitsList = _db.getSegmentsOfCategorie(_categoriesProduitsList.get(0));
		_segmentsOfSelectedCategorieProduitsList.add(new  Categorie()); // FIXME : ajouter un element vide sinon une colonne ne s'affiche pas
		initQuantitiesArray();
		
		createCategorieProduitsSpinner(this._targetActivity, containerLayout);
		createSegmentSpinner(this._targetActivity, containerLayout);
		createPoiSpinner(this._targetActivity, containerLayout);
		/*columns = new String[4];
		columns[0] = "Segment 1";
		columns[1] = "Segment 2";
		columns[2] = "Segment 3";
		columns[3] = "";*/
		
		ScrollView sv = new ScrollView(activity);
		HorizontalScrollView hsv = new HorizontalScrollView(activity);
		
		LinearLayout linearLayout = new LinearLayout(activity);
	    linearLayout.setBackgroundColor(Color.CYAN);
	    linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	    linearLayout.setOrientation(LinearLayout.VERTICAL);
	    
	    hsv.addView(linearLayout);

		createTableLayout(this._targetActivity, linearLayout);
	    
		Button saveButton = new Button(activity);
	    saveButton.setText("Enregistrer");
	    saveButton.setBackgroundColor(Color.parseColor("#cccccc"));
	    saveButton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {	
	        	storeDataOnLocalStorage();
	        }
	    });
	    
	    linearLayout.addView(saveButton);

		sv.addView(hsv);
		containerLayout.addView(sv);

	}
	
	private void storeDataOnLocalStorage() {
		_questionnaire = new Questionnaire();
		_questionnaire.setType(Questionnaire.TYPE_DISPONIBILITE);
		String quantitiesData = getSerializedQuantitiesData();
		_questionnaire.setQuantitiesData(quantitiesData);
		_questionnaire.setLocalisationId(String.valueOf(Statics.lastLocalisationId));
		_questionnaire.setDateCreation(Utils.now());
		_db.createQuestionnaire(_questionnaire);

		Toast.makeText(
				_targetActivity.getApplicationContext(),
				"Questionnaire enregistré !", Toast.LENGTH_SHORT)
				.show();
	}

	private String getSerializedQuantitiesData() {
		String data = "";
		for(int i  = 0; i < _poisList.size(); i++){
			for(int j  = 0; j < _categoriesProduitsList.size(); j++){				//for(int k  = 0; k < db.getAllMarquesOperatingInCategory(_categoriesProduitsList.get(j)).size(); k++){
				for(int k  = 0; k < _produitsList.size(); k++){
					
						int poiId = _poisList.get(i).getId();
						int categorieProduits_id = _categoriesProduitsList.get(j).getId();
						int produitId = _produitsList.get(k).getId();
						int qty = _quantitiesArray[i][j][k];
						data += poiId +";" +categorieProduits_id +";" + produitId + ";" +qty + "||";
				}
			}
		}
		if(data.length() > 0) data = data.substring(0, data.length()-2);
		return data;
	}

	private void createTableLayout(Activity activity, LinearLayout containerLayout) {
		_tableLayout = new TableLayout(this._targetActivity);
		_tableLayout = fillTableLayout();
		_tableLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		containerLayout.addView(_tableLayout);
	}

	public void makeCellEmpty(TableLayout tableLayout, int rowIndex,
			int columnIndex) {
		// get row from table with rowIndex
		TableRow tableRow = (TableRow) tableLayout.getChildAt(rowIndex);

		// get cell from row with columnIndex
		TextView textView = (TextView) tableRow.getChildAt(columnIndex);

		// make it black
		textView.setBackgroundColor(Color.BLACK);
	}

	public void setHeaderTitle(TableLayout tableLayout, int rowIndex,
			int columnIndex) {

		// get row from table with rowIndex
		TableRow tableRow = (TableRow) tableLayout.getChildAt(rowIndex);

		// get cell from row with columnIndex
		TextView textView = (TextView) tableRow.getChildAt(columnIndex);

		textView.setText("Hello");
	}

	private TableLayout fillTableLayout() {
		_editTextsArray = new EditText[_produitsList.size()];
		
		_tableLayout.removeAllViews();
		
		
		List rows = _produitsList;
		int rowCount = rows.size();
		
		// 1) Create a tableLayout and its params
		TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
		_tableLayout.setBackgroundColor(Color.BLACK);

		// 2) create tableRow params
		TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
		tableRowParams.setMargins(1, 1, 1, 1);
		tableRowParams.weight = 1;

		for (int i = 0; i < rowCount; i++) {
			// 3) create tableRow
			TableRow tableRow = new TableRow(this._targetActivity);
			tableRow.setBackgroundColor(Color.parseColor("#eeeeee"));

			
			TextView textView = new TextView(this._targetActivity);
			textView.setTextSize(16);
			textView.setText(rows.get(i).toString());
			textView.setTextColor(Color.RED);
			textView.setTypeface(null, Typeface.BOLD);
			tableRow.addView(textView, tableRowParams);	
					
			_editTextsArray[i] = new EditText(this._targetActivity);
			// textView.setText(String.valueOf(j));
			//_editTextsArray[i][j].setBackgroundColor(Color.WHITE);
			//_editTextsArray[i][j].getLayoutParams().width = 50;
			_editTextsArray[i].setGravity(Gravity.CENTER);
			_editTextsArray[i].setInputType(InputType.TYPE_CLASS_NUMBER);
			
			final int produitIdx = i;
			
			int qty = _quantitiesArray[_cb_poi.getSelectedItemPosition()][_cb_categorie.getSelectedItemPosition()][produitIdx];
			_editTextsArray[i].setText(String.valueOf(qty));
			TextWatcher editTextWatcher = new TextWatcher() {

		          public void afterTextChanged(Editable editable) {
		        	  if(editable.toString().isEmpty())
		        		  return;
		        	  int qty = Integer.valueOf(editable.toString());
		        	  Categorie selectedCategorieProduits = _categoriesProduitsList.get(
								_cb_categorie.getSelectedItemPosition());
		        	  Poi selectedPoi = _poisList.get( _cb_poi.getSelectedItemPosition() );
		        	  
		        	  _quantitiesArray[_cb_poi.getSelectedItemPosition()]
		        			  		 [_cb_categorie.getSelectedItemPosition()]
		        			  		 [produitIdx]
		        			  		 = qty;
		          }
		          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		          public void onTextChanged(CharSequence s, int start, int before, int count) {}
		    };
		    _editTextsArray[i].addTextChangedListener(editTextWatcher);
			
			//editTextsArray[i][j] = editText; // save editText to the array to make it easy to get back it's value when needed
			
			tableRow.addView(_editTextsArray[i], tableRowParams);			
				
			// add tableRow to tableLayout
			_tableLayout.addView(tableRow, tableLayoutParams);
		}
		
		_tableLayout.refreshDrawableState();
		return _tableLayout;
	}
	
	private void createCategorieProduitsSpinner(Activity activity, LinearLayout containerLayout){
		_cb_categorie = new Spinner(activity);
	    List<String> dataArray = new ArrayList<String>();
	    for(Categorie categorie : _categoriesProduitsList){
	    	if(categorie.getNom() != null)
	    		dataArray.add(categorie.toString());
	    }
	    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, dataArray);
	    _cb_categorie.setAdapter(spinnerArrayAdapter);
	    containerLayout.addView(_cb_categorie);
	    
	    _cb_categorie.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				Categorie selectedCategorieProduits = _categoriesProduitsList.get(
						_cb_categorie.getSelectedItemPosition());
				_produitsList = _db.getAllProduits();
				//_produitsList.add(new Produit()); // FIXME : ajouter un element vide sinon la marque ne s'affiche pas
				
				_segmentsOfSelectedCategorieProduitsList = _db.getSegmentsOfCategorie(selectedCategorieProduits);
				_segmentsOfSelectedCategorieProduitsList.add(new  Categorie()); // FIXME : ajouter un element vide sinon une colonne ne s'affiche pas
				
				QuestionnaireDisponibilite.this.fillTableLayout();

			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});
	}
	
	private void createSegmentSpinner(Activity activity, LinearLayout containerLayout){
		_cb_categorie = new Spinner(activity);
	    List<String> dataArray = new ArrayList<String>();
	    for(Categorie categorie : _segmentsOfSelectedCategorieProduitsList){
	    	if(categorie.getNom() != null)
	    		dataArray.add(categorie.toString());
	    }
	    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, dataArray);
	    _cb_categorie.setAdapter(spinnerArrayAdapter);
	    containerLayout.addView(_cb_categorie);
	    
	    _cb_categorie.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				Categorie selectedCategorieProduits = _categoriesProduitsList.get(
						_cb_categorie.getSelectedItemPosition());
				_produitsList = _db.getAllProduits();
				//_produitsList.add(new Produit()); // FIXME : ajouter un element vide sinon la marque ne s'affiche pas
				
				_segmentsOfSelectedCategorieProduitsList = _db.getSegmentsOfCategorie(selectedCategorieProduits);
				_segmentsOfSelectedCategorieProduitsList.add(new  Categorie()); // FIXME : ajouter un element vide sinon une colonne ne s'affiche pas
				
				QuestionnaireDisponibilite.this.fillTableLayout();
		
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});
	}
	
	private void createPoiSpinner(Activity activity, LinearLayout containerLayout){
		_cb_poi = new Spinner(activity);
	    List<String> dataArray = new ArrayList<String>();
	    for(Poi poi : _poisList){
	    	if(poi.getLibelle() != null)
	    		dataArray.add(poi.toString());
	    }	 
	    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, dataArray);
	    _cb_poi.setAdapter(spinnerArrayAdapter);
	    containerLayout.addView(_cb_poi);
	    
	    _cb_poi.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				QuestionnaireDisponibilite.this.fillTableLayout();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				
			}
		});
	}
	
	private void initQuantitiesArray(){
		// FIXME : enlever les valeurs spécifiés en dur lors de la création du tableau des quantités
		_quantitiesArray = new int[_poisList.size()][_categoriesProduitsList.size()][50];
		for(int i  = 0; i < _poisList.size(); i++){
			for(int j  = 0; j < _categoriesProduitsList.size(); j++){
				for(int k  = 0; k < 50; k++){
						_quantitiesArray[i][j][k] = -1;
				}
			}
		}
		
		for(int i  = 0; i < _poisList.size(); i++){
			for(int j  = 0; j < _categoriesProduitsList.size(); j++){
				//for(int k  = 0; k < db.getAllMarquesOperatingInCategory(_categoriesProduitsList.get(j)).size(); k++){
				for(int k  = 0; k < _produitsList.size(); k++){
					_quantitiesArray[i][j][k] = 0;
				}
			}
		}
	}
	
}