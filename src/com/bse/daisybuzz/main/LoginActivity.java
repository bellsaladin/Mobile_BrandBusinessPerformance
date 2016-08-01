package com.bse.daisybuzz.main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.bse.daisybuzz.helper.Common;
import com.bse.daisybuzz.helper.Constants;
import com.bse.daisybuzz.helper.Preferences;
import com.bse.daisybuzz.helper.Statics;
import com.bse.daisybuzz.helper.Utils;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {

	private EditText editText_username = null;
	private EditText editText_password = null;
	// private TextView attempts;
	private Button login;
	// int counter = 3;
	
	
	
	static InputStream inputStream = null;
	static String result = null;
	static String line = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login);
		

		editText_username = (EditText) findViewById(R.id.editText1);
		editText_password = (EditText) findViewById(R.id.editText2);

		// for debug
		// editText_username.setText("animateur1");
		editText_password.setText("123456");

		/*
		 * attempts = (TextView)findViewById(R.id.textView5);
		 * attempts.setText(Integer.toString(counter));
		 */
		login = (Button) findViewById(R.id.button1);
		
		if (android.os.Build.VERSION.SDK_INT > 9) { 
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		// get preferences
		Preferences preferences = new Preferences(this);
		String webserviceRootUrl = preferences
				.getStringValue("PARAM_WEBSERVICE_ROOT_URL");

		// check for fist use of the application
		if (webserviceRootUrl.isEmpty()){					
			preferences.saveValue("PARAM_WEBSERVICE_ROOT_URL",
					Constants.DEFAULT_WEBSERVICE_URL_ROOT);
			//promptEmptyWebServiceUrlDialog();
		}

		String storedUsername = preferences.getStringValue("USERNAME");
		editText_username.setText(storedUsername);

		
	}

	public void login(View view) {		
		
		/*
		 * if (username.getText().toString().equals("admin") &&
		 * password.getText().toString().equals("admin")) {
		 * Toast.makeText(getApplicationContext(), "Redirecting...",
		 * Toast.LENGTH_SHORT).show(); } else {
		 * Toast.makeText(getApplicationContext(), "Wrong Credentials",
		 * Toast.LENGTH_SHORT).show();
		 * 
		 * //attempts.setBackgroundColor(Color.RED); counter--;
		 * //attempts.setText(Integer.toString(counter)); if(counter==0){
		 * //login.setEnabled(false); }
		 * 
		 * }
		 */

		String inputUsername = editText_username.getText().toString();
		String inputPassword = editText_password.getText().toString();
		// fetch the Password form database for respective user name

		Preferences preferences = new Preferences(this);
			

		if(Common.isNetworkAvailable(this)){
			int authenticationResult = authenticateUser(inputUsername, inputPassword);
			if (authenticationResult > 0) {
				Toast.makeText(getApplicationContext(),
						"Connexion en cours...", Toast.LENGTH_SHORT).show();
				
				Statics.sfoId = Integer.valueOf(preferences.getStringValue("ANIMATEUR_ID"));
				
				Intent intent = new Intent(this, StartActivity.class);
				startActivity(intent);
				finish();
			} else if (authenticationResult == 0){
				Toast.makeText(getApplicationContext(), "Compte invalide ! (Connexion via serveur)",
						Toast.LENGTH_SHORT).show();
			} else if (authenticationResult == -1){
				Toast.makeText(
						this.getApplicationContext(),
						"Impossible de communiquer avec le serveur distant ! Réessayer plus tard ...",
						Toast.LENGTH_LONG).show();
			}
		}else{
			// try to connect using local data
			String storedUsername = preferences.getStringValue("USERNAME");
			String storedPassword = preferences.getStringValue("PASSWORD");	
			
			if(storedUsername.isEmpty()){
				Toast.makeText(
						this.getApplicationContext(),
						"Pour pouvoir vous connecter à l'application sans internet il vous faut réaliser une connexion en vous connectant à internet !",
						Toast.LENGTH_LONG).show();
				return; 
			}
			
			if (!storedUsername.isEmpty() && !storedPassword.isEmpty()) {
				// check if the Stored username and password matches with Password entered by
				// user
				if (inputUsername.equals(storedUsername) && inputPassword.equals(storedPassword)) {				
					Toast.makeText(getApplicationContext(),
							"Connexion en cours...", Toast.LENGTH_SHORT).show();
					
					Statics.sfoId = Integer.valueOf(preferences.getStringValue("ANIMATEUR_ID"));
					
					Intent intent = new Intent(this, StartActivity.class);
					startActivity(intent);
					finish();
				} else {
					Toast.makeText(getApplicationContext(), "Compte invalide ! (Connexion locale)",
							Toast.LENGTH_SHORT).show();
				}
			}
		}

	}

	private int authenticateUser(String username, String password) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("username",
				username));
		nameValuePairs.add(new BasicNameValuePair("password",
				password));
		/*Toast.makeText(getApplicationContext(),
				"Communication avec le serveur...", Toast.LENGTH_SHORT).show();*/		
		
		try {
			Utils.initHttpParams();
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.DEFAULT_WEBSERVICE_URL_ROOT
					+ "/authentification");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
			Log.e("pass 1", "connection success");
			
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			inputStream.close();
			result = sb.toString();
			Log.e("DEBUG AAAAA", result);
			int animateurId = Integer.valueOf(result.trim());
			Log.e("DEBUG", String.valueOf(animateurId));
			if(animateurId > 0){
				Preferences preferences = new Preferences(this);
				preferences.saveValue("USERNAME", username);
				preferences.saveValue("PASSWORD", password);
				preferences.saveValue("ANIMATEUR_ID", String.valueOf(animateurId));
				Log.e("DEBUG", String.valueOf(animateurId));
			}
			
			return animateurId;
		} catch (Exception e) {
			Log.e("Fail Login", e.toString());			
			
			return -1; // error
		}
				
	}
	
	
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
								LoginActivity.this);
						preferences.saveValue("PARAM_WEBSERVICE_ROOT_URL",
								value);
												
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
