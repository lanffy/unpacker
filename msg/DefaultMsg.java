package resolver.msg;

import resolver.util.BufferReader;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.StructConfig;
import com.wk.conv.mode.VRouterPackageMode;
import com.wk.eai.unitepay.UnitePayXMLPackageMode;
import com.wk.eai.unitepay.XMLPackageMode;
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
		testXML();
	}
	
	public static void testVrouter() {
		ChannelBuffer buffer = BufferReader.createRequestMsg("response");
		StructConfig config = new StructConfig(com.wk.conv.mode.Modes.getPackageMode("vrouterserver"), false);
		ServiceData data = new ServiceData();
		config.getPackageMode().unpack(new PacketChannelBuffer(buffer), config, data, buffer.readableBytes());
		System.out.println("done");
	}
	
	public static void testXML() {
		ChannelBuffer buffer = BufferReader.createRequestMsg("xml2");
		System.out.println("*****1begin*****");
		System.out.println(buffer.toHexString());
		System.out.println("*****1end*****");
		UnitePayXMLPackageMode xmlMode = new XMLPackageMode("xml"); 
		StructConfig config = new StructConfig(xmlMode, false);
		ServiceData data = new ServiceData();
		config.getPackageMode().unpack(new PacketChannelBuffer(buffer), config, data, buffer.readableBytes());
		System.out.println("*****2begin*****");
		System.out.println(data);
		System.out.println("*****2end*****");
		System.out.println("xml done");
	}
}
