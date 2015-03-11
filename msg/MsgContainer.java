package resolver.msg;

import java.util.concurrent.ConcurrentHashMap;

import com.wk.eai.config.PackageConfig;

/**
 * @description ������������
 * @author raoliang
 * @version 2015��2��10�� ����10:41:15
 */
public class MsgContainer {
	/**
	 * �������������յ�����Ӧ����,key:match_id,value:��Ӧ����
	 */
	private static final ConcurrentHashMap<String, PacketsInfo> respMsgs = new ConcurrentHashMap<String, PacketsInfo>();
	/**
	 * �����Ѿ���������������,key:match_id,value:����ϵͳserverCode
	 */
	private static final ConcurrentHashMap<String, String> unpackedConf = new ConcurrentHashMap<String, String>();
	
	/**
	 * �����Ѿ��������������ĵı��������ã������������Ӧ
	 */
	private static final ConcurrentHashMap<String, PackageConfig> bodyConfigs = new ConcurrentHashMap<String, PackageConfig>();
	
	/**
	* @description �������������յ�����Ӧ����,key:match_id,value:��Ӧ����
	* @param info
	*/
	public static void putResponseMsg(String key, PacketsInfo info) {
		respMsgs.put(key, info);
	}
	
	/**
	* @description �����Ѿ��������������������ķ���ϵͳ����
	* @param match_id �������Ӧ��ʶ��
	* @param server ����ϵͳserverCode
	*/
	public static void putUnpackedServerCode(String match_id, String server) {
		unpackedConf.put(match_id, server);
	}
	
	/**
	* @description �����Ѿ��������������ĵı��������ã������������Ӧ
	* @param match_id �������Ӧ��ʶ��
	* @param bodyConfig	�����ĵı��������ã������������Ӧ
	*/
	public static void putUnpackedBodyConf(String match_id, PackageConfig bodyConfig) {
		bodyConfigs.put(match_id, bodyConfig);
	}
	
	/**
	* @description ����
	* @param match_id
	* @return
	*/
	public static PacketsInfo getResponseMsg(String match_id) {
		return respMsgs.get(match_id);
	}
	
	/**
	* @description ����match_id�õ��Ѿ��������������������ķ���ϵͳ����
	* @param match_id
	* @return
	*/
	public static String getUnpackedServerCode(String match_id) {
		return unpackedConf.get(match_id);
	}
	
	/**
	* @description ����match_id�õ��Ѿ��������ı��ĵı���������
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
