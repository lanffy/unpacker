package unpacker.msg;

import unpacker.util.BufferReader;

import com.wk.nio.ChannelBuffer;

/**
 * @description ��ģ�ⱨ�ģ����ڲ���
 * @author raoliang
 * @version 2015��2��2�� ����2:12:40
 */
public class SimulateMsg {
	private static ChannelBuffer buffer = ChannelBuffer.allocate(1024);
	// agent_id �ɼ���ID ���ַ���
	private static String agent_id = "agent_id_value";
	// msg_id �ɼ�����ϢID ����
	private static int msg_id = 1;
	// src_ip ����ϵͳIP ���ַ���
	private static String src_ip = "127.0.0.1";
	// src_port ����ϵͳ���Ӷ˿� ����
	private static int src_port = 8083;
	// dst_ip ����ϵͳIP ���ַ���
	private static String dst_ip = "10.1.1.227";
	// dst_port ����ϵͳ���Ӷ˿� ����
	private static int dst_prot = 9101;
	// packet_type �������ͣ�������Ӧ�� ����
	private static int packet_type = 2;
	// mactch_id ����ƥ��ID ���ַ���
	private static String mactch_id = "0808";
	// send_time ����ʱ�� ���ַ���
	private static String send_time = "2015-02-02 15:39:44.000";
	// has_ack �Ƿ��յ��Է�ACK ����
	private static int has_ack = 1;
	// ack_time �յ�ACK��ʱ�� ���ַ���
	private static String ack_time = "2015-02-02 15:40:22.123";
	// packet Ӧ�ñ��� ���ֽ���
	private static byte[] packet;
	
	public static ChannelBuffer packHeadBuffer(ChannelBuffer sendedBuffer) {
		buffer.putInt(0);
		putStringAttr("agent_id", agent_id);
		putIntAttr("msg_id", msg_id);
		putStringAttr("src_ip", src_ip);
		putIntAttr("src_port", src_port);
		putStringAttr("dst_ip", dst_ip);
		putIntAttr("dst_prot", dst_prot);
		putIntAttr("packet_type", packet_type);
		putStringAttr("mactch_id", mactch_id);
		putStringAttr("send_time", send_time);
		putIntAttr("has_ack", has_ack);
		putStringAttr("ack_time", ack_time);
		packet = new byte[sendedBuffer.readableBytes()];
		sendedBuffer.getBytes(packet);
		putBytesAttr("packet", packet);
		buffer.putInt(0, buffer.readableBytes());
		return buffer;
	}
	
	public static void putStringAttr(String attr_name, String attr_value) {
		//�ֶ�������
		buffer.putByte((byte) attr_name.length());
		//�ֶ���
		buffer.putBytes(attr_name.getBytes());
		//�ֶ�����
		buffer.putByte((byte)0x07);
		//�ֶ�ֵ����
		buffer.putByte((byte)attr_value.length());
		//�ֶ�ֵ
		buffer.putBytes(attr_value.getBytes());
	}
	
	public static void putIntAttr(String attr_name, int attr_value) {
		//�ֶ�������
		buffer.putByte((byte) attr_name.length());
		//�ֶ���
		buffer.putBytes(attr_name.getBytes());
		//�ֶ�����
		buffer.putByte((byte)0x05);
		//�ֶ�ֵ����
		//�ֶ�ֵ
		buffer.putInt(attr_value);
	}
	
	public static void putBytesAttr(String attr_name, byte[] bytes) {
		int len = bytes.length;
		// �ֶ�������
		buffer.putByte((byte) attr_name.length());
		// �ֶ���
		buffer.putBytes(attr_name.getBytes());
		// �ֶ�����  ���ֽ���
		buffer.putByte((byte) 0x12);
		// �ֶ�ֵ����
		buffer.putShort((short) len);
		// �ֶ�ֵ
		buffer.putBytes(bytes);
	}
	
	public static void main(String[] args) {
		ChannelBuffer sendedBuffer = BufferReader.createRequestMsg("8813resp");
		ChannelBuffer buffer = packHeadBuffer(sendedBuffer);
		System.out.println(buffer.toHexString());
		System.out.println(buffer.readableBytes());
		System.out.println(buffer.capacity());
	}
}
