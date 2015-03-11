package resolver.msg;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import resolver.conf.ChannelDistConf;
import resolver.conf.TransConfigs;
import resolver.conf.DecryptServerConf;
import resolver.conf.Servers;
import resolver.conf.TransDistinguishConf;
import resolver.excption.UnpackRequestException;
import resolver.excption.UnpackResponseException;
import resolver.msg.impl.TranCodeImpl;
import resolver.msg.impl.TranDecryptImpl;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.StructConfig;
import com.wk.eai.config.PackageConfig;
import com.wk.lang.SystemException;
import com.wk.logging.Log;
import com.wk.logging.LogFactory;
import com.wk.nio.ChannelBuffer;
import com.wk.sdo.ServiceData;

/**
 * @description ���������ױ���
 * @author raoliang
 * @version 2015��2��10�� ����3:55:09
 */
public class Resolver {
	private final Log logger = LogFactory.getLog();
	private final SendClient client = new SendClient();
	
	public ChannelBuffer unpackeTranBuffer(PacketsInfo info, ResponseInfo responseInfo) {
		String ret_code = "0";
		String ret_msg = "����ɹ�";
		String loggermsg = "������������";
		Writer writer = new StringWriter();
		int typeFlag = info.getPacket_type();
		String ip, server = "";
		try {
			if(typeFlag == 1) {
				logger.info("�������ͣ�[{}]", "request");
				ip = info.getDst_ip() + "+" + info.getDst_prot();
				server = Servers.getServerByIp(ip);
				if(server == null) {
					return packetRes(responseInfo, ip);
				}
				logger.info("�յ�����ip:[{}]�ı��ģ���������ϵͳ:[{} -> {}]", info.getSrc_ip(), ip, server);
				unpackRequestMsg(info, server);
				ret_msg = "�������ĳɹ�";
			}else if(typeFlag == 2){
				logger.info("�������ͣ�[{}]", "response");
				//���ݽ���ϵͳip�ͷ���ϵͳipȷ��������������
				ip = info.getSrc_ip() + "+" + info.getSrc_port();
				server = Servers.getServerByIp(ip);
				if(server == null) {
					return packetRes(responseInfo, ip);
				}
				logger.info("�յ�����ip:[{}]�ı��ģ���������ϵͳ:[{} -> {}]", info.getSrc_ip(), info.getDst_ip(), server);
				String key = info.getDst_ip() + "+" + info.getDst_prot() + "+" + 
						info.getSrc_ip() + "+" + info.getSrc_port();
				String unpackedServer = MsgContainer.getUnpackedServerCode(key);
				if(unpackedServer != null) {
					logger.info("�յ��Ѿ��������ж�Ӧ�����ĵ���Ӧ����,���������Ĳ��");
					unpackResponseMsg(info, server);
					ret_msg = "����Ӧ���ĳɹ�";
				}else {
					ret_code = "6";
					ret_msg = "�յ��޶�Ӧ�����ĵ���Ӧ����,��ʱ����,�����.";
					MsgContainer.putResponseMsg(key, info);
					loggermsg = ret_msg;
				}
			}else {
				ret_code = "2";
				ret_msg = "��������:["+typeFlag+"]������!";
				loggermsg = ret_msg;
			}
		} catch (UnpackRequestException e) {
			ret_code = "3";
			ret_msg = "���������쳣!";
			e.printStackTrace(new PrintWriter(writer));
			logger.error("{}", writer.toString());
			loggermsg = "���������쳣,serverCode:{},�����쳣����";
		} catch (UnpackResponseException e) {
			ret_code = "4";
			ret_msg = "����Ӧ�����쳣!";
			e.printStackTrace(new PrintWriter(writer));
			logger.error("{}", writer.toString());
			loggermsg = "����Ӧ�����쳣,serverCode:{},�����쳣����";
		} catch (Exception e) {
			ret_code = "5";
			ret_msg = "�����쳣!";
			e.printStackTrace(new PrintWriter(writer));
			logger.error("{}", writer.toString());
			loggermsg = "�����쳣,serverCode:{},�����쳣����";
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
				logger.info("����������,���ܺ���:\n{}", buffer.toHexString());
			}else {
				buffer = new PacketChannelBuffer(info.getPacket());
			}
			ChannelBuffer tempBuffer = buffer.duplicate();
			ServiceData data = info.getData();
			ServiceData tran_data = new ServiceData();
			//�Ȳ���ͷ����ʶ����
			PackageConfig headConfig = TransConfigs.getHeadConfig(server);
			StructConfig reqHeadConfig = headConfig.getRequestConfig();
			reqHeadConfig.getPackageMode().unpack(buffer, reqHeadConfig, tran_data, buffer.readableBytes());
			logger.info("������ͷ��,����:[\n{}\n]", tran_data);
			//��ȡ������
//			String tranCodeExpr = TransDistinguishConf.getTranDistField(server);
			String sys_service_code = getTranCode(tran_data, server);
			if(sys_service_code == null) {
				sys_service_code = getTranCode(tempBuffer, server);
				if(sys_service_code == null) {
					throw new UnpackRequestException("��ȡ������ʧ��,��鿴�����Ƿ���ȷ.")
						.addScene("config_file", "tranDist.properties").addScene("Server", server);
				}
			}
			logger.info("��ȡ�����룺{}", sys_service_code);
			
			//if need unpacket body
			PackageConfig bodyConfig = null;
			if(buffer.readableBytes() > 0) {
				bodyConfig = TransConfigs.getBodyConfig(server, sys_service_code);
				StructConfig reqBodyConfig = bodyConfig.getRequestConfig();
				reqBodyConfig.getPackageMode().unpack(buffer, reqBodyConfig, tran_data, buffer.readableBytes());
				logger.info("���������,����:[\n{}\n]", tran_data);
			}else {
				logger.info("����Ҫ����������.");
			}
			//���ݽ���ϵͳip�ͷ���ϵͳipȷ��������������
			String send_sys_expr = info.getSrc_ip() + "+" + info.getDst_ip()
					+ "+" + info.getDst_prot();
			String send_sys = ChannelDistConf.getChannelName(send_sys_expr);
			if(send_sys == null) {
				logger.error("ʶ��������ʧ��,��鿴�����ļ�channels.properites�ļ����Ƿ�����[{}]��ֵ.", send_sys_expr);
				throw new UnpackRequestException("ʶ��������ʧ��")
					.addScene("config_file", "channels.properties")
					.addScene("parameter", send_sys_expr);
			}
			data.putServiceData("packet", tran_data);
			data.putString("recv_sys", server);
			data.putString("tran_code", sys_service_code);
			data.putString("send_sys", send_sys);
			if(logger.isDebugEnabled()) {
				logger.debug("��������������ݣ�\n{}", data);
			}
			//TODO: �˴�����ת��data�ķ���
//			client.send(new JSONMsg(data));
			
			//��¼�Ѿ���������������match_id : server������ϵͳ���룻sys_service_code�������룻send_sys:������������
//			MsgContainer.putUnpackedServerCode(info.getMatch_id(), server+">"+sys_service_code+">"+send_sys);
			key = info.getSrc_ip() + "+" + info.getSrc_port() + "+"
					+ info.getDst_ip() + "+" + info.getDst_prot();
			MsgContainer.putUnpackedServerCode(key, server+">"+sys_service_code+">"+send_sys);
			//��¼�Ѿ��������������ĵı���������ã�����Ӧ����Ӧ��������
			if(bodyConfig != null)
				MsgContainer.putUnpackedBodyConf(key, bodyConfig);
		} catch (Exception e) {
			throw new UnpackRequestException(e);
		}
		//����match_id�ж�֮ǰ�Ƿ��յ���Ӧ���ģ�����յ������
		PacketsInfo respInfo = MsgContainer.getResponseMsg(key);
		if (respInfo != null) {
			logger.info("�������Ķ�Ӧ����Ӧ����");
			unpackResponseMsg(respInfo, server);
		} else {
			logger.info("��δ�յ������Ķ�Ӧ����Ӧ����");
		}
	}
	
	public void unpackResponseMsg(PacketsInfo info, String server) {
		ServiceData data = info.getData();
		ServiceData tran_data = new ServiceData();
		String key = info.getDst_ip() + "+" + info.getDst_prot() + "+" + 
				info.getSrc_ip() + "+" + info.getSrc_port();
		try {
			PacketChannelBuffer buffer;
			String decClz = DecryptServerConf.getResponseDecClz(server);
			if(decClz != null) {
				buffer = new PacketChannelBuffer(decryptBuffer(decClz, new PacketChannelBuffer(info.getPacket())));
				logger.info("������Ӧ����,���ܺ���:\n{}", buffer.toHexString());
			}else {
				buffer = new PacketChannelBuffer(info.getPacket());
			}
			//�Ȳ���ͷ
			PackageConfig headConfig = TransConfigs.getHeadConfig(server);
			StructConfig respHeadConfig = headConfig.getResponseConfig();
			respHeadConfig.getPackageMode().unpack(buffer, respHeadConfig, tran_data, buffer.readableBytes());
			logger.info("����Ӧͷ��,����:[\n{}\n]", tran_data);
			//if need unpacket body
			if(buffer.readableBytes() > 0) {
				PackageConfig bodyConfig = MsgContainer.getUnpackedBodyConf(key);
				StructConfig respBodyConfig = bodyConfig.getResponseConfig();
				respBodyConfig.getPackageMode().unpack(buffer, respBodyConfig, tran_data, buffer.readableBytes());
				logger.info("����Ӧ���,����:[\n{}\n]", tran_data);
			}else {
				logger.info("����Ҫ����Ӧ������");
			}
		} catch (Exception e) {
			throw new UnpackResponseException(e);
		}
		String sys_infoStr = MsgContainer.getUnpackedServerCode(key);
		String[] sys_info = sys_infoStr.split(">");
		if(sys_info.length != 3) {
			throw new SystemException("����Ӧ����ʱ����쳣").addScene("sys_infoStr", sys_infoStr);
		}
		data.putServiceData("packet", tran_data);
		data.putString("recv_sys", server);
		data.putString("tran_code", sys_info[1]);
		data.putString("send_sys", sys_info[2]);
		if(logger.isDebugEnabled()) {
			logger.debug("���������Ӧ���ݣ�\n{}", data);
		}
		//TODO: �˴�����ת��data�ķ���
//		client.send(new JSONMsg(data));
		
		removeInfo(info);
	}
	
	private void removeInfo(PacketsInfo info) {
		String key = info.getDst_ip() + "+" + info.getDst_prot() + "+" + 
				info.getSrc_ip() + "+" + info.getSrc_port();
		MsgContainer.removeResponseMsg(key);
		MsgContainer.removeUnpackedConf(key);
		MsgContainer.removeUnpackedBodyConf(key);
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
			e.printStackTrace();
		}
		return null;
	}
	
	private ChannelBuffer decryptBuffer(String clzName, ChannelBuffer buffer) {
		ChannelBuffer tempBuffer = buffer.duplicate();
		try {
			TranDecryptImpl c = (TranDecryptImpl) Class.forName(clzName).newInstance();
			return c.decrypt(tempBuffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private ChannelBuffer packetRes(ResponseInfo responseInfo, String ip) {
		logger.warn("Ŀ�ķ���ϵͳIPӳ�䲻���ڣ���鿴�����ļ�server.properties���Ƿ�����[{}]��ֵ!", ip);
		responseInfo.setRet_code("1");
		responseInfo.setRet_msg("Ŀ�ķ���ϵͳIPӳ�䲻���ڣ���鿴�����ļ�server.properties���Ƿ�����[" + ip+ "]��ֵ!");
		logger.info("�����쳣��Ӧ����");
		return ResponseMsg.packRepMsg(responseInfo);
	}
	
}
