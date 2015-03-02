package resolver.msg;

import com.wk.logging.Log;
import com.wk.logging.LogFactory;
import com.wk.nio.ChannelBuffer;

/**
 * @description
 * @author raoliang
 * @version 2015年3月2日 下午2:45:08
 */
public class ResponseMsg {
	private static final Log logger = LogFactory.getLog();
	private static final int repBufferMaxSize = 10240;
	
	public static ChannelBuffer packRepMsg(ResponseInfo info) {
//		if(logger.isDebugEnabled()) {
			logger.debug("返回数据内容：\n{}", info);
//		}
		ChannelBuffer buffer = ChannelBuffer.allocate(repBufferMaxSize);
		buffer.putInt(0);
		putIntAttr(buffer, "msg_id", info.getMsg_id());
		putStringAttr(buffer, "recv_time", info.getRecv_time());
		putStringAttr(buffer, "ret_code", info.getRet_code());
		putStringAttr(buffer, "ret_msg", info.getRet_msg());
		buffer.putInt(0, buffer.readableBytes());
		return buffer;
	}
	
	private static void putStringAttr(ChannelBuffer buffer, String attr_name, String attr_value) {
		//字段名长度
		buffer.putByte((byte) attr_name.length());
		//字段名
		buffer.putBytes(attr_name.getBytes());
		//字段类型
		buffer.putByte((byte)0x07);
		//字段值长度
		buffer.putByte((byte)attr_value.length());
		//字段值
		buffer.putBytes(attr_value.getBytes());
	}
	
	private static void putIntAttr(ChannelBuffer buffer, String attr_name, int attr_value) {
		//字段名长度
		buffer.putByte((byte) attr_name.length());
		//字段名
		buffer.putBytes(attr_name.getBytes());
		//字段类型
		buffer.putByte((byte)0x05);
		//字段值长度
		//字段值
	}
	
}
