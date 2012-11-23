package rs.hakaton.stedivodu;

import org.json.JSONException;
import org.json.JSONObject;

import rs.hakaton.stedivodu.remainder.RemainderActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Bounce;

public class GraphicActivity extends Activity {

	private static final String TAG = "GraphicActivity";

	LinearLayout llMarker;
	LinearLayout llRaising;
	LinearLayout llRaisingSub;
	LinearLayout llVasaPotrosnja;
	ImageView imageVoda;
	TextView tvYourAverage;
	TextView tvProsek;
	
	private TweenManager tweenManager;
	private boolean isAnimationRunning = true;
	TextView scoreTextView;
	int screenWidth;
	int screenHeight;
	float prosekVeci = 0f;
	float prosekManji = 0f;
	static float vodaContainerFinalPosY;
	static float prosekProcenat;
	static boolean needToIncresePercentage = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
//		Display display = getWindowManager().getDefaultDisplay();
//		Point size = new Point();
//		display.getSize(size);
//		screenWidth = size.x;
//		screenHeight = size.y;
		
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
		
		Log.d(TAG, "Width=" + screenWidth);
		Log.d(TAG, "Height=" + screenHeight);
		
		Log.d(TAG, "Racun=" + User.racun);
		Log.d(TAG, "Tip Racuna=" + User.tipRacuna);
		Log.d(TAG, "jeKuca=" + User.jeKuca);
		Log.d(TAG, "Grad=" + User.grad);

		populateDatabase();
		
		setContentView(R.layout.activity_graphic);
		llMarker = (LinearLayout) findViewById(R.id.linearLayoutМаркеr);
		llRaising = (LinearLayout) findViewById(R.id.linearLayoutRaising);
		imageVoda = (ImageView) findViewById(R.id.imageVoda);
		scoreTextView = (TextView) findViewById(R.id.textViewScore1);
		llRaisingSub = (LinearLayout) findViewById(R.id.linearLayoutRaisingSub);
		llVasaPotrosnja = (LinearLayout) findViewById(R.id.linearLayoutVasaPotrosnja);
		tvYourAverage = (TextView) findViewById(R.id.textViewYourAverage);
		tvProsek = (TextView) findViewById(R.id.textViewProsek);
		
		llRaisingSub.setVisibility(View.GONE);
		tvYourAverage.setVisibility(View.INVISIBLE);
		
		tweenManager = new TweenManager();
		
		setTweenEngine();
		
