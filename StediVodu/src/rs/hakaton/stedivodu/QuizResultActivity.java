package rs.hakaton.stedivodu;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class QuizResultActivity extends Activity {
	
	TextView tvResult;
	TextView tvPercentage;
	Button shareButton;
	String caption;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_qresult);
		
		tvResult = (TextView) findViewById(R.id.textViewResult);
		tvPercentage = (TextView) findViewById(R.id.textViewRPercent);
		shareButton = (Button) findViewById(R.id.buttonShareQResult);
		
		int correctAnswers = getIntent().getIntExtra("correctAnswers", 0);
		
		tvResult.setText(String.format("Vaš učinak je %d od 10", correctAnswers));
		
		int percentage = (int) (correctAnswers / 10f * 100f);
		
		tvPercentage.setText(percentage + "%");
		
		caption = String.format("Moj IQ o vodi je %d%%. Proverite vaš.", percentage);
		
		shareButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(QuizResultActivity.this, ShareActivity.class);
				i.putExtra("social", "facebook");
				i.putExtra("caption", caption);
				startActivity(i);
				finish();
			}
		});
	}
}
