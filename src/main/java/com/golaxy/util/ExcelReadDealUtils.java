package com.golaxy.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.golaxy.entity.WebVisitsCount;
import com.golaxy.main.SingleVistisRequest;


/**
 * 
 * Excel�ļ���ȡ��װ ʹ��˵����<br> 
 * 1��ʹ��loadExcelFile(String excelFilePath)��������Excel�ļ�,����ʹ��workbook��̬����;<br> 
 * 2��ʹ��closeDestory()���������ļ���,�����ø÷�����,����Դ������;<br>
 * 3��ʹ��Map<Object, Object> getRowDataToMap(Row row, boolean isValueKey), ���˽����isValueKey������,��ȡExcel�ļ�һ�е�����,��Map�洢,�÷���������Excel���ͷ�еĻ�ȡ;<br>
 * 4��ʹ��List<String> getRowDataToList(Row row)���� ��ȡExcel�ļ�һ�е�����,��List�洢,List�ɰ����ظ�ֵ,�������������ݵĻ�ȡ;<br>
 * 5��ʹ��Map<Object, List<String>> getBatchRowDataToList(Sheet sheet, int startRowIndex, int endRowIndex) ������ѯ,��ȡExcel�ļ���ָ����ʼ�±�����������±�������֮�������;<br>
 * 6��ʹ��Object getCellValue(Cell cell)������ȡָ����Ԫ�������;<br> 
 * 7��ʹ��getGivenSheetDatas(String excelFilePath, String[] sheetNames, int headIndex, String[] attributes, boolean isMerge, String[] mergeAttributes)<br> 
 *      ��ȡָ��Excel�ļ���ָ��Sheet����ָ������м���ָ����Ҫ�ϲ��е����� ,�����ڽϴ�����,ʹ�ø÷�������ҪloadExcelFile()�Լ�closeDestory()����; �����ļ���Excel�ļ���رա������ļ����Զ��ڷ��������; 
 * 8��ʹ��getByGivenAttributeAndRowValue(Map<Object, Object> headDataMap, Row row, String[] attributes) ��ȡһ��ָ���е����ݼ�;<br>
 * 9��ʹ��getMergeCellRowsData(Sheet sheet, Map<Object, Object> headRowData, int rowIndex, String[] attributes, String mergeAttribute, boolean isMerge, String[] mergeAttributes) ����ָ��Sheet����ʼ�н��л�ȡһ���ϲ���Ԫ��;<br>
 * 10��ʹ��getCellToDate(Object cellDateValue)����ȡ����Cell��Ԫ��Ϊ��������ʱ,ͨ����ʾ5λ����Double����,ת����Java��Date <br> 
 * 11��ʹ��deleteRows(Sheet sheet, int startRow, int endRow)����ɾ��ָ����ʼ��������,��������ԭ·����;<br>
 * 12��ʹ��getExcelWorkbook()������ȡ����Excel�ļ����Workbook����<br>
 *  
 * ע�⣺deleteRows����δ����,���ڴ˷��������ڲ�ѯ�Ĺ��ܷ�Χ��
 * @author qiqitrue
 * @version 1.0
 * @since 2015-1-5
 */
public class ExcelReadDealUtils extends ExcelCommon {
	private static final Logger logger = Logger.getLogger(SingleVistisRequest.class);
	private static SqlSession sqlSession = SqlSessionUtil.getSqlSession();
    /**
     * excel��������
     */
    public static Workbook workbook;

    /**
     * excel�ļ���,����closeDestory����һ�δ򿪻Ự
     */
    private static FileInputStream fis;

   

