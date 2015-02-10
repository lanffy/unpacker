package unpacker;

import java.text.SimpleDateFormat;
import java.util.Date;

import unpacker.conf.Configs;
import unpacker.conf.Servers;
import unpacker.msg.DefaultMsg;
import unpacker.msg.MsgContainer;
import unpacker.msg.PacketsInfo;

import com.wk.actor.Actor;
import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.StructConfig;
import com.wk.eai.config.PackageConfig;
import com.wk.lang.SystemException;
import com.wk.logging.Log;
import com.wk.logging.LogFactory;
import com.wk.net.ChannelBufferMsg;
import com.wk.net.CommManagers;
import com.wk.net.Request;
import com.wk.net.ServerCommManager;
import com.wk.nio.ChannelBuffer;
import com.wk.sdo.ServiceData;

/**
 * @description
 * @author raoliang
 * @version 2015年1月16日 下午7:48:05
 */
public class Receiver {
	
	protected static final Log logger = LogFactory.getLog("unpacker");
	
	public static void main(String[] args) throws Exception {
		new Receiver();
		System.out.println("listening");
		Thread.sleep(Integer.MAX_VALUE);
	}
	private static final String serverCommName = "MPserver";
	
	private ServerCommManager server;
	
	public Receiver(){
		this.server = getServerCommManager();
	}
	
	private ServerCommManager getServerCommManager(){
		return CommManagers.getServerCommManager(serverCommName, ChannelBufferMsg.class, ReqActor.class);
	}
	
}
class ReqActor<T extends ChannelBufferMsg> extends Actor<Request<T>> {
	private static final Log logger = LogFactory.getLog("unpacker");
	
	private String recv_time;
	private int msg_id;
	private String ret_code;
	private String ret_msg;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void act(Request<T> request) {
		recv_time = getTime();
		ChannelBuffer buffer = request.getRequestMsg().toChannelBuffer();
		
		System.out.printf("before unpack,buffer length:{%d},buffer:{\n%s\n}",buffer.getInt(), buffer.toHexString());
		logger.info("收到报文,报文长度:[{}],报文:{}", buffer.getInt(), buffer.toHexString());
		
		ServiceData data  = DefaultMsg.unpack(new PacketChannelBuffer(buffer));
		
		logger.info("第一次拆包:{}", data);
		System.out.println(data);
		
		PacketsInfo info = new PacketsInfo(data);
		msg_id = info.getMsg_id();
		unpackeTranBuffer(info);
		ChannelBuffer responseBuffer = packResponseBuffer();
		request.doResponse((T)new ChannelBufferMsg(responseBuffer));
	}
	
	private void unpackeTranBuffer(PacketsInfo info) {
		String server = Servers.getServerByIp(info.getDst_ip() + "." + info.getDst_prot());
		if(server == null) {
			logger.warn("目的服务系统IP:[{}]不存在，请查看相应配置文件中是否配置!",
					info.getDst_ip()+":"+info.getDst_prot());
			ret_code = "1";
			ret_msg = "目的服务系统IP:["+ info.getDst_ip()+":"+info.getDst_prot() + "]不存在";
			return;
		}
		logger.info("收到来自ip:[{}]的报文，发往服务系统:[{}],服务系统ip:[{}]",
				info.getSrc_ip(), server, info.getDst_ip()+":"+info.getDst_prot());
		int typeFlag = info.getPacket_type();
		if(typeFlag == 1) {
			logger.info("报文类型：[{}]", "request");
			//如果是请求报文，则直接拆包
			unpackRequestMsg(info, server);
		}else if(typeFlag == 2){
			logger.info("报文类型：[{}]", "response");
			//如果是响应报文,则保存报文
			MsgContainer.putResponseMsg(info);
			//判断是否已经解析过相同match_id的请求报文
			String unpackedServer = MsgContainer.getUnpackedConf(info.getMatch_id());
			//如果已经解析过相同match_id的请求报文
			if(unpackedServer != null) {
				unpackResponseMsg(info, server);
			}
		}else {
			logger.warn("报文类型:[{}]不存在!", typeFlag);
			ret_code = "2";
			ret_msg = "报文类型:["+typeFlag+"]不存在!";
			return;
		}
	}
	
