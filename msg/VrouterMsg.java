package unpacker.msg;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.StructConfig;
import com.wk.conv.mode.VRouterStandardPackageMode;
import com.wk.nio.ChannelBuffer;
import com.wk.sdo.ServiceData;

/**
 * @description
 * @author raoliang
 * @version 2015年1月20日 上午12:15:23
 */
public class VrouterMsg {
	
	/**
	* @description vrouter标准报文拆包
	* @param buffer
	* @return
	* @version 2015年1月20日 上午12:21:06
	*/
	public static ServiceData unpack(ChannelBuffer buffer) {
		ServiceData data = new ServiceData();
		VRouterStandardPackageMode vrouter = new VRouterStandardPackageMode("vrouter", false);
		StructConfig response = new StructConfig(vrouter, false);
		data = vrouter.unpack(new PacketChannelBuffer(buffer), response, data, buffer.readableBytes());
		return data;
	}
}
