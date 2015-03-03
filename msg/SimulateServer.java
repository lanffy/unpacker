package resolver.msg;

import com.wk.actor.Actor;
import com.wk.net.ChannelBufferMsg;
import com.wk.net.CommManagers;
import com.wk.net.Request;
import com.wk.net.ServerCommManager;
import com.wk.nio.ChannelBuffer;

/**
 * @description
 * @author raoliang
 * @version 2015年2月27日 上午11:36:16
 */
public class SimulateServer {

	public static void main(String[] args) throws Exception {
		new SimulateServer();
		System.out.println("listening");
		Thread.sleep(Integer.MAX_VALUE);
	}
	
	private ServerCommManager server;
	
	public SimulateServer(){
		this.server = getServerCommManager();
	}
	
	private ServerCommManager getServerCommManager(){
		return CommManagers.getServerCommManager("simulateServer", ChannelBufferMsg.class, SimulateServerReqActor.class);
	}

}

class SimulateServerReqActor<T extends ChannelBufferMsg> extends Actor<Request<T>> {
	
	@SuppressWarnings("unchecked")
	@Override
	protected void act(Request<T> request) {
		
		ChannelBuffer buffer = request.getRequestMsg().toChannelBuffer();
		
		System.out.println("***server***\n" + buffer.toHexString());
		
		ChannelBufferMsg respMsg = new ChannelBufferMsg(buffer);
		request.doResponse((T)respMsg);
	}
	
}
