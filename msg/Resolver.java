package resolver.msg;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import resolver.conf.Configs;
import resolver.conf.Servers;
import resolver.conf.TransDistinguishConf;
import resolver.excption.UnpackRequestException;
import resolver.excption.UnpackResponseException;

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
 * @description ���������ױ���
 * @author raoliang
 * @version 2015��2��10�� ����3:55:09
 */
public class Resolver {
	private static final Log logger = LogFactory.getLog();
	
	public static ChannelBuffer unpackeTranBuffer(PacketsInfo info, ResponseInfo responseInfo) {
		String server = Servers.getServerByIp(info.getDst_ip() + ":" + info.getDst_prot());
		if(server == null) {
			logger.warn("Ŀ�ķ���ϵͳIP:[{}]�����ڣ���鿴��Ӧ�����ļ����Ƿ�����!",
					info.getDst_ip()+":"+info.getDst_prot());
			responseInfo.setRet_code("1");
			responseInfo.setRet_msg("Ŀ�ķ���ϵͳIP:["+ info.getDst_ip()+":"+info.getDst_prot() + "]������");
			logger.info("�����쳣��Ӧ����");
			return ResponseMsg.packRepMsg(responseInfo);
		}
		logger.info("�յ�����ip:[{}]�ı��ģ���������ϵͳ:[{}],����ϵͳip:[{}]",
				info.getSrc_ip(), server, info.getDst_ip()+":"+info.getDst_prot());
		String ret_code = "0";
		String ret_msg = "����ɹ�";
		String loggermsg = "������������";
		Writer writer = new StringWriter();
		int typeFlag = info.getPacket_type();
		try {
			if(typeFlag == 1) {
				logger.info("�������ͣ�[{}]", "request");
				//����������ģ���ֱ�Ӳ��
				unpackRequestMsg(info, server);
				ret_msg = "�������ĳɹ�";
			}else if(typeFlag == 2){
				logger.info("�������ͣ�[{}]", "response");
				String unpackedServer = MsgContainer.getUnpackedServerCode(info.getMatch_id());
				if(unpackedServer != null) {
					logger.info("�յ��Ѿ��������ж�Ӧ�����ĵ���Ӧ����,���������Ĳ��");
					unpackResponseMsg(info, server);
					ret_msg = "����Ӧ���ĳɹ�";
				}else {
					ret_code = "6";
					ret_msg = "�յ��޶�Ӧ�����ĵ���Ӧ����,��ʱ����,�����.";
					MsgContainer.putResponseMsg(info);
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
	
	public static void unpackRequestMsg(PacketsInfo info, String server) {
		try {
			PacketChannelBuffer buffer = new PacketChannelBuffer(info.getPacket());
			ServiceData data = info.getData();
			ServiceData tran_data = new ServiceData();
			//�Ȳ���ͷ����ʶ����
			PackageConfig headConfig = Configs.getHeadConfig(server);
			StructConfig reqHeadConfig = headConfig.getRequestConfig();
			reqHeadConfig.getPackageMode().unpack(buffer, reqHeadConfig, tran_data, buffer.readableBytes());
			logger.info("������ͷ��,����:[\n{}\n]", tran_data);
			
			//if need unpacket body
			PackageConfig bodyConfig = null;
			String sys_service_code = null;
			if(buffer.readableBytes() > 0) {
				//ʶ������
				sys_service_code = getTranCode(tran_data, TransDistinguishConf.getTranDistField(server));
				logger.info("��ȡ�����룺{}", sys_service_code);
				//�ٲ�����
				bodyConfig = Configs.getBodyConfig(server, sys_service_code);
				StructConfig reqBodyConfig = bodyConfig.getRequestConfig();
				reqBodyConfig.getPackageMode().unpack(buffer, reqBodyConfig, tran_data, buffer.readableBytes());
				logger.info("���������,����:[\n{}\n]", tran_data);
			}else {
				logger.info("����Ҫ����������");
			}
			//���ݽ���ϵͳip�ͷ���ϵͳipȷ��������������
			String send_sys = resolver.conf.ChannelDistConf.getChannelName(info
					.getSrc_ip()
					+ "+"
					+ info.getDst_ip()
					+ ":"
					+ info.getDst_prot());
			
			data.putServiceData("packet", tran_data);
			data.putString("recv_sys", server);
			data.putString("tran_code", sys_service_code);
			data.putString("send_sys", send_sys);
			if(logger.isDebugEnabled()) {
				logger.debug("��������������ݣ�\n{}", data);
			}
			//TODO: �˴�����ת��data�ķ���
			new SimulateClient().send(new JSONMsg(data));
			
			//��¼�Ѿ���������������match_id : server������ϵͳ���룻sys_service_code�������룻send_sys:������������
			MsgContainer.putUnpackedServerCode(info.getMatch_id(), server+">"+sys_service_code+">"+send_sys);
			//��¼�Ѿ��������������ĵı���������ã�����Ӧ����Ӧ��������
			if(bodyConfig != null)
				MsgContainer.putUnpackedBodyConf(info.getMatch_id(), bodyConfig);
		} catch (Exception e) {
			throw new UnpackRequestException(e);
		}
		//����match_id�ж�֮ǰ�Ƿ��յ���Ӧ���ģ�����յ������
		PacketsInfo respInfo = MsgContainer.getResponseMsg(info.getMatch_id());
		if (respInfo != null) {
			logger.info("�������Ķ�Ӧ����Ӧ����");
			unpackResponseMsg(respInfo, server);
		} else {
			logger.info("��δ�յ������Ķ�Ӧ����Ӧ����");
		}
	}
	
	public static void unpackResponseMsg(PacketsInfo info, String server) {
		ServiceData data = info.getData();
		ServiceData tran_data = new ServiceData();
		try {
			PacketChannelBuffer buffer = new PacketChannelBuffer(info.getPacket());
			//�Ȳ���ͷ
			PackageConfig headConfig = Configs.getHeadConfig(server);
			StructConfig respHeadConfig = headConfig.getResponseConfig();
			respHeadConfig.getPackageMode().unpack(buffer, respHeadConfig, tran_data, buffer.readableBytes());
			logger.info("����Ӧͷ��,����:[\n{}\n]", tran_data);
			//if need unpacket body
			if(buffer.readableBytes() > 0) {
				PackageConfig bodyConfig = MsgContainer.getUnpackedBodyConf(info.getMatch_id());
				StructConfig respBodyConfig = bodyConfig.getResponseConfig();
				respBodyConfig.getPackageMode().unpack(buffer, respBodyConfig, tran_data, buffer.readableBytes());
				logger.info("����Ӧ���,����:[\n{}\n]", tran_data);
			}else {
				logger.info("����Ҫ����Ӧ������");
			}
		} catch (Exception e) {
			throw new UnpackResponseException(e);
		}
		String sys_infoStr = MsgContainer.getUnpackedServerCode(info.getMatch_id());
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
		new SimulateClient().send(new JSONMsg(data));
		
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
