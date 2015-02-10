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
 * @version 2015��1��16�� ����7:48:05
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
		logger.info("�յ�����,���ĳ���:[{}],����:{}", buffer.getInt(), buffer.toHexString());
		
		ServiceData data  = DefaultMsg.unpack(new PacketChannelBuffer(buffer));
		
		logger.info("��һ�β��:{}", data);
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
			logger.warn("Ŀ�ķ���ϵͳIP:[{}]�����ڣ���鿴��Ӧ�����ļ����Ƿ�����!",
					info.getDst_ip()+":"+info.getDst_prot());
			ret_code = "1";
			ret_msg = "Ŀ�ķ���ϵͳIP:["+ info.getDst_ip()+":"+info.getDst_prot() + "]������";
			return;
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
				unpackResponseMsg(info, server);
			}
		}else {
			logger.warn("��������:[{}]������!", typeFlag);
			ret_code = "2";
			ret_msg = "��������:["+typeFlag+"]������!";
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
		logger.info("������ͷ��,����:[\n{}\n]", data);
		reqBodyConfig.getPackageMode().unpack(buffer, reqBodyConfig, data, buffer.readableBytes());
		logger.info("���������,����:[\n{}\n]", data);
		//TODO: �˴�����ת��data�ķ���
		
		//��¼�Ѿ���������������
		MsgContainer.putUnpackedConf(info.getMatch_id(), server);
		
		//����match_id�ж�֮ǰ�Ƿ��յ���Ӧ���ģ�����յ������
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
		logger.info("������ͷ��,����:[\n{}\n]", data);
		respBodyConfig.getPackageMode().unpack(buffer, respBodyConfig, data, buffer.readableBytes());
		logger.info("���������,����:[\n{}\n]", data);
		//TODO: �˴�����ת��data�ķ���
		
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
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");//�������ڸ�ʽ
		return df.format(new Date());// new Date()Ϊ��ȡ��ǰϵͳʱ��
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
}
