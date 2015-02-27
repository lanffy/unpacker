package resolver.msg;

import resolver.conf.Configs;
import resolver.conf.Servers;
import resolver.conf.TransDistinguishConf;

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
 * @version 2015��2��10�� ����3:55:09
 */
public class Resolver {
	private static final Log logger = LogFactory.getLog();
	
	private static String recv_time;
	private static int msg_id;
	private static String ret_code;
	private static String ret_msg;

	public static ChannelBuffer unpackeTranBuffer(PacketsInfo info) {
		String server = Servers.getServerByIp(info.getDst_ip() + ":" + info.getDst_prot());
		if(server == null) {
			logger.warn("Ŀ�ķ���ϵͳIP:[{}]�����ڣ���鿴��Ӧ�����ļ����Ƿ�����!",
					info.getDst_ip()+":"+info.getDst_prot());
			ret_code = "1";
			ret_msg = "Ŀ�ķ���ϵͳIP:["+ info.getDst_ip()+":"+info.getDst_prot() + "]������";
			logger.info("�����쳣��Ӧ����");
			return packResponseBuffer();
		}
		logger.info("�յ�����ip:[{}]�ı��ģ���������ϵͳ:[{}],����ϵͳip:[{}]",
				info.getSrc_ip(), server, info.getDst_ip()+":"+info.getDst_prot());
		int typeFlag = info.getPacket_type();
		if(typeFlag == 1) {
			logger.info("�������ͣ�[{}]", "request");
			//����������ģ���ֱ�Ӳ��
			unpackRequestMsg(info, server);
		}else if(typeFlag == 2){
			logger.info("�������ͣ�[{}]", "response");
			//�������Ӧ����,�򱣴汨��
			MsgContainer.putResponseMsg(info);
			//�ж��Ƿ��Ѿ���������ͬmatch_id��������
			String unpackedServer = MsgContainer.getUnpackedConf(info.getMatch_id());
			//����Ѿ���������ͬmatch_id��������
			if(unpackedServer != null) {
				logger.info("�յ��ж�Ӧ�����ĵ���Ӧ����,���������Ĳ��");
				unpackResponseMsg(info, server);
			}else {
				logger.info("�յ��޶�Ӧ�����ĵ���Ӧ����,��ʱ����,�����.");
			}
		}else {
			logger.warn("��������:[{}]������!", typeFlag);
			ret_code = "2";
			ret_msg = "��������:["+typeFlag+"]������!";
			logger.info("�����쳣��Ӧ����");
			return packResponseBuffer();
		}
		ret_code = "0";
		ret_msg = "����ɹ�";
		logger.info("���سɹ���Ӧ����");
		return packResponseBuffer();
	}
	
	public static void unpackRequestMsg(PacketsInfo info, String server) {
		PacketChannelBuffer buffer = new PacketChannelBuffer(info.getPacket());
		ServiceData data = new ServiceData();
		//�Ȳ���ͷ����ʶ����
		PackageConfig headConfig = Configs.getHeadConfig(server);
		StructConfig reqHeadConfig = headConfig.getRequestConfig();
		reqHeadConfig.getPackageMode().unpack(buffer, reqHeadConfig, data, buffer.readableBytes());
		logger.info("������ͷ��,����:[\n{}\n]", data);
		
		//if need unpacket body
		if(buffer.readableBytes() > 0) {
			//ʶ������
			String sys_service_code = getTranCode(data, TransDistinguishConf.getTranDistField(server));
			//�ٲ�����
			PackageConfig bodyConfig = Configs.getBodyConfig(server, sys_service_code);
			StructConfig reqBodyConfig = bodyConfig.getRequestConfig();
			reqBodyConfig.getPackageMode().unpack(buffer, reqBodyConfig, data, buffer.readableBytes());
			logger.info("���������,����:[\n{}\n]", data);
		}else {
			logger.info("����Ҫ������");
		}
		//TODO: �˴�����ת��data�ķ���
		
		//��¼�Ѿ���������������
		MsgContainer.putUnpackedConf(info.getMatch_id(), server);
		
		//����match_id�ж�֮ǰ�Ƿ��յ���Ӧ���ģ�����յ������
		PacketsInfo respInfo = MsgContainer.getResponseMsg(info.getMatch_id());
		if(respInfo != null) {
			logger.info("�������Ķ�Ӧ����Ӧ����");
			unpackResponseMsg(respInfo, server);
		} else {
			logger.info("��δ�յ������Ķ�Ӧ����Ӧ����");
		}
	}
	
	public static void unpackResponseMsg(PacketsInfo info, String server) {
		ServiceData data = new ServiceData();
		PacketChannelBuffer buffer = new PacketChannelBuffer(info.getPacket());
		//�Ȳ���ͷ
		PackageConfig headConfig = Configs.getHeadConfig(server);
		StructConfig respHeadConfig = headConfig.getResponseConfig();
		respHeadConfig.getPackageMode().unpack(buffer, respHeadConfig, data, buffer.readableBytes());
		logger.info("����Ӧͷ��,����:[\n{}\n]", data);
		//if need unpacket body
		if(buffer.readableBytes() > 0) {
			//ʶ������
			String sys_service_code = getTranCode(data, TransDistinguishConf.getTranDistField(server));
			//unpacket body
			PackageConfig bodyConfig = Configs.getBodyConfig(server, sys_service_code);
			StructConfig respBodyConfig = bodyConfig.getResponseConfig();
			respBodyConfig.getPackageMode().unpack(buffer, respBodyConfig, data, buffer.readableBytes());
			logger.info("����Ӧ���,����:[\n{}\n]", data);
		}
		//TODO: �˴�����ת��data�ķ���
		
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
	
	public static void putIntAttr(ChannelBuffer buffer, String attr_name, int attr_value) {
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
	
	private static String getTranCode(ServiceData data, String tranCodeExpr) {
		if(!tranCodeExpr.contains(">")) {
			return data.getString(tranCodeExpr);
		}else  {
			int index = tranCodeExpr.indexOf(">");
			return getTranCode(data.getServiceData(tranCodeExpr.substring(0, index)), tranCodeExpr.substring(index));
		}
	}
	
}
