package unpacker.util;

import java.io.IOException;
import java.io.InputStream;

import com.wk.net.ChannelBufferMsg;
import com.wk.nio.ChannelBuffer;
import com.wk.pnet.SocketReceiver;

/**
 * @description
 * @author raoliang
 * @version 2015年1月20日 上午9:39:34
 */
public class BufferReader {
	//创建请求报文
	public static ChannelBufferMsg createRequestMsg(String fileName) {
		byte[] tempBytes = null;
		try {
			InputStream in = SocketReceiver.class.getResourceAsStream(fileName);
			int total = in.available();
			tempBytes = new byte[total];
			in.read(tempBytes);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ChannelBuffer buffer = ChannelBuffer.allocate(tempBytes.length);
		buffer.putBytes(tempBytes);
		return new ChannelBufferMsg(buffer);
	}
}
