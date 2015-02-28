package resolver.conf;

import java.util.HashMap;
import java.util.Map;

import com.wk.conv.mode.DefaultPackageMode;
import com.wk.conv.mode.FieldMode;
import com.wk.conv.mode.Modes;
import com.wk.conv.mode.PackageMode;
import com.wk.sdo.FieldType;

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
}
