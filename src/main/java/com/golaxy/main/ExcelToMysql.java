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
        // 加载excel文件对象
		ExcelReadDealUtils.loadExcelFile(ConfigData.exFilePath);
        // 获取指定sheet名称的excel Sheet对象
        Sheet sheet = ExcelReadDealUtils.workbook.getSheet(ConfigData.exSheet);
        // 验证是否存在,有些时候没有必要验证
        if (isExistSheet(sheet)) {
            // 获取Sheet最后行号
            int lastRowNum = sheet.getLastRowNum();
            // 获取表头转换成Map形式存储
            Map<Object, Object> headMap = ExcelReadDealUtils.getRowDataToMap(sheet.getRow(0), true);
            // 定义要获取Excel中的表头列
            String[] attributes = new String[headMap.size()];
            String []excelField = ConfigData.exField.split(",");
            for(int i=0;i<excelField.length;i++){
            	attributes[i] = excelField[i];
            }
            ArrayList<String> dateList = TimeUtils.getListDate(ConfigData.exStart, ConfigData.exEnd);
            for(int i=0;i<dateList.size();i++){
            	attributes[i+2]=dateList.get(i).replace("-", "");
            }
            // 从索引为2开始，需注意根据实际情况来定
            for (int i = 1; i <= lastRowNum; i++) {
                // 获取行对象
                Row row = sheet.getRow(i);
                int count = 0;
                // 验证是否存在行
                if (isExistRow(row)) {
                	HashMap<String, Object> dataMap = new HashMap<String, Object>();
                    // 获取一行指定列的信息
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
        // 销毁打开会话的文件
        	try {
				ExcelReadDealUtils.closeDestory();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}
