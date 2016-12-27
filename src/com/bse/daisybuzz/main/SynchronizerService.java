package com.bse.daisybuzz.main;

import java.util.List;

import com.bse.daisybuzz.helper.Common;
import com.bse.daisybuzz.helper.Constants;
import com.bse.daisybuzz.helper.SqliteDatabaseHelper;
import com.bse.daizybuzz.model.Localisation;
import com.bse.daizybuzz.model.Questionnaire;
import com.bse.daizybuzz.model.Rapport;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SynchronizerService extends Service{
	final public static String ONE_TIME = "onetime";
	public static SqliteDatabaseHelper db;	
	public static Localisation processedLocalisation;
	public static Rapport processedRapport;
	
    @Override
    public void onCreate() {
        super.onCreate();
        if(MainActivity.getInstance() == null) return;
        
        if(db == null){
        	db = new SqliteDatabaseHelper(MainActivity.getInstance());
        }
        //Log.d("Testing", "Service got created");
        //Toast.makeText(this, "ServiceClass.onCreate()", Toast.LENGTH_LONG).show();
    }


	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        //Toast.makeText(this, "ServiceClass.onStart()", Toast.LENGTH_LONG).show();
    	super.onStart(intent, startId);
    	if(MainActivity.getInstance() == null) return;
    	if(db == null){
        	try{
    		db = new SqliteDatabaseHelper(MainActivity.getInstance());
    		if(db.getReadableDatabase() == null) return;
        	}catch (Exception e){
        		return; // stop on start if we can't get an instance of "database"
        	}
        }
         
         int localisationsCount = db.getRecordsCount("localisation");
         int rapportsCount = db.getRecordsCount("rapport");
         
     	 if(localisationsCount == 0){
     		MainActivity.showSynchronizationIndicator("Aucune donnée à envoyer au serveur",true);
     		return;
  	     }
     	 
     	
         if(localisationsCount > 0){
        	processedLocalisation = db.getAllLocalisations().get(0);        	
         	List<Questionnaire> questionnairesOfLocalisation = db.getAllQuestionnairesOfLocalisation(processedLocalisation);
         	
         	// FIXME : DEBUG BLOCK --------------------------------------
        	for(Localisation l : db.getAllLocalisations()){
        		Log.e("Localistion ID " , "" + l.getId());
        	}
        	for(Rapport r : db.getAllRapports()){
        		Log.e("Rapport ID " , "" + r.getLocalisationId());
        	}        	
        	Log.e("Info", "Localisations : " + localisationsCount + ", Rapports of localisation :" + questionnairesOfLocalisation.size() + ", ID of current localisation : " + processedLocalisation.getId() + ", All Rapports : " + rapportsCount);
        	// FIXME : DEBUG BLOCK --------------------------------------        	        	
        	
        	int insertedLocalisationId = -1;
    		if(processedLocalisation.getInsertedInServerWithId().isEmpty()){ // localisation not inserted
    			MainActivity.showSynchronizationIndicator("Envoi des données au serveur ...",false);
    			Common.sendLocalisationToServer(processedLocalisation,Constants.DEFAULT_WEBSERVICE_URL_ROOT, db, MainActivity.getInstance());    			
    			return; // FIXME : OPTIMIZATION STORE ONE ENTITY AT ONCE  
    		}else{
    			insertedLocalisationId = Integer.valueOf(processedLocalisation.getInsertedInServerWithId());
    		}
    		
        	for(Questionnaire questionnaire : questionnairesOfLocalisation){
        		
        		if(insertedLocalisationId != -1){ // if not error        			
        			questionnaire.setLocalisationId(String.valueOf(insertedLocalisationId));
					// try send it to the server
        			MainActivity.showSynchronizationIndicator("Envoi des données au serveur ...",false);
					Common.sendQuestionnaireToServer(questionnaire,Constants.DEFAULT_WEBSERVICE_URL_ROOT, db, MainActivity.getInstance());
					
					return; // FIXME : OPTIMIZATION STORE ONE ENTITY AT ONCE
        		}        		        			
        	}
        	
        	if(localisationsCount > 1 && questionnairesOfLocalisation.size() <= 0){
    			// we do not remove that last localisation because the user might still add rapport on int
    			db.deleteLocalisation(processedLocalisation);
    			return;
    		}
        	
        	if(!processedLocalisation.getInsertedInServerWithId().isEmpty() && questionnairesOfLocalisation.size() == 0){
          		MainActivity.showSynchronizationIndicator("Vos données sont synchronisées",false);
          		return;
       	    }
        	
        	if(!Common.isNetworkAvailable(MainActivity.getInstance())){
         		MainActivity.showSynchronizationIndicator("Tentative de synchronisation : Internet indisponible",true);
         		return;
      	    }
 
         }		 
         
        //Log.d("Testing", "Service got started");
    }
    
    public void createSynchronizationAlarmManager(Context context)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SynchronizerService.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Constants.SYNCHRONIZER_INTERVAL , pi); 
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, SynchronizerService.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
    public void setOnetimeTimer(Context context){
    	AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SynchronizerService.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
    }

}
