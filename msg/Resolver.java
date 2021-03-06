package resolver.msg;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import resolver.conf.ChannelDistConf;
import resolver.conf.DecryptServerConf;
import resolver.conf.Servers;
import resolver.conf.TransConfigs;
import resolver.conf.TransDistinguishConf;
import resolver.excption.UnpackRequestException;
import resolver.excption.UnpackResponseException;
import resolver.msg.impl.TranCodeImpl;
import resolver.msg.impl.TranDecryptImpl;

import com.wk.SystemConfig;
import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.StructConfig;
import com.wk.eai.config.PackageConfig;
import com.wk.lang.SystemException;
import com.wk.logging.Log;
import com.wk.logging.LogFactory;
import com.wk.net.JSONMsg;
import com.wk.nio.ChannelBuffer;
import com.wk.sdo.ServiceData;

/**
 * @description 解析器拆交易报文
 * @author raoliang
 * @version 2015年2月10日 下午3:55:09
 */
public class Resolver {
	private final Log logger = LogFactory.getLog();
	private static final SystemConfig config = SystemConfig.getInstance();
	private static final Boolean isSendMsg = config.getBoolean("resolver.isSendMsg", false);
	private final SendClient client = new SendClient();
	
	public ChannelBuffer unpackeTranBuffer(PacketsInfo info, ResponseInfo responseInfo) {
		String ret_code = "0";
		String ret_msg = "拆包成功";
		String loggermsg = "返回正常报文";
		Writer writer = new StringWriter();
		int typeFlag = info.getPacket_type();
		String ip, server = "";
		try {
			if(typeFlag == 1) {
				logger.info("报文类型：[{}]", "request");
				ip = info.getDst_ip() + "+" + info.getDst_port();
				server = Servers.getServerByIp(ip);
				if(server == null) {
					return packetRes(responseInfo, ip);
				}
				logger.info("收到来自ip:[{}]的报文，发往服务系统:[{} -> {}]", info.getSrc_ip(), ip, server);
				unpackRequestMsg(info, server);
				ret_msg = "拆请求报文成功";
			}else if(typeFlag == 2){
				logger.info("报文类型：[{}]", "response");
				//根据接收系统ip和发送系统ip确定接收渠道名称
				ip = info.getSrc_ip() + "+" + info.getSrc_port();
				server = Servers.getServerByIp(ip);
				if(server == null) {
					return packetRes(responseInfo, ip);
				}
				logger.info("收到来自ip:[{}]的报文，发往渠道系统:[{} -> {}]", info.getSrc_ip(), info.getDst_ip(), server);
				String key = info.getDst_ip() + "+" + info.getDst_port() + "+" + 
						info.getSrc_ip() + "+" + info.getSrc_port();
				String unpackedServer = MsgContainer.getUnpackedServerCode(key);
				if(unpackedServer != null) {
					logger.info("收到已经解析过有对应请求报文的响应报文,根据请求报文拆包");
					unpackResponseMsg(info, server);
					ret_msg = "拆响应报文成功";
				}else {
					ret_code = "6";
					ret_msg = "收到无对应请求报文的响应报文,暂时保存,不拆包.";
					MsgContainer.putResponseMsg(key, info);
					loggermsg = ret_msg;
				}
			}else {
				ret_code = "2";
				ret_msg = "报文类型:["+typeFlag+"]不存在!";
				loggermsg = ret_msg;
			}
		} catch (UnpackRequestException e) {
			ret_code = "3";
			ret_msg = "拆请求报文异常!";
			e.printStackTrace(new PrintWriter(writer));
			logger.error("{}", writer.toString());
			loggermsg = "拆请求报文异常,serverCode:{},返回异常报文";
		} catch (UnpackResponseException e) {
			ret_code = "4";
			ret_msg = "拆响应报文异常!";
			e.printStackTrace(new PrintWriter(writer));
			logger.error("{}", writer.toString());
			loggermsg = "拆响应报文异常,serverCode:{},返回异常报文";
		} catch (Exception e) {
			ret_code = "5";
			ret_msg = "拆报文异常!";
			e.printStackTrace(new PrintWriter(writer));
			logger.error("{}", writer.toString());
			loggermsg = "拆报文异常,serverCode:{},返回异常报文";
		}finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		responseInfo.setRet_code(ret_code);
		responseInfo.setRet_msg(ret_msg);
		logger.info(loggermsg, server);
		return ResponseMsg.packRepMsg(responseInfo);
	}
	
