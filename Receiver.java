package resolver;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import resolver.conf.ChannelDistConf;
import resolver.conf.DecryptServerConf;
import resolver.conf.ModeLoader;
import resolver.conf.Servers;
import resolver.conf.TranConfigLoader;
import resolver.conf.TransDistinguishConf;
import resolver.msg.DefaultMsg;
import resolver.msg.PacketsInfo;
import resolver.msg.Resolver;
import resolver.msg.ResponseInfo;
import resolver.msg.ResponseMsg;
import resolver.msg.TimeOutExcpDetectTask;

import com.wk.SystemConfig;
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
import com.wk.threadpool.ThreadPool;

/**
 * @description
 * @author raoliang
 * @version 2015年1月16日 下午7:48:05
 */
@SuppressWarnings("unchecked")
public class Receiver {
	
	protected static final Log logger = LogFactory.getLog();
	private static final SystemConfig config = SystemConfig.getInstance();
	private static final int timeOutDetectInterval = config.getInt("resolver.timeOutDetectInterval", 5000);
	
	public static void main(String[] args) throws Exception {
		logger.info("begin start...");
		ChannelDistConf.load();
		Servers.load();
		TransDistinguishConf.load();
		DecryptServerConf.load();
		ModeLoader.load();
		TranConfigLoader.load();
		new Receiver();
		ThreadPool.getThreadPool().executeAt(new TimeOutExcpDetectTask(), new Date(), timeOutDetectInterval);
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
	private static final AtomicLong match_id = new AtomicLong(0);
	@SuppressWarnings("unchecked")
	@Override
	protected void act(Request<T> request) {
		ChannelBuffer buffer = request.getRequestMsg().toChannelBuffer();
		ResponseInfo responseInfo = new ResponseInfo();
		Receiver.logger.info("收到报文,报文长度:[{}],报文:\n{}", buffer.getInt() - 4,
				buffer.toHexString());
		ServiceData data = DefaultMsg.unpack(new PacketChannelBuffer(buffer));
		data.putString("match_id", String.valueOf(getMatch_id()));
		// 需求变更，接收到报文后先返回响应，再处理收到的报文
		PacketsInfo info = new PacketsInfo(data);
		request.doResponse((T) packetResp(info.getMsg_id(), responseInfo));
		// resolve packet
		new Resolver().unpackeTranBuffer(info,
				responseInfo);
	}
	
	private ChannelBufferMsg packetResp(int msg_id, ResponseInfo responseInfo) {
		responseInfo.setMsg_id(msg_id);
		responseInfo.setRet_code("0");
		responseInfo.setRet_msg("");
		return new ChannelBufferMsg(ResponseMsg.packRepMsg(responseInfo));
	}
	
	private String getMatch_id() {
		long id = match_id.incrementAndGet();
		if (id == 99999) {
			match_id.set(1);
		}
		return String.valueOf(id);
	}
	
}
