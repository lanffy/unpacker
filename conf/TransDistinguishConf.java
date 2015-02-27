package resolver.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * @description
 * @author raoliang
 * @version 2015年2月27日 下午2:01:08
 */
public class TransDistinguishConf extends Loader{
	private static HashMap<String, String> trans = new HashMap<String, String>();
	private static final String transConfFilePath = basePath + "tranDist.properties";
	
	public static void main(String[] args) {
		loadTransDistConf();
		System.out.println(getTranDistField("hxzh-nb"));
	}
	
	public static void loadTransDistConf() {
		File confFile = getFile(transConfFilePath);
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(confFile));
			BufferedReader in = new BufferedReader(reader);
			String line = "";
			while((line=in.readLine()) != null && line.length() != 0) {
				if(!line.trim().startsWith("#"))
					putConf(line);
			}
			in.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("Load Transaction Distinguish Config End ==========================");
	}
	
	public static String getTranDistField(String serviceName) {
		return trans.get(serviceName);
	}
	
	private static void putConf(String line) {
		String[] confs = line.split("=");
		String service = confs[0].trim();
		String tran_field = confs[1].trim();
		trans.put(service, tran_field);
		logger.info("Load Transaction Distinguish Config：{} -> {}", service, tran_field);
	}
}
