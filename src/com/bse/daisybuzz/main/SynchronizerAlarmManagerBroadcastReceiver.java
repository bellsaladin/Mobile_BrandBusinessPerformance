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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class SynchronizerAlarmManagerBroadcastReceiver extends BroadcastReceiver {	
	
	public static SqliteDatabaseHelper db;	
	public static Localisation processedLocalisation;
	public static Rapport processedRapport;
	
	
	public SynchronizerAlarmManagerBroadcastReceiver(){
		
	}
	
	final public static String ONE_TIME = "onetime";
	@Override
	public void onReceive(Context context, Intent intent) {
		 if(MainActivity.getInstance() == null){
        	 return;
         }
		 PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
         PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
         //Acquire the lock
                 
         wl.acquire();
         if(db == null){
        	db = new SqliteDatabaseHelper(MainActivity.getInstance());
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
         
         //Release the lock
         wl.release();
         
	}
	public void createSynchronizationAlarmManager(Context context)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SynchronizerAlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after 30 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Constants.SYNCHRONIZER_INTERVAL , pi); 
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, SynchronizerAlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
    public void setOnetimeTimer(Context context){
    	AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SynchronizerAlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
    }
}
