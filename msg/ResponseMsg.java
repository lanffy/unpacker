package resolver.msg;

import com.wk.logging.Log;
import com.wk.logging.LogFactory;
import com.wk.nio.ChannelBuffer;

/**
 * @description
 * @author raoliang
 * @version 2015��3��2�� ����2:45:08
 */
public class ResponseMsg {
	private static final Log logger = LogFactory.getLog();
	
	public static ChannelBuffer packRepMsg(ResponseInfo info) {
		ChannelBuffer buffer = ChannelBuffer.allocate(10240);
		if(logger.isDebugEnabled()) {
			logger.debug("�����������ݣ�\n{}", info);
		}
//		buffer.putInt(0);
		putIntAttr(buffer, "msg_id", info.getMsg_id());
		putStringAttr(buffer, "recv_time", info.getRecv_time());
		putStringAttr(buffer, "ret_code", info.getRet_code());
		putStringAttr(buffer, "ret_msg", info.getRet_msg());
//		buffer.putInt(0, buffer.readableBytes());
		return buffer;
	}
	
	public static void putStringAttr(ChannelBuffer buffer, String attr_name, String attr_value) {
		//�ֶ�������
		buffer.putByte((byte) attr_name.length());
		//�ֶ���
		buffer.putBytes(attr_name.getBytes());
		//�ֶ�����
		buffer.putByte((byte)0x07);
		//�ֶ�ֵ����
		byte[] bytes = attr_value.getBytes();
		buffer.putByte((byte)bytes.length);
		//�ֶ�ֵ
		buffer.putBytes(bytes);
	}
	
	public static void putIntAttr(ChannelBuffer buffer, String attr_name, int attr_value) {
		//�ֶ�������
		buffer.putByte((byte) attr_name.length());
		//�ֶ���
		buffer.putBytes(attr_name.getBytes());
		//�ֶ�����
		buffer.putByte((byte)0x05);
		//�ֶ�ֵ����
		//�ֶ�ֵ
		buffer.putInt(attr_value);
	}
}
