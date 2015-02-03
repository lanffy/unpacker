package unpacker;

import unpacker.msg.DefaultMsg;

import com.wk.actor.Actor;
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
public class MessageReceiver {
	
	private Log logger = LogFactory.getLog("Resolve");
	
	public static void main(String[] args) throws Exception {
		new MessageReceiver();
		System.out.println("listening");
		Thread.sleep(Integer.MAX_VALUE);
	}
	private static final String serverCommName = "MPserver";
	
	private ServerCommManager server;
	
	public MessageReceiver(){
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
		ChannelBuffer buffer = request.getRequestMsg().toChannelBuffer();
		int len = buffer.getInt();
		System.out.printf("before unpack,buffer length:{%d},buffer:{\n%s\n}",len, buffer.toHexString());
		logger.info("�յ�����,���ĳ���:[{}],����:{}", len, buffer.toHexString());
		ServiceData data = new ServiceData();
//		data = VrouterMsg.unpack(buffer);
//		data = InbankMsg.unpack(buffer);
		data = DefaultMsg.unpack(buffer);
		System.out.println(data);
		logger.info("��һ�β��:{}", data);
		request.doResponse((T)new ChannelBufferMsg(buffer));
	}
}
