package resolver.msg;

import resolver.conf.Configs;
import resolver.conf.Servers;
import resolver.conf.TransDistinguishConf;
import resolver.excption.UnpackRequestException;
import resolver.excption.UnpackResponseException;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.StructConfig;
import com.wk.eai.config.PackageConfig;
import com.wk.logging.Log;
import com.wk.logging.LogFactory;
import com.wk.nio.ChannelBuffer;
import com.wk.sdo.ServiceData;

/**
 * @description 解析器拆交易报文
 * @author raoliang
 * @version 2015年2月10日 下午3:55:09
 */
public class Resolver {
	private static final Log logger = LogFactory.getLog();
	
	public static ChannelBuffer unpackeTranBuffer(PacketsInfo info, ResponseInfo responseInfo) {
		String server = Servers.getServerByIp(info.getDst_ip() + ":" + info.getDst_prot());
		if(server == null) {
			logger.warn("目的服务系统IP:[{}]不存在，请查看相应配置文件中是否配置!",
					info.getDst_ip()+":"+info.getDst_prot());
			responseInfo.setRet_code("1");
			responseInfo.setRet_msg("目的服务系统IP:["+ info.getDst_ip()+":"+info.getDst_prot() + "]不存在");
			logger.info("返回异常响应报文");
			return ResponseMsg.packRepMsg(responseInfo);
		}
		logger.info("收到来自ip:[{}]的报文，发往服务系统:[{}],服务系统ip:[{}]",
				info.getSrc_ip(), server, info.getDst_ip()+":"+info.getDst_prot());
		String ret_code = "0";
		String ret_msg = "拆包成功";
		String loggermsg = "返回正常报文";
		int typeFlag = info.getPacket_type();
		try {
			if(typeFlag == 1) {
				logger.info("报文类型：[{}]", "request");
				//如果是请求报文，则直接拆包
				unpackRequestMsg(info, server);
			}else if(typeFlag == 2){
				logger.info("报文类型：[{}]", "response");
				String unpackedServer = MsgContainer.getUnpackedServerCode(info.getMatch_id());
				if(unpackedServer != null) {
					logger.info("收到已经解析过有对应请求报文的响应报文,根据请求报文拆包");
					unpackResponseMsg(info, server);
				}else {
					MsgContainer.putResponseMsg(info);
					logger.info("收到无对应请求报文的响应报文,暂时保存,不拆包.");
				}
			}else {
				responseInfo.setRet_code("2");
				responseInfo.setRet_msg("报文类型:["+typeFlag+"]不存在!");
				logger.error("报文类型:[{}]不存在!返回异常响应报文", typeFlag);
				return ResponseMsg.packRepMsg(responseInfo);
			}
		} catch (UnpackRequestException e) {
			ret_code = "3";
			ret_msg = "拆请求报文异常!";
			e.printStackTrace();
			loggermsg = "拆请求报文异常,serverCode:{},返回异常报文";
		} catch (UnpackResponseException e) {
			ret_code = "4";
			ret_msg = "拆响应报文异常!";
			e.printStackTrace();
			loggermsg = "拆响应报文异常,serverCode:{},返回异常报文";
		} catch (Exception e) {
			ret_code = "5";
			ret_msg = "拆报文异常!";
			e.printStackTrace();
			loggermsg = "拆报文异常,serverCode:{},返回异常报文";
		}
		responseInfo.setRet_code(ret_code);
		responseInfo.setRet_msg(ret_msg);
		logger.info(loggermsg, server);
		return ResponseMsg.packRepMsg(responseInfo);
	}
	
