package com.bse.daisybuzz.test;

import java.util.ArrayList;
import java.util.List;
import com.bse.daisybuzz.helper.SqliteDatabaseHelper;
import com.bse.daisybuzz.helper.Statics;
import com.bse.daisybuzz.helper.Utils;
import com.bse.daisybuzz.main.Fragment2;
import com.bse.daisybuzz.main.QuestionnaireSummaryPopup;
import com.bse.daizybuzz.model.Categorie;
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
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.content.DialogInterface;

public class QuestionnaireDisponibilite {
	private static final String ALL_CATEGORIES = "Toutes les catégories";
	private static final String ALL_SEGMENTS = "Tous les segments";
	
	TableLayout _tableLayout;
	static List<Produit> _produitsList;
	static List<Poi> _poisList;
	List<Categorie> _categoriesProduitsList;
	List<Categorie> _segmentsOfSelectedCategorieProduitsList;
	
	private static Activity _targetActivity;
	private static int[][] _quantitiesArray;
	private ArrayList<EditText> _editTextsArray;
	private static SqliteDatabaseHelper _db;
	
	Spinner _cb_categorie;
	Spinner _cb_segment;
	Spinner _cb_poi;
	
	EditText _filterEditText;
	
	public static Questionnaire _questionnaire;
	private static int _nbrLignesTraitees = 0;
	private static float _tempsRemplissage = 0;
	private static long _lastSysTimeMillis = 0; // used to help calculate _tempsRemplissage
	
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
		createFilterEditBox(this._targetActivity, containerLayout);
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
	    
