package unpacker.msg;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.StructConfig;
import com.wk.conv.mode.PackageMode;
import com.wk.conv.mode.VRouterPackageMode;
import com.wk.sdo.ServiceData;

/**
 * @description VRouter���Ĳ����
 * @author raoliang
 * @version 2015��2��2�� ����7:33:55
 */
public class DefaultMsg extends Msg{

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
	public PackageMode getReqPackageMode(String reqModeName) {
		return new VRouterPackageMode();
	}

	@Override
	public PackageMode getRespPackageMode(String respModeName) {
		return new VRouterPackageMode();
	}

	@Override
	public PackageMode getErrPackageMode(String errModeName) {
		return new VRouterPackageMode();
	}
}