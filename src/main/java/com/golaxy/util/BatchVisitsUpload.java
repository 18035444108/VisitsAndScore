package com.golaxy.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.golaxy.entity.QrjrwzEntity;

import cn.eversec.amc.utils.AESUtil;

public class BatchVisitsUpload {
	private static final Logger logger = Logger.getLogger(BatchVisitsUpload.class);
	private static SqlSession sqlSession = SqlSessionUtil.getSqlSession();
	// private static String filePath =
	// "/home/lixiang/lx/sourcequery/batchrequest/";
	private static String filePath = "I:/";

	public static void main(String[] args) {
		HashMap<String, Integer> wzidMap = new HashMap<String, Integer>();
		List<QrjrwzEntity> qrjrwzs = new ArrayList<QrjrwzEntity>();
		HashMap<String, Integer> parmMap = new HashMap<String, Integer>();
		parmMap.put("wzid", ConfigData.wzid);
		parmMap.put("size", ConfigData.size);
		qrjrwzs = sqlSession.selectList("queryAllIf", parmMap);
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		/* ��qrjrwz���л�ȡ���� */
		for (QrjrwzEntity qrjrwzEntity : qrjrwzs) {
			wzidMap.put(qrjrwzEntity.getYm(), qrjrwzEntity.getWzid());
		}

		/* ��ȡ����������������������json�� */
		String batchRequest = CommonUtils.getRequestBody(qrjrwzs);

		/* �����Ľ��м��ܲ��� */
		String body = "";
		try {
			body = AESUtil.getEncryptionData(batchRequest).toJSONString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		/* �����ܺ������д����.json�������ļ��� */
		String date = CommonUtils.getCurrentDay();
		String fileName = "req_1_" + date + ".json";
		FileUtils.writeFile(filePath + fileName, false, body);

		/* ��.json�ļ�����gzѹ������ָ��Ϊָ������ */
		try {
			GZipUtils.compress(new File(filePath + fileName), false);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		SFTPUtils sftpUtils = new SFTPUtils("cuftp", "cuftp2018", "202.108.211.109", 21013);
		sftpUtils.connect();

		/* �ϴ���sftp������ */
		try{
			sftpUtils.uploadFile("/request/1", filePath + fileName + ".gz", fileName + ".gz");
			System.out.println("�ϴ���ɣ�");
		} catch (Exception e){
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		

		/* ��sftp���������� */
		// try {
		// Thread.sleep(100000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// String downFileName = "resp_1_"+"*.json.gz";
		// String localGzName = "resp_1_"+date+".json.gz";
		// String localUnGzName = "resp_1_"+date+".json";
		// sftpUtils.downloadFile("/response/1", downFileName,
		// filePath+localGzName);
		// System.out.println("������ɣ�");
		//
		// /*���õ�������н�ѹ*/
		// try {
		// GZipUtils.decompress(new File(filePath+localGzName), false);
		// } catch (Exception e) {
		// logger.error(e.getMessage());
		// e.printStackTrace();
		// }
		//
		// /*��ȡsftp�������ļ�������*/
		// String respData ="";
		// try {
		// respData = FileUtils.readFile(filePath+localUnGzName);
		// System.out.println(respData);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// /*����*/
		// JSONArray dataArray=null;
		// try {
		// dataArray = AESUtil.getDecodeArrayData(JSON.parseObject(respData));
		// System.out.println(dataArray);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// /*����ȡ�Ľ��д��mysql���ݿ�*/
		// int count=0;
		// try {
		// for(int i=0;i<dataArray.size();i++){
		// JSONObject termData = dataArray.getJSONObject(i);
		// dataMap.put("wzid", wzidMap.get(termData.get("name")));
		// dataMap.put("ym", termData.get("name"));
		// dataMap.put("gatherdate", new Date(System.currentTimeMillis()));
		// dataMap.put("visits", termData.get("visitsCount"));
		// System.out.println(dataMap);
		//// sqlSession.insert("addWebVisits",dataMap);
		//// if(count++ == 30){
		//// sqlSession.commit();
		//// count=0;
		//// }
		// }
		// } catch (Exception e) {
		// sqlSession.commit();
		// }
		//// sqlSession.commit();
		//// sqlSession.close();
		//
		// sftpUtils.disconnect();
	}
}
