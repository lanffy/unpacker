package resolver.msg.impl;

import com.wk.nio.ChannelBuffer;

/**
 * @description
 * @author raoliang
 * @version 2015年3月4日 下午7:59:04
 */
public interface TranDecryptImpl {

	public ChannelBuffer decrypt(ChannelBuffer buffer);
}