		Button addProductReferenceButton = new Button(activity);
		addProductReferenceButton.setText("AJOUTER REFERENCE");
		addProductReferenceButton.setBackgroundColor(Color.parseColor("#ffffff"));
		addProductReferenceButton.setTextColor(Color.parseColor("#3366ff"));
		addProductReferenceButton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {	
	        	
	        	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	        	builder.setTitle("Nouvelle reference");
	            LinearLayout dialogLayout = new LinearLayout(activity);
	            dialogLayout.setOrientation(LinearLayout.VERTICAL);

	            LayoutParams LLParams = new LayoutParams(LayoutParams.MATCH_PARENT,400);

	            dialogLayout.setLayoutParams(LLParams);

	        	final EditText skuInput = new EditText(activity);
	        	skuInput.setLayoutParams( new LayoutParams(LayoutParams.MATCH_PARENT,120));
	        	// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
	        	skuInput.setInputType(InputType.TYPE_CLASS_TEXT);
	        	
	        	String[] strings={"REF","WM","CTV","AC"};
	            final Spinner typeSpinner = new Spinner(activity);
	            typeSpinner.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item,strings));
	            typeSpinner.setLayoutParams( new LayoutParams(LayoutParams.MATCH_PARENT,120));
	        	
	        	dialogLayout.addView(typeSpinner);
	        	dialogLayout.addView(skuInput);

	        	builder.setView(dialogLayout);
	        	
	        	builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
	        	});
	        	
	        	builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int produitId = _db.getRecordsCount("produit") + 1;
						String sku = skuInput.getText().toString();
						String type = typeSpinner.getSelectedItem().toString();
						
						// create new 'produit' locally
			        	Produit produit = new Produit(produitId, sku, sku, "");;
			        	
			        	produit.setAddedLocaly(1); // important : for synchronisation conflict handling
			        	produit.setType(type);
			        	_db.createProduit(produit);
			        	
			        	Log.e("Debug QD", " > " + _db.getRecordsCount("produit") );
			        	// refill tableLayout
			        	fillTableLayout();
					}
	        	});

	        	builder.show();
	        	
	        }
	    });
	    
	    linearLayout.addView(addProductReferenceButton);
	    
		
		Button saveButton = new Button(activity);
	    saveButton.setText("Enregistrer");
	    saveButton.setBackgroundColor(Color.parseColor("#cccccc"));
	    saveButton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {	
	        	//storeDataOnLocalStorage();
	        	QuestionnaireSummaryPopup.show();
	        }
	    });
	    
	    linearLayout.addView(saveButton);

		sv.addView(hsv);
		containerLayout.addView(sv);

	}
	
	public static void storeDataOnLocalStorage() {
		stopTempsRemplissageCount();
		_questionnaire = new Questionnaire();
		_questionnaire.setType(Questionnaire.TYPE_DISPONIBILITE);
		String quantitiesData = getSerializedQuantitiesData();
		_questionnaire.setQuantitiesData(quantitiesData);
		_questionnaire.setLocalisationId(String.valueOf(Statics.lastLocalisationId));
		_questionnaire.setDateCreation(Utils.now());
		_questionnaire.setNbrLignesTraitees(_nbrLignesTraitees);
		_questionnaire.setTempsRemplissage(_tempsRemplissage);
		_db.createQuestionnaire(_questionnaire);
	}

	private static String getSerializedQuantitiesData() {
		String data = "";
		for(int i  = 0; i < _poisList.size(); i++){
				for(int k  = 0; k < _produitsList.size(); k++){
						int newProduit = (_produitsList.get(k).isAddedLocaly())?1:0;
						String type = _produitsList.get(k).getType();
						String poiId = String.valueOf(_poisList.get(i).getId());
						//String categorieProduits_id =String.valueOf(_categoriesProduitsList.get(j).getId());
						String produitId = String.valueOf(_produitsList.get(k).getId());
						if(_produitsList.get(k).isAddedLocaly())
							produitId = _produitsList.get(k).getSku();
						int qty = _quantitiesArray[i][k];
						if(qty >0) _nbrLignesTraitees++;
						data += newProduit + ";" + type + ";" + poiId +";" + produitId + ";" +qty + "||";
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
		_categoriesProduitsList = _db.getAllCategoriesOfProduits(); 
		_poisList = _db.getAllPois();
		_produitsList = _db.getAllProduits();
		_segmentsOfSelectedCategorieProduitsList = _db.getSegmentsOfCategorie(_categoriesProduitsList.get(0));
		_segmentsOfSelectedCategorieProduitsList.add(new  Categorie()); // FIXME : ajouter un element vide sinon une colonne ne s'affiche pas
		initQuantitiesArray();
		
		_editTextsArray = new ArrayList<EditText>();
		_tableLayout.removeAllViews();
		
		//_produitsList = _db.getAllProduits();
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
			EditText productQtyEditText =	new EditText(this._targetActivity);	
			
			// textView.setText(String.valueOf(j));
			//_editTextsArray[i][j].setBackgroundColor(Color.WHITE);
			//_editTextsArray[i][j].getLayoutParams().width = 50;
			productQtyEditText.setGravity(Gravity.CENTER);
			//_editTextsArray[i].setInputType(InputType.TYPE_CLASS_NUMBER);
			productQtyEditText.setInputType(InputType.TYPE_NULL);
			productQtyEditText.setWidth(140);
			
			final int produitIdx = i;
			
			int qty = _quantitiesArray[_cb_poi.getSelectedItemPosition()] [produitIdx];
			productQtyEditText.setText(String.valueOf(qty));
			productQtyEditText.addTextChangedListener(new TextWatcher() {
					          public void afterTextChanged(Editable editable) {
					        	  if(editable.toString().isEmpty())
					        		  return;
					        	  int qty = Integer.valueOf(editable.toString());
					        	  /*Categorie selectedCategorieProduits = _categoriesProduitsList.get(
											_cb_categorie.getSelectedItemPosition() - 1);
					        	  Poi selectedPoi = _poisList.get( _cb_poi.getSelectedItemPosition() );*/
					        	  
					        	  _quantitiesArray[_cb_poi.getSelectedItemPosition()]
					        			  		 [produitIdx]
					        			  		 = qty;
					          }
					          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
					          public void onTextChanged(CharSequence s, int start, int before, int count) {}
					    }
			);
		    
		    final EditText finalEditText = productQtyEditText; 
		    productQtyEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

		        @Override
		        public void onFocusChange(View v, boolean hasFocus) {
		            if (hasFocus) {
		            	Fragment2._currentlySelectedEditText = finalEditText;
		            	Fragment2.layout_bottomControls.setVisibility(LinearLayout.VISIBLE);
		            }
		        }
		    });
		    _editTextsArray.add(productQtyEditText);
			//editTextsArray[i][j] = editText; // save editText to the array to make it easy to get back it's value when needed
			
			tableRow.addView(productQtyEditText, tableRowParams);			
				
			// add tableRow to tableLayout
			_tableLayout.addView(tableRow, tableLayoutParams);
		}
		
		_tableLayout.refreshDrawableState();
		return _tableLayout;
	}
	
	private void createCategorieProduitsSpinner(final Activity activity, LinearLayout containerLayout){
		_cb_categorie = new Spinner(activity);
	    List<String> dataArray = new ArrayList<String>();
	    //dataArray.add(ALL_CATEGORIES);
	    for(Categorie categorie : _categoriesProduitsList){
	    	if(categorie.getNom() != null)
	    		dataArray.add(categorie.toString());
	    }
	    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, dataArray);
	    _cb_categorie.setAdapter(spinnerArrayAdapter);
	    containerLayout.addView(_cb_categorie);
	    
	    _cb_categorie.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedItemIndex, long id) {
				if(_cb_categorie.getItemAtPosition(selectedItemIndex).toString().equals(ALL_CATEGORIES)){
					//_cb_segment.setVisibility(View.GONE);
					//_produitsList.add(new Produit()); // FIXME : ajouter un element vide sinon la marque ne s'affiche pas
					//_segmentsOfSelectedCategorieProduitsList = _db.getSegmentsOfCategorie(selectedCategorieProduits);
					//_segmentsOfSelectedCategorieProduitsList.add(new  Categorie()); // FIXME : ajouter un element vide sinon une colonne ne s'affiche pas
				    // get selected item
			    	//_produitsList = _db.getAllProduits();
			    	//filterTableProduits(new String[]{});
			    	
			    }else{
			    	//_cb_segment.setVisibility(View.VISIBLE);
			    	Categorie selectedCategorieProduits = _categoriesProduitsList.get(_cb_categorie.getSelectedItemPosition());
			    	_segmentsOfSelectedCategorieProduitsList = _db.getSegmentsOfCategorie(selectedCategorieProduits);
			    	fill_CB_Segment(_segmentsOfSelectedCategorieProduitsList);
			    	// show only products related to the segments of the selected categorie
				    String[] segmentsIds = new String[_segmentsOfSelectedCategorieProduitsList.size()];
				    int i = 0;
				    for(Categorie segment : _segmentsOfSelectedCategorieProduitsList){
				    	segmentsIds[i] = String.valueOf(segment.getId());
				    	i++;
				    }
					//_produitsList = _db.getAllProduits(segmentsIds);
					filterTableProduits(segmentsIds);
					
			    }
				//QuestionnaireDisponibilite.this.fillTableLayout();

			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});
	}
	
	
	
	private void createSegmentSpinner(Activity activity, LinearLayout containerLayout){
		_cb_segment = new Spinner(activity);
		fill_CB_Segment(_segmentsOfSelectedCategorieProduitsList);
	    containerLayout.addView(_cb_segment);
	    
	    _cb_segment.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedItemIndex, long id) {
				if(_cb_categorie.getSelectedItem().toString().equals(ALL_CATEGORIES)){
					return; // do nothing because this would inivisible
				}
				
				Categorie selectedCategorieProduits = _categoriesProduitsList.get(_cb_categorie.getSelectedItemPosition());
				_segmentsOfSelectedCategorieProduitsList = _db.getSegmentsOfCategorie(selectedCategorieProduits);
				
				if(_cb_segment.getItemAtPosition(selectedItemIndex).toString().equals(ALL_SEGMENTS)){
					
				    String[] segmentsIds = new String[_segmentsOfSelectedCategorieProduitsList.size()];
				    int i = 0;
				    for(Categorie segment : _segmentsOfSelectedCategorieProduitsList){
				    	segmentsIds[i] = String.valueOf(segment.getId());
				    	i++;
				    }
					//_produitsList = _db.getAllProduits(segmentsIds);
					filterTableProduits(segmentsIds);
			    }else{
			    	String segmentId = String.valueOf(_segmentsOfSelectedCategorieProduitsList.get(selectedItemIndex - 1).getId());
					//_produitsList = _db.getAllProduits(new String[]{segmentId});
					filterTableProduits(new String[]{segmentId});
			    }
				
				//_produitsList.add(new Produit()); // FIXME : ajouter un element vide sinon la marque ne s'affiche pas
				//_segmentsOfSelectedCategorieProduitsList.add(new  Categorie()); // FIXME : ajouter un element vide sinon une colonne ne s'affiche pas
				//QuestionnaireDisponibilite.this.fillTableLayout();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});
	}
	
	private void fill_CB_Segment(List<Categorie> segments){
		List<String> dataArray = new ArrayList<String>();
		dataArray.add(ALL_SEGMENTS);
	    for(Categorie categorie : segments){
	    	if(categorie.getNom() != null)
	    		dataArray.add(categorie.toString());
	    }
	    
	    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this._targetActivity, android.R.layout.simple_spinner_dropdown_item, dataArray);
	    _cb_segment.setAdapter(spinnerArrayAdapter);
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
				//QuestionnaireDisponibilite.this.fillTableLayout();
				for(int i = 0, j = _tableLayout.getChildCount(); i < j; i++) {
					Produit respectiveProduct = _produitsList.get(i);
				    View view = _tableLayout.getChildAt(i);
				    if (view instanceof TableRow) {
				        // then, you can remove the the row you want...
				        // for instance...
				        TableRow row = (TableRow) view;
				        EditText productQtyEditText = (EditText) row.getChildAt(1);
				        final int produitIdx = i;
				        int qty = _quantitiesArray[_cb_poi.getSelectedItemPosition()] [produitIdx];
						productQtyEditText.setText(String.valueOf(qty));
						productQtyEditText.addTextChangedListener(new TextWatcher() {
						          public void afterTextChanged(Editable editable) {
						        	  if(editable.toString().isEmpty())
						        		  return;
						        	  int qty = Integer.valueOf(editable.toString());
						        	  
						        	  _quantitiesArray[_cb_poi.getSelectedItemPosition()]
						        			  		 [produitIdx]
						        			  		 = qty;
						          }
						          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				
						          public void onTextChanged(CharSequence s, int start, int before, int count) {}
						    }
						);
				    }
				}
				
			}
			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				
			}
		});
	}
	
	private void createFilterEditBox(Activity activity, LinearLayout containerLayout){
		_filterEditText = new EditText(activity);
		_filterEditText.setHint("Recherche");
		containerLayout.addView(_filterEditText);
		_filterEditText .addTextChangedListener(new TextWatcher() {
	          public void afterTextChanged(Editable editable) {
	        	  if(!editable.toString().isEmpty()){
	        		  filterTableProduits(null, editable.toString());
	        	  }
	          }
	          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	          public void onTextChanged(CharSequence s, int start, int before, int count) {}
	       });
	}
	
	private void filterTableProduits(String[] segmentsIds, String product_sku){
		for(int i = 0, j = _tableLayout.getChildCount(); i < j; i++) {
			Produit respectiveProduct = _produitsList.get(i);
		    View view = _tableLayout.getChildAt(i);
		    if (view instanceof TableRow) {
		        // then, you can remove the the row you want...
		        // for instance...
		        TableRow row = (TableRow) view;
		        row.setVisibility(View.GONE);
		        if(product_sku != null){
		        	if(respectiveProduct.getSku().toLowerCase().contains(product_sku.toLowerCase())){
		        		row.setVisibility(View.VISIBLE);
		        	}	
				}
		        if(segmentsIds != null){
			        if(segmentsIds.length == 0){
			        	row.setVisibility(View.VISIBLE);
			        	continue;
			        } 
			        for(String segmentId : segmentsIds){
			        	if(respectiveProduct.getCategorieId().equals(segmentId)){
			        		row.setVisibility(View.VISIBLE);
			        		break;
						}
					}
		        }
		    }
		}
	}
	
	private void filterTableProduits(String[] segmentsIds){
		filterTableProduits(segmentsIds, null);
	}
	
	private void initQuantitiesArray(){
		// FIXME : enlever les valeurs spécifiés en dur lors de la création du tableau des quantités
		//int MAX_NBR_OF_PRODUCTS = 500;
		_quantitiesArray = new int[_poisList.size()][_produitsList.size()];
		for(int i  = 0; i < _poisList.size(); i++){
			for(int k  = 0; k < _produitsList.size(); k++){
					_quantitiesArray[i][k] = 0;
			}
		}
		
		/*for(int i  = 0; i < _poisList.size(); i++){
			for(int j  = 0; j < _categoriesProduitsList.size(); j++){
				//for(int k  = 0; k < db.getAllMarquesOperatingInCategory(_categoriesProduitsList.get(j)).size(); k++){
				for(int k  = 0; k < _produitsList.size(); k++){
					_quantitiesArray[i][j][k] = 0;
				}
			}
		}*/
	}
	
	public static void stopTempsRemplissageCount(){
		long millis = System.currentTimeMillis() - _lastSysTimeMillis;
        int seconds = (int) (millis / 1000);
        _tempsRemplissage += seconds;
	}
	
	public void startTempsRemlissageCount(){
		_lastSysTimeMillis = System.currentTimeMillis();
	}

	public static String getInputSummary() {
		int poiDoneCount = 0;
		List<String> poiNotFilled = new ArrayList<String>(); 
		for(int i  = 0; i < _poisList.size(); i++){
			boolean poiDataWasFilled = false;
			for(int k  = 0; k < _produitsList.size(); k++){
				if(_quantitiesArray[i][k] != 0){
					poiDataWasFilled = true;
					poiDoneCount++;
				}
			}
			if(!poiDataWasFilled)
				poiNotFilled.add(_poisList.get(i).getLibelle());
		}
		
		String content = "";
		content += "<h4>Questionnaire Disponiblité</h4>";
		if(poiNotFilled.size() == 0){
			content += "<font color='green'><br/> Point d'intérêts réalisés <b>" + poiDoneCount +" /7</font></b><br/>";
		}else{
			content += "<font color='red'><br/> Point d'intérêts réalisés <b>" + poiDoneCount +" /7</font></b><br/>";
			content += "<p>Les POI non réalisés : </p>";
			for(String poi : poiNotFilled){
				content += "<p>  - " + poi + "<p>";
			}
		}
		return content;
	}
	
}