	public void unpackRequestMsg(PacketsInfo info, String server) {
		String key = "";
		try {
			PacketChannelBuffer buffer;
			String decClz = DecryptServerConf.getRequestDecClz(server);
			if(decClz != null) {
				buffer = new PacketChannelBuffer(decryptBuffer(decClz, new PacketChannelBuffer(info.getPacket())));
				logger.info("解密请求报文,解密后报文:\n{}", buffer.toHexString());
			}else {
				buffer = new PacketChannelBuffer(info.getPacket());
			}
			ChannelBuffer tempBuffer = buffer.duplicate();
			ServiceData data = info.getData();
			ServiceData tran_data = new ServiceData();
			//先拆报文头，并识别交易
			PackageConfig headConfig = TransConfigs.getHeadConfig(server);
			StructConfig reqHeadConfig = headConfig.getRequestConfig();
			reqHeadConfig.getPackageMode().unpack(buffer, reqHeadConfig, tran_data, buffer.readableBytes());
			logger.info("拆请求头后,报文:[\n{}\n]", tran_data);
			//获取交易码
//			String tranCodeExpr = TransDistinguishConf.getTranDistField(server);
			String sys_service_code = getTranCode(tran_data, server);
			if(sys_service_code == null) {
				sys_service_code = getTranCode(tempBuffer, server);
				if(sys_service_code == null) {
					throw new UnpackRequestException("获取交易码失败,请查看配置是否正确.")
						.addScene("config_file", "tranDist.properties").addScene("Server", server);
				}
			}
			logger.info("取得交易码：{}", sys_service_code);
			
			//if need unpacket body
			PackageConfig bodyConfig = null;
			if(buffer.readableBytes() > 0) {
				bodyConfig = TransConfigs.getBodyConfig(server, sys_service_code);
				StructConfig reqBodyConfig = bodyConfig.getRequestConfig();
				reqBodyConfig.getPackageMode().unpack(buffer, reqBodyConfig, tran_data, buffer.readableBytes());
				logger.info("拆请求体后,报文:[\n{}\n]", tran_data);
			}else {
				logger.info("不需要拆请求报文体.");
			}
			//根据接收系统ip和发送系统ip确定发送渠道名称
			String send_sys_expr = info.getSrc_ip() + "+" + info.getDst_ip()
					+ "+" + info.getDst_port();
			String send_sys = ChannelDistConf.getChannelName(send_sys_expr);
			if(send_sys == null) {
				logger.error("识别发送渠道失败,请查看配置文件channels.properites文件中是否配置[{}]的值.", send_sys_expr);
				throw new UnpackRequestException("识别发送渠道失败")
					.addScene("config_file", "channels.properties")
					.addScene("parameter", send_sys_expr);
			}
			data.putServiceData("packet", tran_data);
			data.putString("recv_sys", server);
			data.putString("tran_code", sys_service_code);
			data.putString("send_sys", send_sys);
			if(logger.isDebugEnabled()) {
				logger.debug("被传输的请求数据：\n{}", data);
			}
			if(isSendMsg) {
				client.send(new JSONMsg(data));
			}
			
			//记录已经解析过的请求报文match_id : server：服务系统编码；sys_service_code：交易码；send_sys:发送渠道编码
			key = info.getSrc_ip() + "+" + info.getSrc_port() + "+"
					+ info.getDst_ip() + "+" + info.getDst_port();
			MsgContainer.putUnpackedServerCode(key, server+">"+sys_service_code+">"+send_sys);
			//保存已经处理过的请求报文
			MsgContainer.putUnpackedReqPacket(key, info);
			//记录已经解析过的请求报文的报文体的配置，供相应的响应报文体拆包
			if(bodyConfig != null)
				MsgContainer.putUnpackedBodyConf(key, bodyConfig);
		} catch (Exception e) {
			throw new UnpackRequestException(e);
		}
		//根据match_id判断之前是否收到响应报文，如果收到则解析
		PacketsInfo respInfo = MsgContainer.getResponseMsg(key);
		if (respInfo != null) {
			logger.info("拆请求报文对应的响应报文");
			unpackResponseMsg(respInfo, server);
		} else {
			logger.info("暂未收到请求报文对应的响应报文");
		}
	}
	
