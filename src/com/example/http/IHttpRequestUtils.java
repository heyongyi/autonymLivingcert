package com.example.http;

import android.os.Environment;

public interface IHttpRequestUtils {
	public final  int TIME_OUT = 4000;
	public final  String LOADING_TEST            = "119.75.217.109";//"134.32.32.145""119.75.217.109"
//	public final  String SRV_URL                 = "http://10.72.95.179:8082";//"http://61.156.3.144:18088";   
	public final  String SRV_URL                 = "http://61.156.3.144:18088";
//	public final  String OCR_DIR                 = "/productcenter/order/order/OneKeyOpenMgr.json";
	public final  String OCR_DIR                 = "/productcenter_/order/order/OneKeyOpenMgr.json";
	
	public final String GZT_URL                  ="http://124.192.161.110:8080";
	public final  String F_GZT_OCR_DIR             ="/ocr/analyse/p1/front";
	public final  String B_GZT_OCR_DIR             ="/ocr/analyse/p1/back";
	public final  String C_GZT_OCR_DIR             ="/face/decrypt/verify/check";
	
	public static String SDPATH = Environment.getExternalStorageDirectory() + "/Photo_LJ/";

}
