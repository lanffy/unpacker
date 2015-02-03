package unpacker.msg;

import com.wk.logging.Log;
import com.wk.logging.LogFactory;
import com.wk.nio.ChannelBuffer;
import com.wk.sdo.ServiceData;

/**
 * @description
 * @author raoliang
 * @version 2015年2月2日 下午7:21:34
 */
public class BypassMsgProtocol {
	
	protected static final Log logger = LogFactory.getLog("resolve");
	
	public static void unpack(ChannelBuffer buffer, ServiceData data) {
		logger.info("unpack buffer:{}", buffer.toHexString());
		
	}
}
