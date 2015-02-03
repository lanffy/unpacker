package unpacker;

import java.io.IOException;
import java.io.InputStream;

import unpacker.msg.VrouterMsg;
import unpacker.util.JSONFileUtil;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.StructConfig;
import com.wk.conv.mode.VRouterStandardPackageMode;
import com.wk.net.ChannelBufferMsg;
import com.wk.nio.ChannelBuffer;
import com.wk.sdo.ServiceData;

/**
 * @description
 * @author raoliang
 * @version 2015年1月19日 上午9:40:34
 */
public class SocketReceiver {
	public static void main(String[] args) {
//		unpack();
//		System.out.println("done");
		ServiceData data = VrouterMsg.unpack(new PacketChannelBuffer(createRequestMsg("0673").toChannelBuffer()));
		System.out.println("******************");
		String json = JSONFileUtil.convertServiceDataToJson(data);
		System.out.println(json);
	}
	public static void unpack() {
		VRouterStandardPackageMode vrouter = new VRouterStandardPackageMode("vrouter", false);
		StructConfig response = new StructConfig(vrouter, false);
		ChannelBuffer buffer = createRequestMsg("0673").toChannelBuffer();
		System.out.println(buffer.toHexString());
		System.out.println("******************");
		ServiceData data = new ServiceData();
		data = vrouter.unpack(new PacketChannelBuffer(buffer), response, data, buffer.readableBytes());
		System.out.println(data);
	}
	//创建请求报文
	private static ChannelBufferMsg createRequestMsg(String fileName) {
		byte[] tempBytes = null;
		try {
			InputStream in = SocketReceiver.class.getResourceAsStream(fileName);
			int total = in.available();
			tempBytes = new byte[total];
			in.read(tempBytes);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ChannelBuffer buffer = ChannelBuffer.allocate(tempBytes.length);
		buffer.putBytes(tempBytes);
		return new ChannelBufferMsg(buffer);
	}
}
