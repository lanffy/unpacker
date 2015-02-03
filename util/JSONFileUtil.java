package unpacker.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import com.wk.lang.SystemException;
import com.wk.logging.Log;
import com.wk.logging.LogFactory;
import com.wk.sdo.ServiceData;
import com.wk.util.JSON;
import com.wk.util.JSONCaseType;
import compare.DBCompare;

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
	private static final Log logger = LogFactory.getLog("unpacker");
	
	/**
	* @description ��ȡjson��ʽ�ļ����ݣ�ת����ServiceData��ʽ
	* @param fileName json��ʽ�ļ�·������
	* @return ServiceData��ʽ����
	* @author raoliang
	* @version 2014��11��5�� ����10:28:00
	*/
	public static ServiceData loadJsonFileToServiceData(String fileName) {
		URL url = JSONFileUtil.class.getResource(fileName);
		if(url == null) {
			throw new SystemException("SYS_LOAD_JSON_FILE_NOT_EXIST").addScene("fileName", fileName);
		}
		File file = new File(url.getFile());
		return loadJsonFileToServiceData(file);
	}
	
	/**
	* @description ��ȡjson��ʽ�ļ����ݣ�ת����ServiceData��ʽ
	* @param file json��ʽ�ļ�
	* @return ServiceData��ʽ����
	* @author raoliang
	* @version 2014��11��5�� ����10:28:02
	*/
	public static ServiceData loadJsonFileToServiceData(File file) {
        String json = "";
		try {
			json = readFileToString(file);
		} catch (IOException e) {
			throw new SystemException("SYS_UNPACKER_READ_JSON_FILE_ERROR")
				.addScene("filePath", file.getAbsolutePath());
		}
		if(json.length() == 0){
			logger.warn("JSON�ļ���������,�ļ�:{}", file.getAbsolutePath());
			return null;
		}
		ServiceData data = null;
//		try {
			data = JSON.toServiceDataByType(json, JSONCaseType.DEFAULT);
//		} catch (Exception e) {
//			throw new SystemException("SYS_UNPACKER_CONVERT_JSON_TO_SERVICEDATA_ERROR").addScene("json_file", file.getAbsolutePath());
//		}
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
	
}
