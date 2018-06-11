package com.lx.test;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.golaxy.main.SingleScoreRequest;
import com.golaxy.util.HttpRequestScore;

import cn.eversec.amc.utils.AESUtil;
import ict.http.HttpClient;
import ict.http.Response;

public class HttpTest {
	
	private static final HttpClient httpClient;
	private static final String url ;
	static {
		httpClient = HttpClient.createSSLInstance();
		url = ConfigData.visitsUrl;
	}
	
	public static long getVisits(String domainName) throws Exception {
		// 获得加密的请求体json
		String body = "";
		try {
			body = AESUtil.getEncryptionData(domainName).toJSONString();
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Response response = httpClient.post(url, body, "application/json", "UTF-8");
		// 获取返回(结果是加密的)
		String respData = response.getResponseAsString();
		JSONObject respJson = JSON.parseObject(respData);
//		System.out.println(respJson);
		JSONObject dataJson = AESUtil.getDecodeData(respJson);
		System.out.println(dataJson);
		if(respJson!=null){
			return dataJson.getLong("visitsCount");
		}
		return -1;
	}
	
	/**
	 * 现获取加密后的body
	 * @param args
	 * @throws Exception 
	 */
	
	
	public static void main(String[] args) throws Exception {
		
		long start = System.currentTimeMillis();
		System.out.println(getVisits("{'name':'163.com'}"));
		
//		System.out.println(HttpRequestScore.getScore("{'name':'baidu.com'}"));
//		long end = System.currentTimeMillis();
//		System.out.println(end-start);
		
//		SimpleDateFormat formate = new SimpleDateFormat("yyyyMMddhhmmss");
//		String date = formate.format(new Date());
//		System.out.println(date);
		
	}
}
