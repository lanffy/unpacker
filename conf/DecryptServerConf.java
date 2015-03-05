package resolver.conf;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.wk.lang.SystemException;

/**
 * @description 加载需要解密报文的服务系统配置
 * @author raoliang
 * @version 2015年3月5日 上午9:42:42
 */
public class DecryptServerConf extends Loader{
	private static final String decServerConfFileName = "decrypt.properties";
	
	private static final ConcurrentHashMap<String, String> requestMap = new ConcurrentHashMap<String, String>();
	private static final ConcurrentHashMap<String, String> responseMap = new ConcurrentHashMap<String, String>();
	
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
		try {
			prop.load(new FileInputStream(file));
		} catch (Exception e) {
			throw new SystemException("Config File Not Exist").addScene("fileName", decServerConfFileName);
		}
		
		String[] requests = prop.getProperty("request").split(",");
		String[] responses = prop.getProperty("response").split(",");
		
		loadConf(requests, requestMap, "Request");
		loadConf(responses, responseMap, "Response");
		logger.info("Load Decrypt Server Config End ==========================");
	}
	
	private static void loadConf(String[] configs, ConcurrentHashMap<String, String> map, String flag) {
		String value;
		for(String key : configs) {
			value = prop.getProperty(key);
			if(value == null) {
				throw new SystemException("Decrypt Server Should Not Null").addScene("ServerName", key);
			}else {
				map.put(key, value);
				logger.info("Load Server {} Decrypt Config：[{}] -> [{}]", flag, key, value);
			}
		}
	}
	
}