	public void unpackResponseMsg(PacketsInfo info, String server) {
		ServiceData data = info.getData();
		ServiceData tran_data = new ServiceData();
		String key = info.getDst_ip() + "+" + info.getDst_port() + "+" + 
				info.getSrc_ip() + "+" + info.getSrc_port();
		try {
			PacketChannelBuffer buffer;
			//如果你正在重构下面解密的功能，这里的解密和请求报文中的解密处理是一样的，如果请求和响应的解密处理不同
			//那么只需要重新按照请求的处理方式新增一个响应解密处理接口即可
			String decClz = DecryptServerConf.getResponseDecClz(server);
			if(decClz != null) {
				buffer = new PacketChannelBuffer(decryptBuffer(decClz, new PacketChannelBuffer(info.getPacket())));
				logger.info("解密响应报文,解密后报文:\n{}", buffer.toHexString());
			}else {
				buffer = new PacketChannelBuffer(info.getPacket());
			}
			//先拆报文头
			PackageConfig headConfig = TransConfigs.getHeadConfig(server);
			StructConfig respHeadConfig = headConfig.getResponseConfig();
			respHeadConfig.getPackageMode().unpack(buffer, respHeadConfig, tran_data, buffer.readableBytes());
			logger.info("拆响应头后,报文:[\n{}\n]", tran_data);
			//if need unpacket body
			if(buffer.readableBytes() > 0) {
				PackageConfig bodyConfig = MsgContainer.getUnpackedBodyConf(key);
				StructConfig respBodyConfig = bodyConfig.getResponseConfig();
				respBodyConfig.getPackageMode().unpack(buffer, respBodyConfig, tran_data, buffer.readableBytes());
				logger.info("拆响应体后,报文:[\n{}\n]", tran_data);
			}else {
				logger.info("不需要拆响应报文体");
			}
		} catch (Exception e) {
			throw new UnpackResponseException(e);
		}
		String sys_infoStr = MsgContainer.getUnpackedServerCode(key);
		String[] sys_info = sys_infoStr.split(">");
		if(sys_info.length != 3) {
			throw new SystemException("拆响应报文时组包异常").addScene("sys_infoStr", sys_infoStr);
		}
		data.putServiceData("packet", tran_data);
		data.putString("recv_sys", server);
		data.putString("tran_code", sys_info[1]);
		data.putString("send_sys", sys_info[2]);
		if(logger.isDebugEnabled()) {
			logger.debug("被传输的响应数据：\n{}", data);
		}
		if(isSendMsg) {
			client.send(new JSONMsg(data));
		}
		removeInfo(info);
	}
	
	private void removeInfo(PacketsInfo info) {
		String key = info.getDst_ip() + "+" + info.getDst_port() + "+" + 
				info.getSrc_ip() + "+" + info.getSrc_port();
		MsgContainer.removeResponseMsg(key);
		MsgContainer.removeUnpackedConf(key);
		MsgContainer.removeUnpackedBodyConf(key);
		MsgContainer.removeUnpackedReqPacket(key);
	}
	
	private String getTranCode(ServiceData data, String server) {
		String tranCodeExpr = TransDistinguishConf.getTranDistField(server);
		return _getTranCode(data, tranCodeExpr);
	}
	
	private String _getTranCode(ServiceData data, String tranCodeExpr) {
		int index = tranCodeExpr.indexOf(">");
		if(index < 0) {
			return data.getString(tranCodeExpr);
		}else  {
			return _getTranCode(
					data.getServiceData(tranCodeExpr.substring(0, index)),
					tranCodeExpr.substring(index + 1));
		}
	}
	
	private String getTranCode(ChannelBuffer buffer, String server) {
		String tranCodeExpr = TransDistinguishConf.getTranDistField(server);
		return _getTranCode(buffer, tranCodeExpr);
		
	}
	
	private String _getTranCode(ChannelBuffer buffer, String clzName) {
		ChannelBuffer tempBuffer = buffer.duplicate();
		try {
			TranCodeImpl c = (TranCodeImpl) Class.forName(clzName).newInstance();
			return c.getTranCode(tempBuffer);
		} catch (Exception e) {
			throw new SystemException("SYS_GET_TRANCODE_ERROR").addScene("clzName", clzName);
		}
	}
	
	private ChannelBuffer decryptBuffer(String clzName, ChannelBuffer buffer) {
		ChannelBuffer tempBuffer = buffer.duplicate();
		try {
			TranDecryptImpl c = (TranDecryptImpl) Class.forName(clzName).newInstance();
			return c.decrypt(tempBuffer);
		} catch (Exception e) {
			throw new SystemException("SYS_DECRYPT_BUFFER_ERROR").addScene("clzName", clzName);
		}
	}
	
	private ChannelBuffer packetRes(ResponseInfo responseInfo, String ip) {
		logger.warn("目的服务系统IP映射不存在，请查看配置文件server.properties中是否配置[{}]的值!", ip);
		responseInfo.setRet_code("1");
		responseInfo.setRet_msg("目的服务系统IP映射不存在，请查看配置文件server.properties中是否配置[" + ip+ "]的值!");
		logger.info("返回异常响应报文");
		return ResponseMsg.packRepMsg(responseInfo);
	}
	
}
