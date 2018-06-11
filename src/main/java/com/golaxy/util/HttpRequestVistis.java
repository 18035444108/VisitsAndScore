package com.golaxy.util;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.eversec.amc.utils.AESUtil;
import ict.http.HttpClient;
import ict.http.Response;

public class HttpRequestVistis {
	private static final Logger logger = Logger.getLogger(HttpRequestVistis.class);
	private static final HttpClient httpClient;
	private static final String url ;
	static {
		httpClient = HttpClient.createSSLInstance();
		url = ConfigData.visitsUrl;
	}
	
	public static JSONObject getVisitsJson(String domainName) throws Exception {
		// 获得加密的请求体json
		String body = "";
		try {
			body = AESUtil.getEncryptionData(domainName).toJSONString();
			
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		Response response = httpClient.post(url, body, "application/json", "UTF-8");
		// 获取返回(结果是加密的)
		String respData = response.getResponseAsString();
		JSONObject respJson = JSON.parseObject(respData);
		
		JSONObject dataJson = AESUtil.getDecodeData(respJson);
		return dataJson;
	}
	
	/**
	 * 现获取加密后的body
	 * @param args
	 * @throws Exception 
	 */
	
	
	public static void main(String[] args) throws Exception {
		
		for(int i=0;i<3;i++){
			
			System.out.println(getVisitsJson("{'name':'baidu.com'}"));
		}
	}
}
