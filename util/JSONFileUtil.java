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
 * <ol>此工具类有以下几个功能
 * <li>读取json格式文件，将其内容转换成ServiceData格式</li>
 * <li>将ServiceData格式内容以json的格式存入文件中</li>
 * </ol>
 * @author raoliang
 * @version 2014年11月5日 上午11:18:30
 */
public class JSONFileUtil {
	private static final Log logger = LogFactory.getLog("unpacker");
	
	/**
	* @description 读取json格式文件内容，转换成ServiceData格式
	* @param fileName json格式文件路径名称
	* @return ServiceData格式内容
	* @author raoliang
	* @version 2014年11月5日 上午10:28:00
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
	* @description 读取json格式文件内容，转换成ServiceData格式
	* @param file json格式文件
	* @return ServiceData格式内容
	* @author raoliang
	* @version 2014年11月5日 上午10:28:02
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
			logger.warn("JSON文件中无数据,文件:{}", file.getAbsolutePath());
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
