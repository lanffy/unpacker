package resolver.msg;

import java.text.SimpleDateFormat;
import java.util.Date;

import resolver.util.BufferReader;

import com.wk.nio.ChannelBuffer;

/**
 * @description ��ģ�ⱨ�ģ����ڲ���
 * @author raoliang
 * @version 2015��2��2�� ����2:12:40
 */
public class SimulateMsg {
	private static ChannelBuffer buffer = ChannelBuffer.allocate(10240);
	// agent_id �ɼ���ID ���ַ���
	private static String agent_id = "agent_id_value";
	// msg_id �ɼ�����ϢID ����
	private static int msg_id = 529001;
	// src_ip ����ϵͳIP ���ַ���
	private static String src_ip = "127.0.0.1";
	// src_port ����ϵͳ���Ӷ˿� ����
	private static int src_port = 8083;
	// dst_ip ����ϵͳIP ���ַ���
	private static String dst_ip = "127.0.0.1";
	// dst_port ����ϵͳ���Ӷ˿� ����
	private static int dst_port = 8881;
	// packet_type �������ͣ�1-����2-��Ӧ�� ����
	private static int packet_type = 1;
	// match_id ����ƥ��ID ���ַ���
	private static String match_id = "0808";
	// send_time ����ʱ�� ���ַ���
	private static String send_time = getTime();
	// has_ack �Ƿ��յ��Է�ACK ����
	private static int has_ack = 1;
	// ack_time �յ�ACK��ʱ�� ���ַ���
	private static String ack_time = getTime();
	// packet Ӧ�ñ��� ���ֽ���
	private static byte[] packet;
	
	public static ChannelBuffer packRequestBuffer(ChannelBuffer sendedBuffer) {
		buffer.putInt(0);
		putStringAttr("agent_id", agent_id);
		putIntAttr("msg_id", msg_id);
		putStringAttr("src_ip", src_ip);
		putIntAttr("src_port", src_port);
		putStringAttr("dst_ip", dst_ip);
		putIntAttr("dst_prot", dst_port);
		putIntAttr("packet_type", packet_type);
		putStringAttr("match_id", match_id);
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
		byte[] bytes = attr_value.getBytes();
		buffer.putByte((byte)bytes.length);
		//�ֶ�ֵ
		buffer.putBytes(bytes);
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
		buffer.putByte((byte) 0x0C);
		// �ֶ�ֵ����
		buffer.putShort((short) len);
		// �ֶ�ֵ
		buffer.putBytes(bytes);
	}
	
	public static void main(String[] args) {
//		ChannelBuffer sendedBuffer = BufferReader.createRequestMsg("807030");
		
//		ChannelBuffer buffer = proBuffer8813req();
//		ChannelBuffer buffer = proBuffer8813resp();
//		ChannelBuffer buffer = proBuffer806010req();
		ChannelBuffer buffer = proBuffer806010resp();
		System.out.println(buffer.toHexString());
		System.out.println(buffer.readableBytes());
		System.out.println(buffer.capacity());
	}
	
	
	public static ChannelBuffer proBuffer806010req() {
		msg_id = 8060101;
		src_ip = "123.123.123.86";
		src_port = 80601;
		dst_ip = "127.0.0.1";
		dst_port = 8883;
		packet_type = 1;
		match_id = "806010buffer";
		return packRequestBuffer(BufferReader.createRequestMsg("806010"));
	}
	
	public static ChannelBuffer proBuffer806010resp() {
		msg_id = 8060102;
		src_ip = "123.123.123.86";
		src_port = 80601;
		dst_ip = "127.0.0.1";
		dst_port = 8883;
		packet_type = 2;
		match_id = "806010buffer";
		return packRequestBuffer(BufferReader.createRequestMsg("806010"));
	}
	
	public static ChannelBuffer proBuffer8813req() {
		msg_id = 88131;
		src_ip = "123.123.123.123";
		src_port = 88131;
		dst_ip = "127.0.0.1";
		dst_port = 8885;
		packet_type = 1;
		match_id = "8813buffer";
		return packRequestBuffer(BufferReader.createRequestMsg("8813req"));
	}
	
	public static ChannelBuffer proBuffer8813resp() {
		msg_id = 88131;
		src_ip = "123.123.123.123";
		src_port = 88131;
		dst_ip = "127.0.0.1";
		dst_port = 8885;
		packet_type = 2;
		match_id = "8813buffer";
		return packRequestBuffer(BufferReader.createRequestMsg("8813resp"));
	}
	
	public static String getTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");//�������ڸ�ʽ
		return df.format(new Date());// new Date()Ϊ��ȡ��ǰϵͳʱ��
	}
	
}
