package com.golaxy.util;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimeUtils {

	public static ArrayList<String> getListDate(String startS,String endS){
		ArrayList<String> dateList = new ArrayList<String>();
		String dates[] = startS.split("-");
		Calendar start = Calendar.getInstance();  
	    start.set(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]),Integer.parseInt(dates[2]));  
	    Long startTIme = start.getTimeInMillis();  
	  
	    dates = endS.split("-");
	    Calendar end = Calendar.getInstance();  
	    end.set(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]),Integer.parseInt(dates[2]));  
	    Long endTime = end.getTimeInMillis();  
	  
	    Long oneDay = 1000 * 60 * 60 * 24l;  
	  
	    Long time = startTIme;  
	    while (time <= endTime) {  
	        Date d = new Date(time);  
	        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
	        dateList.add(df.format(d));
	        time += oneDay;  
	    }  
	    return dateList;
	}
	public static void main(String[] args) {
		System.out.println(getListDate("2018-4-04", "2018-5-03"));
	}
}
