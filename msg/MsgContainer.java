package unpacker.msg;

import java.util.HashMap;

/**
 * @description
 * @author raoliang
 * @version 2015��2��10�� ����10:41:15
 */
public class MsgContainer {
	//�������������յ�����Ӧ����
	private static final HashMap<String, PacketsInfo> respMsgs = new HashMap<String, PacketsInfo>();
	//�����Ѿ��������������ĵ�match_id
	private static final HashMap<String, String> unpackedConf = new HashMap<String, String>();
	
	public static void putResponseMsg(PacketsInfo info) {
		respMsgs.put(info.getMatch_id(), info);
	}
	
	public static void putUnpackedConf(String match_id, String server) {
		unpackedConf.put(match_id, server);
	}
	
	public static PacketsInfo getResponseMsg(String match_id) {
		return respMsgs.get(match_id);
	}
	
	public static String getUnpackedConf(String match_id) {
		return unpackedConf.get(match_id);
	}
	
	public static void removeResponseMsg(String match_id) {
		respMsgs.remove(match_id);
	}
	
	public static void removeUnpackedConf(String match_id) {
		unpackedConf.remove(match_id);
	}
	
}
