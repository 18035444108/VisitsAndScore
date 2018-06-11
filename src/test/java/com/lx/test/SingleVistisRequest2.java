package com.lx.test;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.golaxy.entity.QrjrwzEntity;
import com.golaxy.util.SqlSessionUtil;

/**
 * 单次查询网站访问量
 * @author lx
 *
 */
public class SingleVistisRequest2 {
	private static final Logger logger = Logger.getLogger(SingleVistisRequest2.class);
	private static SqlSession sqlSession = SqlSessionUtil.getSqlSession();
	public static void main(String[] args) throws Exception {
		
		List<QrjrwzEntity> qrjrwzs = new ArrayList<QrjrwzEntity>();
//		HashMap<String, Object> dataMap = new HashMap<String, Object>();
//		qrjrwzs = sqlSession.selectList("queryAllQrjrwz");
//		SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
//		
//		
//		for (QrjrwzEntity qrjrwzEntity : qrjrwzs) {
//			FileUtils.writeFile("J:/ym.txt", true, qrjrwzEntity.getWzid()+"\t"+qrjrwzEntity.getYm());
//		}
		Calendar start = Calendar.getInstance();  
	    start.set(2018, 4, 01);  
	    Long startTIme = start.getTimeInMillis();  
	  
	    Calendar end = Calendar.getInstance();  
	    end.set(2018, 5, 03);  
	    Long endTime = end.getTimeInMillis();  
	  
	    Long oneDay = 1000 * 60 * 60 * 24l;  
	  
	    Long time = startTIme;  
	    while (time <= endTime) {  
	        Date d = new Date(time);  
	        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
	        System.out.println(df.format(d));  
	        time += oneDay;  
	    }  
		
	}
}
