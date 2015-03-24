package resolver.msg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.wk.SystemConfig;
import com.wk.logging.Log;
import com.wk.logging.LogFactory;
import com.wk.net.JSONMsg;
import com.wk.sdo.ServiceData;
import com.wk.threadpool.Task;
import com.wk.threadpool.ThreadPool;

/**
 * @description 检测请求是否收到响应
 * @author raoliang
 * @version 2015年3月19日 下午2:58:56
 */
public class TimeOutExcpDetectTask extends Task{
	
	protected static final Log logger = LogFactory.getLog();
	private static final SystemConfig config = SystemConfig.getInstance();
	private static final int timeOut = config.getInt("resolver.timeOut", 30000);
	private static final Boolean isSendMsg = config.getBoolean("resolver.isSendMsg", false);
	private final SendClient client = new SendClient();

	public static void main(String[] args) {
		ThreadPool.getThreadPool().executeAt(new TimeOutExcpDetectTask(), new Date(), 5000);
		try {
			Thread.sleep(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void execute() {
		if(logger.isDebugEnabled()) {
			logger.debug("检测是否有超时的请求...");
		}
		doDetect();
	}
	
	private void doDetect() {
		String end = getTime();
		ConcurrentHashMap<String, PacketsInfo> unpackedReqPacket = MsgContainer.getUnpackedReqPacket();
		List<String> timeOutKey = new ArrayList<String>();
		String begin = "";
		for(Entry<String, PacketsInfo> map : unpackedReqPacket.entrySet()) {
			begin = map.getValue().getSend_time();
			long counttime = countTime(begin, end);
			if(logger.isDebugEnabled()) {
				logger.debug("time begin: -> [{}], time end : -> [{}]", begin, end);
				logger.debug("countTime: -> [{}], timeOut: -> [{}]", counttime, timeOut);
			}
			if(countTime(begin, end) > timeOut) {
				timeOutKey.add(map.getKey());
			}
		}
		for(String key : timeOutKey) {
			ServiceData data = getResultData(unpackedReqPacket.get(key));
			data.putString("packet_type", "2");
			if(isSendMsg) {
				client.send(new JSONMsg(data));
			}
			MsgContainer.removeResponseMsg(key);
			MsgContainer.removeUnpackedConf(key);
			MsgContainer.removeUnpackedBodyConf(key);
			MsgContainer.removeUnpackedReqPacket(key);
			logger.info("请求报文等待响应超时,key -> [{}]", key);
		}
	}
	
	public ServiceData getResultData(PacketsInfo info) {
		ServiceData data = info.getData();
		ServiceData tran_data = new ServiceData();
		tran_data.putString("error_code", "000000");
		tran_data.putString("error_msg", "等待请求超时");
		data.putServiceData("packet", tran_data);
		return data;
	}
	
	public static long countTime(String begin, String end) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		long countTime = 0;
		try {
			Date begin_time = df.parse(begin);
			Date end_time = df.parse(end);
			countTime = (end_time.getTime() - begin_time.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return countTime;
	}
	
	private static String getTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//设置日期格式
		return df.format(new Date());// new Date()为获取当前系统时间
	}
}
