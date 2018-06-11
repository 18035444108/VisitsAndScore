
package com.golaxy.main;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.golaxy.entity.QrjrwzEntity;
import com.golaxy.util.ConfigData;
import com.golaxy.util.HttpRequestScore;
import com.golaxy.util.HttpRequestVistis;
import com.golaxy.util.SqlSessionUtil;

import ict.http.HttpClient;

/**
 * 单次查询网行指数
 * @author lx
 *
 */
public class SingleScoreRequest {
	private static final Logger logger = Logger.getLogger(SingleScoreRequest.class);
	private static SqlSession sqlSession = SqlSessionUtil.getSqlSession();
	public static void main(String[] args) throws Exception {
		
		List<QrjrwzEntity> qrjrwzs = new ArrayList<QrjrwzEntity>();
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		
		HashMap<String, Integer> parmMap = new HashMap<String, Integer>();
		parmMap.put("wzid", ConfigData.wzid);
		parmMap.put("size", ConfigData.size);
		qrjrwzs = sqlSession.selectList("queryAllIf", parmMap);
		int i=0;
		for (QrjrwzEntity qrjrwzEntity : qrjrwzs) {
			dataMap.put("wzid", qrjrwzEntity.getWzid());
			dataMap.put("ym", qrjrwzEntity.getYm());
			String dataJson = "{'name':'"+qrjrwzEntity.getYm()+"'}";
			JSONObject resultJson = HttpRequestScore.getScore(dataJson);
			if(resultJson!=null){
				dataMap.put("gatherdate", new Date(System.currentTimeMillis()));
				dataMap.put("score", resultJson.getDouble("score"));
				dataMap.put("scoreRanking", resultJson.getLong("scoreRanking"));
				dataMap.put("updateTime", resultJson.getString("updateTime"));
				System.out.println(dataMap);
				sqlSession.insert("addWebScore",dataMap);
				if(i++ == 30){
					sqlSession.commit();
					i=0;
				}
			}else{
				logger.error("域名："+qrjrwzEntity.getYm()+"获取结果出错！");
			}
		}
		sqlSession.commit();
		sqlSession.close();
	}
}
