package resolver.msg;

import java.text.SimpleDateFormat;
import java.util.Date;

import resolver.util.BufferReader;

import com.wk.nio.ChannelBuffer;

/**
 * @description 组模拟报文，用于测试
 * @author raoliang
 * @version 2015年2月2日 下午2:12:40
 */
public class SimulateMsg {
	private static ChannelBuffer buffer = ChannelBuffer.allocate(10240);
	// agent_id 采集端ID 短字符串
	private static String agent_id = "agent_id_value";
	// msg_id 采集端消息ID 整型
	private static int msg_id = 529001;
	// src_ip 发起系统IP 短字符串
	private static String src_ip = "127.0.0.1";
	// src_port 发起系统连接端口 整型
	private static int src_port = 8083;
	// dst_ip 接收系统IP 短字符串
	private static String dst_ip = "127.0.0.1";
	// dst_port 接收系统连接端口 整型
	private static int dst_port = 8881;
	// packet_type 报文类型（1-请求，2-响应） 整型
	private static int packet_type = 1;
	// match_id 报文匹配ID 短字符串
	private static String match_id = "0808";
	// send_time 发送时间 短字符串
	private static String send_time = getTime();
	// has_ack 是否收到对方ACK 整型
	private static int has_ack = 1;
	// ack_time 收到ACK的时间 短字符串
	private static String ack_time = getTime();
	// packet 应用报文 长字节流
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
		//字段名长度
		buffer.putByte((byte) attr_name.length());
		//字段名
		buffer.putBytes(attr_name.getBytes());
		//字段类型
		buffer.putByte((byte)0x07);
		//字段值长度
		byte[] bytes = attr_value.getBytes();
		buffer.putByte((byte)bytes.length);
		//字段值
		buffer.putBytes(bytes);
	}
	
	public static void putIntAttr(String attr_name, int attr_value) {
		//字段名长度
		buffer.putByte((byte) attr_name.length());
		//字段名
		buffer.putBytes(attr_name.getBytes());
		//字段类型
		buffer.putByte((byte)0x05);
		//字段值长度
		//字段值
		buffer.putInt(attr_value);
	}
	
	public static void putBytesAttr(String attr_name, byte[] bytes) {
		int len = bytes.length;
		// 字段名长度
		buffer.putByte((byte) attr_name.length());
		// 字段名
		buffer.putBytes(attr_name.getBytes());
		// 字段类型  长字节流
		buffer.putByte((byte) 0x0C);
		// 字段值长度
		buffer.putShort((short) len);
		// 字段值
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
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");//设置日期格式
		return df.format(new Date());// new Date()为获取当前系统时间
	}
	
}
