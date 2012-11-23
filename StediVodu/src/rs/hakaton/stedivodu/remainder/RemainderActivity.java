package rs.hakaton.stedivodu.remainder;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class RemainderActivity extends Activity {
	
	private static final String TAG = "RemainderActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Log.d(TAG, "Podsetnik je u onCreate");
		
		setMonthlyAlarm(this);
		
		finish();
	}
	
	public static void setMonthlyAlarm(Context c) {
		
		Log.d(TAG, "Podsetnik je u setMonthlyAlarm");
		
		Calendar calendarNow = Calendar.getInstance();
		
		calendarNow.add(Calendar.MONTH, 1);
		calendarNow.set(calendarNow.get(Calendar.YEAR), calendarNow.get(Calendar.MONTH), 15, 13, 30);
		
		long ms = calendarNow.getTimeInMillis();
		
		AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(c, OnAlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(c, 0, i, 0);	
		mgr.setInexactRepeating(AlarmManager.RTC, ms, AlarmManager.INTERVAL_DAY * 30, pi);
	}
	
	public static void setMinutesAlarm(Context c) {
		
		Log.d(TAG, "Podsetnik je u setMinutesAlarm");
		
		Calendar calendarNow = Calendar.getInstance();
		
		calendarNow.add(Calendar.MINUTE, 1);
		
		Log.d(TAG, "Ukljucuje se u min=" + calendarNow.get(Calendar.MINUTE));
				
		long ms = calendarNow.getTimeInMillis();
		
		Log.d(TAG, "Ukljucuje se u ms=" + ms);
		
		long currentMs = System.currentTimeMillis();
		Log.d(TAG, "Trenutni ms=" + currentMs);
		
		Log.d(TAG, "Razlika u ms=" + (ms - currentMs) );
		
		AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(c, OnAlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(c, 0, i, 0);	
//		mgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
//				SystemClock.elapsedRealtime() + 10000, pi);
		mgr.setInexactRepeating(AlarmManager.RTC, ms, 60000, pi);
	}
}
