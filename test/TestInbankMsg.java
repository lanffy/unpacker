package unpacker.test;

import unpacker.msg.InbankMsg;
import unpacker.msg.SimulateMsg;
import unpacker.util.BufferReader;

import com.wk.nio.ChannelBuffer;
import com.wk.sdo.ServiceData;
import com.wk.test.TestCase;

/**
 * @description
 * @author raoliang
 * @version 2015年2月2日 上午9:59:37
 */
public class TestInbankMsg extends TestCase{
	
	public void atest_unpack_inbankmsg() {
		ChannelBuffer buffer = BufferReader.createRequestMsg("8808resp2");
		ServiceData data = InbankMsg.unpack(buffer);
		assertEquals("AAAAAAA", data.getString("O1MGID"));
	}
	
	public void atest_unpack_inbankmsg_havearray() {
		ChannelBuffer buffer = BufferReader.createRequestMsg("8813resp");
		ServiceData data = InbankMsg.unpack(buffer);
		assertEquals("AAAAAAA", data.getString("O1MGID"));
	}
	
	public void test_send_and_unpack() {
		ChannelBuffer sendedBuffer = BufferReader.createRequestMsg("8813resp");
		ChannelBuffer buffer = SimulateMsg.packHeadBuffer(sendedBuffer);
		System.out.println(buffer.toHexString());
	}
	
}
