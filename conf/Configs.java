package unpacker.conf;

import java.util.HashMap;

import com.wk.eai.config.PackageConfig;
import com.wk.lang.SystemException;

/**
 * @description
 * @author raoliang
 * @version 2015年2月9日 下午3:40:59
 */
public class Configs extends Loader{

	private static final HashMap<String, PackageConfig> headConfigs = new HashMap<String, PackageConfig>();
	private static final HashMap<String, PackageConfig> bodyConfigs = new HashMap<String, PackageConfig>();
	
	public static void putHeadConfig(String serverCode, PackageConfig value) {
		headConfigs.put(serverCode, value);
	}
	
	public static void putBodyConfig(String serverCode, String tranCode, PackageConfig value) {
		bodyConfigs.put(serverCode + "_" + tranCode, value);
	}
	
	public static PackageConfig getHeadConfig(String key) {
		if(headConfigs.containsKey(key))
			return (PackageConfig)headConfigs.get(key);
		logger.warn("无此服务系统报文头配置,服务系统名称:[ {} ]", key);
		throw new SystemException("无此服务系统报文头配置,请查看服务系统报文头配置是否正确")
			.addScene("ServerName", key);
	}
	
	public static PackageConfig getBodyConfig(String serverCode, String tranCode) {
		if(bodyConfigs.containsKey(serverCode + "_" + tranCode))
			return (PackageConfig)bodyConfigs.get(serverCode + "_" + tranCode);
		logger.warn("无此服务报文体配置,服务系统名称:[ {} ], 服务：[ {} ]", serverCode, tranCode);
		throw new SystemException("无此服务报文体配置,请查看服务报文体配置是否正确")
			.addScene("ServerCode", serverCode)
			.addScene("tranCode", tranCode);
	}
	
	public static HashMap<String, PackageConfig> getHeadConfigMap() {
		return headConfigs;
	}
	
	public static HashMap<String, PackageConfig> getBodyConfigMap() {
		return bodyConfigs;
	}
}
