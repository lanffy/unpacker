package resolver.conf;

import java.util.HashMap;
import java.util.Map;

import resolver.util.BufferReader;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.FieldConfig;
import com.wk.conv.config.StructConfig;
import com.wk.conv.mode.DefaultPackageMode;
import com.wk.conv.mode.FieldMode;
import com.wk.conv.mode.Modes;
import com.wk.conv.mode.PackageMode;
import com.wk.nio.ChannelBuffer;
import com.wk.sdo.FieldType;
import com.wk.sdo.ServiceData;

/**
 * @description 安徽核心服务系统拆包模式类 用于测试
 * @author raoliang
 * @version 2015年2月2日 上午11:00:38
 */
public class InbankMode {
	public static PackageMode initInankMode() {
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
		
		return outsys;
	}
	
	public static StructConfig getConfig() {
		 StructConfig config = new StructConfig(null, initInankMode(), true);
		 config.putChild(new FieldConfig("I1TRCD", FieldType.FIELD_STRING, 4));
		 return config;
	}
	
	public static void main(String[] args) {
		ChannelBuffer tran_code_buffer = getTranCodeBuffer();
		StructConfig config = getConfig();
		ServiceData data = new ServiceData();
		config.getPackageMode().unpack(new PacketChannelBuffer(tran_code_buffer), config, data, tran_code_buffer.readableBytes());
		System.out.println(data);
	}
	
	public static ChannelBuffer getTranCodeBuffer() {
		ChannelBuffer buffer = BufferReader.createRequestMsg("8813req");
		ChannelBuffer tempBuffer = buffer.duplicate();
		byte[] oldBytes = new byte[tempBuffer.readableBytes()];
		tempBuffer.getBytes(oldBytes);
		byte[] tran_code_bytes = new byte[4];
		System.arraycopy(oldBytes, 0, tran_code_bytes, 0, 4);
		ChannelBuffer tran_code_buffer = ChannelBuffer.allocate(tran_code_bytes.length);
		tran_code_buffer.putBytes(tran_code_bytes);
		System.out.format("return tran code buffer: %s\n", tran_code_buffer.toHexString());
		return tran_code_buffer;
	}
}
