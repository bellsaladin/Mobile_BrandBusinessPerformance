package com.bse.daisybuzz.test;

import java.util.ArrayList;
import java.util.List;

import com.bse.daisybuzz.helper.SqliteDatabaseHelper;
import com.bse.daizybuzz.model.Categorie;
import com.bse.daizybuzz.model.Marque;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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

public class QuestionnaireShelfShareCreator {
	TableLayout _tableLayout;
	List<Marque> _marquesList;
	List<Categorie> _categoriesProduitsList;
	List<Categorie> _segmentsOfSelectedCategorieProduitsList;
	
	private Activity targetActivity;
	private EditText[][] editTextsArray;
	private SqliteDatabaseHelper db;
	
	public void init(final Activity activity, LinearLayout containerLayout){
		this.targetActivity = activity;
		
		db = new SqliteDatabaseHelper(this.targetActivity.getApplicationContext());
				
		_categoriesProduitsList = db.getAllCategoriesOfProduits(); 
		
		
		/*_segmentsList = new ArrayList<Categorie>();
		_segmentsList.add(new Categorie(1, "Frontal","",""));
		_segmentsList.add(new Categorie(2, "Automatic","",""));
		_segmentsList.add(new Categorie(3, "Semi-Auto","",""));
		_segmentsList.add(new Categorie());*/
		//_categoriesProduitsList.add(new Categorie()); // important : ajouter un element vide sinon une colonne ne s'affiche pas
		
		_marquesList = db.getAllMarquesOperatingInCategory(_categoriesProduitsList.get(0));
		_segmentsOfSelectedCategorieProduitsList = db.getSegmentsOfCategorie(_categoriesProduitsList.get(0));
		_segmentsOfSelectedCategorieProduitsList.add(new  Categorie()); // FIXME : ajouter un element vide sinon une colonne ne s'affiche pas

		
		createSpinner(this.targetActivity, containerLayout);
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

		createTableLayout(this.targetActivity, linearLayout);
	    
		Button saveButton = new Button(activity);
	    saveButton.setText("Enregistrer");
	    saveButton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	
	        	for (int i = 0; i < _marquesList.size(); i++) {
	        		TableRow row = (TableRow)_tableLayout.getChildAt(i);
	        		row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	    			for (int j = 1; j <= _segmentsOfSelectedCategorieProduitsList.size() - 1; j++) {
	    				View child = row.getChildAt(j);
	    		        if (child instanceof EditText) {
	    		        	AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(activity);
		    				dlgAlert.setMessage("EditText[" + i + "]["+j+"].value : " + ((EditText)child).getText());
		    				dlgAlert.setTitle("App Title");
		    				dlgAlert.setPositiveButton("OK", null);
		    				dlgAlert.setCancelable(true);
		    				dlgAlert.create().show();
			        	    Log.d("----", "Button index: " + (i+j) +  ((EditText)child).getText());
	    		        } else if (child instanceof TextView) {
	    		            //validate RadioButton
	    		        }
	    			}
	        	}
	        }
	    });
	    
	    linearLayout.addView(saveButton);

		sv.addView(hsv);
		containerLayout.addView(sv);

	}

	private void createTableLayout(Activity activity, LinearLayout containerLayout) {
		_tableLayout = new TableLayout(this.targetActivity);
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
		editTextsArray = new EditText[_marquesList.size()][_segmentsOfSelectedCategorieProduitsList.size()];
		
		_tableLayout.removeAllViews();
		
		List columns = _segmentsOfSelectedCategorieProduitsList;
		List rows = _marquesList;
		int rowCount = rows.size();
		int columnCount = columns.size();

		Log.d("--", "R-Lenght--" + rowCount + "   " + "C-Lenght--" + columnCount);
		
		// 1) Create a tableLayout and its params
		TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
		_tableLayout.setBackgroundColor(Color.BLACK);

		// 2) create tableRow params
		TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
		tableRowParams.setMargins(1, 1, 1, 1);
		tableRowParams.weight = 1;

		for (int i = 0; i < rowCount; i++) {
			// 3) create tableRow
			TableRow tableRow = new TableRow(this.targetActivity);
			tableRow.setBackgroundColor(Color.parseColor("#eeeeee"));

			for (int j = 0; j < columnCount; j++) {
			
				String s1 = Integer.toString(i);
				String s2 = Integer.toString(j);
				String s3 = s1 + s2;
				int id = Integer.parseInt(s3);
				Log.d("TAG", "-___>" + id);
				if (i == 0 && j == 0) {
					TextView textView = new TextView(this.targetActivity);
					textView.setText("");
					tableRow.addView(textView, tableRowParams);
				} else if (i == 0) {
					Log.d("TAAG", "set Column Headers");
					TextView textView = new TextView(this.targetActivity);
					textView.setTextSize(18);
					textView.setText(columns.get(j - 1).toString());
					textView.setTextColor(Color.DKGRAY);
					textView.setTypeface(null, Typeface.BOLD);
					//textView.setTextAppearance(android.R.style.TextAppearance_Small);
					tableRow.addView(textView, tableRowParams);
				} else if (j == 0) {
					Log.d("TAAG", "Set Row Headers");
					TextView textView = new TextView(this.targetActivity);
					textView.setTextSize(16);
					textView.setText(rows.get(i - 1).toString());
					textView.setTextColor(Color.RED);
					textView.setTypeface(null, Typeface.BOLD);
					tableRow.addView(textView, tableRowParams);
				} else {
					
					editTextsArray[i][j] = new EditText(this.targetActivity);
					// textView.setText(String.valueOf(j));
					//editTextsArray[i][j].setBackgroundColor(Color.WHITE);
					//editTextsArray[i][j].getLayoutParams().width = 50;
					editTextsArray[i][j].setGravity(Gravity.CENTER);
					editTextsArray[i][j].setInputType(InputType.TYPE_CLASS_NUMBER);
					editTextsArray[i][j].setText("0");
					//editTextsArray[i][j] = editText; // save editText to the array to make it easy to get back it's value when needed
					
					tableRow.addView(editTextsArray[i][j], tableRowParams);			
				}

			}

			// 6) add tableRow to tableLayout
			_tableLayout.addView(tableRow, tableLayoutParams);
		}
		_tableLayout.refreshDrawableState();
		return _tableLayout;
	}
	
	public void createSpinner(Activity activity, LinearLayout containerLayout){
		final Spinner cb_categorie = new Spinner(activity);
	    List<String> dataArray = new ArrayList<String>();
	    for(Categorie categorie : _categoriesProduitsList){
	    	if(categorie.getNom() != null)
	    		dataArray.add(categorie.toString());
	    }
	    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, dataArray);
	    cb_categorie.setAdapter(spinnerArrayAdapter);
	    containerLayout.addView(cb_categorie);
	    
	    cb_categorie.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				Categorie selectedCategorieProduits = _categoriesProduitsList.get(
						cb_categorie.getSelectedItemPosition());
				_marquesList = db.getAllMarquesOperatingInCategory(selectedCategorieProduits);
				_marquesList.add(new Marque()); // FIXME : ajouter un element vide sinon la marque ne s'affiche pas
				
				_segmentsOfSelectedCategorieProduitsList = db.getSegmentsOfCategorie(selectedCategorieProduits);
				_segmentsOfSelectedCategorieProduitsList.add(new  Categorie()); // FIXME : ajouter un element vide sinon une colonne ne s'affiche pas
				
				QuestionnaireShelfShareCreator.this.fillTableLayout();
				//_tableLayout = new TableLayout(QuestionnaireShelfShareCreator.this.targetActivity);
				//_tableLayout.removeAllViews();
//				Toast.makeText(
//						QuestionnaireShelfShareCreator.this.targetActivity.getApplicationContext(),
//						"Catégorie séléctionnée  : " + selectedCategory + " , _marquesList.size "  + _marquesList.size(), Toast.LENGTH_SHORT)
//						.show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});
	}
}