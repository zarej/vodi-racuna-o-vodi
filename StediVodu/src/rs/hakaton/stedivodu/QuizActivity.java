package rs.hakaton.stedivodu;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

public class QuizActivity extends Activity {
	
	TextView tvA1;
	TextView tvA2;
	TextView tvA3;
	TextView tvA4;
	TextView tvQuestion;
	TextView tvQuestionNum;
	ArrayList<Question> questions;
	int currentQuestion = -1;
	float defaultFontSize = 16f;
	
	TranslateAnimation taHideLeft;
	TranslateAnimation taHideRight;
	TranslateAnimation taShowLeft;
	TranslateAnimation taShowRight;
	
	boolean a3IsHidden = false;
	boolean a4IsHidden = false;
	
	boolean firstFocusChanged = false;
	boolean isCorrectAnswer = false;
	
	String TAG = "QuizActivity";
	
	int correctAnswers = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_quiz);
		
		tvA1 = (TextView) findViewById(R.id.textViewA1);
		tvA2 = (TextView) findViewById(R.id.textViewA2);
		tvA3 = (TextView) findViewById(R.id.textViewA3);
		tvA4 = (TextView) findViewById(R.id.textViewA4);
		tvQuestion = (TextView) findViewById(R.id.textViewQuestion);
		tvQuestionNum = (TextView) findViewById(R.id.textViewNumber);
		
		tvA1.setOnClickListener(answerClick);
		tvA2.setOnClickListener(answerClick);
		tvA3.setOnClickListener(answerClick);
		tvA4.setOnClickListener(answerClick);
		
		DbAdapter db = new DbAdapter(this);
		db.open();
		questions = db.getQuestions();
		db.close();
		
	}
	
	OnClickListener answerClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			int answer = 0;
			
			if (v.equals(tvA1)) {
				answer = 1;
			} else if (v.equals(tvA2)) {
				answer = 2;
			} else if (v.equals(tvA3)) {
				answer = 3;
			} else if (v.equals(tvA4)) {
				answer = 4;
			}
			
			
			if (questions.get(currentQuestion).correctAnswer == answer){
				isCorrectAnswer = true;
				correctAnswers++;
			} else isCorrectAnswer = false;
			
			animateHideButtons();
		}
	};

	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		
		if (!firstFocusChanged) {
			setupAnimations();
		    animateShowButtons();
		    firstFocusChanged = true;		    
		}
	    
	}
		
	private void setupAnimations() {
		taShowRight = new TranslateAnimation(tvA2.getWidth(), 0, 0, 0);
		taShowRight.setDuration(1000);
		taShowRight.setFillAfter(true);
		
		taShowLeft = new TranslateAnimation(-tvA2.getWidth(), 0, 0, 0);
		taShowLeft.setDuration(1000);
		taShowLeft.setFillAfter(true);
		
		taHideLeft = new TranslateAnimation(0, -tvA2.getWidth(), 0, 0);
		taHideLeft.setDuration(1000);
		taHideLeft.setFillAfter(true);
		taHideLeft.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Pozovi dijalog
				// 1. Instantiate an AlertDialog.Builder with its constructor
				AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);

				// 2. Chain together various setter methods to set the dialog characteristics
				String title = isCorrectAnswer? "Bravo, odgovor je tačan :)" : "Odgovor nije tačan :(";
				builder.setMessage(questions.get(currentQuestion).opis)
				       .setTitle(title);
				
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // User clicked OK button
			        	   animateShowButtons();
			           }
			       });

				// 3. Get the AlertDialog from create()
				AlertDialog dialog = builder.create();
				
				dialog.show();
			}
		});
		
		taHideRight = new TranslateAnimation(0, tvA2.getWidth(), 0, 0);
		taHideRight.setDuration(1000);
		taHideRight.setFillAfter(true);
	}
	
	private void animateHideButtons() {
		tvA1.startAnimation(taHideLeft);
		tvA2.startAnimation(taHideRight);
		if (!a3IsHidden) {
			Log.d(TAG, "Sakrivam A3");
			tvA3.startAnimation(taHideLeft);
			a3IsHidden = true;
		}
		if (!a4IsHidden) {
			Log.d(TAG, "Sakrivam A4");
			tvA4.startAnimation(taHideRight);
			a4IsHidden = true;
		}
	}
	
	private void animateShowButtons() {
		
		currentQuestion++;
		if(currentQuestion == 10) {
			
			Intent intent = new Intent(this, QuizResultActivity.class);
			intent.putExtra("correctAnswers", correctAnswers);
			startActivity(intent);
			finish();
			return;
		}
		setupQuestion();
		
		int numberOfAnswers = questions.get(currentQuestion).tip;
		
		if (numberOfAnswers<2) numberOfAnswers = 2;
		
		Log.d(TAG, "Broj odgovora=" + numberOfAnswers);
		
		
		tvQuestionNum.setText("Pitanje " + (currentQuestion + 1) + " od 10");
		
		tvA1.startAnimation(taShowLeft);
		tvA2.startAnimation(taShowRight);
		
		if (numberOfAnswers == 3) {
			tvA3.startAnimation(taShowLeft);
			a3IsHidden = false;
		} else if (numberOfAnswers == 4) {
			tvA3.startAnimation(taShowLeft);
			a3IsHidden = false;
			tvA4.startAnimation(taShowRight);
			a4IsHidden = false;
		}
		
		Log.d(TAG, "Pitanje 3 je skriveno==" + a3IsHidden);
		Log.d(TAG, "Pitanje 4 je skriveno==" + a4IsHidden);
	}
	
	private void setupQuestion() {
		Question question = questions.get(currentQuestion);
		
		tvQuestion.setText(question.question);
		tvA1.setText(question.answer1);
		tvA2.setText(question.answer2);
		tvA3.setText(question.answer3);
		tvA4.setText(question.answer4);
		
		//podesavanje velicine fonta za odredjena pitanja, pitanja pocinju od 0
		if (currentQuestion == 1) {
			tvA1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f);
		} else {
			tvA1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, defaultFontSize);
		}
		
	}


}
