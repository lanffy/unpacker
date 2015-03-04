package resolver.msg;

import com.wk.actor.Actor;
import com.wk.net.ChannelBufferMsg;
import com.wk.net.ClientCommManager;
import com.wk.net.CommManagers;
import com.wk.net.JSONMsg;
import com.wk.net.Msg;
import com.wk.net.Request;
import com.wk.nio.ChannelBuffer;
import com.wk.sdo.ServiceData;

/**
 * @description
 * @author raoliang
 * @version 2015年2月27日 上午11:36:16
 */
public class SendClient {
	
	private ClientCommManager client;
	
	public SendClient() {
		client = getServerCommManager();
	}
	
	private ClientCommManager getServerCommManager(){
		return CommManagers.getClientCommManager("sendClient", JSONMsg.class, com.wk.net.NullResponseActor.class);
	}
	
	public void send(Msg msg) {
		client.send(msg);
	}
	
	public static void main(String[] args) throws InterruptedException {
		ServiceData data = new ServiceData();
		data.putString("key", "value");
		JSONMsg jsonmsg = new JSONMsg(data);
		new SendClient().send(jsonmsg);
		Thread.sleep(Integer.MAX_VALUE);
	}
	
}

class SimulateClientReqActor<T extends ChannelBufferMsg> extends Actor<Request<T>> {
	
	@SuppressWarnings("unchecked")
	@Override
	protected void act(Request<T> request) {
		ChannelBuffer buffer = request.getRequestMsg().toChannelBuffer();
		
		ChannelBufferMsg respMsg = new ChannelBufferMsg(buffer);
		request.doResponse((T)respMsg);
	}
	
}
