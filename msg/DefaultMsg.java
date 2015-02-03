package unpacker.msg;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.StructConfig;
import com.wk.conv.mode.PackageMode;
import com.wk.conv.mode.VRouterPackageMode;
import com.wk.nio.ChannelBuffer;
import com.wk.sdo.ServiceData;

/**
 * @description
 * @author raoliang
 * @version 2015年2月2日 下午7:33:55
 */
public class DefaultMsg {
	
	public static ServiceData unpack(ChannelBuffer buffer) {
		ServiceData data = new ServiceData();
		PackageMode mode = new VRouterPackageMode();
		StructConfig response = new StructConfig(mode, false);
		data = mode.unpack(new PacketChannelBuffer(buffer), response, data, buffer.readableBytes());
		return data;
	}
}
