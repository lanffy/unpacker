package resolver.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import com.wk.lang.SystemException;

/**
 * @description
 * @author raoliang
 * @version 2015年2月9日 下午6:36:38
 */
public class Servers extends Loader{

	private static HashMap<String, String> Servers = new HashMap<String, String>();
	private static final String serverFilePath = basePath + "server.properties";
	
	public static void main(String[] args) {
		loadServer();
		System.out.println(getServerByIp("127.0.0.1:8080"));
	}
	
	public static void loadServer() {
		URL url = Servers.class.getResource(serverFilePath);
		if(url == null) {
			throw new SystemException("IP-Server Mapping File Is Not Exist").addScene("FilePath", serverFilePath);
		}
		File serverFile = new File(url.getFile());
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(serverFile));
			BufferedReader in = new BufferedReader(reader);
			String line = "";
			while((line=in.readLine()) != null && line.length() != 0) {
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
		return (String)Servers.get(ip);
	}
	
	private static void putServer(String line) {
		String[] servers = line.split("=");
		String ip_prot = servers[0].trim();
		String server = servers[1].trim();
		Servers.put(ip_prot, server);
		logger.info("Load Server Address Mapping：{} -> {}", ip_prot, server);
	}
}
