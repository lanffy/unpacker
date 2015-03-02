package resolver.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.wk.lang.SystemException;

/**
 * @description ���ظ�������ϵͳ����ͷ�б�ʾ�������׵��ֶ�<br/>
 * <pre>�����ļ���tranDist.properties.ÿ�б�ʾһ������ϵͳ���á�
 * �����ʽΪ��<strong>����ϵͳ����=����ͷ�б�ʾ�������׵��ֶ�����</strong>
 * ����ֶδ���һ���ṹ�����ʽΪ��
 * <strong>����ϵͳ����=�ṹ����>�������׵��ֶ�����</strong>
 * ����ֶδ��ڶ��Ƕ�׵Ľṹ�У���ʽΪ��
 * <strong>����ϵͳ����=�ṹ����1>�ṹ����2>�������׵��ֶ�����</strong></pre>
 * @author raoliang
 * @version 2015��2��27�� ����2:01:08
 */
public class TransDistinguishConf extends Loader{
	private static HashMap<String, String> trans = new HashMap<String, String>();
	private static final String transConfFilePath = basePath + "tranDist.properties";
	
	public static void main(String[] args) {
		loadTransDistConf();
		System.out.println(getTranDistField("outsys"));
		System.out.println(getTranDistField("hxzh-nb"));
		System.out.println(getTranDistField("hxzh-tb"));
	}
	
	public static void loadTransDistConf() {
		File confFile = getFile(transConfFilePath);
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(confFile));
			BufferedReader in = new BufferedReader(reader);
			String line = "";
			while((line=in.readLine()) != null) {
				if(line.trim().length() == 0 || line.startsWith("#"))
					continue;
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
		if(line.indexOf("=") < 0) {
			throw new SystemException(
					"SYS_RESOLVER_TRANDIST_MAPPING_CONFIG_FORMAT_ERROR")
					.addScene("filePath", transConfFilePath)
					.addScene("line", line);
		}
		String[] confs = line.split("=");
		String service = confs[0].trim();
		String tran_field = confs[1].trim();
		if(service.length() == 0 || tran_field.length() == 0) {
			throw new SystemException(
					"SYS_RESOLVER_TRANDIST_MAPPING_CONFIG_CONTENT_ERROR")
					.addScene("filePath", transConfFilePath)
					.addScene("line", line);
		}
		trans.put(service, tran_field);
		logger.info("Load Transaction Distinguish Config��{} -> {}", service, tran_field);
	}
}
