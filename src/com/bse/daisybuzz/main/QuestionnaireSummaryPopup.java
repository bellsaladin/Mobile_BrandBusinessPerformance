package com.bse.daisybuzz.main;

import com.bse.daisybuzz.test.QuestionnaireDisponibilite;
import com.bse.daisybuzz.test.QuestionnaireShelfShare;

import android.app.Activity;
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
	
	public static void setup(Activity activity, Button btnOpenPopup) {
		QuestionnaireSummaryPopup.activity = activity;
		QuestionnaireSummaryPopup.btnOpenPopup = btnOpenPopup;
	}
	
	public static void init(){
		LayoutInflater layoutInflater = (LayoutInflater) activity.getBaseContext()
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		popupView = layoutInflater.inflate(R.layout.popup, null);
		popupWindow = new PopupWindow(popupView,LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		

		Button btnCancel = (Button) popupView
				.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});
		
		Button btnConfirm = (Button) popupView
				.findViewById(R.id.btn_confirm);
		btnConfirm.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				
	        	// Save the two 'Questionnaires'
				QuestionnaireShelfShare.storeDataOnLocalStorage();
				QuestionnaireDisponibilite.storeDataOnLocalStorage();
				// remove rapport tab and go back to localisation
				MainActivity.removeRapportTab();
				popupWindow.dismiss();
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
	
	
	// public QuestionnaireSummaryPopup(Button button)

	/*@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// final Button btnOpenPopup = (Button)findViewById(R.id.openpopup);
		btnOpenPopup.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				

			}
		});
	}*/
}