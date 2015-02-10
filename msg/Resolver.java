package resolver.msg;

import resolver.conf.Configs;
import resolver.conf.Servers;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.StructConfig;
import com.wk.eai.config.PackageConfig;
import com.wk.logging.Log;
import com.wk.logging.LogFactory;
import com.wk.nio.ChannelBuffer;
import com.wk.sdo.ServiceData;

/**
 * @description
 * @author raoliang
 * @version 2015年2月10日 下午3:55:09
 */
public class Resolver {
	private static final Log logger = LogFactory.getLog("resolver");
	
	private static String recv_time;
	private static int msg_id;
	private static String ret_code;
	private static String ret_msg;

	public static ChannelBuffer unpackeTranBuffer(PacketsInfo info) {
		String server = Servers.getServerByIp(info.getDst_ip() + ":" + info.getDst_prot());
		if(server == null) {
			logger.warn("目的服务系统IP:[{}]不存在，请查看相应配置文件中是否配置!",
					info.getDst_ip()+":"+info.getDst_prot());
			ret_code = "1";
			ret_msg = "目的服务系统IP:["+ info.getDst_ip()+":"+info.getDst_prot() + "]不存在";
			logger.info("组异常响应报文");
			return packResponseBuffer();
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
				logger.info("收到有对应请求报文的响应报文,根据请求报文拆包");
				unpackResponseMsg(info, server);
			}else {
				logger.info("收到无对应请求报文的响应报文,暂时保存,不拆包.");
			}
		}else {
			logger.warn("报文类型:[{}]不存在!", typeFlag);
			ret_code = "2";
			ret_msg = "报文类型:["+typeFlag+"]不存在!";
			logger.info("组异常返回报文");
			return packResponseBuffer();
		}
		ret_code = "0";
		ret_msg = "拆包成功";
		logger.info("组响应报文");
		return packResponseBuffer();
	}
	
	public static void unpackRequestMsg(PacketsInfo info, String server) {
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
			logger.info("拆请求报文对应的响应报文");
			unpackResponseMsg(respInfo, server);
		} else {
			logger.info("暂未收到请求报文对应的响应报文");
		}
	}
	
	public static void unpackResponseMsg(PacketsInfo info, String server) {
		PackageConfig headConfig = Configs.getHeadConfig(server);
		PackageConfig bodyConfig = Configs.getBodyConfig(server, msg_id + "");
		StructConfig respHeadConfig = headConfig.getResponseConfig();
		StructConfig respBodyConfig = bodyConfig.getResponseConfig();
		ServiceData data = new ServiceData();
		PacketChannelBuffer buffer = new PacketChannelBuffer(info.getPacket());
		
		respHeadConfig.getPackageMode().unpack(buffer, respHeadConfig, data, buffer.readableBytes());
		logger.info("拆响应头后,报文:[\n{}\n]", data);
		respBodyConfig.getPackageMode().unpack(buffer, respBodyConfig, data, buffer.readableBytes());
		logger.info("拆响应体后,报文:[\n{}\n]", data);
		//TODO: 此处调用转发data的方法
		
		MsgContainer.removeResponseMsg(info.getMatch_id());
		MsgContainer.removeUnpackedConf(info.getMatch_id());
	}
	
	private static ChannelBuffer packResponseBuffer() {
		ChannelBuffer buffer = ChannelBuffer.allocate(4096);
		buffer.putInt(0);
		putIntAttr(buffer, "msg_id", msg_id);
		putStringAttr(buffer, "recv_time", recv_time);
		putStringAttr(buffer, "ret_code", ret_code);
		putStringAttr(buffer, "ret_msg", ret_msg);
		buffer.putInt(0, buffer.readableBytes());
		return buffer;
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

	public static String getRecv_time() {
		return recv_time;
	}

	public static void setRecv_time(String recv_time) {
		Resolver.recv_time = recv_time;
	}

	public static int getMsg_id() {
		return msg_id;
	}

	public static void setMsg_id(int msg_id) {
		Resolver.msg_id = msg_id;
	}

	public static String getRet_code() {
		return ret_code;
	}

	public static void setRet_code(String ret_code) {
		Resolver.ret_code = ret_code;
	}

	public static String getRet_msg() {
		return ret_msg;
	}

	public static void setRet_msg(String ret_msg) {
		Resolver.ret_msg = ret_msg;
	}

}
