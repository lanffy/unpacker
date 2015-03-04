package resolver.msg.impl;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.FieldConfig;
import com.wk.conv.config.StructConfig;
import com.wk.conv.mode.PackageMode;
import com.wk.nio.ChannelBuffer;
import com.wk.sdo.FieldType;
import com.wk.sdo.ServiceData;

/**
 * @description
 * @author raoliang
 * @version 2015年3月4日 下午4:05:44
 */
public class InbankSrvTranCode implements TranCodeImpl {

	public String getTranCode(ChannelBuffer buffer, PackageMode mode) {
		ChannelBuffer tran_code_buffer = getTranCodeBuffer(buffer);
		StructConfig config = getConfig(mode);
		ServiceData data = new ServiceData();
		config.getPackageMode().unpack(
				new PacketChannelBuffer(tran_code_buffer), config, data,
				tran_code_buffer.readableBytes());
		return data.getString("I1TRCD");
	}
	
	private static ChannelBuffer getTranCodeBuffer(ChannelBuffer buffer) {
		ChannelBuffer tempBuffer = buffer.duplicate();
		byte[] oldBytes = new byte[tempBuffer.readableBytes()];
		tempBuffer.getBytes(oldBytes);
		byte[] tran_code_bytes = new byte[4];
		System.arraycopy(oldBytes, 0, tran_code_bytes, 0, 4);
		ChannelBuffer tran_code_buffer = ChannelBuffer.allocate(tran_code_bytes.length);
		tran_code_buffer.putBytes(tran_code_bytes);
		return tran_code_buffer;
	}
	
	private static StructConfig getConfig(PackageMode mode) {
		 StructConfig config = new StructConfig(null, mode, true);
		 config.putChild(new FieldConfig("I1TRCD", FieldType.FIELD_STRING, 4));
		 return config;
	}

}
