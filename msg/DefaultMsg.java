package resolver.msg;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.StructConfig;
import com.wk.conv.mode.VRouterPackageMode;
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
}
