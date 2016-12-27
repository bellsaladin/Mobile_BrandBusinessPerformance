package com.bse.daisybuzz.main;

import com.bse.daisybuzz.test.QuestionnaireDisponibilite;
import com.bse.daisybuzz.test.QuestionnaireShelfShare;
//import com.example.asynctask.MainActivity.AsyncTaskRunner;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

public class QuestionnaireSummaryPopup {
	
	static Activity activity;
	static Button btnOpenPopup;
	static PopupWindow popupWindow;
	static View popupView;
	static Button btnConfirm;
	static Button btnCancel;
	
	public static void setup(Activity activity, Button btnOpenPopup) {
		QuestionnaireSummaryPopup.activity = activity;
		QuestionnaireSummaryPopup.btnOpenPopup = btnOpenPopup;
	}
	
	public static void init(){
		LayoutInflater layoutInflater = (LayoutInflater) activity.getBaseContext()
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		popupView = layoutInflater.inflate(R.layout.popup, null);
		popupWindow = new PopupWindow(popupView,LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		

		btnCancel = (Button) popupView
				.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});
		
		btnConfirm = (Button) popupView
				.findViewById(R.id.btn_confirm);
		btnConfirm.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(btnConfirm.getText().equals("En cours...")) return;
				AsyncTaskRunner runner = new AsyncTaskRunner();
				runner.execute();
			}
		});
		
	}
	
	public static void show(){
		String content = "<h2>Récapitulatif de la collecte des données</h2><hr>"; 
		content += QuestionnaireShelfShare.getInputSummary();
		content += QuestionnaireDisponibilite.getInputSummary();
		//content = "<h2>Title</h2><br><p>Description here 1</p><br/><p>Description here 1</p>";
		if(popupWindow == null){
			init();
		}
		((TextView)popupView.findViewById(R.id.text_view)).setText(Html.fromHtml(content));
		
		
		//popupWindow.showAsDropDown(btnOpenPopup, 50, -30);
		popupWindow.showAtLocation( btnOpenPopup, Gravity.CENTER, 0, 0);
	}
	
	
	
	/**************************************************************************************/
	/****************************     AsyncTaskRunner       *******************************/
	/**************************************************************************************/
	
	static class AsyncTaskRunner extends AsyncTask<String, String, String> {

		private String resp;

		@Override
		protected String doInBackground(String... params) {
			try {
				// Save the two 'Questionnaires'
				QuestionnaireShelfShare.storeDataOnLocalStorage();
				QuestionnaireDisponibilite.storeDataOnLocalStorage();
				// remove rapport tab and go back to localisation
			} catch (Exception e) {
				e.printStackTrace();
				resp = e.getMessage();
			}
			return resp;
		}

		
		@Override
		protected void onPostExecute(String result) {
			// execution of result of Long time consuming operation
			//finalResult.setText(result);
			MainActivity.removeRapportTab();
			popupWindow.dismiss();
			btnConfirm.setText("Enregistrer");
			btnCancel.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPreExecute() {
			// Things to be done before execution of long running operation. For
			// example showing ProgessDialog
			btnConfirm.setText("En cours...");
			btnCancel.setVisibility(View.INVISIBLE);
		}

		@Override
		protected void onProgressUpdate(String... text) {
			//finalResult.setText(text[0]);
			// Things to be done while execution of long running operation is in
			// progress. For example updating ProgessDialog
		}
	}
}