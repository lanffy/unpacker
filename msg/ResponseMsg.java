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
	
	public static ChannelBuffer packRepMsg(ResponseInfo info) {
		ChannelBuffer buffer = ChannelBuffer.allocate(10240);
		if(logger.isDebugEnabled()) {
			logger.debug("返回数据内容：\n{}", info);
		}
//		buffer.putInt(0);
		putIntAttr(buffer, "msg_id", info.getMsg_id());
		putStringAttr(buffer, "recv_time", info.getRecv_time());
		putStringAttr(buffer, "ret_code", info.getRet_code());
		putStringAttr(buffer, "ret_msg", info.getRet_msg());
//		buffer.putInt(0, buffer.readableBytes());
		return buffer;
	}
	
	public static void putStringAttr(ChannelBuffer buffer, String attr_name, String attr_value) {
		//字段名长度
		buffer.putByte((byte) attr_name.length());
		//字段名
		buffer.putBytes(attr_name.getBytes());
		//字段类型
		buffer.putByte((byte)0x07);
		//字段值长度
		byte[] bytes = attr_value.getBytes();
		buffer.putByte((byte)bytes.length);
		//字段值
		buffer.putBytes(bytes);
	}
	
	public static void putIntAttr(ChannelBuffer buffer, String attr_name, int attr_value) {
		//字段名长度
		buffer.putByte((byte) attr_name.length());
		//字段名
		buffer.putBytes(attr_name.getBytes());
		//字段类型
		buffer.putByte((byte)0x05);
		//字段值长度
		//字段值
		buffer.putInt(attr_value);
	}
}
