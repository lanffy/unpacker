package unpacker.msg;

import java.util.HashMap;
import java.util.Map;

import com.wk.conv.config.FieldConfig;
import com.wk.conv.config.StructConfig;
import com.wk.conv.converter.DefaultConverter;
import com.wk.conv.mode.DefaultPackageMode;
import com.wk.conv.mode.FieldMode;
import com.wk.conv.mode.Modes;
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
		FieldMode str_ebcd = Modes.getFieldMode("strEBCD");
		FieldMode zone = Modes.getFieldMode("zone");
		FieldMode pack = Modes.getFieldMode("pack");
		Map<FieldType, FieldMode> outsys_mode = new HashMap<FieldType, FieldMode>();
		outsys_mode.put(FieldType.FIELD_STRING, str_ebcd);
		outsys_mode.put(FieldType.FIELD_BYTE, zone);
		outsys_mode.put(FieldType.FIELD_SHORT, zone);
		outsys_mode.put(FieldType.FIELD_INT, zone);
		outsys_mode.put(FieldType.FIELD_LONG, zone);
		outsys_mode.put(FieldType.FIELD_FLOAT, pack);
		outsys_mode.put(FieldType.FIELD_DOUBLE, pack);
		StructConfig struct = new StructConfig(new DefaultPackageMode("outsys_mode", outsys_mode), true);
		struct.putChild(new FieldConfig("O1ACUR", FieldType.FIELD_INT, 2));
		struct.putChild(new FieldConfig("O1TRDT", FieldType.FIELD_INT, 8));
		struct.putChild(new FieldConfig("O1TRTM", FieldType.FIELD_INT, 6));
		struct.putChild(new FieldConfig("O1TLSQ", FieldType.FIELD_STRING, 10));
		struct.putChild(new FieldConfig("O1SBNO", FieldType.FIELD_STRING, 10));
		struct.putChild(new FieldConfig("O1CTNO", FieldType.FIELD_STRING, 12));
		struct.putChild(new FieldConfig("O1ACCN", FieldType.FIELD_STRING, 23));
		struct.putChild(new FieldConfig("O1SVAC", FieldType.FIELD_STRING, 19));
		struct.putChild(new FieldConfig("O1ACNO", FieldType.FIELD_STRING, 25));
		struct.putChild(new FieldConfig("O1CUNM", FieldType.FIELD_STRING, 62));
		struct.putChild(new FieldConfig("O1ACBL", FieldType.FIELD_DOUBLE, 15, 2));
		struct.putChild(new FieldConfig("O1CUBL", FieldType.FIELD_DOUBLE, 15, 2));
		struct.putChild(new FieldConfig("O1BLDE", FieldType.FIELD_STRING, 1));
		
		DefaultConverter converter = new DefaultConverter(struct);
		ServiceData data = new ServiceData();
		data = converter.fromChannelBuffer(buffer);
		return data;
	}
}
