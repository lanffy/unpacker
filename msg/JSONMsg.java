package unpacker.msg;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.StructConfig;
import com.wk.conv.mode.Modes;
import com.wk.conv.mode.PackageMode;
import com.wk.nio.ChannelBuffer;
import com.wk.sdo.ServiceData;

/**
 * @description
 * @author raoliang
 * @version 2015年1月20日 上午12:24:28
 */
public class JSONMsg {
	
	/**
	* @description json报文拆包
	* @param buffer
	* @return
	* @author raoliang
	* @version 2015年1月20日 上午12:28:48
	*/
	public static ServiceData unpack(ChannelBuffer buffer) {
		ServiceData data = new ServiceData();
		PackageMode json = Modes.getPackageMode("json");
		StructConfig response = new StructConfig(json, false);
		data = json.unpack(new PacketChannelBuffer(buffer), response, data, buffer.readableBytes());
		return data;
	}
}
