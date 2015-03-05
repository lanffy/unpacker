package resolver.conf;

import java.util.HashMap;

import com.wk.eai.config.PackageConfig;
import com.wk.lang.SystemException;
import com.wk.logging.Log;
import com.wk.logging.LogFactory;

/**
 * @description ������������
 * @author raoliang
 * @version 2015��2��9�� ����3:40:59
 */
public class Configs {
	public static final Log logger = LogFactory.getLog();

	private static final HashMap<String, PackageConfig> headConfigs = new HashMap<String, PackageConfig>();
	private static final HashMap<String, PackageConfig> bodyConfigs = new HashMap<String, PackageConfig>();
	
	/**
	* @description ���汨��ͷ����
	* @param serverCode ����ϵͳ����
	* @param value ����ͷ����
	*/
	public static void putHeadConfig(String serverCode, PackageConfig value) {
		headConfigs.put(serverCode, value);
	}
	
	/**
	* @description ���汨��������
	* @param serverCode ����ϵͳ����
	* @param tranCode ������
	* @param value ����������
	*/
	public static void putBodyConfig(String serverCode, String tranCode, PackageConfig value) {
		bodyConfigs.put(serverCode + "_" + tranCode, value);
	}
	
	public static PackageConfig getHeadConfig(String key) {
		if(headConfigs.containsKey(key))
			return (PackageConfig)headConfigs.get(key);
		logger.warn("�޴˷���ϵͳ����ͷ����,����ϵͳ����:[ {} ]", key);
		throw new SystemException("�޴˷���ϵͳ����ͷ����,��鿴����ϵͳ����ͷ�����Ƿ���ȷ")
			.addScene("ServerName", key);
	}
	
	public static PackageConfig getBodyConfig(String serverCode, String tranCode) {
		if(bodyConfigs.containsKey(serverCode + "_" + tranCode))
			return (PackageConfig)bodyConfigs.get(serverCode + "_" + tranCode);
		logger.warn("�޴˷�����������,����ϵͳ����:[ {} ], ����[ {} ]", serverCode, tranCode);
		throw new SystemException("�޴˷�����������,��鿴�������������Ƿ���ȷ")
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
