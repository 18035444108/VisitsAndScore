package com.golaxy.util;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * 
 * Excel������װ��<br>
 * ˵��������ΪExcel����д�Ĺ��������:<br>
 * 1).��ȡ��Ԫ���ֵ;<br>
 * 2).�ж�Sheet��Row��Cell�Ƿ����;<br>
 * 3).�ӿ���̨����List<List<Object>>���϶���;<br>
 * 
 * 
 * @author ����
 * @version 1.0
 * @since 2014-7-8
 */
public class ExcelCommon {
    /**
     * 
     * ��ȡCell������
     * 
     * @param cell
     * @return ��Ԫ�������
     */
    protected static Object getCellValue(Cell cell) {
        Object obj = null;
        if(cell == null){
            return "";
        }
        int cellType = cell.getCellType();
        switch (cellType) {
        case Cell.CELL_TYPE_BOOLEAN:
            obj = cell.getBooleanCellValue();
            break;
        case Cell.CELL_TYPE_FORMULA:
            obj = cell.getCellFormula();
            break;
        case Cell.CELL_TYPE_NUMERIC:
            obj = cell.getNumericCellValue();
            break;
        case Cell.CELL_TYPE_STRING:
            obj = cell.getStringCellValue();
            break;
        case Cell.CELL_TYPE_BLANK:
            obj = "";
            break;
        default:
            obj = cell.getRichStringCellValue();
            throw new RuntimeException("��Ԫ��Ϊδ֪����!");
        }
        return obj;
    }

    /**
     * 
     * ��֤Sheet������Ƿ����,����Boolean���
     * 
     * @param sheet
     *            ��֤��Sheet�����
     * @return �Ƿ����Sheet���Booleanֵ
     */
    protected static boolean isExistSheet(Sheet sheet) {
        return isExist(sheet);
    }

    /**
     * 
     * ��֤һ�ж����Ƿ����,����Boolean���
     * 
     * @param row
     *            ��֤���ж���
     * @return �Ƿ�����е�Booleanֵ
     */
    protected static boolean isExistRow(Row row) {
        return isExist(row);
    }

    /**
     * 
     * ��֤һ��Ԫ���Ƿ���� ,����Boolean���
     * 
     * @param cell
     *            ��֤�ĵ�Ԫ�����
     * @return �Ƿ�����е�Booleanֵ
     */
    protected static boolean isExistCell(Cell cell) {
        return isExist(cell);
    }

    /**
     * �����Ƿ�Ϊ�ն���
     * 
     * @param object
     *            ��֤�Ķ���
     * @return �����Ƿ�Ϊ��
     */
    protected static boolean isExist(Object object) {
        if (object == null) {
            return false;
        }
        return true;
    }

    /**
     * 
     * ����̨������List<List<Object>>����
     * 
     * @param datas
     */
    public static void control(List<List<Object>> datas) {
        for (int i = 0; i < datas.size(); i++) {
            List<Object> rowData = datas.get(i);
            StringBuffer sbf = new StringBuffer("�ڡ�" + i + "���У�");
            for (Object object : rowData) {
                sbf.append("\t" + String.valueOf(object));
            }
            System.out.println(sbf.toString());
        }
    }
}
