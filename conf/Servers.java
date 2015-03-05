package resolver.conf;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @description 加载ip地址和服务系统的映射文件<br/>
 * <pre> ip地址和端口与对应的服务系统映射保存在<code>server.properties</code>
 * 文件中，每行表示一条映射，格式为：<code>ip:port=serverCode</code></pre>
 * @author raoliang
 * @version 2015年2月9日 下午6:36:38
 */
public class Servers extends Loader{

	private static final ConcurrentHashMap<String, String> Servers = new ConcurrentHashMap<String, String>();
	private static final String serverFileName = "server.properties";
	
	public static void main(String[] args) {
		load();
		System.out.println(getServerByIp("127.0.0.1+8881"));
		System.out.println(getServerByIp("127.0.0.1+8882"));
		System.out.println(getServerByIp("127.0.0.1+8883"));
		System.out.println(getServerByIp("127.0.0.1+8884"));
	}
	
	public static void load() {
		_load(serverFileName, Servers, "Server Address Mapping");
	}
	
	public static String getServerByIp(String ip) {
		return Servers.get(ip);
	}
	
}
