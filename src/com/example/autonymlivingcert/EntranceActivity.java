package com.example.autonymlivingcert;

import com.example.utils.Loger;
import com.gzt.faceid5sdk.DetectionAuthentic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class EntranceActivity extends Activity {
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.entranceactivity);
	 }
	 
	 public void todoactivate(View view) {
			Loger.i("TEST","todoactivate");
			Intent intentMain = new Intent(EntranceActivity.this, MainActivity.class);
			EntranceActivity.this.startActivity(intentMain);
		}
}
