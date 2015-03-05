package resolver;

import resolver.conf.ChannelDistConf;
import resolver.conf.TranConfigLoader;
import resolver.conf.DecryptServerConf;
import resolver.conf.ModeLoader;
import resolver.conf.Servers;
import resolver.conf.TransDistinguishConf;
import resolver.msg.DefaultMsg;
import resolver.msg.PacketsInfo;
import resolver.msg.Resolver;
import resolver.msg.ResponseInfo;

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
		logger.info("begin start...");
		ChannelDistConf.load();
		Servers.load();
		TransDistinguishConf.load();
		DecryptServerConf.load();
		ModeLoader.load();
		TranConfigLoader.load();
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
		
		Receiver.logger.info("收到报文,报文长度:[{}],报文:\n{}", buffer.getInt() - 4, buffer.toHexString());
		
		//第一次拆包
		ServiceData data  = DefaultMsg.unpack(new PacketChannelBuffer(buffer));
		
		PacketsInfo info = new PacketsInfo(data);
		responseInfo.setMsg_id(info.getMsg_id());
		ChannelBuffer responseBuffer = new Resolver().unpackeTranBuffer(info, responseInfo);
		ChannelBufferMsg respMsg = new ChannelBufferMsg(responseBuffer);
		Receiver.logger.info("返回响应报文Hex: \n{}", responseBuffer.toHexString());
		request.doResponse((T)respMsg);
	}
	
}
