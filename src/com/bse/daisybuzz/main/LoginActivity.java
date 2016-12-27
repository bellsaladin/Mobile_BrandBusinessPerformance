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
import com.bse.daisybuzz.main.QuestionnaireSummaryPopup.AsyncTaskRunner;
import com.bse.daisybuzz.test.QuestionnaireDisponibilite;
import com.bse.daisybuzz.test.QuestionnaireShelfShare;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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
	private Button btnLogin;
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
		editText_password.setText("");

		/*
		 * attempts = (TextView)findViewById(R.id.textView5);
		 * attempts.setText(Integer.toString(counter));
		 */
		btnLogin = (Button) findViewById(R.id.button1);
		btnLogin.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(btnLogin.getText().equals("En cours...")) return;
				LoginAsyncTaskRunner runner = new LoginAsyncTaskRunner();
				String sleepTime = "1000";
				runner.execute(sleepTime);
			}
		});
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
	
	/**************************************************************************************/
	/****************************     AsyncTaskRunner       *******************************/
	/**************************************************************************************/
	
	class LoginAsyncTaskRunner extends AsyncTask<String, String, String> {
		
		private String resp;
		private int authenticationResult;
		private Preferences preferences;

		@Override
		protected String doInBackground(String... params) {
			publishProgress("Sleeping..."); // Calls onProgressUpdate()
			try {
				String inputUsername = editText_username.getText().toString();
				String inputPassword = editText_password.getText().toString();
				// fetch the Password form database for respective user name

				preferences = new Preferences(LoginActivity.this);
					

				if(Common.isNetworkAvailable(LoginActivity.this)){
					authenticationResult = authenticateUser(inputUsername, inputPassword);
					return String.valueOf(authenticationResult);
				}else{
					// try to connect using local data
					String storedUsername = preferences.getStringValue("USERNAME");
					String storedPassword = preferences.getStringValue("PASSWORD");	
					
					if(storedUsername.isEmpty()){
						return "LOCAL_CONNECTION_FAILED__NEVER_CONNECTED_ONLINE"; 
					}
					
					if (!storedUsername.isEmpty() && !storedPassword.isEmpty()) {
						// check if the Stored username and password matches with Password entered by
						// user
						if (inputUsername.equals(storedUsername) && inputPassword.equals(storedPassword)) {				
							return "LOCAL_CONNECTION_SUCCESS";
						} else {
							return "LOCAL_CONNECTION_INCORRECT_CREDENTIALS";
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				resp = e.getMessage();
			}
			return resp;
		}

		
		@Override
		protected void onPostExecute(String result) {
			LoginActivity.this.btnLogin.setText("Connexion");
			if(result.equals("LOCAL_CONNECTION_FAILED__NEVER_CONNECTED_ONLINE")){
				Toast.makeText(
						LoginActivity.this.getApplicationContext(),
						"Pour pouvoir vous connecter à l'application sans internet il vous faut réaliser une connexion en vous connectant à internet !",
						Toast.LENGTH_LONG).show();
				return;
			}
			if(result.equals("LOCAL_CONNECTION_INCORRECT_CREDENTIALS")){
				Toast.makeText(getApplicationContext(), "Compte invalide ! (Connexion locale)",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if(result.equals("LOCAL_CONNECTION_SUCCESS")){
				Toast.makeText(getApplicationContext(),
						"Connexion en cours...", Toast.LENGTH_SHORT).show();
				
				Statics.sfoId = Integer.valueOf(preferences.getStringValue("ANIMATEUR_ID"));
				
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				return;
			}
			// CONNECTED ONLINE result contains the SFO_ID
			if (Integer.valueOf(result) >1) {
				Toast.makeText(getApplicationContext(),
						"Connexion en cours...", Toast.LENGTH_SHORT).show();
				
				Statics.sfoId = Integer.valueOf(preferences.getStringValue("ANIMATEUR_ID"));
				
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				return;
			} else if (Integer.valueOf(result) == 0){
				Toast.makeText(getApplicationContext(), "Compte invalide ! (Connexion via serveur)",
						Toast.LENGTH_SHORT).show();
			} else if (Integer.valueOf(result) == -1){
				Toast.makeText(
						LoginActivity.this.getApplicationContext(),
						"Impossible de communiquer avec le serveur distant ! Réessayer plus tard ...",
						Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onPreExecute() {
			// Things to be done before execution of long running operation. For
			// example showing ProgessDialog
			LoginActivity.this.btnLogin.setText("En cours...");
		}

		@Override
		protected void onProgressUpdate(String... text) {
			//finalResult.setText(text[0]);
			// Things to be done while execution of long running operation is in
			// progress. For example updating ProgessDialog
		}
	}
}
