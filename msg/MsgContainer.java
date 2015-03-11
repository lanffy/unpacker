package resolver.msg;

import java.util.concurrent.ConcurrentHashMap;

import com.wk.eai.config.PackageConfig;

/**
 * @description 报文容器工具
 * @author raoliang
 * @version 2015年2月10日 上午10:41:15
 */
public class MsgContainer {
	/**
	 * 保存先于请求收到的响应报文,key:match_id,value:响应报文
	 */
	private static final ConcurrentHashMap<String, PacketsInfo> respMsgs = new ConcurrentHashMap<String, PacketsInfo>();
	/**
	 * 保存已经解析过的请求报文,key:match_id,value:服务系统serverCode
	 */
	private static final ConcurrentHashMap<String, String> unpackedConf = new ConcurrentHashMap<String, String>();
	
	/**
	 * 保存已经解析过的请求报文的报文体配置，包括请求和响应
	 */
	private static final ConcurrentHashMap<String, PackageConfig> bodyConfigs = new ConcurrentHashMap<String, PackageConfig>();
	
	/**
	* @description 保存先于请求收到的响应报文,key:match_id,value:响应报文
	* @param info
	*/
	public static void putResponseMsg(String key, PacketsInfo info) {
		respMsgs.put(key, info);
	}
	
	/**
	* @description 保存已经解析过的请求报文所属的服务系统名称
	* @param match_id 请求和响应标识码
	* @param server 服务系统serverCode
	*/
	public static void putUnpackedServerCode(String match_id, String server) {
		unpackedConf.put(match_id, server);
	}
	
	/**
	* @description 保存已经解析过的请求报文的报文体配置，包括请求和响应
	* @param match_id 请求和响应标识码
	* @param bodyConfig	请求报文的报文体配置，包括请求和响应
	*/
	public static void putUnpackedBodyConf(String match_id, PackageConfig bodyConfig) {
		bodyConfigs.put(match_id, bodyConfig);
	}
	
	/**
	* @description 根据
	* @param match_id
	* @return
	*/
	public static PacketsInfo getResponseMsg(String match_id) {
		return respMsgs.get(match_id);
	}
	
	/**
	* @description 根据match_id得到已经解析过的请求报文所属的服务系统名称
	* @param match_id
	* @return
	*/
	public static String getUnpackedServerCode(String match_id) {
		return unpackedConf.get(match_id);
	}
	
	/**
	* @description 根据match_id得到已经解析过的报文的报文体配置
	* @param match_id
	* @return
	*/
	public static PackageConfig getUnpackedBodyConf(String match_id) {
		return bodyConfigs.get(match_id);
	}
	
	public static void removeResponseMsg(String match_id) {
		respMsgs.remove(match_id);
	}
	
	public static void removeUnpackedConf(String match_id) {
		unpackedConf.remove(match_id);
	}
	
	public static void removeUnpackedBodyConf(String match_id) {
		bodyConfigs.remove(match_id);
	}
	
	
}
