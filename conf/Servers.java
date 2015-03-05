package resolver.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.wk.lang.SystemException;

/**
 * @description 加载ip地址和服务系统的映射文件<br/>
 * <pre> ip地址和端口与对应的服务系统映射保存在<code>server.properties</code>
 * 文件中，每行表示一条映射，格式为：<code>ip:port=serverCode</code></pre>
 * @author raoliang
 * @version 2015年2月9日 下午6:36:38
 */
public class Servers extends Loader{

	private static HashMap<String, String> Servers = new HashMap<String, String>();
	private static final String serverFileName = "server.properties";
	
	public static void main(String[] args) {
		load();
		System.out.println(getServerByIp("127.0.0.1:8881"));
		System.out.println(getServerByIp("127.0.0.1:8882"));
		System.out.println(getServerByIp("127.0.0.1:8883"));
		System.out.println(getServerByIp("127.0.0.1:8884"));
	}
	
	public static void load() {
		File serverFile = getFile(serverFileName);
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(serverFile));
			BufferedReader in = new BufferedReader(reader);
			String line = "";
			while((line=in.readLine()) != null) {
				if(line.trim().length() == 0 || line.startsWith("#"))
					continue;
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
					.addScene("filePath", serverFileName)
					.addScene("line", line);
		}
		String[] servers = line.split("=");
		String ip_prot = servers[0].trim();
		String server = servers[1].trim();
		if(ip_prot.length() == 0 || server.length() == 0) {
			throw new SystemException(
					"SYS_RESOLVER_SERVER_MAPPING_CONFIG_CONTENT_ERROR")
					.addScene("filePath", serverFileName)
					.addScene("line", line);
		}
		Servers.put(ip_prot, server);
		logger.info("Load Server Address Mapping：{} -> {}", ip_prot, server);
	}
	
}
