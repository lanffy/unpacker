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
		int typeFlag = info.getPacket_type();
		try {
			if(typeFlag == 1) {
				logger.info("�������ͣ�[{}]", "request");
				//����������ģ���ֱ�Ӳ��
				unpackRequestMsg(info, server);
			}else if(typeFlag == 2){
				logger.info("�������ͣ�[{}]", "response");
				String unpackedServer = MsgContainer.getUnpackedServerCode(info.getMatch_id());
				if(unpackedServer != null) {
					logger.info("�յ��Ѿ��������ж�Ӧ�����ĵ���Ӧ����,���������Ĳ��");
					unpackResponseMsg(info, server);
				}else {
					MsgContainer.putResponseMsg(info);
					logger.info("�յ��޶�Ӧ�����ĵ���Ӧ����,��ʱ����,�����.");
				}
			}else {
				responseInfo.setRet_code("2");
				responseInfo.setRet_msg("��������:["+typeFlag+"]������!");
				logger.error("��������:[{}]������!�����쳣��Ӧ����", typeFlag);
				return ResponseMsg.packRepMsg(responseInfo);
			}
		} catch (UnpackRequestException e) {
			ret_code = "3";
			ret_msg = "���������쳣!";
			e.printStackTrace();
			loggermsg = "���������쳣,serverCode:{},�����쳣����";
		} catch (UnpackResponseException e) {
			ret_code = "4";
			ret_msg = "����Ӧ�����쳣!";
			e.printStackTrace();
			loggermsg = "����Ӧ�����쳣,serverCode:{},�����쳣����";
		} catch (Exception e) {
			ret_code = "5";
			ret_msg = "�����쳣!";
			e.printStackTrace();
			loggermsg = "�����쳣,serverCode:{},�����쳣����";
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
			//�Ȳ���ͷ����ʶ����
			PackageConfig headConfig = Configs.getHeadConfig(server);
			StructConfig reqHeadConfig = headConfig.getRequestConfig();
			reqHeadConfig.getPackageMode().unpack(buffer, reqHeadConfig, tran_data, buffer.readableBytes());
			logger.info("������ͷ��,����:[\n{}\n]", tran_data);
			
			//if need unpacket body
			PackageConfig bodyConfig = null;
			if(buffer.readableBytes() > 0) {
				//ʶ������
				String sys_service_code = getTranCode(tran_data, TransDistinguishConf.getTranDistField(server));
				logger.info("��ȡ�������ֶ����ƣ�{}", sys_service_code);
				//�ٲ�����
				bodyConfig = Configs.getBodyConfig(server, sys_service_code);
				StructConfig reqBodyConfig = bodyConfig.getRequestConfig();
				reqBodyConfig.getPackageMode().unpack(buffer, reqBodyConfig, tran_data, buffer.readableBytes());
				logger.info("���������,����:[\n{}\n]", tran_data);
			}else {
				logger.info("����Ҫ����������");
			}
			//TODO: �˴�����ת��data�ķ���
			
			//��¼�Ѿ���������������match_id
			MsgContainer.putUnpackedServerCode(info.getMatch_id(), server);
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
		try {
			ServiceData data = new ServiceData();
			PacketChannelBuffer buffer = new PacketChannelBuffer(info.getPacket());
			//�Ȳ���ͷ
			PackageConfig headConfig = Configs.getHeadConfig(server);
			StructConfig respHeadConfig = headConfig.getResponseConfig();
			respHeadConfig.getPackageMode().unpack(buffer, respHeadConfig, data, buffer.readableBytes());
			logger.info("����Ӧͷ��,����:[\n{}\n]", data);
			//if need unpacket body
			if(buffer.readableBytes() > 0) {
				//unpacket body
	//			PackageConfig bodyConfig = Configs.getBodyConfig(server, sys_service_code);
				PackageConfig bodyConfig = MsgContainer.getUnpackedBodyConf(info.getMatch_id());
				StructConfig respBodyConfig = bodyConfig.getResponseConfig();
				respBodyConfig.getPackageMode().unpack(buffer, respBodyConfig, data, buffer.readableBytes());
				logger.info("����Ӧ���,����:[\n{}\n]", data);
			}else {
				logger.info("����Ҫ����Ӧ������");
			}
		} catch (Exception e) {
			throw new UnpackResponseException(e);
		}
		//TODO: �˴�����ת��data�ķ���
		
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
