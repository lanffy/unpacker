package resolver;

import java.text.SimpleDateFormat;
import java.util.Date;

import resolver.conf.ConfigLoader;
import resolver.conf.ModeLoader;
import resolver.conf.Servers;
import resolver.msg.DefaultMsg;
import resolver.msg.PacketsInfo;
import resolver.msg.Resolver;

import com.wk.actor.Actor;
import com.wk.conv.PacketChannelBuffer;
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
	
	protected static final Log logger = LogFactory.getLog();
	
	public static void main(String[] args) throws Exception {
		Servers.loadServer();
		ModeLoader.loadMode();
		ConfigLoader.loadConf();
		new Receiver();
		logger.info("listening...");
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
	
	@SuppressWarnings("unchecked")
	@Override
	protected void act(Request<T> request) {
		Resolver.setRecv_time(getTime());
		ChannelBuffer buffer = request.getRequestMsg().toChannelBuffer();
		
//		System.out.printf("before unpack,buffer length:{%d},buffer:{\n%s\n}",buffer.getInt(), buffer.toHexString());
		Receiver.logger.info("�յ�����,���ĳ���:[{}],����:\n{}", buffer.getInt(), buffer.toHexString());
		
		ServiceData data  = DefaultMsg.unpack(new PacketChannelBuffer(buffer));
		
		Receiver.logger.info("���:{}", data);
		
		PacketsInfo info = new PacketsInfo(data);
		Resolver.setMsg_id(info.getMsg_id());
		ChannelBuffer responseBuffer = Resolver.unpackeTranBuffer(info);
		Receiver.logger.info("������Ӧ��Ӧ����:\n{}", responseBuffer.toHexString());
		request.doResponse((T)new ChannelBufferMsg(responseBuffer));
	}
	
	private static String getTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");//�������ڸ�ʽ
		return df.format(new Date());// new Date()Ϊ��ȡ��ǰϵͳʱ��
	}
	
}
