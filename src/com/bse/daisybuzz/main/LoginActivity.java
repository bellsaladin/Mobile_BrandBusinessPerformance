package com.bse.daisybuzz.main;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
	
	LoginDataBaseAdapter loginDataBaseAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		// create a instance of SQLite Database
		loginDataBaseAdapter = new LoginDataBaseAdapter(this);
		loginDataBaseAdapter = loginDataBaseAdapter.open();
		
		// Save the Data in Database
	    loginDataBaseAdapter.insertEntry("admin", "123");
		
		editText_username = (EditText) findViewById(R.id.editText1);
		editText_password = (EditText) findViewById(R.id.editText2);
		
		// for debug 
		editText_username.setText("admin");
		editText_password.setText("123");
		
		
		/*
		 * attempts = (TextView)findViewById(R.id.textView5);
		 * attempts.setText(Integer.toString(counter));
		 */
		login = (Button) findViewById(R.id.button1);
	}

	public void login(View view) {
		/*if (username.getText().toString().equals("admin")
				&& password.getText().toString().equals("admin")) {
			Toast.makeText(getApplicationContext(), "Redirecting...",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "Wrong Credentials",
					Toast.LENGTH_SHORT).show();
			
			//attempts.setBackgroundColor(Color.RED); counter--;
			//attempts.setText(Integer.toString(counter)); if(counter==0){
			//login.setEnabled(false); }
			 
		}*/
		
		
		String username = editText_username.getText().toString();
		String password = editText_password.getText().toString();
		// fetch the Password form database for respective user name
		String storedPassword=loginDataBaseAdapter.getSinlgeEntry(username);
		
		// check if the Stored password matches with  Password entered by user
		if(password.equals(storedPassword))
		{
			Toast.makeText(getApplicationContext(), "Connexion en cours...",Toast.LENGTH_SHORT).show();
			
			Intent intent = new Intent(this, StartActivity.class);
		    startActivity(intent);
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Compte invalide !",
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
