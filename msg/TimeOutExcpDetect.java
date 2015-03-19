package resolver.msg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.wk.SystemConfig;
import com.wk.net.JSONMsg;
import com.wk.sdo.ServiceData;
import com.wk.threadpool.Task;

/**
 * @description 检测请求是否收到响应
 * @author raoliang
 * @version 2015年3月19日 下午2:58:56
 */
public class TimeOutExcpDetect extends Task{
	private static final SystemConfig config = SystemConfig.getInstance();
	private static final int timeOut = config.getInt("timeOut", 30000);
	private static final Boolean isSendMsg = config.getBoolean("isSendMsg", false);
	private final SendClient client = new SendClient();

	public static void main(String[] args) {
		String begin = "2015-03-11 23:59:01.111";
		String end = "2015-03-12 00:00:01.666";
//		String end = getTime();
//		System.out.println(end);
		countTime(begin, end);
	}
	
	public void execute() {
		String end = getTime();
		ConcurrentHashMap<String, PacketsInfo> unpackedReqPacket = MsgContainer.getUnpackedReqPacket();
		List<String> timeOutKey = new ArrayList<String>();
		String begin = "";
		for(Entry<String, PacketsInfo> map : unpackedReqPacket.entrySet()) {
			begin = map.getValue().getSend_time();
			if(countTime(begin, end) > timeOut) {
				timeOutKey.add(map.getKey());
			}
		}
		
		for(String key : timeOutKey) {
			ServiceData data = getResultData(unpackedReqPacket.get(key));
			if(isSendMsg) {
				client.send(new JSONMsg(data));
			}
			
			MsgContainer.removeResponseMsg(key);
			MsgContainer.removeUnpackedConf(key);
			MsgContainer.removeUnpackedBodyConf(key);
			MsgContainer.removeUnpackedReqPacket(key);
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
			System.out.format("begin time:%s, end time:%s.\n", begin_time, end_time);
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
