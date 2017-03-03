package com.example.autonymlivingcert;



import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.example.http.IHttpRequestUtils;
import com.example.utils.Loger;
import com.example.view.NetstatusToast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Window;

public class LoadingActivity extends Activity{

	private boolean bIsRunningNetMonitor = false;
	private boolean bNetWorkIsConnect = false;
	private boolean bNetWorkMonitorStatus = false;

	
	private final int NET_EXIT = 101;    //退出当前activity
	private final int NET_SUCCESS = 102; //有网络
	private final int NET_FAILED = 103;  //无网络
	
	private NetstatusToast netstatusToast;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_loading);
        netstatusToast = new NetstatusToast(this);
        bIsRunningNetMonitor = true;
		netWorkThread.start();
    }
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		bIsRunningNetMonitor = false;
		if(netWorkThread!=null){
			Loger.i("TEST", "login挂起监听网络线程。。。。");
			try {
				netstatusToast.hide();
			} catch (Exception e) {
				e.printStackTrace();
			}
			netstatusToast = null;
			netWorkThread.interrupt();
			netWorkThread = null;
		}
		super.onPause();
	}
	
	private void initSplash(){
		Intent intent = new Intent(LoadingActivity.this, EntranceActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}
	private Thread netWorkThread = new Thread(new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
//				if(false==bIsRunningNetMonitor){
//					final Message msg=new Message();
//					msg.what=NET_EXIT;
//				    handler.sendMessage(msg);
//					break;
//				}
				
				bNetWorkMonitorStatus = connect(IHttpRequestUtils.LOADING_TEST,80);
				Loger.i("TEST", ""+bNetWorkMonitorStatus);
//				bNetWorkMonitorStatus = true;
				if(bNetWorkMonitorStatus){
					//当前有网络连接
					initSplash();
					break;
				}else{
					//当前无网络连接
					showToast(netstatusToast);
					continue;
				}
			}
		}
	});
	
	
//	Handler handler = new Handler(){
//		public void handleMessage(final Message msg) {
//			switch(msg.what){
//			case NET_EXIT:
//				bIsRunningNetMonitor = false;
//				if(netWorkThread!=null){
//					try {
//						netstatusToast.hide();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					netstatusToast = null;
//					netWorkThread.interrupt();
//					netWorkThread = null;
//				}
//				break;
//			case NET_SUCCESS:
//				bNetWorkIsConnect = true;
//				try {
//					netstatusToast.hide();
//					initSplash();
//				} catch (Exception e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				break;
//			case NET_FAILED:
//				bNetWorkIsConnect = false;
//				Loger.i("TEST","NETWORK_FAILED");
//				try {
//					showToast(netstatusToast);
//				} catch (Exception e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				break;
//			default:
//				break;
//			}
//		}
//	};
	private boolean connect(String host, int port) {   
        Socket connect = new Socket();
        try {  
            connect.connect(new InetSocketAddress(host, port), 10*1000);  
            return connect.isConnected();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }finally{  
            try {  
                connect.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return false;  
    } 
	private void showToast(final NetstatusToast toast) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					toast.show(-1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Looper.loop();
			}
		}).start();
	}
}
