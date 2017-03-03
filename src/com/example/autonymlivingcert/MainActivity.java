package com.example.autonymlivingcert;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.gzt.faceid5sdk.DetectionAuthentic;
import com.gzt.faceid5sdk.listener.ResultListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oliveapp.face.livenessdetectorsdk.utilities.algorithms.DetectedRect;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autonymlivingcert.R;
import com.example.http.IHttpRequestUtils;
import com.example.utils.Loger;

public class MainActivity extends Activity {
	private ActionBar actionBar;
	private ImageView main_return;
	private TextView title_name;
	
	private Button activatebutton; 
	private Button repealbutton;
	private EditText phoneEdit;
	private EditText iccidEdit;
	private RelativeLayout phoneerrImg;
	private RelativeLayout icciderrImg;
	
	private String phonenum;
	private String iccidcode;
	public static List<JSONObject> jsonObjList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);
        
		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
        activatebutton = (Button)findViewById(R.id.activate);
        repealbutton = (Button)findViewById(R.id.repeal);
        phoneEdit = (EditText)findViewById(R.id.et_phone);
        iccidEdit = (EditText)findViewById(R.id.et_iccid);
        phoneerrImg = (RelativeLayout)findViewById(R.id.iv_phone);
        icciderrImg = (RelativeLayout)findViewById(R.id.iv_iccid);
        
        icciderrImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				iccidEdit.setText("");
				icciderrImg.setVisibility(View.GONE);
				iccidcode="";
			}
		});
        phoneerrImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				phoneEdit.setText("");
				phoneerrImg.setVisibility(View.GONE);
				phonenum="";
			}
		});
        phoneEdit.addTextChangedListener(new TextWatcher() {
    		@Override
    		public void onTextChanged(CharSequence s, int start, int before, int count) {
    			// TODO Auto-generated method stub
    			activatebutton.setEnabled(false);
    			phoneerrImg.setVisibility(View.GONE);
    		}
    		
    		@Override
    		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
 					int arg3) {
    			// TODO Auto-generated method stub
    		}
    		
    		@Override
    		public void afterTextChanged(Editable arg0) {
    			// TODO Auto-generated method stub
    			iccidEdit.setText("");
				icciderrImg.setVisibility(View.GONE);
				iccidcode="";//手机号更改卡号重输
				
				phonenum="";
				phoneerrImg.setVisibility(View.GONE);
    			if (arg0.toString().length() >= 11 ) {//
//    				Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,3,5-9]))\\d{8}$");  
//    				Matcher m = p.matcher(arg0.toString());  
//    				if(m.matches()){
    					phonenum = arg0.toString();
//    					Loger.i("TEST", "phonenum="+phonenum);
//    				}else{
//    					phoneerrImg.setVisibility(View.VISIBLE);
//    				}
    			
     		    }
     		    else if (arg0.toString().length() == 0) {
     		    	phoneerrImg.setVisibility(View.GONE);
     		    }
    		}
    	});
        iccidEdit.addTextChangedListener(new TextWatcher() {
        	int l=0;////////记录字符串被删除字符之前，字符串的长度
	  		int location=0;//记录光标的位置
    		@Override
    		public void onTextChanged(CharSequence s, int start, int before, int count) {
    			// TODO Auto-generated method stub
    			activatebutton.setEnabled(false);
    			icciderrImg.setVisibility(View.GONE);
    		}
    		
    		@Override
    		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
 					int arg3) {
    			// TODO Auto-generated method stub
    			l=arg0.length();
     		    location=phoneEdit.getSelectionStart();
     		   Loger.i("TEST", "l="+l);
    		}
    		
    		@Override
    		public void afterTextChanged(Editable arg0) {
    			// TODO Auto-generated method stub
    			if (arg0.toString().length() ==8) {//8位卡号
    				Pattern p = Pattern.compile("^\\d{7}[A-Z]$"); 
    				Matcher m = p.matcher(arg0.toString());  
    				if(m.matches()){
    					iccidcode = arg0.toString();
    					if(phonenum.length()==11 || phonenum.length()==13){
    						Loger.i("TEST", "phonenum="+phonenum);
    						Loger.i("TEST", "iccidcode="+iccidcode);
    						gethttpdata(phonenum,iccidcode,1);
    					}else{
    						phoneerrImg.setVisibility(View.VISIBLE);
    					}
    				}else{
    					icciderrImg.setVisibility(View.VISIBLE);
    				}
     		    }else if(arg0.toString().length() < 8 && l >arg0.toString().length()){
     		    	icciderrImg.setVisibility(View.GONE);
     		    }
     		    else if (arg0.toString().length() == 0) {
     		    	icciderrImg.setVisibility(View.GONE);
     		    }
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
        title_name.setText("开通激活");
        main_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MainActivity.this.finish();
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
    
    public void doactivate(View view) {
		Intent intentMain = new Intent(MainActivity.this, AfteractivActivity.class);
		intentMain.putExtra("iccid", iccidcode);
		intentMain.putExtra("phone", phonenum);
		MainActivity.this.startActivity(intentMain); 
		MainActivity.this.finish();
	}
    private void gethttpdata(String contactPhone,String iccid,int request_index){
		jsonObjList = new ArrayList<JSONObject>();
		RequestParams params = new RequestParams();
		AsyncHttpClient client = new AsyncHttpClient();	
		client.setTimeout(40000);
//		client.addHeader("Content-Type", "application/json");
		params.put("mark","testIccid");
		params.put("contactPhone",contactPhone);
		params.put("iccid",iccid);
		
		Loger.i("TEST","gethttpdata" );
		client.post(IHttpRequestUtils.SRV_URL+IHttpRequestUtils.OCR_DIR, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] response,
					Throwable arg3) {
				// TODO Auto-generated method stub
				if(response.length != 0){
					Loger.i("TEST", "onFailure="+new String(response));
				}else{
					Loger.i("TEST", "onFailure="+null);
				}
				
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] response) {
				// TODO Auto-generated method stub

				try {
					org.json.JSONObject jsonContext = new JSONObject(new String(response));
					org.json.JSONObject jsondataSet = jsonContext.getJSONObject("result").getJSONObject("dataSet");
					Loger.i("TEST", "dataSet="+jsondataSet);
					if(jsondataSet.get("code").toString().equals("200")){
						activatebutton.setEnabled(true);
					}else{
						Loger.i("TEST", "code= "+jsondataSet.get("code").toString());
						Toast toast = Toast.makeText(getApplicationContext(),
								jsondataSet.get("msg").toString(), Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}  
            
        });  
//		client.post(IHttpRequestUtils.SRV_URL+IHttpRequestUtils.OCR_DIR,params, new JsonHttpResponseHandler() {
//			@Override
//			public void onSuccess(int statusCode,
//					org.apache.http.Header[] headers,
//					org.json.JSONObject response) {
//				// TODO Auto-generated method stub
//				org.json.JSONObject jsonContext = response;
//				org.json.JSONObject jsondataSet;
//				try {
//					jsondataSet = response.getJSONObject("result").getJSONObject("dataSet");
//					Loger.i("TEST", "dataSet="+jsondataSet);
//					if(jsondataSet.get("code").toString().equals("200")){
//						activatebutton.setEnabled(true);
//					}else{
//						Loger.i("TEST", "code= "+jsondataSet.get("code").toString());
//						Toast toast = Toast.makeText(getApplicationContext(),
//								jsondataSet.get("msg").toString(), Toast.LENGTH_LONG);
//						toast.setGravity(Gravity.CENTER, 0, 0);
//						toast.show();
//					}
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				
//				super.onSuccess(statusCode, headers,response);
//			}
//			@Override
//			public void onSuccess(int statusCode,
//					org.apache.http.Header[] headers,
//					org.json.JSONArray response) {
//				// TODO Auto-generated method stub
//				org.json.JSONArray jsonContext = response;
//				Loger.i("TEST", "JSONArray="+jsonContext.toString());
//				super.onSuccess(statusCode, headers, response);
//			}
//			@Override
//			public void onFailure(int statusCode,
//					org.apache.http.Header[] headers,
//					String responseString,
//					Throwable throwable) {
//				// TODO Auto-generated method stub
//				Loger.i("TEST", "onFailure="+responseString);
//				super.onFailure(statusCode, headers,
//						responseString, throwable);
//			}
//		});
	}

}
