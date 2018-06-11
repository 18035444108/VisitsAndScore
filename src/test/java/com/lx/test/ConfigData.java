package com.lx.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
	}
	public static void main(String[] args) {
		System.out.println(size);
	}

}
