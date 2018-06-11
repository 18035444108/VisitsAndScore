package com.golaxy.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.log4j.PropertyConfigurator;

public class ConfigData {

	public static final String password;
	public static final String userId;
	public static final String offset;
	public static final String visitsUrl;
	public static final String scoreUrl;
	public static final int wzid;
	public static final int size;
	public static final String fileSavePath;
	public static final String exField;
	public static final String exStart;
	public static final String exEnd;
	public static final String exFilePath;
	public static final String exSheet;
	public static final InputStream passwordIs ;
	public static final File file;
	public static final String cronFile ;
	static {
		    PropertyConfigurator.configure("./conf/log4j.properties");
			Properties prop = new Properties();
			try {
//				prop.load(Resources.getResourceAsReader("config.properties"));
				prop.load(new FileReader(new File("./conf/config.properties")));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			password = prop.getProperty("password");
			userId = prop.getProperty("userId");
			offset = prop.getProperty("offset");
			visitsUrl = prop.getProperty("visitsUrl");
			scoreUrl = prop.getProperty("scoreUrl");
			wzid = Integer.parseInt(prop.getProperty("wzid"));
			size = Integer.parseInt(prop.getProperty("size"));
			fileSavePath = prop.getProperty("fileSavePath");
			exField = prop.getProperty("ex.field");
			exStart= prop.getProperty("ex.time.start");
			exEnd= prop.getProperty("ex.time.end");
			exFilePath = prop.getProperty("ex.filePath");
			exSheet = prop.getProperty("ex.sheetName");
			cronFile = prop.getProperty("cron.filePath");
			InputStream is = null;
			try {
				file = new File("./conf/nifa.cer");
				passwordIs = new FileInputStream(new File("./conf/nifa.cer"));
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			System.out.println("================");
//			passwordIs = is;
	}
	public static void main(String[] args) {
		System.out.println(passwordIs);
	}

}
