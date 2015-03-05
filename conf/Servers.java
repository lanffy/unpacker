package resolver.conf;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @description ����ip��ַ�ͷ���ϵͳ��ӳ���ļ�<br/>
 * <pre> ip��ַ�Ͷ˿����Ӧ�ķ���ϵͳӳ�䱣����<code>server.properties</code>
 * �ļ��У�ÿ�б�ʾһ��ӳ�䣬��ʽΪ��<code>ip:port=serverCode</code></pre>
 * @author raoliang
 * @version 2015��2��9�� ����6:36:38
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
