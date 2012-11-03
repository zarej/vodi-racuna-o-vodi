package rs.hakaton.stedivodu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	private TweenManager tweenManager;
	private boolean isAnimationRunning = true;
	TextView scoreTextView;
	
	//Test github

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Intent callingIntent = getIntent();

		// Log.d(TAG, "Racun=" + callingIntent.getFloatExtra("racun", 0f));
		// Log.d(TAG, "Tip Racuna=" +
		// callingIntent.getStringExtra("tip racuna"));
		// Log.d(TAG, "jeKuca=" + callingIntent.getBooleanExtra("stanovanje",
		// true));
		// Log.d(TAG, "Grad=" + callingIntent.getBooleanExtra("stanovanje",
		// true));

		Log.d(TAG, "Racun=" + User.racun);
		Log.d(TAG, "Tip Racuna=" + User.tipRacuna);
		Log.d(TAG, "jeKuca=" + User.jeKuca);
		Log.d(TAG, "Grad=" + User.grad);

//		populateDatabase();
		
		setContentView(R.layout.activity_graphic);
		llMarker = (LinearLayout) findViewById(R.id.linearLayoutМаркеr);
		llRaising = (LinearLayout) findViewById(R.id.linearLayoutRaising);
		imageVoda = (ImageView) findViewById(R.id.imageVoda);
		scoreTextView = (TextView) findViewById(R.id.textViewScore1);
		llRaisingSub = (LinearLayout) findViewById(R.id.linearLayoutRaisingSub);
		llVasaPotrosnja = (LinearLayout) findViewById(R.id.linearLayoutVasaPotrosnja);
		
		llRaisingSub.setVisibility(View.GONE);
		llVasaPotrosnja.setVisibility(View.INVISIBLE);
		
		tweenManager = new TweenManager();
		
		setTweenEngine();
		startAnimation();

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
			} else if (result.equalsIgnoreCase("ok")) {
				Toast.makeText(GraphicActivity.this, "Uspesno unet rekord", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(GraphicActivity.this, "Nepoznata greska. Ovo ne bi trebalo nikada da se pojavi :). Vidi php", Toast.LENGTH_SHORT).show();
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
	    
	    Tween.to(markerContainer, ViewContainerAccessor.POSITION_XY, 0.5f)
	    .target(0, 100)
	    .ease(Bounce.OUT)
	    .delay(1.0f)
	    .start(tweenManager);
	    
	    Tween.to(vodaContainer, ViewContainerAccessor.POSITION_XY, 0.1f)
	    .target(0, 720)
	    .start(tweenManager);
	    
	    Tween.to(vodaContainer, ViewContainerAccessor.POSITION_XY, 2.5f)
	    .target(0, 100)
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
			llVasaPotrosnja.setVisibility(View.VISIBLE);
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
//                // Create an Intent that will start the main activity.
//                Intent mainIntent = new Intent(SplashScreenActivity.this, BlueTexasHoldEmActivity.class);
//                SplashScreenActivity.this.startActivity(mainIntent);
            	
            	llRaisingSub.setVisibility(View.VISIBLE);
            }
        }, 1000);

	}
}
