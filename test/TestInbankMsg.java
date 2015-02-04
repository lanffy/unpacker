package unpacker.test;

import unpacker.msg.InbankMsg;
import unpacker.msg.Msg;
import unpacker.msg.SimulateMsg;
import unpacker.util.BufferReader;

import com.wk.conv.PacketChannelBuffer;
import com.wk.nio.ChannelBuffer;
import com.wk.sdo.ServiceData;
import com.wk.test.TestCase;

/**
 * @description
 * @author raoliang
 * @version 2015年2月2日 上午9:59:37
 */
public class TestInbankMsg extends TestCase{
	
	public void test_unpack_inbankmsg_havearray() {
		ChannelBuffer buffer = BufferReader.createRequestMsg("8813resp");
		Msg inbankMsg = new InbankMsg("outsys_mode", "outsys_mode", "outsys_mode");
		ServiceData data = inbankMsg.unpackResponse(new PacketChannelBuffer(buffer));
		assertEquals("AAAAAAA", data.getString("O1MGID"));
	}
	
	public void atest_send_and_unpack() {
		ChannelBuffer sendedBuffer = BufferReader.createRequestMsg("8813resp");
		ChannelBuffer buffer = SimulateMsg.packHeadBuffer(sendedBuffer);
		System.out.println(buffer.toHexString());
	}
	
}
