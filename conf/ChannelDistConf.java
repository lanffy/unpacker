package resolver.conf;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @description 加载渠道识别配置<br/>
 * <pre>保存文件：channels.properties.每行表示一个渠道映射配置。
 * 保存格式为：<strong>src_ip:+dst_ip:dst_port=渠道名称</strong>
 * @author raoliang
 * @version 2015年3月3日 下午5:11:03
 */
public class ChannelDistConf extends Loader {
	private static final ConcurrentHashMap<String, String> channels = new ConcurrentHashMap<String, String>();
	private static final String channelsFileName = "channels.properties"; 
	
	public static void main(String[] args) {
		load();
		System.out.println(getChannelName("123.123.123.86+127.0.0.1+8883"));
	}
	
	public static void load() {
		_load(channelsFileName, channels, "Channel Mapping");
	}
	
	/**
	* @description
	* @param ips src_ip+dst_ip:dst_port
	* @return
	*/
	public static String getChannelName(String ips) {
		return channels.get(ips);
	}
	
}