	public static void unpackRequestMsg(PacketsInfo info, String server) {
		try {
			PacketChannelBuffer buffer = new PacketChannelBuffer(info.getPacket());
//			ServiceData data = info.getData();
			ServiceData tran_data = new ServiceData();
			//先拆报文头，并识别交易
			PackageConfig headConfig = Configs.getHeadConfig(server);
			StructConfig reqHeadConfig = headConfig.getRequestConfig();
			reqHeadConfig.getPackageMode().unpack(buffer, reqHeadConfig, tran_data, buffer.readableBytes());
			logger.info("拆请求头后,报文:[\n{}\n]", tran_data);
			
			//if need unpacket body
			PackageConfig bodyConfig = null;
			if(buffer.readableBytes() > 0) {
				//识别交易码
				String sys_service_code = getTranCode(tran_data, TransDistinguishConf.getTranDistField(server));
				logger.info("获取交易码字段名称：{}", sys_service_code);
				//再拆报文体
				bodyConfig = Configs.getBodyConfig(server, sys_service_code);
				StructConfig reqBodyConfig = bodyConfig.getRequestConfig();
				reqBodyConfig.getPackageMode().unpack(buffer, reqBodyConfig, tran_data, buffer.readableBytes());
				logger.info("拆请求体后,报文:[\n{}\n]", tran_data);
			}else {
				logger.info("不需要拆请求报文体");
			}
			//TODO: 此处调用转发data的方法
			
			//记录已经解析过的请求报文match_id
			MsgContainer.putUnpackedServerCode(info.getMatch_id(), server);
			//记录已经解析过的请求报文的报文体的配置，供相应的响应报文体拆包
			if(bodyConfig != null)
				MsgContainer.putUnpackedBodyConf(info.getMatch_id(), bodyConfig);
		} catch (Exception e) {
			throw new UnpackRequestException(e);
		}
		//根据match_id判断之前是否收到响应报文，如果收到则解析
		PacketsInfo respInfo = MsgContainer.getResponseMsg(info.getMatch_id());
		if (respInfo != null) {
			logger.info("拆请求报文对应的响应报文");
			unpackResponseMsg(respInfo, server);
		} else {
			logger.info("暂未收到请求报文对应的响应报文");
		}
	}
	
	public static void unpackResponseMsg(PacketsInfo info, String server) {
		try {
			ServiceData data = new ServiceData();
			PacketChannelBuffer buffer = new PacketChannelBuffer(info.getPacket());
			//先拆报文头
			PackageConfig headConfig = Configs.getHeadConfig(server);
			StructConfig respHeadConfig = headConfig.getResponseConfig();
			respHeadConfig.getPackageMode().unpack(buffer, respHeadConfig, data, buffer.readableBytes());
			logger.info("拆响应头后,报文:[\n{}\n]", data);
			//if need unpacket body
			if(buffer.readableBytes() > 0) {
				//unpacket body
	//			PackageConfig bodyConfig = Configs.getBodyConfig(server, sys_service_code);
				PackageConfig bodyConfig = MsgContainer.getUnpackedBodyConf(info.getMatch_id());
				StructConfig respBodyConfig = bodyConfig.getResponseConfig();
				respBodyConfig.getPackageMode().unpack(buffer, respBodyConfig, data, buffer.readableBytes());
				logger.info("拆响应体后,报文:[\n{}\n]", data);
			}else {
				logger.info("不需要拆响应报文体");
			}
		} catch (Exception e) {
			throw new UnpackResponseException(e);
		}
		//TODO: 此处调用转发data的方法
		
		removeInfo(info);
	}
	
	private static void removeInfo(PacketsInfo info) {
		MsgContainer.removeResponseMsg(info.getMatch_id());
		MsgContainer.removeUnpackedConf(info.getMatch_id());
		MsgContainer.removeUnpackedBodyConf(info.getMatch_id());
	}
	
	private static String getTranCode(ServiceData data, String tranCodeExpr) {
		int index = tranCodeExpr.indexOf(">");
		if(index < 0) {
			return data.getString(tranCodeExpr);
		}else  {
			return getTranCode(
					data.getServiceData(tranCodeExpr.substring(0, index)),
					tranCodeExpr.substring(index + 1));
		}
	}
	
}
