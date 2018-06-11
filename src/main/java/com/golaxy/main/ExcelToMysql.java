package com.golaxy.main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.golaxy.util.ConfigData;
import com.golaxy.util.ExcelCommon;
import com.golaxy.util.ExcelReadDealUtils;
import com.golaxy.util.SqlSessionUtil;
import com.golaxy.util.TimeUtils;

public class ExcelToMysql extends ExcelCommon{
	private static final Logger logger = Logger.getLogger(ExcelToMysql.class);
	private static SqlSession sqlSession = SqlSessionUtil.getSqlSession();
	public static void main(String[] args) {
        // ����excel�ļ�����
		ExcelReadDealUtils.loadExcelFile(ConfigData.exFilePath);
        // ��ȡָ��sheet���Ƶ�excel Sheet����
        Sheet sheet = ExcelReadDealUtils.workbook.getSheet(ConfigData.exSheet);
        // ��֤�Ƿ����,��Щʱ��û�б�Ҫ��֤
        if (isExistSheet(sheet)) {
            // ��ȡSheet����к�
            int lastRowNum = sheet.getLastRowNum();
            // ��ȡ��ͷת����Map��ʽ�洢
            Map<Object, Object> headMap = ExcelReadDealUtils.getRowDataToMap(sheet.getRow(0), true);
            // ����Ҫ��ȡExcel�еı�ͷ��
            String[] attributes = new String[headMap.size()];
            String []excelField = ConfigData.exField.split(",");
            for(int i=0;i<excelField.length;i++){
            	attributes[i] = excelField[i];
            }
            ArrayList<String> dateList = TimeUtils.getListDate(ConfigData.exStart, ConfigData.exEnd);
            for(int i=0;i<dateList.size();i++){
            	attributes[i+2]=dateList.get(i).replace("-", "");
            }
            // ������Ϊ2��ʼ����ע�����ʵ���������
            for (int i = 1; i <= lastRowNum; i++) {
                // ��ȡ�ж���
                Row row = sheet.getRow(i);
                int count = 0;
                // ��֤�Ƿ������
                if (isExistRow(row)) {
                	HashMap<String, Object> dataMap = new HashMap<String, Object>();
                    // ��ȡһ��ָ���е���Ϣ
                    List<Object> byGivenAttributeAndRowValue = ExcelReadDealUtils.getByGivenAttributeAndRowValue(headMap, row, attributes);
                    int dateIndex=0;
                    dataMap.put("wzid", String.valueOf(byGivenAttributeAndRowValue.get(0)));
    				dataMap.put("ym", String.valueOf(byGivenAttributeAndRowValue.get(1)));
                    for (int j = 0; j < byGivenAttributeAndRowValue.size(); j++) {
                    	if(j>=2){
                    		dataMap.put("gatherdate", dateList.get(dateIndex++));
                    		dataMap.put("visits", String.valueOf(byGivenAttributeAndRowValue.get(j)));
                    		logger.info(dataMap);
                    		sqlSession.insert("addWebVisits",dataMap);
//                    		 if(count++ == 30){
             					sqlSession.commit();
//             					count=0;
//             				}
                    	}
                    }
                }
            }
            sqlSession.commit();
            sqlSession.close();
        }
        // ���ٴ򿪻Ự���ļ�
        	try {
				ExcelReadDealUtils.closeDestory();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}
