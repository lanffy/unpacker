package resolver.msg;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.StructConfig;
import com.wk.conv.mode.VRouterPackageMode;
import com.wk.sdo.ServiceData;

/**
 * @description VRouter报文拆组包,用于拆符合解析器接口的报文
 * @author raoliang
 * @version 2015年2月2日 下午7:33:55
 */
public class DefaultMsg{

	public static ServiceData unpack(PacketChannelBuffer buffer) {
		ServiceData data = new ServiceData();
		StructConfig config = new StructConfig(new VRouterPackageMode(), false);
		data = config.getPackageMode().unpack(buffer, config, data, buffer.readableBytes());
		return data;
	}
}
