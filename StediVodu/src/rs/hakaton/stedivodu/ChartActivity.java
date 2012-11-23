package rs.hakaton.stedivodu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class ChartActivity extends Activity {
	
	CategorySeries series;
	ArrayList<String> labels = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		series = new CategorySeries("Potrošnja u din");
		
		new SendPostReqAsyncTask().execute("voda", "getstatistic");
		
	}
	

	public XYMultipleSeriesRenderer getBarDemoRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		r.setColor(Color.BLUE);
		renderer.addSeriesRenderer(r);
//		r = new SimpleSeriesRenderer();
//		r.setColor(Color.GREEN);
//		renderer.addSeriesRenderer(r);
		return renderer;
	}

	private XYMultipleSeriesDataset getBarDemoDataset() {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
//		final int nr = 10;
//		Random r = new Random();
//		for (int i = 0; i < 1; i++) {
//			CategorySeries series = new CategorySeries("Demo series " + (i + 1));
//			for (int k = 0; k < nr; k++) {
//				series.add(100 + r.nextInt() % 100);
//			}
//			dataset.addSeries(series.toXYSeries());
//		}
		dataset.addSeries(series.toXYSeries());
		return dataset;
	}

	private void setChartSettings(XYMultipleSeriesRenderer renderer) {
		renderer.setChartTitle("Potrošnja po mesecima");
		renderer.setXTitle("mesec");
		renderer.setYTitle("dinara");
		renderer.setXAxisMin(0.5f);
		renderer.setXAxisMax(3);
		renderer.setYAxisMin(0);
		renderer.setYAxisMax(1000);
		renderer.setXLabels(0);
		renderer.setBarSpacing(0.5f);
		
		for (int i=0; i < labels.size(); i++) {
			
			String dateStr = labels.get(i); 

			SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd"); 
			Date dateObj = null;
			try {
				dateObj = curFormater.parse(dateStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			SimpleDateFormat postFormater = new SimpleDateFormat("MMM yyyy"); 

			String newDateStr = postFormater.format(dateObj); 
			Log.d("","Datum=" + newDateStr);
			
			renderer.addXTextLabel(i+1, newDateStr);
		}
		
		
		
		
		
//		for (int i = 0; i < count; i++) {
//			  renderer.addYTextLabel(...);
//			} 
		
	}
	
	private void startChartScreen() {
		XYMultipleSeriesRenderer renderer = getBarDemoRenderer();
		setChartSettings(renderer);
		Intent intent = ChartFactory.getBarChartIntent(this, getBarDemoDataset(),
				renderer, Type.DEFAULT);
		startActivity(intent);
		finish();
	}
	
	class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
		
		ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progressDialog = ProgressDialog.show(ChartActivity.this, "",
					"Učitavanje...", true);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return PostHelper.post(ChartActivity.this, params);
		}
		
		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			if (result == null) {
				Toast.makeText(ChartActivity.this, "Verovatno te zeza net", Toast.LENGTH_SHORT).show();				
				return;
			}
			
			Log.d("result", "Result posta: " + result );
			
			try {
				JSONObject json = new JSONObject(result);
				String status = json.getString("status");
				
				if (status.equals("success")) {
					
					JSONArray resultArray = new JSONArray(json.get("result").toString());
					
					for (int i = 0; i < resultArray.length(); i++) {
						JSONObject resultItem = resultArray.getJSONObject(i);
						series.add(resultItem.getDouble("racun"));
						labels.add(resultItem.getString("datum"));
					}
					startChartScreen();
					
				} else {
					Toast.makeText(ChartActivity.this, "Greška prilikom dovlačenja podataka", Toast.LENGTH_SHORT).show();
				}
				
			} catch (JSONException ex) {
				ex.printStackTrace();
			}
		}
		
	}
}
