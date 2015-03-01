package resolver.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.wk.lang.SystemException;

/**
 * @description ����ip��ַ�ͷ���ϵͳ��ӳ���ļ�<br/>
 * <pre> ip��ַ�Ͷ˿����Ӧ�ķ���ϵͳӳ�䱣����<code>server.properties</code>
 * �ļ��У�ÿ�б�ʾһ��ӳ�䣬��ʽΪ��<code>ip:port=serverCode</code></pre>
 * @author raoliang
 * @version 2015��2��9�� ����6:36:38
 */
public class Servers extends Loader{

	private static HashMap<String, String> Servers = new HashMap<String, String>();
	private static final String serverFilePath = basePath + "server.properties";
	
	public static void main(String[] args) {
		loadServer();
		System.out.println(getServerByIp("127.0.0.1:8881"));
	}
	
	public static void loadServer() {
		File serverFile = getFile(serverFilePath);
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(serverFile));
			BufferedReader in = new BufferedReader(reader);
			String line = "";
			while((line=in.readLine()) != null && line.trim().length() != 0) {
				if(!line.trim().startsWith("#"))
					putServer(line);
			}
			in.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("Load Server Address Mapping End ==========================");
	}
	
	public static String getServerByIp(String ip) {
		return Servers.get(ip);
	}
	
	private static void putServer(String line) {
		if(line.indexOf("=") < 0) {
			throw new SystemException(
					"SYS_RESOLVER_SERVER_MAPPING_CONFIG_FORMAT_ERROR")
					.addScene("filePath", serverFilePath)
					.addScene("line", line);
		}
		String[] servers = line.split("=");
		String ip_prot = servers[0].trim();
		String server = servers[1].trim();
		if(ip_prot.length() == 0 || server.length() == 0) {
			throw new SystemException(
					"SYS_RESOLVER_SERVER_MAPPING_CONFIG_CONTENT_ERROR")
					.addScene("filePath", serverFilePath)
					.addScene("line", line);
		}
		Servers.put(ip_prot, server);
		logger.info("Load Server Address Mapping��{} -> {}", ip_prot, server);
	}
	
}
