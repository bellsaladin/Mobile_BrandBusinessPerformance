package com.bse.daisybuzz.main;

import com.bse.daisybuzz.helper.Common;
import com.bse.daisybuzz.helper.Constants;
import com.bse.daisybuzz.helper.Preferences;
import com.bse.daisybuzz.helper.SqliteDatabaseHelper;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;

public class StartActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		
		setContentView(R.layout.activity_start);
		
		/*
		 * IMPORTANT FOR THE
		 * Webservice request to
		 * work
		 */
		if (android.os.Build.VERSION.SDK_INT > 9) { 
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		
		// create sqllite database if not exists ()
		SqliteDatabaseHelper db = new SqliteDatabaseHelper(this);
		
		// get preferences
		Preferences preferences = new Preferences(this);
		String webserviceRootUrl = preferences
				.getStringValue("PARAM_WEBSERVICE_ROOT_URL");

		// check for fist use of the application
		if (webserviceRootUrl.isEmpty()){
			promptEmptyWebServiceUrlDialog();
		}
		else{
			Common.synchronizeAll(this);
			// load main activity
			Intent intent = new Intent(StartActivity.this, MainActivity.class);
		    startActivity(intent);
		    finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		
		return true;
	}

	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/
	
	private void promptEmptyWebServiceUrlDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Première utilisation");
		alert.setMessage("Vous devez lié l'application à un webservice : Ex : http://192.168.1.29/_testZone/webservice/");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setText(Constants.DEFAULT_WEBSERVICE_URL_ROOT);
		alert.setView(input);

		alert.setPositiveButton("Continuer",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String value = input.getText().toString();
						Preferences preferences = new Preferences(
								StartActivity.this);
						preferences.saveValue("PARAM_WEBSERVICE_ROOT_URL",
								value);
						Common.synchronizeAll(StartActivity.this, value);
						
						// load main activity
						Intent intent = new Intent(StartActivity.this, MainActivity.class);
					    startActivity(intent);
					}
				});

		alert.setNegativeButton("Quitter l'application",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// stop the appication on cancel
						finish();
					}
				});

		alert.show();
	}
}
