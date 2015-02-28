package resolver.msg;

import com.wk.sdo.ServiceData;

/**
 * @description ���������Ľӿ�
 * @author raoliang
 * @version 2015��2��9�� ����4:16:04
 */
public class PacketsInfo {

	private ServiceData data;

	public PacketsInfo(ServiceData data) {
		this.data = data;
	}

	// agent_id �ɼ���ID ���ַ���
	public String getAgent_id() {
		return data.getString("agent_id");
	}
	
	// msg_id �ɼ�����ϢID ����
	public int getMsg_id() {
		return data.getInt("msg_id");
	}
	
	// src_ip ����ϵͳIP ���ַ���
	public String getSrc_ip() {
		return data.getString("src_ip");
	}
	
	// src_port ����ϵͳ���Ӷ˿� ����
	public int getSrc_port() {
		return data.getInt("src_port");
	}
	
	// dst_ip ����ϵͳIP ���ַ���
	public String getDst_ip() {
		return data.getString("dst_ip");
	}
	
	// dst_prot ����ϵͳ���Ӷ˿� ����
	public int getDst_prot() {
		return data.getInt("dst_prot");
	}
	
	// packet_type �������ͣ�������Ӧ�� ����
	public int getPacket_type() {
		return data.getInt("packet_type");
	}
	
	// match_id ����ƥ��ID ���ַ���
	public String getMatch_id() {
		return data.getString("match_id");
	}
	
	// send_time ����ʱ�� ���ַ���
	public String getSend_time() {
		return data.getString("send_time");
	}
	
	// has_ack �Ƿ��յ��Է�ACK ����
	public int getHas_ack() {
		return data.getInt("has_ack");
	}
	
	// ack_time �յ�ACK��ʱ�� ���ַ���
	public String getAck_time() {
		return data.getString("ack_time");
	}
	
	// packet Ӧ�ñ��� ���ֽ���
	public byte[] getPacket() {
		return data.getImage("packet");
	}

	/**
	// agent_id �ɼ���ID ���ַ���
	private String agent_id;
	// msg_id �ɼ�����ϢID ����
	private int msg_id;
	// src_ip ����ϵͳIP ���ַ���
	private String src_ip;
	// src_port ����ϵͳ���Ӷ˿� ����
	private int src_port;
	// dst_ip ����ϵͳIP ���ַ���
	private String dst_ip;
	// dst_prot ����ϵͳ���Ӷ˿� ����
	private int dst_prot;
	// packet_type �������ͣ�������Ӧ�� ����
	private int packet_type;
	// match_id ����ƥ��ID ���ַ���
	private String match_id;
	// send_time ����ʱ�� ���ַ���
	private String send_time;
	// has_ack �Ƿ��յ��Է�ACK ����
	private int has_ack;
	// ack_time �յ�ACK��ʱ�� ���ַ���
	private String ack_time;
	// packet Ӧ�ñ��� ���ֽ���
	private byte[] packet;
	*/
}
