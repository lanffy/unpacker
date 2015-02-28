package resolver.msg;

import com.wk.sdo.ServiceData;

/**
 * @description 解析器报文接口
 * @author raoliang
 * @version 2015年2月9日 下午4:16:04
 */
public class PacketsInfo {

	private ServiceData data;

	public PacketsInfo(ServiceData data) {
		this.data = data;
	}

	// agent_id 采集端ID 短字符串
	public String getAgent_id() {
		return data.getString("agent_id");
	}
	
	// msg_id 采集端消息ID 整型
	public int getMsg_id() {
		return data.getInt("msg_id");
	}
	
	// src_ip 发起系统IP 短字符串
	public String getSrc_ip() {
		return data.getString("src_ip");
	}
	
	// src_port 发起系统连接端口 整型
	public int getSrc_port() {
		return data.getInt("src_port");
	}
	
	// dst_ip 接收系统IP 短字符串
	public String getDst_ip() {
		return data.getString("dst_ip");
	}
	
	// dst_prot 接收系统连接端口 整型
	public int getDst_prot() {
		return data.getInt("dst_prot");
	}
	
	// packet_type 报文类型（请求、响应） 整型
	public int getPacket_type() {
		return data.getInt("packet_type");
	}
	
	// match_id 报文匹配ID 短字符串
	public String getMatch_id() {
		return data.getString("match_id");
	}
	
	// send_time 发送时间 短字符串
	public String getSend_time() {
		return data.getString("send_time");
	}
	
	// has_ack 是否收到对方ACK 整型
	public int getHas_ack() {
		return data.getInt("has_ack");
	}
	
	// ack_time 收到ACK的时间 短字符串
	public String getAck_time() {
		return data.getString("ack_time");
	}
	
	// packet 应用报文 长字节流
	public byte[] getPacket() {
		return data.getImage("packet");
	}

	/**
	// agent_id 采集端ID 短字符串
	private String agent_id;
	// msg_id 采集端消息ID 整型
	private int msg_id;
	// src_ip 发起系统IP 短字符串
	private String src_ip;
	// src_port 发起系统连接端口 整型
	private int src_port;
	// dst_ip 接收系统IP 短字符串
	private String dst_ip;
	// dst_prot 接收系统连接端口 整型
	private int dst_prot;
	// packet_type 报文类型（请求、响应） 整型
	private int packet_type;
	// match_id 报文匹配ID 短字符串
	private String match_id;
	// send_time 发送时间 短字符串
	private String send_time;
	// has_ack 是否收到对方ACK 整型
	private int has_ack;
	// ack_time 收到ACK的时间 短字符串
	private String ack_time;
	// packet 应用报文 长字节流
	private byte[] packet;
	*/
}
