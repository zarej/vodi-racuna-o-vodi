package rs.hakaton.stedivodu;

import java.net.URLEncoder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class ShareActivity extends Activity {
	
	WebView webView;
	
	String shareLink = "http://minimaldevelop.com/blog/vodi-racuna-o-vodi/";
	String picture = "http://minimaldevelop.com/blog/wp-content/uploads/2012/11/icon.jpg";
	String name = "Vodi računa o vodi";
	String caption = "Ovo je testna verzija aplikacije";
	String description = "Molimo Vas da isprobate i napišete komentar ukoliko imate nekih primedbi ili znate kako da unapredimo aplikaciju.";
	String facebookUrl;
	String twitterUrl;

	private String TAG = "ShareActivity";
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_share);
		
		String socialCall = getIntent().getStringExtra("social");
		
		if (socialCall != null) {
			String captionExtra = getIntent().getStringExtra("caption");
			if (captionExtra != null) caption = captionExtra;
		}
		
		caption = URLEncoder.encode(caption);
		name = URLEncoder.encode(name);
		description = URLEncoder.encode(description);
		shareLink = URLEncoder.encode(shareLink);
		
		Button facebookButton = (Button) findViewById(R.id.buttonFacebook);
		Button twitterButton = (Button) findViewById(R.id.buttonTwitter);
		Button zavrsiButton = (Button) findViewById(R.id.buttonZavrsi);
		
		facebookButton.setOnClickListener(fbButtonClick);
		twitterButton.setOnClickListener(twButtonClick);
		zavrsiButton.setOnClickListener(zaButtonClick);
		
		webView = (WebView) findViewById(R.id.webView1);
		webView.setWebViewClient(new Callback());  //HERE IS THE MAIN CHANGE
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setBackgroundColor(0x00000000); 
		
		facebookUrl = "http://www.facebook.com/dialog/feed?" +
				"app_id=195756400549497&" +
				"link=" + shareLink + "&" +
				"picture=" + picture + "&" +
				"name=" + name + "&" +
				"caption=" + caption + "&" +
				"description=" + description + "&" +
				"redirect_uri=http://minimaldevelop.com";
		
		twitterUrl = "https://twitter.com/intent/tweet?" +
				"text=" + caption + "&" + 
				"url=" + shareLink;

		if (socialCall != null) {
			if (socialCall.equals("facebook")) {			
				webView.loadUrl(facebookUrl);
			} else if (socialCall.equals("twitter")) {			
				webView.loadUrl(twitterUrl);
			}
		}
        
	}
	
	private class Callback extends WebViewClient{  //HERE IS THE MAIN CHANGE. 

		ProgressDialog _dialog;
		boolean loadingFinished = true;
		boolean redirect = false;		
		
		   @Override
		   public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
		       if (!loadingFinished) {
		          redirect = true;
		       }

		   loadingFinished = false;
		   webView.loadUrl(urlNewString);
		   return true;
		   }

		   @Override
		   public void onPageStarted(WebView view, String url, Bitmap favicon) {
		        loadingFinished = false;
		        Log.d(TAG , "Load je poceo");
		        //SHOW LOADING IF IT ISNT ALREADY VISIBLE  
		        if (_dialog==null)
		        _dialog = ProgressDialog.show(ShareActivity.this, "", "Učitavanje...");
		    }

		   @Override
		   public void onPageFinished(WebView view, String url) {
		       if(!redirect){
		          loadingFinished = true;
		          Log.d(TAG , "Load je zavrsen");
		       }

		       if(loadingFinished && !redirect){
		         //HIDE LOADING IT HAS FINISHED
		    	   _dialog.dismiss();  
		    	   _dialog=null;
		       } else{
		          redirect = false; 
		       }
		    }
    }
	
	OnClickListener fbButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			webView.loadUrl(facebookUrl);
		}
	};
	
	OnClickListener twButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			webView.loadUrl(twitterUrl);
		}
	};
	
	OnClickListener zaButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	};
}
