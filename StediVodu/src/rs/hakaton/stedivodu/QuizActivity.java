package rs.hakaton.stedivodu;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class QuizActivity extends Activity {
	
	TextView tvQ1;
	TextView tvQ2;
	TextView tvQ3;
	TextView tvQ4;
	ImageView imageStatus;
	ArrayList<Question> questions;
	int currentQuestion = 0;
	
	String TAG = "QuizActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_quiz);
		
		tvQ1 = (TextView) findViewById(R.id.textViewQ1);
		tvQ2 = (TextView) findViewById(R.id.textViewQ2);
		tvQ3 = (TextView) findViewById(R.id.textViewQ3);
		tvQ4 = (TextView) findViewById(R.id.textViewQ4);
		imageStatus = (ImageView) findViewById(R.id.imageViewStatus);
		
		tvQ1.setOnClickListener(answerClick);
		tvQ2.setOnClickListener(answerClick);
		tvQ3.setOnClickListener(answerClick);
		tvQ4.setOnClickListener(answerClick);
		
		DbAdapter db = new DbAdapter(this);
		db.open();
		questions = db.getQuestions();
		db.close();
		
	}
	
	OnClickListener answerClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			animateButtons(false);
		}
	};
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		
		Animation animationRight = new TranslateAnimation(tvQ2.getWidth()/2, 0, 0, 0);
		animationRight.setDuration(1000);
		animationRight.setFillAfter(true);
		tvQ2.startAnimation(animationRight);
		tvQ4.startAnimation(animationRight);
		
		Animation animationLeft = new TranslateAnimation(-tvQ2.getWidth()/2, 0, 0, 0);
	    animationLeft.setDuration(1000);
	    animationLeft.setFillAfter(true);
	    tvQ1.startAnimation(animationLeft);
	    tvQ3.startAnimation(animationLeft);
	}
	
	private void animateButtons(boolean success) {
		AnimationSet as = new AnimationSet(true);
		as.setFillEnabled(true);
		as.setInterpolator(new BounceInterpolator());

		TranslateAnimation ta = new TranslateAnimation(tvQ1.getWidth(), -tvQ1.getWidth(), 0, 0); 
		ta.setDuration(2000);
		as.addAnimation(ta);

		TranslateAnimation ta2 = new TranslateAnimation(-tvQ1.getWidth(), tvQ1.getWidth(), 0, 0); 
		ta2.setDuration(2000);
		ta2.setStartOffset(2000); // allowing 2000 milliseconds for ta to finish
		as.addAnimation(ta2);
		
		AnimationSet as2 = new AnimationSet(true);
		as2.setFillEnabled(true);
		as2.setInterpolator(new BounceInterpolator());

		TranslateAnimation ta12 = new TranslateAnimation(-tvQ1.getWidth(), tvQ1.getWidth(), 0, 0); 
		ta12.setDuration(2000);
		as2.addAnimation(ta12);

		TranslateAnimation ta22 = new TranslateAnimation(tvQ1.getWidth(), -tvQ1.getWidth(), 0, 0); 
		ta22.setDuration(2000);
		ta22.setStartOffset(2000); // allowing 2000 milliseconds for ta to finish
		as2.addAnimation(ta22);
		
		tvQ1.startAnimation(as);
		tvQ2.startAnimation(as2);
		tvQ3.startAnimation(as);
		tvQ4.startAnimation(as2);
		
		Animation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
		fadeIn.setDuration(1000);

		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
		fadeOut.setStartOffset(1000);
		fadeOut.setDuration(1000);

		AnimationSet animation = new AnimationSet(false); //change to false
		animation.addAnimation(fadeIn);
		animation.addAnimation(fadeOut);
		imageStatus.setAnimation(animation);
	}

}
