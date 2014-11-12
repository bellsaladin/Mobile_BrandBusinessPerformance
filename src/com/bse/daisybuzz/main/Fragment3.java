package com.bse.daisybuzz.main;

import com.bse.daisybuzz.helper.Common;
import com.bse.daisybuzz.helper.Preferences;
import com.bse.daisybuzz.helper.SqliteDatabaseHelper;
import com.bse.daizybuzz.model.Cadeau;
import com.bse.daizybuzz.model.Marque;
import com.bse.daizybuzz.model.PDV;
import com.bse.daizybuzz.model.Superviseur;
import com.loopj.android.http.RequestParams;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Fragment3 extends Fragment {
	
	private Button btn_synchronize, btn_pushLocalDataToServer, btn_disconnect;

	ProgressDialog prgDialog;

	RequestParams params = new RequestParams();
	String id;
	String name;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		prgDialog = new ProgressDialog(this.getActivity());
		// Set Cancelable as False
		prgDialog.setCancelable(false);
		View view = inflater.inflate(R.layout.fragment3, null);

		btn_disconnect = (Button) view.findViewById(R.id.btn_disconnect);
		btn_disconnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				disconnecte(v);
			}
		});
		btn_synchronize = (Button) view.findViewById(R.id.btn_synchronize);
		btn_synchronize.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				synchronize(v);
			}
		});
		
		btn_pushLocalDataToServer = (Button) view.findViewById(R.id.btn_pushLocalDataToServer);
		btn_pushLocalDataToServer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pushLocalDataToServer(Fragment3.this.getActivity(), prgDialog);
			}
		});
			
		return view;
	}

	public void synchronize(View v) {
		
		Common.synchronizeAll(this.getActivity());
		
	}
	
	public void pushLocalDataToServer(Activity activity, ProgressDialog prgDialog){
		Common.pushDataToServer(activity, prgDialog);
	}

	public void disconnecte(View v) {
		this.getActivity().finish();
		System.exit(0);
	}

	
	
}