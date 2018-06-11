package com.golaxy.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.golaxy.entity.QrjrwzEntity;

public class CommonUtils {
	
	public static String getRequestBody(List<QrjrwzEntity> qrjrwzs){
		StringBuffer resultArray = new StringBuffer("[");
		String ymJson = "";
		for (int i=0;i<qrjrwzs.size();i++) {
			ymJson = "{'name':'"+qrjrwzs.get(i).getYm()+"'}";
			resultArray.append(ymJson);
			if(i!=qrjrwzs.size()-1){
				resultArray.append(",");
			}
		}
		resultArray.append("]");
		return resultArray.toString();
	}
	
	public static String getCurrentDay(){
		SimpleDateFormat formate = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = formate.format(new Date());
		return date;
	}
	
	
	public static void main(String[] args) {
//		QrjrwzEntity qrjrwzEntity = new QrjrwzEntity();
//		qrjrwzEntity.setYm("baidu.com");
//		QrjrwzEntity qrjrwzEntity1 = new QrjrwzEntity();
//		qrjrwzEntity1.setYm("163.com");
//		QrjrwzEntity qrjrwzEntity2 = new QrjrwzEntity();
//		qrjrwzEntity2.setYm("sina.com");
//		List<QrjrwzEntity> qrjrwzs = new ArrayList<QrjrwzEntity>();
//		qrjrwzs.add(qrjrwzEntity1);
//		qrjrwzs.add(qrjrwzEntity);
//		qrjrwzs.add(qrjrwzEntity2);
//		System.out.println(getRequestBody(qrjrwzs));
		System.out.println(getCurrentDay());
		
		
		
	}
}
