package com.golaxy.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.golaxy.entity.QrjrwzEntity;
import com.golaxy.util.ConfigData;
import com.golaxy.util.FileUtils;
import com.golaxy.util.HttpRequestVistis;
import com.golaxy.util.SqlSessionUtil;

/**
 * 单次查询网站访问量
 * @author lx
 *
 */
public class SingleVistisRequest {
	private static final Logger logger = Logger.getLogger(SingleVistisRequest.class);
	private static SqlSession sqlSession = SqlSessionUtil.getSqlSession();
	public static void main(String[] args) throws Exception {
		
		List<QrjrwzEntity> qrjrwzs = new ArrayList<QrjrwzEntity>();
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		HashMap<String, Integer> parmMap = new HashMap<String, Integer>();
		parmMap.put("wzid", ConfigData.wzid);
		parmMap.put("size", ConfigData.size);
		qrjrwzs = sqlSession.selectList("queryAllIf", parmMap);
		SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
		
		int i=0;
		try {
			for (QrjrwzEntity qrjrwzEntity : qrjrwzs) {
				dataMap.put("wzid", qrjrwzEntity.getWzid());
				dataMap.put("ym", qrjrwzEntity.getYm());
				String nameJson = "{'name':'"+qrjrwzEntity.getYm()+"'}";
				dataMap.put("gatherdate", new Date(System.currentTimeMillis()));
				JSONObject dataJson = HttpRequestVistis.getVisitsJson(nameJson);
				dataMap.put("gatherdate", formate.parse(dataJson.getString("statisticTime")));
				dataMap.put("visits", dataJson.get("visitsCount"));
				logger.info(dataJson);
				logger.info(dataMap);
				sqlSession.insert("addWebVisits",dataMap);
				if(i++ == 30){
					sqlSession.commit();
					i=0;
				}
			}
		} catch (Exception e) {
			sqlSession.commit();
			logger.error(e.getMessage());
		}
		sqlSession.commit();
		sqlSession.close();
		
		FileUtils.writeFile(ConfigData.cronFile+"visitsRun.txt", true, new Date(System.currentTimeMillis()).toString()+" is complete!");
	}
}
