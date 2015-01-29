package unpacker.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wk.lang.SystemException;
import com.wk.logging.Log;
import com.wk.logging.LogFactory;
import com.wk.sdo.ServiceData;
import com.wk.util.JSON;
import com.wk.util.JSONCaseType;
import com.wk.util.StringUtil;

/**
 * @description 
 * <ol>�˹����������¼�������
 * <li>��ȡjson��ʽ�ļ�����������ת����ServiceData��ʽ</li>
 * <li>��ServiceData��ʽ������json�ĸ�ʽ�����ļ���</li>
 * </ol>
 * @author raoliang
 * @version 2014��11��5�� ����11:18:30
 */
public class JSONFileUtil {
	private static final Log logger = LogFactory.getLog("dbcompare");
	/**
	* @description ServiceData��ʽ����ת����json�ַ���
	* @param data ����Դ
	* @return json�ַ���
	* @author raoliang
	* @version 2014��11��5�� ����10:27:58
	*/
	public static String convertServiceDataToJson(ServiceData data) {
		return JSON.fromServiceData(data, JSONCaseType.DEFAULT);
	}
	
	/**
	* @description ��ServiceData��json��ʽд���ļ���
	* @param data ����Դ
	* @param filePath �ļ�·��
	* @author raoliang
	* @version 2014��11��5�� ����11:17:13
	*/
	public static void storeServiceDataToJsonFile(ServiceData data, String filePath){
		creatNewFile(filePath);
		storeServiceDataToJsonFile(data, new File(filePath));
	}
	
	/**
	* @description ��ServiceData��json��ʽд���ļ���
	* @param data ����Դ
	* @param file �ļ�
	* @author raoliang
	* @version 2014��11��5�� ����11:15:58
	*/
	public static void storeServiceDataToJsonFile(ServiceData data, File file) {
		String json = "";
//		logger.warn("���ļ�{}��д����\n{}", file.getAbsolutePath(), data);
		FileWriter writer = null;
		try {
			json = convertServiceDataToJson(data);
			writer = new FileWriter(file);
			writer.write(json);
		} catch (IOException e) {
			throw new SystemException("SYS_DB_COMPARE_GET_FILE_WRITER_ERROR")
					.addScene("filePath", file.getAbsolutePath());
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					throw new SystemException(
							"SYS_DB_COMPARE_CLOSE_FILE_WRITER_ERROR").addScene(
							"filePath", file.getAbsolutePath());
				}
			}
		}
	}
	
	/**
	* @description ��ȡjson��ʽ�ļ����ݣ�ת����ServiceData��ʽ
	* @param fileName json��ʽ�ļ�·������
	* @return ServiceData��ʽ����
	* @author raoliang
	* @version 2014��11��5�� ����10:28:00
	*/
	public static ServiceData loadJsonFileToServiceData(String fileName) {
		isFileExist(fileName);
		return loadJsonFileToServiceData(new File(fileName));
	}
	
	/**
	* @description ��ȡjson��ʽ�ļ����ݣ�ת����ServiceData��ʽ
	* @param file json��ʽ�ļ�
	* @return ServiceData��ʽ����
	* @author raoliang
	* @version 2014��11��5�� ����10:28:02
	*/
	public static ServiceData loadJsonFileToServiceData(File file) {
		isFileExist(file);
        String json = "";
		try {
			json = readFileToString(file);
		} catch (IOException e) {
			throw new SystemException("SYS_DB_COMPARE_READ_FILE_TO_SERVICEDATA_ERROR")
				.addScene("filePath", file.getAbsolutePath());
		}
		if(json.length() == 0){
			logger.warn("JSON�ļ���������,�ļ�:{}", file.getAbsolutePath());
			return null;
		}
		ServiceData data = JSON.toServiceDataByType(json, JSONCaseType.DEFAULT);
//		logger.info("���ļ�{}�ж�ȡ����\n{}", file.getAbsolutePath(), data);
		return data;
    }
	
	private static String readFileToString(File file) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = "";
		StringBuilder sb = new StringBuilder();
		while((line = reader.readLine()) != null){
			sb.append(line);
		}
		reader.close();
		return sb.toString();
	}
	
	/**
	* @description ���ļ���ȡ���ݵ������У�ÿһ��Ϊһ��Ԫ��
	* @param file
	* @return
	* @author raoliang
	* @version 2014��11��18�� ����11:31:46
	*/
	public static List<String> readFileToStringArray(File file){
		List<String> list = new ArrayList<String>();
		if(!file.exists()){
			return list;
		}
		BufferedReader reader = null;
		String line = "";
		try {
			reader = new BufferedReader(new FileReader(file));
			while(!StringUtil.isEmpty((line = reader.readLine()))){
				list.add(line);
			}
			reader.close();
		} catch (IOException e) {
			throw new SystemException("SYS_DB_COMPARE_READ_FILE_TO_STRING_ARRAY_ERROR")
			.addScene("filePath", file.getAbsolutePath());
		}
		return list;
	}
	
	private static void isFileExist(String filePath){
		File file = new File(filePath);
		isFileExist(file);
	}
	
	private static void isFileExist(File file){
		if (!file.exists()) {
			throw new SystemException("SYS_DB_COMPARE_FILE_IS_NOT_EXIST")
					.addScene("filePath", file.getAbsolutePath());
		}
	}
	
	private static void creatNewFile(String filePath){
		File file = new File(filePath);
		if(file.exists()){
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new SystemException("SYS_DB_COMPARE_CREATE_FILE_ERROR")
			.addScene("filePath", filePath);
		}
	}
}