package com.golaxy.util;

import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.eversec.amc.utils.AESUtil;
import ict.http.HttpClient;
import ict.http.Response;

public class HttpRequestScore {
	
	private static final HttpClient httpClient;
	private static final String url ;
	static {
		httpClient = HttpClient.createSSLInstance();
		url = ConfigData.scoreUrl;
	}
	
	public static JSONObject getScore(String domainName) throws Exception {
		// ��ü��ܵ�������json
		String body = "";
		try {
			body = AESUtil.getEncryptionData(domainName).toJSONString();
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Response response = httpClient.post(url, body, "application/json", "UTF-8");
		// ��ȡ����(����Ǽ��ܵ�)
		String respData = response.getResponseAsString();
		JSONObject respJson = JSON.parseObject(respData);
		System.out.println(respJson);
		JSONObject dataJson = AESUtil.getDecodeData(respJson);
		return dataJson;
	}
	
	/**
	 * �ֻ�ȡ���ܺ��body
	 * @param args
	 * @throws Exception 
	 */
	
	
	public static void main(String[] args) throws Exception {
		
		System.out.println(getScore("{'name':'baidu.com'}"));
	}
}
