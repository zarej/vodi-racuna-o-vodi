package rs.hakaton.stedivodu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rs.hakaton.stedivodu.MyLocation.LocationResult;
import rs.hakaton.stedivodu.remainder.OnAlarmReceiver;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Spinner spinnerView;
	private static final String TAG="MainActivity";
	ArrayList<String> items_lat = new ArrayList<String>();
	ArrayList<String> items_cir = new ArrayList<String>();
	LocationResult locationResult;
	Button izracunajButton;
	Button nadjiButton;
	Button kvizButton;
	Button statistikaButton;
	LinearLayout fakeButton;
	TextView textGrad;
	ProgressDialog progressDialog;
	LinearLayout llMainWrapper;
	TextView tvIzaberiGrad;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(OnAlarmReceiver.uniqueID);
		
		spinnerView = (Spinner) findViewById(R.id.spinner);
		fakeButton = (LinearLayout) findViewById(R.id.fakeBUtton);
		textGrad = (TextView) findViewById(R.id.textGrad);
		izracunajButton = (Button) findViewById(R.id.buttonIzracunaj);
		nadjiButton = (Button) findViewById(R.id.buttonNadji);
		kvizButton = (Button) findViewById(R.id.buttonKviz);
		statistikaButton = (Button) findViewById(R.id.buttonStatistika);
		llMainWrapper = (LinearLayout) findViewById(R.id.llMainWrapper);
		tvIzaberiGrad = (TextView) findViewById(R.id.textIzaberiGrad);
		
		setupUser();

		locationResult = new LocationResult() {

			@Override
			public void gotLocation(Location location) {
				progressDialog.dismiss();
				// TODO Auto-generated method stub
				
				if (location == null) {
					User.grad = items_lat.get(0);
					return;
				}
				
				Log.d(TAG, "Location received Lat=" + location.getLatitude());
				Log.d(TAG, "Location received Lon=" + location.getLongitude());
				
				try {
					Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
					List<Address> addresses = gcd.getFromLocation(location.getLatitude(),
							location.getLongitude(), 1);
					String strlocation = "";
					if (addresses.size() > 0)
						strlocation = String.valueOf(addresses.get(0)
								.getLocality());
					Toast.makeText(MainActivity.this, strlocation, Toast.LENGTH_SHORT)
							.show();
					
					Log.d(TAG, "items_lat.size()=" + items_lat.size());
					Log.d(TAG, "items_lat.size()=" + items_cir.size());
					
					int currentCityPos = 0; //0 je Nepoznata lokacija
					for (int i=1; i < items_lat.size(); i++) {
						if (strlocation.equals(items_lat.get(i)) || strlocation.equals(items_cir.get(i))) {
							currentCityPos = i;
							textGrad.setText(items_lat.get(i));
							User.grad = items_lat.get(i);
							Log.d(TAG, "Grad je na poziciji " + i);
							spinnerView.setSelection(currentCityPos);
						}
					}
					
					if (currentCityPos == 0 && !strlocation.equals("")) {
						textGrad.setText(strlocation);
						User.grad = strlocation;
						items_lat.add(strlocation);
						spinnerView.setSelection(items_lat.size()-1);
					} 
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		izracunajButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!validateCity()) {
					//korisnik je izabrao grad: Nepoznato					
					return;
				}
				Intent i = new Intent(MainActivity.this, InputActivity.class);
				startActivity(i);
			}
		});
		
		nadjiButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new SendPostReqAsyncTask().execute("voda", "getcities");
			}
		});
		
		kvizButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				Intent i = new Intent(MainActivity.this, QuizActivity.class);
				startActivity(i);
			}
		});
		
		fakeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				spinnerView.performClick();
			}
		});
		
		statistikaButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!validateCity()) {
					//korisnik je izabrao grad: Nepoznato					
					return;
				}
				Intent i = new Intent(MainActivity.this, ChartActivity.class);
				startActivity(i);
			}
		});
		
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		
		if (llMainWrapper.getHeight() < 321) {
			tvIzaberiGrad.setVisibility(View.GONE);
		}
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE); 
		SharedPreferences.Editor editor = sharedPreferences.edit();
    	editor.putString("grad", User.grad);
    	editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progressDialog = ProgressDialog.show(MainActivity.this, "",
					"Učitavanje...", true);

		}

		@Override
		protected String doInBackground(String... params) {
			return PostHelper.post(MainActivity.this, params);
		}

		@Override
		protected void onPostExecute(String result) {
			
			JSONArray citiesLatJsonArray = null;
			JSONArray citiesCirJsonArray = null;
			
			if (result == null) {
				Toast.makeText(MainActivity.this, "Verovatno te zeza net", Toast.LENGTH_SHORT).show();
				textGrad.setText("Nepoznat");
				progressDialog.dismiss();
				return;
			}
			
			Log.d("result", "Result posta: " + result );
			
			try {
				JSONObject json = new JSONObject(result);
				citiesLatJsonArray = new JSONArray(json.get("grad").toString());
				citiesCirJsonArray = new JSONArray(json.get("grad_cir").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			items_lat.clear();
			items_cir.clear();
			items_lat.add("Nepoznata");
			items_cir.add("Nepoznata");

			for (int i = 0; i < citiesLatJsonArray.length(); i++) {
				try {
					items_lat.add(citiesLatJsonArray.get(i).toString());
					items_cir.add(citiesCirJsonArray.get(i).toString());
					Log.d("json", citiesLatJsonArray.get(i).toString());
					Log.d("json_cir", citiesCirJsonArray.get(i).toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			DbAdapter db = new DbAdapter(MainActivity.this);
			db.open();
			db.insertCities(items_lat);
			db.close();

			setupSpinner();
			
			MyLocation myLocation = new MyLocation();
			boolean locationListenerCalled = myLocation.getLocation(MainActivity.this, locationResult);
			
			if (!locationListenerCalled) {
				progressDialog.dismiss();
			}
			//ovo obrisi kad otkomentarises ovo gore
//			progressDialog.dismiss();
		}
	}
	
	private void setupUser() {
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);    
	    
	    User.userId = sharedPreferences.getString("userId", "none");
	    User.grad = sharedPreferences.getString("grad", "none");
	    
	    if (User.userId.equals("none")) {
	    	//user id is not set
	    	User.userId = UUID.randomUUID().toString();
	    	SharedPreferences.Editor editor = sharedPreferences.edit();
	    	editor.putString("userId", User.userId);
	    	editor.commit();
	    }
	    
	    if (User.grad.equals("none")) {
	    	new SendPostReqAsyncTask().execute("voda", "getcities");
	    } else {
	    	//populate items.lat
	    	DbAdapter db = new DbAdapter(this);
			db.open();
			for (String grad : db.getCities()) {
				items_lat.add(grad);
			}
			db.close();
			
			//setup spinner and select
			setupSpinner();
			for (int i=1; i < items_lat.size(); i++) {
				if (User.grad.equals(items_lat.get(i)) ) {
					textGrad.setText(items_lat.get(i));
					User.grad = items_lat.get(i);
					Log.d(TAG, "Grad je na poziciji " + i);
					spinnerView.setSelection(i);
				}
			}
			
	    }
	    
	}
	
	private void setupSpinner() {
		ArrayAdapter<String> aa = new ArrayAdapter<String>(
				MainActivity.this, android.R.layout.simple_spinner_item,
				items_lat);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerView.setAdapter(aa);
		spinnerView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				User.grad = items_lat.get(position);
				textGrad.setText(items_lat.get(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}
	
	private boolean validateCity() {
		if (User.grad.equals(items_lat.get(0))) {
			//korisnik je izabrao grad: Nepoznato
			Toast.makeText(MainActivity.this, "Izaberite grad ili idite na 'Nađi moju lokaciju'", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}
	}
	
}
