package com.golaxy.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.golaxy.entity.QrjrwzEntity;
import com.golaxy.util.CommonUtils;
import com.golaxy.util.ConfigData;
import com.golaxy.util.FileUtils;
import com.golaxy.util.GZipUtils;
import com.golaxy.util.SFTPUtils;
import com.golaxy.util.SqlSessionUtil;

import cn.eversec.amc.utils.AESUtil;


/**
 * 批量查询网行指数
 * @author lx
 *
 */
public class BatchWebScore {
	private static final Logger logger = Logger.getLogger(BatchWebScore.class);
	private static SqlSession sqlSession = SqlSessionUtil.getSqlSession();
//	private static String filePath =  ConfigData.fileSavePath;
	private static String filePath =  "I:/";
	public static void main(String[] args) {

		HashMap<String, Integer> wzidMap = new HashMap<String, Integer>();
		List<QrjrwzEntity> qrjrwzs = new ArrayList<QrjrwzEntity>();
		HashMap<String, Integer> parmMap = new HashMap<String, Integer>();
		parmMap.put("wzid", ConfigData.wzid);
		parmMap.put("size", ConfigData.size);
		qrjrwzs = sqlSession.selectList("queryAllIf", parmMap);
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		/* 从qrjrwz表中获取域名 */
		for (QrjrwzEntity qrjrwzEntity : qrjrwzs) {
			wzidMap.put(qrjrwzEntity.getYm(), qrjrwzEntity.getWzid());
		}

		/* 获取多个域名组成批量请求明文json串 */
		String batchRequest = CommonUtils.getRequestBody(qrjrwzs);
		logger.info("明文请求参数："+batchRequest);

		/* 将明文进行加密操作 */
		String body = "";
		try {
			body = AESUtil.getEncryptionData(batchRequest).toJSONString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		/* 将机密后的密文写入以.json结束的文件中 */
		String date = CommonUtils.getCurrentDay();
		String fileName = "req_2_" + date + ".json";
		FileUtils.writeFile(filePath + fileName, false, body);

		/* 将.json文件进行gz压缩，并指定为指定名称 */
		try {
			GZipUtils.compress(new File(filePath + fileName), false);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		SFTPUtils sftpUtils = new SFTPUtils("cuftp", "cuftp2018", "202.108.211.109", 21013);
		sftpUtils.connect();
		
		sftpUtils.delete("/response/2","resp_2_*" );

		/* 上传到sftp服务器 */
		try{
			sftpUtils.uploadFile("/request/2", filePath + fileName + ".gz", fileName + ".gz");
			System.out.println("上传完成！");
		} catch (Exception e){
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		

//		String downFileName = "resp_2_*.json.gz";
		String downFileName = sftpUtils.getDownFileName("/response/2");
		String localGzName = "resp_2_" + date + ".json.gz";
		String localUnGzName = "resp_2_" + date + ".json";
		/* 从sftp服务器下载 */
		sftpUtils.downloadFile("/response/2", downFileName, filePath + localGzName);
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
        		dataMap.put("score", termData.getDouble("score"));
				dataMap.put("scoreRanking", termData.getLong("scoreRanking"));
				dataMap.put("updateTime", termData.getString("updateTime"));
        		System.out.println(dataMap);
    			sqlSession.insert("addWebScore",dataMap);
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
