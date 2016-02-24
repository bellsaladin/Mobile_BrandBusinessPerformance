package com.bse.daisybuzz.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings.TextSize;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	EditText[][] editTextsArray;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final String[] rows = { "Brand 1", "Brand 2", "Brand 3", "Brand 4", "Brand 5",
				"Brand 6", "Brand 7" };
		final String[] columns = {"Segment 1", "Segment 2", "Segment 3","" };
		editTextsArray = new EditText[rows.length][columns.length];
		int rl = rows.length;
		int cl = columns.length;

		Log.d("--", "R-Lenght--" + rl + "   " + "C-Lenght--" + cl);

		ScrollView sv = new ScrollView(this);
		HorizontalScrollView hsv = new HorizontalScrollView(this);
		
		LinearLayout linearLayout = new LinearLayout(this);
	    linearLayout.setBackgroundColor(Color.CYAN);
	    linearLayout.setOrientation(LinearLayout.VERTICAL);
	    
	    hsv.addView(linearLayout);

		final TableLayout tableLayout = createTableLayout(rows, columns, rl, cl);

		Button saveButton = new Button(this);
	    saveButton.setText("Enregistrer");
	    saveButton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	
	        	for (int i = 0; i < rows.length; i++) {
	        		TableRow row = (TableRow)tableLayout.getChildAt(i);
	    			for (int j = 1; j <= columns.length - 1; j++) {
	    				View child = row.getChildAt(j);
	    		        if (child instanceof EditText) {
	    		        	AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainActivity.this);
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
	    
	    linearLayout.addView(tableLayout);
	    linearLayout.addView(saveButton);
	    
		sv.addView(hsv);
		setContentView(sv);

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

	private TableLayout createTableLayout(String[] rows, String[] columns,
			int rowCount, int columnCount) {
		// 1) Create a tableLayout and its params
		TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
		TableLayout tableLayout = new TableLayout(this);
		tableLayout.setBackgroundColor(Color.BLACK);

		// 2) create tableRow params
		TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
		tableRowParams.setMargins(1, 1, 1, 1);
		tableRowParams.weight = 1;

		for (int i = 0; i < rowCount; i++) {
			// 3) create tableRow
			TableRow tableRow = new TableRow(this);
			tableRow.setBackgroundColor(Color.GRAY);

			for (int j = 0; j < columnCount; j++) {
			
				String s1 = Integer.toString(i);
				String s2 = Integer.toString(j);
				String s3 = s1 + s2;
				int id = Integer.parseInt(s3);
				Log.d("TAG", "-___>" + id);
				if (i == 0 && j == 0) {
					TextView textView = new TextView(this);
					textView.setText("");
					tableRow.addView(textView, tableRowParams);
				} else if (i == 0) {
					Log.d("TAAG", "set Column Headers");
					TextView textView = new TextView(this);
					textView.setTextSize(18);
					textView.setText(columns[j - 1]);
					tableRow.addView(textView, tableRowParams);
				} else if (j == 0) {
					Log.d("TAAG", "Set Row Headers");
					TextView textView = new TextView(this);
					textView.setTextSize(16);
					textView.setText(rows[i - 1]);
					tableRow.addView(textView, tableRowParams);
				} else {
					
					editTextsArray[i][j]  = new EditText(this);
					// textView.setText(String.valueOf(j));
					editTextsArray[i][j].setBackgroundColor(Color.WHITE);
					editTextsArray[i][j].setGravity(Gravity.CENTER);
					editTextsArray[i][j].setInputType(InputType.TYPE_CLASS_NUMBER);
					editTextsArray[i][j].setText("" + id);
					//editTextsArray[i][j] = editText; // save editText to the array to make it easy to get back it's value when needed
					
					tableRow.addView(editTextsArray[i][j], tableRowParams);
					
				}

			}

			// 6) add tableRow to tableLayout
			tableLayout.addView(tableRow, tableLayoutParams);
		}

		return tableLayout;
	}
}