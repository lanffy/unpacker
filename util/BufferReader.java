package resolver.util;

import java.io.IOException;
import java.io.InputStream;

import resolver.Receiver;

import com.wk.net.ChannelBufferMsg;
import com.wk.nio.ChannelBuffer;

/**
 * @description ���Ķ�ȡ��
 * @author raoliang
 * @version 2015��1��20�� ����9:39:34
 */
public class BufferReader {
	//����������
	public static ChannelBuffer createRequestMsg(String fileName) {
		byte[] tempBytes = null;
		try {
			InputStream in = Receiver.class.getResourceAsStream(fileName);
			int total = in.available();
			tempBytes = new byte[total];
			in.read(tempBytes);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ChannelBuffer buffer = ChannelBuffer.allocate(tempBytes.length);
		buffer.putBytes(tempBytes);
		return new ChannelBufferMsg(buffer).toChannelBuffer();
	}
}
