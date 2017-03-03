package com.example.autonymlivingcert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autonymlivingcert.R;
import com.example.http.IHttpRequestUtils;
import com.example.utils.Base64Util;
import com.example.utils.Loger;
import com.example.utils.Md5Check;
import com.example.utils.util;
import com.gzt.faceid5sdk.DetectionAuthentic;


public class AfteractivActivity extends Activity implements OnClickListener{
	private Button frontscanbutton; 
	private Button backscanbutton;
	private Button nextfacescan;
	private ImageView frontimg;
	private ImageView backimg;
	private TextView ruletext;
//	private ImageView front_ok;
//	private ImageView back_ok;
//	private ImageView front_fail;
//	private ImageView back_fail;
	private Drawable draw_up_ok;
	private Drawable draw_up_fail;
	
	private ProgressBar front_progress;
	private ProgressBar back_progress;
	
	private final int R_CODE_F = 10001;
	private final int R_CODE_B = 10002;
	private final int R_ACTION = 10003;
	private final int R_CAMERA_F = 10004;
	private final int R_CAMERA_B = 10005;
	
	private byte[] front_map;
	private String front_string;
	private byte[] back_map;
	private String back_string;
	private byte[] face_map;
	private String face_string;
	private int front_status = 0;//0:初始状态   1:返回成功  2:返回失败
	private int back_status = 0;
	private boolean check_status = false;
	
	public static List<JSONObject> jsonObjList;
	private Intent intent;
	private static String iccid;
	private static String phone;
	private String idCode;
	private String realName;
	
