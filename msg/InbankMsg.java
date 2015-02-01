package unpacker.msg;

import java.util.HashMap;
import java.util.Map;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.FieldConfig;
import com.wk.conv.config.StructConfig;
import com.wk.conv.converter.DefaultConverter;
import com.wk.conv.mode.DefaultPackageMode;
import com.wk.conv.mode.FieldMode;
import com.wk.conv.mode.Modes;
import com.wk.conv.mode.PackageMode;
import com.wk.eai.config.BufferServerConfig;
import com.wk.eai.config.PackageConfig;
import com.wk.eai.inbank.InbankProtocol;
import com.wk.nio.ChannelBuffer;
import com.wk.sdo.FieldType;
import com.wk.sdo.ServiceData;

/**
 * @description
 * @author raoliang
 * @version 2015年1月20日 上午10:51:29
 */
public class InbankMsg {
	/**
	* @description 拆核心报文
	* @param buffer
	* @return
	* @version 2015年1月20日 上午10:58:49
	*/
	public static ServiceData unpack(ChannelBuffer buffer) {
		System.out.println("unpack begin....");
		ServiceData data = new ServiceData();
		FieldMode str_ebcd = Modes.getFieldMode("strEBCD");
		FieldMode std_ebcd = Modes.getFieldMode("stdEBCD");
		FieldMode pack = Modes.getFieldMode("pack");
		FieldMode standard = Modes.getFieldMode("standard");
		//域模式
		Map<FieldType, FieldMode> outsys_mode = new HashMap<FieldType, FieldMode>();
		outsys_mode.put(FieldType.FIELD_STRING, str_ebcd);
		outsys_mode.put(FieldType.FIELD_BYTE, std_ebcd);
		outsys_mode.put(FieldType.FIELD_SHORT, pack);
		outsys_mode.put(FieldType.FIELD_INT, pack);
		outsys_mode.put(FieldType.FIELD_LONG, pack);
		outsys_mode.put(FieldType.FIELD_FLOAT, pack);
		outsys_mode.put(FieldType.FIELD_DOUBLE, pack);
		outsys_mode.put(FieldType.FIELD_IMAGE, standard);
		//包模式
		PackageMode outsys = new DefaultPackageMode("outsys_mode", outsys_mode);
		
		//请求报文头
		StructConfig request = new StructConfig(outsys, true);
		//响应报文头
		StructConfig response = new StructConfig(outsys, true);
		response.putChild(new FieldConfig("O1MGID", FieldType.FIELD_STRING, 7));
		//失败报文
		StructConfig error = new StructConfig(outsys, true);
		error.putChild(new FieldConfig("O1INFO", FieldType.FIELD_STRING, 60));
		
		//拆报文头
		PackageMode response_mode = response.getPackageMode();
		response_mode.unpack(new PacketChannelBuffer(buffer), response, data, buffer.readableBytes());
		
		//服务系统公共的请求头和响应头接口配置
//		PackageConfig head = new PackageConfig(request, response, error);
		
//		String exp = "return true;";
//		@SuppressWarnings("unchecked")
//		BufferServerConfig server_config = new BufferServerConfig("test_server", "MPserver", InbankProtocol.class, exp, null, null, head);
		//请求报文体
		request.putChild(new FieldConfig("I1TRCD", FieldType.FIELD_STRING, 4));
		request.putChild(new FieldConfig("I1SBNO", FieldType.FIELD_STRING, 10));
		request.putChild(new FieldConfig("I1USID", FieldType.FIELD_STRING, 6));
		request.putChild(new FieldConfig("I1AUUS", FieldType.FIELD_STRING, 6));
		request.putChild(new FieldConfig("I1AUPS", FieldType.FIELD_STRING, 6));
		request.putChild(new FieldConfig("I1WSNO", FieldType.FIELD_STRING, 40));
		request.putChild(new FieldConfig("I1PYNO", FieldType.FIELD_STRING, 4));
		
		//响应报文体
		request.putChild(new FieldConfig("O1ACUR", FieldType.FIELD_INT, 2));
		request.putChild(new FieldConfig("O1TRDT", FieldType.FIELD_INT, 8));
		request.putChild(new FieldConfig("O1TRTM", FieldType.FIELD_INT, 6));
		request.putChild(new FieldConfig("O1TLSQ", FieldType.FIELD_STRING, 10));
		request.putChild(new FieldConfig("O1DATE", FieldType.FIELD_INT, 8));
		
//		PackageConfig tran8808 = new PackageConfig(request, response);
//		server_config.putServiceIOConfig("8808", tran8808);
		
		//拆报文体
		response_mode = response.getPackageMode();
		response_mode.unpack(new PacketChannelBuffer(buffer), response, data, buffer.readableBytes());
		
		return data;
	}
}
