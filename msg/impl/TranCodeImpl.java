package resolver.msg.impl;

import com.wk.conv.mode.PackageMode;
import com.wk.nio.ChannelBuffer;

/**
 * @description
 * @author raoliang
 * @version 2015��3��4�� ����4:03:07
 */
public interface TranCodeImpl {

	public String getTranCode(ChannelBuffer buffer, PackageMode mode);
}
