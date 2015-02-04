package unpacker.msg;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.StructConfig;
import com.wk.conv.mode.Modes;
import com.wk.conv.mode.PackageMode;
import com.wk.sdo.ServiceData;

/**
 * @description
 * @author raoliang
 * @version 2015年1月20日 上午12:24:28
 */
public class JSONMsg extends Msg{
	
	@Override
	public ServiceData unpackRequest(PacketChannelBuffer buffer) {
		ServiceData data = new ServiceData();
		StructConfig request = new StructConfig(requestMode, false);
		data = requestMode.unpack(buffer, request, data, buffer.readableBytes());
		return data;
	}

	@Override
	public ServiceData unpackResponse(PacketChannelBuffer buffer) {
		ServiceData data = new ServiceData();
		StructConfig response = new StructConfig(responseMode, false);
		data = responseMode.unpack(buffer, response, data, buffer.readableBytes());
		return data;
	}

	@Override
	public ServiceData unpackError(PacketChannelBuffer buffer) {
		ServiceData data = new ServiceData();
		StructConfig error = new StructConfig(errorMode,false);
		data = errorMode.unpack(buffer, error, data, buffer.readableBytes());
		return data;
	}

	@Override
	public PackageMode getReqPackageMode(String reqModeName) {
		return Modes.getPackageMode("json");
	}

	@Override
	public PackageMode getRespPackageMode(String respModeName) {
		return Modes.getPackageMode("json");
	}

	@Override
	public PackageMode getErrPackageMode(String errModeName) {
		return Modes.getPackageMode("json");
	}


}
