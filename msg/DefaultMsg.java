package unpacker.msg;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.StructConfig;
import com.wk.conv.mode.PackageMode;
import com.wk.conv.mode.VRouterPackageMode;
import com.wk.sdo.ServiceData;

/**
 * @description VRouter报文拆组包
 * @author raoliang
 * @version 2015年2月2日 下午7:33:55
 */
public class DefaultMsg extends Msg{

	public DefaultMsg(String reqModeName, String respModeName,
			String errModeName) {
		super(reqModeName, respModeName, errModeName);
		requestMode = getReqPackageMode();
		responseMode = getRespPackageMode();
		errorMode = getErrPackageMode();
	}

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
		StructConfig error = new StructConfig(errorMode, false);
		data = errorMode.unpack(buffer, error, data, buffer.readableBytes());
		return data;
	}

	@Override
	public PackageMode getReqPackageMode() {
		return new VRouterPackageMode();
	}

	@Override
	public PackageMode getRespPackageMode() {
		return new VRouterPackageMode();
	}

	@Override
	public PackageMode getErrPackageMode() {
		return new VRouterPackageMode();
	}
}
