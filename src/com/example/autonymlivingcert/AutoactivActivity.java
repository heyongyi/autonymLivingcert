package com.example.autonymlivingcert;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.Window;

import com.example.utils.Loger;
import com.gzt.faceid5sdk.DetectionAuthentic;
import com.gzt.faceid5sdk.listener.ResultListener;
import com.example.http.IHttpRequestUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.oliveapp.face.livenessdetectorsdk.utilities.algorithms.DetectedRect;

public class AutoactivActivity extends Activity implements ResultListener{
	
private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        intent = this.getIntent();
        int mode = intent.getIntExtra("mode", 0);
        if(mode==2){
        	String idCode = intent.getStringExtra("idCode");
        	String name = intent.getStringExtra("name");
        	//人脸拍照组件 API： 
        	DetectionAuthentic authentic = DetectionAuthentic.getInstance(this,this);
        	authentic.autenticateToCaptureAction(this, idCode, name);
        }else{
        	//身份证拍照组件 API： 
            DetectionAuthentic authentic = DetectionAuthentic.getInstance(this,this);
            authentic.autenticateToCaptureIdCard (this,mode);
        }
        Loger.i("TEST", "AutoactivActivity onCreate");
        

    }
    
	@Override
	public void onFaceImageCaptured(byte[] arg0) {
		// TODO Auto-generated method stub
		intent.putExtra("mapstring",arg0 );
		AutoactivActivity.this.setResult(RESULT_OK, intent);
		AutoactivActivity.this.finish();
	}

	@Override
	public void onIDCardImageCaptured(byte[] arg0, DetectedRect arg1) {
		// TODO Auto-generated method stub

		intent.putExtra("mapstring",arg0 );
		AutoactivActivity.this.setResult(RESULT_OK, intent);
		AutoactivActivity.this.finish();
	}

	@Override
	public void onSDKUsingFail(String arg0, String arg1) {
		// TODO Auto-generated method stub
		intent.putExtra("errorstring",arg0 );
		AutoactivActivity.this.setResult(RESULT_CANCELED, intent);
		AutoactivActivity.this.finish();
	}

}
