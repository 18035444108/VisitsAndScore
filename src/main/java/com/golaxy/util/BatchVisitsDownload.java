package com.golaxy.util;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.golaxy.entity.QrjrwzEntity;

import cn.eversec.amc.utils.AESUtil;

public class BatchVisitsDownload {
	private static final Logger logger = Logger.getLogger(BatchVisitsDownload.class);
	private static SqlSession sqlSession = SqlSessionUtil.getSqlSession();
//	private static String filePath = "/home/lixiang/lx/sourcequery/batchrequest/";
	private static String filePath = "I:/";

	public static void main(String[] args) {
		PropertyConfigurator.configure("./conf/log4j.properties");
		HashMap<String,Integer> wzidMap = new HashMap<String,Integer>();
		List<QrjrwzEntity> qrjrwzs = new ArrayList<QrjrwzEntity>();
		HashMap<String,Object> dataMap = new HashMap<String,Object>();
		HashMap<String,Integer> parmMap = new HashMap<String, Integer>();
		String date = CommonUtils.getCurrentDay();

		qrjrwzs = sqlSession.selectList("queryAllQrjrwz");
		for (QrjrwzEntity qrjrwzEntity : qrjrwzs) {
			wzidMap.put(qrjrwzEntity.getYm(), Integer.valueOf(qrjrwzEntity.getWzid()));
		}

		SFTPUtils sftpUtils = new SFTPUtils("cuftp", "cuftp2018", "202.108.211.109", 21013);
		sftpUtils.connect();
		
		
		String downFileName = "resp_1_*.json.gz";
		String localGzName = "resp_1_" + date + ".json.gz";
		String localUnGzName = "resp_1_" + date + ".json";
		/* 从sftp服务器下载 */
		sftpUtils.downloadFile("/response/1", downFileName, filePath + localGzName);
		System.out.println("下载完成！");
		
		/*将得到结果进行解压*/
		try {
			GZipUtils.decompress(new File(filePath + localGzName), false);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		/*获取sftp服务器的加密内容*/
		String respData = "";
		try {
			respData = FileUtils.readFile(filePath + localUnGzName);
			System.out.println(respData);
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*解密*/
		JSONArray dataArray = null;
		try {
			dataArray = AESUtil.getDecodeArrayData(JSON.parseObject(respData));
			System.out.println(dataArray);
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*将获取的结果写入mysql数据库*/
		int count=0;
    	try {
    		for(int i=0;i<dataArray.size();i++){
        		JSONObject termData = dataArray.getJSONObject(i);
        		dataMap.put("wzid", wzidMap.get(termData.get("name")));
        		dataMap.put("ym", termData.get("name"));
        		dataMap.put("gatherdate", new Date(System.currentTimeMillis()));
        		dataMap.put("visits", termData.get("visitsCount"));
        		System.out.println(dataMap);
    			sqlSession.insert("addWebVisits",dataMap);
    			if(count++ == 30){
    				sqlSession.commit();
    				count=0;
    			}
        	}
		} catch (Exception e) {
			sqlSession.commit();
		}
    	sqlSession.commit();
		sqlSession.close();
    	
    	sftpUtils.disconnect();
	}
}
