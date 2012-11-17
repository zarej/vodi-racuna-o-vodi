package rs.hakaton.stedivodu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class InputActivity extends Activity {
	
	RadioGroup radioGroup;
	Button submit;
	EditText editText;
	ImageView imageHouse;
	ImageView imageFlat;
	RadioButton defaultSelectedRadio;
	RadioButton selectedRadioButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_input);
		
		radioGroup = (RadioGroup) findViewById(R.id.radioSex);
		submit = (Button) findViewById(R.id.buttonSubmit);
		editText = (EditText) findViewById(R.id.editText1);
		imageHouse = (ImageView) findViewById(R.id.imageKuca);
		imageFlat = (ImageView) findViewById(R.id.imageZgrada);
		defaultSelectedRadio = (RadioButton) findViewById(R.id.radioVoda);
		
		defaultSelectedRadio.setChecked(true);		
		selectedRadioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
		
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (formIsValid()) {
					RadioButton selectedRadioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
					Intent i = new Intent(InputActivity.this, GraphicActivity.class);
					User.tipRacuna = (String) selectedRadioButton.getText();
					User.racun = Float.parseFloat(editText.getText().toString());
					startActivity(i);
					finish();
				} else {
					Toast.makeText(InputActivity.this, "Popunite sva polja", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
		imageHouse.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				v.setSelected(true);
				imageFlat.setSelected(false);
				User.jeKuca = true;			
			}
		});
		
		imageFlat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				v.setSelected(true);
				imageHouse.setSelected(false);
				User.jeKuca = false;
			}
		});
		
	}
	
	private boolean formIsValid() {
		
		if (!imageHouse.isSelected() && !imageFlat.isSelected()) return false;
		if (editText.getText().toString().equals("")) return false;		
		return true;
	}
}
