package resolver.conf;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.wk.lang.SystemException;

/**
 * @description 加载需要解密报文的服务系统配置
 * @author raoliang
 * @version 2015年3月5日 上午9:42:42
 */
public class DecryptServerConf extends Loader{
	private static final String decServerConfFileName = "decrypt.properties";
	
	private static final HashMap<String, String> requestMap = new HashMap<String, String>();
	private static final HashMap<String, String> responseMap = new HashMap<String, String>();
	
	private static final Properties prop = new Properties();
	
	public static void main(String[] args) {
		load();
		for(Map.Entry<String, String> entry : requestMap.entrySet()) {
			System.out.format("key:%s,value:%s\n", entry.getKey(), entry.getValue());
		}
	}
	
	public static void load() {
		loadConf();
	}
	
	public static String getRequestDecClz(String serverName) {
		return requestMap.get(serverName);
	}
	
	public static String getResponseDecClz(String serverName) {
		return responseMap.get(serverName);
	}
	
	private static void loadConf() {
		File file = getFile(decServerConfFileName);
		System.out.println(file.exists());
		try {
			prop.load(new FileInputStream(file));
		} catch (Exception e) {
			throw new SystemException("Config File Not Exist").addScene("fileName", decServerConfFileName);
		}
		
		String[] requests = prop.getProperty("request").split(",");
		String[] responses = prop.getProperty("response").split(",");
		
		loadConf(requests, requestMap);
		loadConf(responses, responseMap);
		logger.info("Load Decrypt Server Config End ==========================");
	}
	
	private static void loadConf(String[] configs, HashMap<String, String> map) {
		String value;
		for(String key : configs) {
			value = prop.getProperty(key);
			if(value == null) {
				throw new SystemException("Decrypt Server Should Not Null").addScene("ServerName", key);
			}else {
				map.put(key, value);
				logger.info("Load Decrypt Server Config：[{}] -> [{}]", key, value);
			}
		}
	}
	
}
