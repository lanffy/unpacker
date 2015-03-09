package resolver.msg;

import resolver.util.BufferReader;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.StructConfig;
import com.wk.conv.mode.VRouterPackageMode;
import com.wk.nio.ChannelBuffer;
import com.wk.sdo.ServiceData;

/**
 * @description VRouter���Ĳ����,���ڲ���Ͻ������ӿڵı���
 * @author raoliang
 * @version 2015��2��2�� ����7:33:55
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
		ChannelBuffer buffer = BufferReader.createRequestMsg("response");
		StructConfig config = new StructConfig(com.wk.conv.mode.Modes.getPackageMode("vrouterserver"), false);
		ServiceData data = new ServiceData();
		config.getPackageMode().unpack(new PacketChannelBuffer(buffer), config, data, buffer.readableBytes());
		System.out.println("done");
	}
}
