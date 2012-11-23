package rs.hakaton.stedivodu.remainder;

import rs.hakaton.stedivodu.MainActivity;
import rs.hakaton.stedivodu.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnAlarmReceiver extends BroadcastReceiver {
	String TAG = "OnAlarmReceiver";
	NotificationManager nm;
	public static final int uniqueID = 22069821;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.d(TAG, "Podsetnik je u onReceive");

		// Notification
		Intent i = new Intent(context, MainActivity.class);
		nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(uniqueID);

		PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);
		String title = "Vodi računa o vodi";
		String body = "Uporedite račun za ovaj mesec";
		Notification n = new Notification(R.drawable.icon, body,
				System.currentTimeMillis());
		n.setLatestEventInfo(context, title, body, pi);
		n.defaults = Notification.DEFAULT_ALL;
		nm.notify(uniqueID, n);
	}
}