	public void unpackRequestMsg(PacketsInfo info, String server) {
		PackageConfig headConfig = Configs.getHeadConfig(server);
		PackageConfig bodyConfig = Configs.getBodyConfig(server, msg_id + "");
		StructConfig reqHeadConfig = headConfig.getRequestConfig();
		StructConfig reqBodyConfig = bodyConfig.getRequestConfig();
		ServiceData data = new ServiceData();
		PacketChannelBuffer buffer = new PacketChannelBuffer(info.getPacket());
		
		reqHeadConfig.getPackageMode().unpack(buffer, reqHeadConfig, data, buffer.readableBytes());
		logger.info("拆请求头后,报文:[\n{}\n]", data);
		reqBodyConfig.getPackageMode().unpack(buffer, reqBodyConfig, data, buffer.readableBytes());
		logger.info("拆请求体后,报文:[\n{}\n]", data);
		//TODO: 此处调用转发data的方法
		
		//记录已经解析过的请求报文
		MsgContainer.putUnpackedConf(info.getMatch_id(), server);
		
		//根据match_id判断之前是否收到响应报文，如果收到则解析
		PacketsInfo respInfo = MsgContainer.getResponseMsg(info.getMatch_id());
		if(respInfo != null) {
			unpackResponseMsg(respInfo, server);
		}
	}
	
	public void unpackResponseMsg(PacketsInfo info, String server) {
		PackageConfig headConfig = Configs.getHeadConfig(server);
		PackageConfig bodyConfig = Configs.getBodyConfig(server, msg_id + "");
		StructConfig respHeadConfig = headConfig.getResponseConfig();
		StructConfig respBodyConfig = bodyConfig.getResponseConfig();
		ServiceData data = new ServiceData();
		PacketChannelBuffer buffer = new PacketChannelBuffer(info.getPacket());
		
		respHeadConfig.getPackageMode().unpack(buffer, respHeadConfig, data, buffer.readableBytes());
		logger.info("拆请求头后,报文:[\n{}\n]", data);
		respBodyConfig.getPackageMode().unpack(buffer, respBodyConfig, data, buffer.readableBytes());
		logger.info("拆请求体后,报文:[\n{}\n]", data);
		//TODO: 此处调用转发data的方法
		
		MsgContainer.removeResponseMsg(info.getMatch_id());
		MsgContainer.removeUnpackedConf(info.getMatch_id());
	}
	
	private ChannelBuffer packResponseBuffer() {
		ChannelBuffer buffer = ChannelBuffer.allocate(100);
		buffer.putInt(0);
		putIntAttr(buffer, "msg_id", msg_id);
		putStringAttr(buffer, "recv_time", recv_time);
		putStringAttr(buffer, "ret_code", ret_code);
		putStringAttr(buffer, "ret_msg", ret_msg);
		buffer.putInt(0, buffer.readableBytes());
		return buffer;
	}
	
	private static String getTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");//设置日期格式
		return df.format(new Date());// new Date()为获取当前系统时间
	}
	
	public static void putStringAttr(ChannelBuffer buffer, String attr_name, String attr_value) {
		//字段名长度
		buffer.putByte((byte) attr_name.length());
		//字段名
		buffer.putBytes(attr_name.getBytes());
		//字段类型
		buffer.putByte((byte)0x07);
		//字段值长度
		buffer.putByte((byte)attr_value.length());
		//字段值
		buffer.putBytes(attr_value.getBytes());
	}
	
	public static void putIntAttr(ChannelBuffer buffer, String attr_name, int attr_value) {
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
}