    /**
     * 
     * ����Excel�ļ�,����Workbook����
     * 
     * @param excelFilePath
     *            Excel�ļ�·��
     */
    public static void loadExcelFile(String excelFilePath) {
        try {
            fis = new FileInputStream(excelFilePath);
            if (excelFilePath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (excelFilePath.endsWith(".xls")) {
                workbook = new HSSFWorkbook(fis);
            } else {
                throw new RuntimeException("������ʾ: �����õ�Excel�ļ������Ϸ�!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ��ȡExcel�ļ���Workbook����
     * @return
     */
    public static Workbook getExcelWorkbook(){
        if(!isExist(workbook)){
            throw new RuntimeException("������ʾ�����Ƚ���Excel����,��ʼ��Workbook����!");
        }
        return workbook;
    }


    /**
     * 
     * �ر�Excel�ļ���
     * @throws IOException 
     */
    public static void closeDestory() throws IOException {
        if(fis == null){
            throw new RuntimeException("������ʾ��Excel�ļ�δ���ػ�δ��ʼ���ļ�!");
        }
        fis.close();
    }

    /********************************************row�в���********************************************/

    /**
     * 
     * ��ȡһ�е�����,Map�洢,�洢��ʽ�ɲ�������
     * 
     * @param row
     *            �ж���
     * @param isValueKey
     *            �Ƿ��Ե�Ԫ��������ΪKey?keyΪ��Ԫ������, valueΪ�±�������keyΪ�±�����, valueΪ��Ԫ������
     * @return һ�е�����,Map�洢
     */
    public static Map<Object, Object> getRowDataToMap(Row row, boolean isValueKey) {
        Map<Object, Object> headDatas = new HashMap<Object, Object>();
        short countCellNum = row.getLastCellNum();
        // �������ж�isValueKey��Ϊ�����Ч��,����ѭ�����н���Ч��
        if (isValueKey) {
            for (int j = 0; j < countCellNum; j++) {
                Cell cell = row.getCell(j);
                if (isExistCell(cell)) {
                    // Key=��Ԫ������, Value=�±�����
                    headDatas.put(getCellValue(cell), j);
                }
            }
        } else {
            for (int j = 0; j < countCellNum; j++) {
                Cell cell = row.getCell(j);
                if (isExistCell(cell)) {
                    // Key=�±�����, Value=��Ԫ������
                    headDatas.put(j, getCellValue(cell));
                }
            }
        }
        return headDatas;
    }

    /**
     * 
     * ��ȡһ�е�����,List�洢
     * 
     * @param row
     *            �ж���
     * @return һ�е�����
     */
    public static List<Object> getRowDataToList(Row row) {
        List<Object> rowData = new ArrayList<Object>();
        if (isExistRow(row)) {
            short countCellNum = row.getLastCellNum();
            for (int i = 0; i < countCellNum; i++) {
                Cell cell = row.getCell(i);
                if (isExistCell(cell)) {
                    rowData.add(getCellValue(cell));
                }
            }
        }
        return rowData;
    }

    /**
     * 
     * ��ȡsheet���������ݣ���List�洢
     * 
     * @param sheet
     *            Sheet�����
     * @param startRowIndex
     *            ��ʼ���±�������
     * @param endRowIndex
     *            �������±�������
     * @return ��������������
     */
    public static List<List<Object>> getBatchRowDataToList(Sheet sheet, int startRowIndex, int endRowIndex) {
        List<List<Object>> batchDatas = new ArrayList<List<Object>>();
        if(startRowIndex > endRowIndex){
            throw new RuntimeException("������ʾ����ʼ�в��ܴ��ڽ�����!");
        }
        // ��ȡsheet������
        int lastRowNum = sheet.getLastRowNum();
        if(startRowIndex > lastRowNum || endRowIndex > lastRowNum){
            throw new RuntimeException("������ʾ����ʼ�л�����в��ܳ���sheet�������!");
        }
        for (int i = startRowIndex; i <= endRowIndex; i++) {
            Row row = sheet.getRow(i);
            batchDatas.add(getRowDataToList(row));// �˴�����Ҫ��֤row�Ƿ�Ϊ��,�ڵײ�getRowData����֤
        }
        return batchDatas;
    }


    /**
     * 
     * ��ȡָ��Excel�ļ���ָ��Sheet����ָ������м���ָ����Ҫ�ϲ��е�����
     * 
     * @param excelFilePath
     *            Excel�ļ�·��,����Excel�ļ���
     * @param sheetNames
     *            Ҫ������Sheet���Ƽ�,null��ʾȫ����ȡ
     * @param headIndex
     *            Sheet��ͷ�±�,��0��ʼ
     * @param attributes
     *            ��Ҫ��ȡExcel��
     * @param isMerge
     *            �Ƿ���Ҫ�ϲ�����,��Ϊfalse,mergeAttributes������Ϊnull
     * @param mergeAttributes
     *            ��Ҫ�ϲ�����(����Ҫ��ȡExcel����)
     * @return ����Excel����������
     * @throws IOException 
     */
    public static List<List<Object>> getGivenSheetDatas(String excelFilePath, String[] sheetNames, int headIndex, String[] attributes, boolean isMerge, String[] mergeAttributes) throws IOException {
        // ����洢Excel���ݼ���,List<Object>��ʾһ�е�����
        List<List<Object>> excelDatas = new ArrayList<List<Object>>();
        // �б�ͷ,key=��Ԫ������,value=���±�������
        Map<Object, Object> headDatasMap = new HashMap<Object, Object>();
        // �����ļ�
        loadExcelFile(excelFilePath);
        // ��������н�����Sheet��
        List<Sheet> sheets = new ArrayList<Sheet>();
        // ��ָ����Sheet���н���
        if (sheetNames != null) {
            for (int i = 0; i < sheetNames.length; i++) {
                // ��ȡ��i��Sheet�����
                Sheet sheet = workbook.getSheet(sheetNames[i]);
                if (!isExistSheet(sheet)) {
                    throw new RuntimeException("������ʾ: ��ָ����Sheet���еġ�" + sheetNames[i]    + "��������,����ԭExcel�ļ�!");
                }
                // �����������Sheet����
                else {
                    sheets.add(sheet);
                }
            }
        }
        // û��ָ��Sheet,��ȫ��
        else {
            int length = workbook.getNumberOfSheets();
            for (int i = 0; i < length; i++) {
                Sheet sheet = workbook.getSheet(workbook.getSheetName(i));
                sheets.add(sheet);
            }
        }
        // �����ǻ�ȡ��Ҫ��ȡ��Sheet��
        for (Sheet sheet : sheets) {
            // ��ȡ��ͷ�����Ƿ����
            Row headRow = sheet.getRow(headIndex);
            if (headRow == null) {
                System.out.println("������ʾ: ��ָ���ı����ͷ�С�" + headIndex + "��������!");
            }
            // ������ָ���ı�ͷ����
            else {
                // ת����Map,Key=��ͷ������,Value=�������±�����
                headDatasMap = getRowDataToMap(headRow, true);
                int eqCount = 0;// ʵ�ʴ��ڵ��и���
                // ������Ҫ��ȡ��������ʵ���в����ڵ��м���
                List<String> noEqAttributes = new ArrayList<String>();
                for (String attribute : attributes) {
                    if (headDatasMap.get(attribute) != null) {
                        eqCount++;
                    }
                    // �����ڵ�����
                    else {
                        noEqAttributes.add(attribute);
                    }
                }
                System.out.println("��ʾ��Sheet������Ϊ[" + sheet.getSheetName()   + "]���赼�����������ڵ�����֮�ȣ�" + attributes.length + ":" + eqCount);
                if (attributes.length != eqCount) {
                    System.out.println("������ʾ: ʵ�ʲ����ڵ����Լ�(��������Ҫ��ȡ������Sheet�в����ڵ���)��");
                    for (int j = 0; j < noEqAttributes.size(); j++) {
                        System.out.print(noEqAttributes.get(j) + "\t");
                    }
                    System.out.println();
                }
                // ȫ�����ڽ��н�����Sheetҳ����������
                else {
                    // ��ȡ��ǰSheet��������
                    int rowCount = sheet.getLastRowNum();
                    // ��������Ϊ��ͷ�к�����+1��ʼ���ж�ȡ
                    for (int k = (headIndex + 1); k <= rowCount; k++) {
                        Row row = sheet.getRow(k);
                        if (!isExistRow(row)) {
                            System.out.println("������ʾ: �ڵ�" + k + "�г��ֿ��С�");
                            continue;
                        }
                        // һ������
                        List<Object> rowDatas = getByGivenAttributeAndRowValue(headDatasMap, row, attributes);
                        excelDatas.add(rowDatas);// ���һ������
                    }
                }
            }
        }
        // ��Ҫ�ϲ�
        if (isMerge) {
            // ��¼��Ҫ�ϲ�����Index
            List<Integer> mergerIndex = new ArrayList<Integer>();
            for (String mergerAttribute : mergeAttributes) {
                Integer integerIndex = (Integer) headDatasMap.get(mergerAttribute);
                if (integerIndex != null) {
                    mergerIndex.add(integerIndex);
                }
            }
            for (int i = 0; i < excelDatas.size(); i++) {
                List<Object> rowDatas = excelDatas.get(i);
                for (int j = 0; j < mergerIndex.size(); j++) {
                    if (rowDatas.get(mergerIndex.get(j)).equals("")) {
                        // �ڵ�һ�в�Ϊ�յ������
                        if (i != 0) {
                            Object value = excelDatas.get(i - 1).get(mergerIndex.get(j));
                            rowDatas.set(mergerIndex.get(j), value);
                        }
                    }
                }
            }
        }
        closeDestory();
        return excelDatas;
    }

    /**
     * 
     * ��ȡһ��ָ���е����ݼ�
     * 
     * @param headDataMap
     *            ����ͷMap����,key=��Ԫ������,valueΪ�±�������
     * @param row
     *            ���ȡ��ָ����
     * @param attributes
     *            ���ȡ��ָ����Ԫ��
     * @return һ��ָ���е����ݼ�
     */
    public static List<Object> getByGivenAttributeAndRowValue(Map<Object, Object> headDataMap, Row row, String[] attributes) {
        List<Object> datas = new ArrayList<Object>();
        for (int i = 0; i < attributes.length; i++) {
            Integer index = (Integer) headDataMap.get(attributes[i]);
            if(index == null){
                System.out.println("��ѯ�У�"+attributes[i]+"ʧ�ܣ�");
            }
            else{
                Cell cell = row.getCell(index);
                Object cellValue = getCellValue(cell);
                if (cellValue == null) {
                    cellValue = "";
                }
                datas.add(cellValue);
            }

        }
        return datas;
    }

    /**
     * 
     * ����ָ��Sheet����ʼ�н��л�ȡһ���ϲ���Ԫ��,��û���,�ñ��Ǵ����ݿ���ֻȡһ����,���ѱ�������ֶη���
     * 
     * @param sheet
     *            ָ��Sheet�����
     * @param headRowData
     *            ��ͷ��,ʹ��getRowDataToMap(Row, boolean)������ȡ
     * @param rowIndex
     *            ��ʼ�ж���,�������ݵĿ�ʼ��
     * @param attributes
     *            ��Ҫ��ȡ�ĵ�Ԫ���ж���
     * @param mergeAttribute
     *            ������һ����Ԫ����кϲ�
     * @param isMerge
     *            �Ƿ���Ҫ�ϲ�
     * @param mergeAttributes
     *            ��Ҫ�ϲ��ĵ�Ԫ��
     * @return ����һ���ϲ���Ԫ��
     */
    public static List<List<Object>> getMergeCellRowsData(Sheet sheet,Map<Object, Object> headRowData, int rowIndex, String[] attributes, String mergeAttribute, boolean isMerge, String[] mergeAttributes) {
        List<List<Object>> rowsData = null;
        // ��Ҫ��ȡ�ĵ�Ԫ���������ڱ���ͷ������ȫ����
        if (headRowData.keySet().containsAll(Arrays.asList(attributes))) {
            Row row = sheet.getRow(rowIndex);
            // ���ڵ�������
            Integer existCellIndex = (Integer) headRowData.get(mergeAttribute);
            Cell cell = row.getCell(existCellIndex);
            Object cellValue = getCellValue(cell);
            if (String.valueOf(cellValue).isEmpty()) {// ֵ�ǿյ�
                System.out.println("������ʾ: �������С�" + rowIndex + "���ġ�"+ mergeAttribute + "��Ϊ��, ����!");
            } else {
                rowsData = new ArrayList<List<Object>>();
                rowsData.add(getByGivenAttributeAndRowValue(headRowData, row, attributes));// ��һ��
                while (true) {
                    Row row2 = sheet.getRow(++rowIndex);
                    Cell cell2 = row2.getCell(existCellIndex);
                    if (String.valueOf(getCellValue(cell2)).isEmpty()) {
                        rowsData.add(getByGivenAttributeAndRowValue(headRowData, row2, attributes));
                    } else {
                        // ��Ҫ�ϲ�
                        if (isMerge) {
                            // ��¼��Ҫ�ϲ�����Index
                            List<Integer> mergerIndex = new ArrayList<Integer>();
                            for (String mergerAttribute : mergeAttributes) {
                                Integer integerIndex = (Integer) headRowData.get(mergerAttribute);
                                if (integerIndex != null) {
                                    mergerIndex.add(integerIndex);
                                }
                            }
                            for (int i = 0; i < rowsData.size(); i++) {
                                List<Object> rowDatas = rowsData.get(i);
                                for (int j = 0; j < mergerIndex.size(); j++) {
                                    if (rowDatas.get(mergerIndex.get(j)).equals("")) {
                                        // �ڵ�һ�в�Ϊ�յ������
                                        if (i != 0) {
                                            Object value = rowsData.get(i - 1).get(mergerIndex.get(j));
                                            rowDatas.set(mergerIndex.get(j), value);
                                        }
                                    }
                                }
                            }
                        }
                        return rowsData;
                    }
                }
            }
        } else {
            System.out.println("������ʾ: Ҫ��ȡ��ͷ���ڡ�" + sheet.getSheetName() + "���е���ͷ����ȫ����, ����!");
        }
        return rowsData;// ��ʱ�����Ƿ���Null
    }

    /**
     * 
     * ����ȡ����Cell��Ԫ��Ϊ��������ʱ,ͨ����ʾ5λ����Double����,ת����Java��Date
     * 
     * @param cellDateValue
     *            ��Ԫ����������
     * @return ����Java Date����
     */
    public static Date getCellToDate(Object cellDateValue) {
        if(cellDateValue == null || cellDateValue.equals("")){
            return null;
        }
        double parseDouble = Double.parseDouble(cellDateValue.toString());
        Date javaDate = HSSFDateUtil.getJavaDate(parseDouble);
        return javaDate;
    }

    /**
     * 
     * ָ��Sheet���н���ɾ��ָ����ʼ���������м����
     * 
     * @param sheet
     *            ָ����Sheet��
     * @param startRow
     *            ָ���Ŀ�ʼ��
     * @param endRow
     *            ָ���Ľ�����
     */
    public static void deleteRows(Sheet sheet, int startRow, int endRow) {
        int lastRowNum = sheet.getLastRowNum();
        // ��ʼɾ���������ɾ���еķ�Χ�ڸ�Sheet��
        if (startRow < lastRowNum && endRow < lastRowNum) {
            sheet.shiftRows(startRow, endRow, -1);// ɾ����startRow�е�endRow�У�Ȼ��ʹ�·���Ԫ������
        }
        // ����ӱ��洦��
    }

    /**
     * 
     * ָ��Sheet���в��ҹؼ���
     * 
     * @param sheet
     *            ָ��Sheet��
     * @param keyWord
     *            ���ҵĹؼ���
     * @param isGoEnd
     *            �Ƿ�һ�ҵ���,����ҵ�һ����,�ͷ���,���Ǽ�����
     * @return ���ؿ���̨��ʾ��Ϣ
     */
    public static String isExistKeyWord(Sheet sheet, String keyWord, boolean isGoEnd) {
        if (isExistSheet(sheet)) {
            StringBuffer sbf = new StringBuffer();
            int findCount = 0;
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 0; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (isExistRow(row)) {
                    short lastCellNum = row.getLastCellNum();
                    for (int j = 0; j <= lastCellNum; j++) {
                        Cell cell = row.getCell(j);
                        if (isExistCell(cell)) {
                            Object cellValue = getCellValue(cell);
                            if (cellValue == null) {
                                continue;
                            } else {
                                if (String.valueOf(cellValue).contains(keyWord)) {
                                    // ����Sheet��ͣЪ����
                                    if (isGoEnd) {
                                        findCount++;
                                        // �˴����һ�����з�
                                        StringBuffer temp = new StringBuffer("��ʾ����Sheet��Ϊ��"+ sheet.getSheetName()   + "���еĵڡ�" + i + "���С�"   + j + "���в��ҵ��ؼ��֡�"   + keyWord + "��\n");
                                        System.out.println(temp.toString());
                                        sbf.append(temp);
                                    } else {
                                        return "��ʾ����Sheet��Ϊ��"   + sheet.getSheetName() + "���еĵڡ�" + i + "���С�" + j + "���в��ҵ��ؼ��֡�" + keyWord + "��";
                                    }
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("���ҽ�����ʾ����Sheet��Ϊ��" + sheet.getSheetName()    + "�����ܼ��ҵ��ؼ��֡�" + keyWord + "��������" + findCount);
            return sbf.toString();
        }
        return null;// ����ʾ������
    }
    
    /**
     * ʹ��ʾ��
     * @param args
     * @throws IOException
     * @throws ParseException 
     */
    public static void main(String[] args) throws IOException, ParseException {
    	SimpleDateFormat formate = new SimpleDateFormat("yyyyMMdd");
//        // ����excel�ļ�����
//        loadExcelFile("D:/�½� Microsoft Excel ������.xlsx");
//        // ��ȡָ��sheet���Ƶ�excel Sheet����
//        Sheet sheet = workbook.getSheet("Sheet1");
//        // ��֤�Ƿ����,��Щʱ��û�б�Ҫ��֤
//        if (isExistSheet(sheet)) {
//            // ��ȡSheet����к�
//            int lastRowNum = sheet.getLastRowNum();
//            // ��ȡ��ͷת����Map��ʽ�洢
//            Map<Object, Object> headMap = getRowDataToMap(sheet.getRow(0), true);
//            // ����Ҫ��ȡExcel�еı�ͷ��
//            String[] attributes = new String[] { "����ID", "Ӣ����", "������", "ʱ��",
//                    "̨����", "ǿ�ȵȼ�", "γ��", "����", "����", "���", "��ѹ", "����", "����",
//                    "6����Ȧ", "7����Ȧ", "8����Ȧ", "10����Ȧ" };
//            // ������Ϊ2��ʼ����ע�����ʵ���������
//            for (int i = 2; i <= lastRowNum; i++) {
//                // ��ȡ�ж���
//                Row row = sheet.getRow(i);
//                // ��֤�Ƿ������
//                if (isExistRow(row)) {
//                    // ��ȡһ��ָ���е���Ϣ
//                    List<Object> byGivenAttributeAndRowValue = getByGivenAttributeAndRowValue(headMap, row, attributes);
//                    for (int j = 0; j < byGivenAttributeAndRowValue.size(); j++) {
//                        // ����ĵ�Ԫ�����ڴ���
//                        if (j == 3) {
//                            System.out.print(getCellToDate(byGivenAttributeAndRowValue.get(j)) + "\t");
//                        }
//                        // ��ͨ�ĵ�Ԫ��ת�����ַ������
//                        else {
//                            System.out.print(String.valueOf(byGivenAttributeAndRowValue.get(j)) + "\t");    
//                        }
//                    }
//                    System.out.println();
//                }
//            }
//        }
//        // ���ٴ򿪻Ự���ļ�
//        closeDestory();
        
        

        // ����excel�ļ�����
        loadExcelFile("J:/WebSiteVisits.xlsx");
        // ��ȡָ��sheet���Ƶ�excel Sheet����
        Sheet sheet = workbook.getSheet("Sheet1");
        // ��֤�Ƿ����,��Щʱ��û�б�Ҫ��֤
        if (isExistSheet(sheet)) {
            // ��ȡSheet����к�
            int lastRowNum = sheet.getLastRowNum();
            // ��ȡ��ͷת����Map��ʽ�洢
            Map<Object, Object> headMap = getRowDataToMap(sheet.getRow(0), true);
            System.out.println(headMap);
            // ����Ҫ��ȡExcel�еı�ͷ��
            
            String[] attributes = new String[] { "��վid", "����","20180501","20180502"};
            ArrayList<String> dateList = TimeUtils.getListDate("2018-4-01", "2018-5-03");
//            // ������Ϊ2��ʼ����ע�����ʵ���������
            for (int i = 1; i <= lastRowNum; i++) {
                // ��ȡ�ж���
                Row row = sheet.getRow(i);
                int count = 0;
                // ��֤�Ƿ������
                if (isExistRow(row)) {
                	HashMap<String, Object> dataMap = new HashMap<String, Object>();
                    // ��ȡһ��ָ���е���Ϣ
                    List<Object> byGivenAttributeAndRowValue = getByGivenAttributeAndRowValue(headMap, row, attributes);
                    int dataIndex=0;
                    dataMap.put("wzid", String.valueOf(byGivenAttributeAndRowValue.get(0)));
    				dataMap.put("ym", String.valueOf(byGivenAttributeAndRowValue.get(1)));
                    for (int j = 0; j < byGivenAttributeAndRowValue.size(); j++) {
                    	if(j>=2){
                    		dataMap.put("gatherdate", dateList.get(dataIndex++));
                    		dataMap.put("visits", String.valueOf(byGivenAttributeAndRowValue.get(j)));
                    		System.out.println(dataMap);
                    		sqlSession.insert("addWebVisits",dataMap);
                    		 if(count++ == 30){
             					sqlSession.commit();
             					count=0;
             				}
                    	}
                    }
                }
            }
            sqlSession.commit();
            sqlSession.close();
        }
        // ���ٴ򿪻Ự���ļ�
        closeDestory();
        
    }
}