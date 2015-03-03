package resolver.msg;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.StructConfig;
import com.wk.conv.mode.VRouterPackageMode;
import com.wk.nio.ChannelBuffer;
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
	
	public static ChannelBuffer pack(ServiceData data) {
		StructConfig config = new StructConfig(new VRouterPackageMode(), false);
		PacketChannelBuffer buffer = new PacketChannelBuffer(10240);
		config.getPackageMode().pack(buffer, config, data);
		return buffer;
	}
	
	public static void main(String[] args) {
		ServiceData data = new ServiceData();
		data.putString("key", "value");
		ChannelBuffer buffer = pack(data);
		System.out.println(buffer.toHexString());
		ServiceData data2 = unpack(new PacketChannelBuffer(buffer));
		System.out.println(data2);
	}
}
