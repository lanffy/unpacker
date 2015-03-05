package resolver.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.wk.lang.SystemException;

/**
 * @description ��������ʶ������<br/>
 * <pre>�����ļ���channels.properties.ÿ�б�ʾһ������ӳ�����á�
 * �����ʽΪ��<strong>src_ip:+dst_ip:dst_port=��������</strong>
 * @author raoliang
 * @version 2015��3��3�� ����5:11:03
 */
public class ChannelDistConf extends Loader {
	private static HashMap<String, String> channels = new HashMap<String, String>();
	private static final String channelsFileName = "channels.properties"; 
	
	public static void main(String[] args) {
		load();
		System.out.println(channels.get("123.123.123.86+127.0.0.1:8883"));
	}
	
	public static void load() {
		File confFile = getFile(channelsFileName);
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(confFile));
			BufferedReader in = new BufferedReader(reader);
			String line = "";
			while((line=in.readLine()) != null) {
				if(line.trim().length() == 0 || line.startsWith("#") || !line.contains("="))
					continue;
				putConf(line);
			}
			in.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("Load Channel Mapping Config End ==========================");
	}
	
	/**
	* @description
	* @param ips src_ip+dst_ip:dst_port
	* @return
	*/
	public static String getChannelName(String ips) {
		return channels.get(ips);
	}
	
	private static void putConf(String line) {
		String[] confs = line.split("=");
		String ips = confs[0].trim();
		String channel = confs[1].trim();
		if(ips.length() == 0 || channel.length() == 0) {
			throw new SystemException(
					"SYS_RESOLVER_TRANDIST_MAPPING_CONFIG_CONTENT_ERROR")
					.addScene("filePath", channelsFileName)
					.addScene("line", line);
		}
		channels.put(ips, channel);
		logger.info("Load Channel Mapping Config��[{}] -> {}", ips, channel);
	}
}