	private ActionBar actionBar;
	private ImageView main_return;
	private TextView title_name;
	private ProgressDialog pd1;
	private PopupWindow popupdealWindow;
	private View popupdealView;
	private LinearLayout topLinearLayout;
	private Button fromphone;
	private Button fromcamera;
	private Button choosecancel;
	void buildprocessdialog(){
		pd1 = new ProgressDialog(this);
		pd1.setMessage("正在上传，请稍侯...");
		pd1.setCancelable(false);
		pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd1.setIndeterminate(false);
	} 
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.afteractivactivity);
		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		
        frontscanbutton = (Button)findViewById(R.id.frontscan);
        backscanbutton = (Button)findViewById(R.id.backscan);
        frontimg = (ImageView)findViewById(R.id.iv_front);
        backimg = (ImageView)findViewById(R.id.iv_back);
        ruletext = (TextView)findViewById(R.id.tc_rule);
        nextfacescan = (Button)findViewById(R.id.bt_nextfacescan);
        draw_up_ok = getResources().getDrawable(R.drawable.uploadok);
        draw_up_fail = getResources().getDrawable(R.drawable.uploadfail);
        
        front_progress = (ProgressBar)findViewById(R.id.front_progress);
        back_progress = (ProgressBar)findViewById(R.id.back_progress);
        draw_up_ok.setBounds(20, 0, 65, 45);
        draw_up_fail.setBounds(20, 0, 65, 45);
        Spanned spanInfo = Html.fromHtml("<font color='#555555'>根据</font><font color='#f25f5f'>国家实名制</font><font color='#555555'>要求，请您上传身份证照片，我们将保障您的信息安全。</font>");
        ruletext.setText(spanInfo);
        intent = this.getIntent();
        try {
			iccid = intent.getStringExtra("iccid");
			phone = intent.getStringExtra("phone");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        buildprocessdialog();
        
        popupdealView =this.getLayoutInflater().inflate(R.layout.identifyoperation, null);
		topLinearLayout = (LinearLayout) popupdealView.findViewById(R.id.photo_choose);
		popupdealView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupdealWindow.dismiss();
			}
		});
		fromphone = (Button)popupdealView.findViewById(R.id.fromphone);
		fromcamera =  (Button)popupdealView.findViewById(R.id.fromcamera);
		choosecancel = (Button)popupdealView.findViewById(R.id.choosecancel);
		fromphone.setOnClickListener(this);
		fromcamera.setOnClickListener(this);
		choosecancel.setOnClickListener(this);
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.choosecancel){
			popupdealWindow.dismiss();
		}
	}
	private void setPopupWindow(int tag) {
		popupdealWindow = new PopupWindow(popupdealView,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ColorDrawable cd = new ColorDrawable(0x90000000);
		popupdealWindow.setBackgroundDrawable(cd);
		TranslateAnimation anim = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0f, Animation.RELATIVE_TO_PARENT, 1f,
				Animation.RELATIVE_TO_PARENT, 0f);
		popupdealWindow.setFocusable(true);
		anim.setDuration(500);
		topLinearLayout.setAnimation(anim);
		popupdealWindow.showAtLocation(this.getWindow()
				.getDecorView(), Gravity.BOTTOM, 0, 0);
		popupdealWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
		if(tag == 1){//正面
			
			fromphone.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					
					Intent intentMain = new Intent(AfteractivActivity.this, AutoactivActivity.class);
					intentMain.putExtra("mode", DetectionAuthentic.CAPTURE_MODE_IDCARD_FRONT);
					AfteractivActivity.this.startActivityForResult(intentMain, R_CODE_F);
				}
			});
			fromcamera.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					
					// TODO Auto-generated method stub
					Intent intent = new Intent(Intent.ACTION_PICK);
			        intent.setType("image/*");//相片类型
			        startActivityForResult(intent, R_CAMERA_F);
				}
			});
			
		}else{
			fromphone.setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					
					
					Intent intentMain = new Intent(AfteractivActivity.this, AutoactivActivity.class);
					intentMain.putExtra("mode", DetectionAuthentic.CAPTURE_MODE_IDCARD_BACK);
					AfteractivActivity.this.startActivityForResult(intentMain, R_CODE_B);//身份证背面
				}
			});
			fromcamera.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Intent.ACTION_PICK);
			        intent.setType("image/*");//相片类型
			        startActivityForResult(intent, R_CAMERA_B);
				}
			});
		}
		
	}
	public void dofrontscan(View view) {
		Loger.i("TEST","dofrontscan");
		setPopupWindow(1);
		
		
		
	}
	public void dobackscan(View view) {
		Loger.i("TEST","dobackscan");
		setPopupWindow(2);
		
		
		
	}
	public void donextface(View view){
		Loger.i("TEST","donextface");
		Intent intentMain = new Intent(AfteractivActivity.this, AutoactivActivity.class);
		intentMain.putExtra("mode", 2);
		intentMain.putExtra("idCode", idCode);
		intentMain.putExtra("name", realName);
		AfteractivActivity.this.startActivityForResult(intentMain, R_ACTION);//身份证背面
		
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
        title_name.setText("卡号激活");
        main_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AfteractivActivity.this.finish();
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==R_CODE_F){
			frontscanbutton.setText("拍攝头像所在面");
			if(resultCode==RESULT_OK){
				popupdealWindow.dismiss();
				frontscanbutton.setCompoundDrawables(null, null, null, null);
				front_progress.setVisibility(View.INVISIBLE);
				nextfacescan.setEnabled(false);
				front_status = 0;
				Loger.i("TEST","R_CODE_F   "+"RESULT_OK" );
				front_progress.setVisibility(View.VISIBLE);
				front_map = data.getByteArrayExtra("mapstring");
				final Bitmap bm = BitmapFactory.decodeByteArray(front_map, 0, front_map.length);
				frontimg.setImageBitmap(bm);
//				front_string = Base64Util.getBase64(front_map);
				try {  
                	uploadFile(bm,1);  
                }catch (IOException e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
                }  
			}else if(resultCode==RESULT_CANCELED){
				
			}
		}else if(requestCode==R_CODE_B){
			backscanbutton.setText("拍摄国徽所在面");
			if(resultCode==RESULT_OK){
				popupdealWindow.dismiss();
				back_status = 0;
				nextfacescan.setEnabled(false);
				backscanbutton.setCompoundDrawables(null, null, null, null);
				back_progress.setVisibility(View.INVISIBLE);
				
				Loger.i("TEST","R_CODE_B     "+"RESULT_OK" );
				back_progress.setVisibility(View.VISIBLE);
				back_map = data.getByteArrayExtra("mapstring");
				Bitmap bm = BitmapFactory.decodeByteArray(back_map, 0, back_map.length);
				backimg.setImageBitmap(bm);
//				back_string = Base64Util.getBase64(back_map);
				try {  
                	uploadFile(bm,2);  
                }catch (IOException e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
                } 
			}else if(resultCode==RESULT_CANCELED){
				
			}
		}else if(requestCode==R_ACTION){
			if(resultCode==RESULT_OK){
				Loger.i("TEST","R_ACTION     "+"RESULT_OK" );
				face_map = data.getByteArrayExtra("mapstring");
				face_string = Base64Util.getBase64(face_map); 
				pd1.show();
				try {  
                	uploadFile(face_string,3);  
                }catch (IOException e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
                }   
			}else if(resultCode==RESULT_CANCELED){
				Toast toast = Toast.makeText(getApplicationContext(),
						data.getStringExtra("errorstring"), Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		}else if(requestCode==R_CAMERA_F){
			if(resultCode==RESULT_OK){
				frontscanbutton.setText("拍攝头像所在面");
				popupdealWindow.dismiss();
				frontscanbutton.setCompoundDrawables(null, null, null, null);
				front_progress.setVisibility(View.INVISIBLE);
				nextfacescan.setEnabled(false);
				front_status = 0;
				
				try {
					Uri uri = data.getData();
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
					front_progress.setVisibility(View.VISIBLE);
					frontimg.setImageBitmap(bitmap);
					try {  
	                	uploadFile(bitmap,1);  
	                }catch (IOException e) {  
	                    // TODO Auto-generated catch block  
	                    e.printStackTrace();  
	                }  
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}else if(requestCode==R_CAMERA_B){
			
			if(resultCode==RESULT_OK){
				backscanbutton.setText("拍摄国徽所在面");
				popupdealWindow.dismiss();
				backscanbutton.setCompoundDrawables(null, null, null, null);
				back_progress.setVisibility(View.INVISIBLE);
				nextfacescan.setEnabled(false);
				back_status = 0;
				try {
					Uri uri = data.getData();
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
					back_progress.setVisibility(View.VISIBLE);
					backimg.setImageBitmap(bitmap);
					try {  
	                	uploadFile(bitmap,2);  
	                }catch (IOException e) {  
	                    // TODO Auto-generated catch block  
	                    e.printStackTrace();  
	                }  
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	
	public  String uploadFile(final Bitmap pic ,final int request_index) throws IOException{  
        //使用HttpClient  
        new Thread(){  
            public void run() { 
            	Loger.i("TEST", "****iccid="+iccid);
            	String path=IHttpRequestUtils.SDPATH+System.currentTimeMillis()+".jpg";
        		File fileDir=new File(IHttpRequestUtils.SDPATH+"");
        		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
        			fileDir.mkdir();
        		}
        		File file = new File(path);
        		try {
        			FileOutputStream out = new FileOutputStream(file);
        			pic.compress(Bitmap.CompressFormat.JPEG, 100,out);
        			out.flush();
        			out.close();
        		} catch (FileNotFoundException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		
        		
            	HttpClient httpClient=new DefaultHttpClient();  
                //必须设置请求的协议  
                httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,HttpVersion.HTTP_1_1);  
                //设置http post请求  

                HttpPost  httpPost=new HttpPost(IHttpRequestUtils.SRV_URL+IHttpRequestUtils.OCR_DIR);  

                //构建上传的文件的实体  

                MultipartEntity  multipartEntity=new MultipartEntity();  

                //构建文件的File的对象  
                //添加文件的  
                ContentBody contentBody=new FileBody(file);  
                multipartEntity.addPart("uploadFile",contentBody);//<input type="file" name="file">  
                
                try {
                	multipartEntity.addPart("iccid",new StringBody(iccid));
                    multipartEntity.addPart("contactPhone",new StringBody(phone));
                	if(request_index==1){	
                    	multipartEntity.addPart("mark",new StringBody("testIdentity"));
                    	multipartEntity.addPart("face",new StringBody("front"));
                	}else if(request_index==2){
                		multipartEntity.addPart("mark",new StringBody("testIdentity"));
                    	multipartEntity.addPart("face",new StringBody("back"));
                	}
                	
        		} catch (UnsupportedEncodingException e1) {
        			// TODO Auto-generated catch block
        			e1.printStackTrace();
        		}

                //把请求实体设置到HttpPost  
                httpPost.setEntity(multipartEntity);  
                Loger.i("TEST", "1****iccid="+iccid);
                //执行这个请求  
                HttpResponse response;
				try {
					response = httpClient.execute(httpPost);
					//通过响应取到状态行  
	                StatusLine statusLine= response.getStatusLine();  

	                //通过状态码去判断  

	                if(statusLine.getStatusCode()==HttpStatus.SC_OK){  
	                	util.delete(fileDir);
	                	
	                         HttpEntity  entity=response.getEntity();  
	                         String result=EntityUtils.toString(entity);  
	                         Loger.i("TEST","*******"+result); 
	                         try {
								JSONObject resultjson = new JSONObject(result);
								final String resultmsg = resultjson.getJSONObject("result").getJSONObject("dataSet").get("msg").toString();
								String resultcode = resultjson.getJSONObject("result").getJSONObject("dataSet").get("code").toString();
								String resultface = resultjson.getJSONObject("pageData").getJSONObject("pf").get("face").toString();
								if(resultcode.equals("200")){
									if(resultface.equals("front")){
										front_status = 1;
										
									}else if(resultface.equals("back")){
										back_status = 1;
									}
								}else{
									if(resultface.equals("front")){
										front_status = 2;
										
									}else if(resultface.equals("back")){
										back_status = 2;
									}
								}
									runOnUiThread(new Runnable()    
							        {    
							            public void run()    
							            {    
							            	if(front_status==1 && back_status==1){
							            		nextfacescan.setEnabled(true);
							            	}
							            	if(front_status==1){
							            		
							            		frontscanbutton.setCompoundDrawables(draw_up_ok, null, null, null);
							            		front_progress.setVisibility(View.INVISIBLE);
							            	}else if(front_status==2){
							            		frontscanbutton.setCompoundDrawables(draw_up_fail, null, null, null);
							            		front_progress.setVisibility(View.INVISIBLE);
							            		Toast toast = Toast.makeText(getApplicationContext(),
							            				resultmsg, Toast.LENGTH_LONG);
												toast.setGravity(Gravity.CENTER, 0, 0);
												toast.show();
												frontscanbutton.setText("上传失败，请重新上传");
							            	}
							            	if(back_status==1){
							            		backscanbutton.setCompoundDrawables(draw_up_ok, null, null, null);
							            		back_progress.setVisibility(View.INVISIBLE);
							            	}else if(back_status==2){
							            		backscanbutton.setCompoundDrawables(draw_up_fail, null, null, null);
							            		back_progress.setVisibility(View.INVISIBLE);
							            		Toast toast = Toast.makeText(getApplicationContext(),
							            				resultmsg, Toast.LENGTH_LONG);
												toast.setGravity(Gravity.CENTER, 0, 0);
												toast.show();
												backscanbutton.setText("上传失败，请重新上传");
							            	}
							            	
							            }    
							        });   								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                }else{  
	                         Loger.i("TEST","请求出了问题");  
	                } 
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  

                 
            };  
        }.start();  
        
        return null;  
} 
	private void uploadFile(final String pic,final int request_index)throws IOException{
		
		//使用HttpClient  
        new Thread(){  
            public void run() { 
            	String path=IHttpRequestUtils.SDPATH+System.currentTimeMillis()+".txt";
        		File fileDir=new File(IHttpRequestUtils.SDPATH+"");
        		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
        			fileDir.mkdir();
        		}
        		File file = new File(path);
        		try {
        			FileOutputStream out = new FileOutputStream(file);
        			 byte[] bytesArray = pic.getBytes();  
        			 out.write(bytesArray);
        			out.flush();
        			out.close();
        		} catch (FileNotFoundException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
            	HttpClient httpClient=new DefaultHttpClient();  
                //必须设置请求的协议  
                httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,HttpVersion.HTTP_1_1);  
                //设置http post请求  

                HttpPost  httpPost=new HttpPost(IHttpRequestUtils.SRV_URL+IHttpRequestUtils.OCR_DIR);  

                //构建上传的文件的实体  

                MultipartEntity  multipartEntity=new MultipartEntity();  

                //构建文件的File的对象  
                //添加文件的  

                ContentBody contentBody=new FileBody(file);  
                multipartEntity.addPart("uploadFile",contentBody);//<input type="file" name="file">  
                
                try {
                	multipartEntity.addPart("iccid",new StringBody(iccid));
                    multipartEntity.addPart("contactPhone",new StringBody(phone));
                    if(request_index==3){
                		multipartEntity.addPart("mark",new StringBody("testFace"));
                	}
                	
        		} catch (UnsupportedEncodingException e1) {
        			// TODO Auto-generated catch block
        			e1.printStackTrace();
        		}

                //把请求实体设置到HttpPost  
                httpPost.setEntity(multipartEntity);  

                //执行这个请求  
                HttpResponse response;
				try {
					response = httpClient.execute(httpPost);
					//通过响应取到状态行  
	                StatusLine statusLine= response.getStatusLine();  

	                //通过状态码去判断  

	                if(statusLine.getStatusCode()==HttpStatus.SC_OK){  
	                         HttpEntity  entity=response.getEntity();  
	                         String result=EntityUtils.toString(entity);  
	                         Loger.i("TEST","*******"+result);  
	                         JSONObject resultjson;
	                         pd1.dismiss();
						try {
							resultjson = new JSONObject(result);
							String resultcode = resultjson.getJSONObject("result").getJSONObject("dataSet").get("code").toString();
							if(resultcode.equals("200")){
								check_status = true;
								Intent intentsuccess = new Intent(AfteractivActivity.this, SuccessActivity.class);
								AfteractivActivity.this.startActivity(intentsuccess);
								AfteractivActivity.this.finish();
							}else{
								check_status = false;
								Intent intentfailed = new Intent(AfteractivActivity.this, FailedActivity.class);
								AfteractivActivity.this.startActivity(intentfailed);
								AfteractivActivity.this.finish();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
								
	                }else{  
	                         Loger.i("TEST","请求出了问题");  
	                } 
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  

                 
            };  
        }.start();  
        
	}
//	private void postlocalhttpdata(String method,String content){
//		RequestParams params = new RequestParams();
//		AsyncHttpClient client = new AsyncHttpClient();	
//		client.setTimeout(40000);
//		params.put("mark","identityLog");
//		params.put("contactPhone",phone);
//		params.put("iccid",iccid);
//		params.put("method",method);
//		params.put("content",content);
//		System.out.println(content);
//		client.post(IHttpRequestUtils.SRV_URL+IHttpRequestUtils.OCR_DIR,params, new JsonHttpResponseHandler() {
//			@Override
//			public void onSuccess(int statusCode,
//					org.apache.http.Header[] headers,
//					org.json.JSONObject response) {
//				// TODO Auto-generated method stub
//				org.json.JSONObject jsonContext = response;
//				super.onSuccess(statusCode, headers,response);
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
//	}

}
