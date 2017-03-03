package com.example.autonymlivingcert;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FailedActivity extends Activity{
	
	private ActionBar actionBar;
	private ImageView main_return;
	private TextView title_name;
	
	private TextView failedtext;
	private Button failedbutton;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.failedactivity);
        
        actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		failedtext = (TextView)findViewById(R.id.failedtext);
		failedbutton = (Button)findViewById(R.id.failedbutton);
		
		failedbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				FailedActivity.this.finish();
			}
		});
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		@SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth();
		@SuppressWarnings("deprecation")
		int height = wm.getDefaultDisplay().getHeight();
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.titlebar, menu);
        RelativeLayout SearchGroup = (RelativeLayout) menu.findItem(
        		R.id.main_function).getActionView();
        main_return = (ImageView)SearchGroup.findViewById(R.id.main_back);
        title_name = (TextView)SearchGroup.findViewById(R.id.title_name);
        title_name.setText("º§ªÓ ß∞‹");
        main_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				FailedActivity.this.finish();
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
}