		TextView tvSoc = (TextView) findViewById(R.id.textViewSoc);
		tvSoc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(GraphicActivity.this, ShareActivity.class);
				i.putExtra("social", "facebook");
				String caption = "Moj račun za vodu je manji od proseka za %d%";
				if (prosekManji != 0f) {
					caption = String.format("Moj račun za vodu je manji od proseka za %d%%. Proverite vaš.", (int)prosekProcenat);
				} else if (prosekVeci != 0f) {
					caption = String.format("Moj račun za vodu je veći od proseka za %d%%. Proverite vaš.", (int)prosekProcenat);
				}
				i.putExtra("caption", caption);
				startActivity(i);
				finish();
			}
		});
		
		
		//Remainder
		RemainderActivity.setMonthlyAlarm(this);
	}


	private void populateDatabase() {
		
		new SendPostReqAsyncTask().execute("voda", "setuserdata");
		
	}
	
	class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dialog = ProgressDialog.show(GraphicActivity.this, "",
					"Loading. Please wait...", true);

		}

		@Override
		protected String doInBackground(String... params) {
			return PostHelper.post(GraphicActivity.this, params);
		}

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			
			Log.d(TAG, "Result = " + result);
			
			if (result == null) {
				Toast.makeText(GraphicActivity.this, "Verovatno te zeza net", Toast.LENGTH_SHORT).show();
				return;
			} 
			
			try {
				JSONObject json = new JSONObject(result);
				if (json.getString("status").equals("success")) {
					JSONObject potrosnjaJson = null;
					try {
						potrosnjaJson = json.getJSONObject("potrosnja");
					} catch (JSONException ex) {
						reactOnCityNotFoundInDb();
						return;
					}
					
					double p39 = potrosnjaJson.getDouble("p_39");
					double p36 = potrosnjaJson.getDouble("p_36");
					double p18 = potrosnjaJson.getDouble("p_18");
					double p5 = potrosnjaJson.getDouble("p_5");
					double p1d7 = potrosnjaJson.getDouble("p_1d7");
					double p0d3 = potrosnjaJson.getDouble("p_0d3");
					double prosek = (p39 * 39f + p36 * 36f + p18 * 18f + p5 * 5f + p1d7 * 1.7f + p0d3 * 0.3f)/ 100;  
					
					populateScores(p39, p36, p18, p5, p1d7, p0d3);
					
					Log.d(TAG, "Prosek u din = " + prosek);
					
					
					tvProsek.setText(getResources().getString(R.string.prosek, String.format("%.2f", prosek)));
					
					TextView tvVasRacun = (TextView) findViewById(R.id.textViewVasRacun);
					tvVasRacun.setText(getResources().getString(R.string.vasracun, String.format("%.2f", User.racun)));
					
					if (prosek > User.racun) {
						prosekManji = 100f - ((User.racun/(float) prosek)) * 100f;
						String sf = String.format("%.2f", prosekManji);
						Log.d(TAG, "Tvoj racun je manji od proseka za " + sf);
						prosekProcenat = prosekManji;
						tvYourAverage.setText(R.string.ispodproseka);
					} else {
//						prosekVeci = 100f - ((float) prosek / User.racun) * 100f;
						prosekVeci = (User.racun - (float) prosek) / (float) prosek * 100;
						String sf = String.format("%.2f", prosekVeci);
						Log.d(TAG, "Tvoj racun je veci od proseka za " + sf);
						prosekProcenat = prosekVeci;
						tvYourAverage.setText(R.string.iznadproseka);
					}
					startAnimation();
					
				} else {
					Toast.makeText(GraphicActivity.this, "Greska prilikom upisa u bazu", Toast.LENGTH_SHORT).show();
				}
				
			} catch (JSONException ex) {
				ex.printStackTrace();
				Toast.makeText(GraphicActivity.this, "Greska prilikom parsiranja json-a. Vidi php", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	

	  /** 
	   * Initiate the Tween Engine
	   */
	  private void setTweenEngine() {
	    tweenManager = new TweenManager();
	    //start animation theread
	    setAnimationThread();

	    //**Register Accessor, this is very important to do!
	    //You need register actually each Accessor, but right now we have global one, which actually suitable for everything.
	    Tween.registerAccessor(ViewContainer.class, new ViewContainerAccessor());

	  }
	  
	  /**
	   * Timeout 1 sec after press
	   * @param v
	   */
	  private void startAnimation() {

	    //Create object which we will animate
	    ViewContainer markerContainer = new ViewContainer();
	    ViewContainer vodaContainer = new ViewContainer();
	    //pass our real container
	    markerContainer.view = llMarker;
	    vodaContainer.view = llRaising;

	    ///start animation
	    
	    ImageView markerImage = (ImageView) findViewById(R.id.imageMarker);
	    RelativeLayout containerLayout = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);
	    Log.d(TAG, "containerLayout VISINA = " + containerLayout.getHeight());
	    screenHeight = containerLayout.getHeight();
	    
	    int prosekPomeri = 0;
	    
	    if (prosekManji != 0f) {
	    	prosekPomeri = (int) (prosekManji*screenHeight/100);
		    Log.d(TAG, "Prosek Podigni = " + prosekPomeri);
		    
		    if (prosekPomeri > screenHeight/2) prosekPomeri = screenHeight/2;
		    Log.d(TAG, "Prosek Podigni Zadnji= " + prosekPomeri);
		    prosekPomeri = -prosekPomeri;
	    } else if (prosekVeci != 0f) {
	    	prosekPomeri = (int) (prosekVeci*screenHeight/100);
		    Log.d(TAG, "Prosek Spusti = " + prosekPomeri);
		    
		    if (prosekPomeri > screenHeight/2) prosekPomeri = screenHeight/2;
		    Log.d(TAG, "Prosek Spusti Zadnji= " + prosekPomeri);
	    }
	    
	    Tween.to(markerContainer, ViewContainerAccessor.POSITION_XY, 0.5f)
	    .target(0, (screenHeight/2 + prosekPomeri) - markerImage.getHeight()/2) //na pola ekrana minus ako je prosek manji za toliko procenata povecati visinu
	    .ease(Bounce.OUT)
	    .delay(1.0f)
	    .start(tweenManager);
	    
	    Tween.to(vodaContainer, ViewContainerAccessor.POSITION_XY, 0.1f)
	    .target(0, screenHeight) //prvo se skriva voda
	    .start(tweenManager);
	    
	    vodaContainerFinalPosY = screenHeight/2 - llVasaPotrosnja.getHeight();
	    needToIncresePercentage = true;
	    
	    Tween.to(vodaContainer, ViewContainerAccessor.POSITION_XY, 2.5f)
	    .target(0, vodaContainerFinalPosY)
	    .delay(1.0f)
	    .setCallback(raisingCallBack)
	    .start(tweenManager);

	  }
	
	/***
	   * Thread that should run for update UI via Tween engine
	   */
	  private void setAnimationThread() {

	    new Thread(new Runnable() {
	      private long lastMillis = -1;

	      @Override public void run() {
	        while (isAnimationRunning) {
	          if (lastMillis > 0) {
	            long currentMillis = System.currentTimeMillis();
	            final float delta = (currentMillis - lastMillis) / 1000f;

	            /*
	            view.post(new Runnable(){
	              @Override public void run() {
	                  
	              }
	            });
	            */
	            /**
	             * We run all animation in UI thread instead of using post for each elements.
	             */
	            runOnUiThread(new Runnable() {

	              @Override public void run() {
	                tweenManager.update(delta);

	              }
	            });

	            lastMillis = currentMillis;
	          } else {
	            lastMillis = System.currentTimeMillis();
	          }

	          try {
	            Thread.sleep(1000 / 60);
	          } catch (InterruptedException ex) {
	          }
	        }
	      }
	    }).start();

	  }
	  
	TweenCallback raisingCallBack = new TweenCallback() {
		
		@Override
		public void onEvent(int arg0, BaseTween<?> arg1) {
			// TODO Auto-generated method stub
			
			Animation fadeIn = new AlphaAnimation(0, 1);
			fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
			fadeIn.setDuration(5000);
			
			needToIncresePercentage = false;
			tvYourAverage.setVisibility(View.VISIBLE);
			tvYourAverage.startAnimation(fadeIn);
			
			displayDelayed();
		}
	};
	
	private void displayDelayed() {
		new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                //Finish the splash activity so it can't be returned to.
//            	SplashScreenActivity.this.finish();
//                // Create an Intent that will start .startAnimation(fadeIn);the main activity.
//                Intent mainIntent = new Intent(SplashScreenActivity.this, BlueTexasHoldEmActivity.class);
//                SplashScreenActivity.this.startActivity(mainIntent);
            	
            	Animation fadeIn = new AlphaAnimation(0, 1);
    			fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
    			fadeIn.setDuration(4000);
            	llRaisingSub.startAnimation(fadeIn);
            	llRaisingSub.setVisibility(View.VISIBLE);
            	
            	
            	
            	//Create object which we will animate
            	RelativeLayout rlSoc = (RelativeLayout) findViewById(R.id.relativeLayoutSocialize);
//            	TextView tvSoc = (TextView) findViewById(R.id.textViewSoc);
//            	tvSoc.setVisibility(View.VISIBLE);     
            	
            	rlSoc.setVisibility(View.VISIBLE); 
            	
//            	ViewContainer socContainer = new ViewContainer();
        	    //pass our real container
//        	    socContainer.view = rlSoc;
        	    
//        	    Tween.to(socContainer, ViewContainerAccessor.POSITION_XY, 0.01f)
//        	    .target(-150, 10)
//        	    .repeatYoyo(1, 5f)
//        	    .start(tweenManager);
        	    
//        	    Tween.to(socContainer, ViewContainerAccessor.POSITION_XY, 5f)
//        	    .target(10, 10) 
//        	    .start(tweenManager);
        	    
        	    Animation animation = new TranslateAnimation(-150, 10,0, 0);
        	    animation.setDuration(1000);
        	    animation.setFillAfter(true);
        	    rlSoc.startAnimation(animation);
            	
            	
            }
        }, 1000);

	}
	
	private void populateScores(double score1, double score2, double score3, double score4,
			double score5, double score6) {
		
		TextView tvScore1 = (TextView) findViewById(R.id.textViewScore11);
		tvScore1.setText(getResources().getString(R.string.trosi, String.format("%.2f", score1)));
		
		TextView tvScore2 = (TextView) findViewById(R.id.textViewScore22);
		tvScore2.setText(getResources().getString(R.string.trosi, String.format("%.2f", score2)));
		
		TextView tvScore3 = (TextView) findViewById(R.id.textViewScore33);
		tvScore3.setText(getResources().getString(R.string.trosi, String.format("%.2f", score3)));
		
		TextView tvScore4 = (TextView) findViewById(R.id.textViewScore44);
		tvScore4.setText(getResources().getString(R.string.trosi, String.format("%.2f", score4)));
		
		TextView tvScore5 = (TextView) findViewById(R.id.textViewScore55);
		tvScore5.setText(getResources().getString(R.string.trosi, String.format("%.2f", score5)));
		
		TextView tvScore6 = (TextView) findViewById(R.id.textViewScore66);
		tvScore6.setText(getResources().getString(R.string.trosi, String.format("%.2f", score6)));
	}
	
	private void reactOnCityNotFoundInDb() {
		llVasaPotrosnja.setVisibility(View.INVISIBLE);
		tvProsek.setVisibility(View.INVISIBLE);
		
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(GraphicActivity.this);

		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setMessage("U toku je prikuljanje informacija za vaš grad...")
		       .setTitle("Trenutno nemamo statistiku za " + User.grad);
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User clicked OK button
	        	   finish();
	           }
	       });

		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();
		
		dialog.show();
	}
	
}
