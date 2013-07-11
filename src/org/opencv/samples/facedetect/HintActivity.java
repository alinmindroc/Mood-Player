package org.opencv.samples.facedetect;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class HintActivity extends Activity {

	public HintActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup);

		final RadioButton rb5 = (RadioButton) findViewById(R.id.radio5sec);
		final RadioButton rb15 = (RadioButton) findViewById(R.id.radio15sec);
		final RadioButton rb60 = (RadioButton) findViewById(R.id.radio60sec);

		
		if(PlayerViewDemoActivity.faceDetectThreshold == 5000){
			rb5.setChecked(true);
			rb15.setChecked(false);
			rb60.setChecked(false);
		}
		
		if(PlayerViewDemoActivity.faceDetectThreshold == 15000){
			rb5.setChecked(false);
			rb15.setChecked(true);
			rb60.setChecked(false);
		}
		
		if(PlayerViewDemoActivity.faceDetectThreshold == 60000){
			rb5.setChecked(false);
			rb15.setChecked(false);
			rb60.setChecked(true);
		}

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (rb5.isChecked())
					PlayerViewDemoActivity.faceDetectThreshold = 5000;
				if (rb15.isChecked())
					PlayerViewDemoActivity.faceDetectThreshold = 15000;
				if (rb60.isChecked())
					PlayerViewDemoActivity.faceDetectThreshold = 60000;
			}
		});

	}
}
