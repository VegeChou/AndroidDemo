package com.zwz.bluetoothdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	private TextView titleTextView = null;
	private EditText contentEditText = null;
	private Button confirmButton = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		initCtrl();
	}

	private void initCtrl() {
		titleTextView = (TextView) findViewById(R.id.tv_title);
		contentEditText = (EditText) findViewById(R.id.et_content);
		confirmButton = (Button) findViewById(R.id.btn_confirm);
		confirmButton.setOnClickListener(myClickListener);
	}
	
	private OnClickListener myClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_confirm:
				
				break;

			default:
				break;
			}
			
		}
	};
}
