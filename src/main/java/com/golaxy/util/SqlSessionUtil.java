package com.golaxy.util;

import java.io.File;
import java.io.FileReader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

public class SqlSessionUtil {
	private static final Logger logger = Logger.getLogger(SqlSession.class);
	private static SqlSession sqlSession; 
	public static SqlSession getSqlSession(){
		try {
			java.io.Reader reader=new FileReader(new File("./conf/Configuration.xml"));
			SqlSessionFactory factory=new SqlSessionFactoryBuilder().build(reader);
			sqlSession= factory.openSession();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return sqlSession;
 	}
}
