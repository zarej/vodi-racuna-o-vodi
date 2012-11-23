package rs.hakaton.stedivodu.remainder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			 Log.d("BOOT_COMPLETED", "Vodi racuna o vodi BOOT_COMPLETED");

			Intent i = new Intent(
					"rs.hakaton.stedivodu.REMAINDER_SETUP");
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// call Activity to setup remainder
			context.startActivity(i);
		}

	}
}
