package resolver;

import resolver.conf.ConfigLoader;
import resolver.conf.ModeLoader;
import resolver.conf.Servers;
import resolver.conf.TransDistinguishConf;
import resolver.msg.DefaultMsg;
import resolver.msg.PacketsInfo;
import resolver.msg.Resolver;
import resolver.msg.ResponseInfo;
import resolver.msg.ResponseMsg;

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
 * @version 2015年1月16日 下午7:48:05
 */
public class Receiver {
	
	protected static final Log logger = LogFactory.getLog();
	
	public static void main(String[] args) throws Exception {
		Servers.loadServer();
		TransDistinguishConf.loadTransDistConf();
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
		ResponseInfo responseInfo = new ResponseInfo();
		ChannelBuffer buffer = request.getRequestMsg().toChannelBuffer();
		
		Receiver.logger.info("收到报文,报文长度:[{}],报文:\n{}", buffer.getInt(), buffer.toHexString());
		
		//第一次拆包
		ServiceData data  = DefaultMsg.unpack(new PacketChannelBuffer(buffer));
		Receiver.logger.info("第一次拆包:{}", data);
		
		PacketsInfo info = new PacketsInfo(data);
		responseInfo.setMsg_id(info.getMsg_id());
		ChannelBuffer responseBuffer = Resolver.unpackeTranBuffer(info, responseInfo);
		ChannelBufferMsg respMsg = new ChannelBufferMsg(responseBuffer);
		Receiver.logger.info("返回响应报文Hex: \n{}", responseBuffer.toHexString());
		request.doResponse((T)respMsg);
	}
	
}
