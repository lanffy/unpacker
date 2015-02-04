package unpacker.msg;

import unpacker.msg.tran.InbankTranConfig;
import unpacker.util.BufferReader;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.FieldConfig;
import com.wk.conv.config.StructConfig;
import com.wk.conv.mode.Modes;
import com.wk.conv.mode.PackageMode;
import com.wk.nio.ChannelBuffer;
import com.wk.sdo.FieldType;
import com.wk.sdo.ServiceData;

/**
 * @description 拆安徽核心报文
 * @author raoliang
 * @version 2015年1月20日 上午10:51:29
 */
public class InbankMsg extends Msg{

	public InbankMsg(String reqModeName, String respModeName, String errModeName) {
		super(reqModeName, respModeName, errModeName);
		System.out.println("reqModeName:" + reqModeName);
		System.out.println("respModeName:" + respModeName);
		System.out.println("errModeName:" + errModeName);
		requestMode = getReqPackageMode();
		responseMode = getRespPackageMode();
		errorMode = getErrPackageMode();
	}

	public static void main(String[] args) {
		test_unpack_inbankmsg();
	}
	
	public static void test_unpack_inbankmsg() {
		ChannelBuffer buffer = BufferReader.createRequestMsg("8813resp");
		InbankMsg inbankMsg = new InbankMsg("outsys_mode", "outsys_mode", "outsys_mode");
		ServiceData data = inbankMsg.unpackResponse(new PacketChannelBuffer(buffer));
		System.out.println(data.getString("O1MGID"));
	}
	
	@Override
	public ServiceData unpackRequest(PacketChannelBuffer buffer) {
		ServiceData data = new ServiceData();
		//请求报文头
		StructConfig request = new StructConfig(requestMode, true);
		
		InbankTranConfig.tran8813ReqConfig(request, requestMode);
		
		//拆报文
		PackageMode request_mode = request.getPackageMode();
		request_mode.unpack(buffer, request, data, buffer.readableBytes());
		System.out.println("after unpack body:\n" + data);
		return data;
	}

	@Override
	public ServiceData unpackResponse(PacketChannelBuffer buffer) {
		ServiceData data = new ServiceData();
		ServiceData datahead = new ServiceData();
		//响应报文头
		StructConfig response = new StructConfig(responseMode, true);
		response.putChild(new FieldConfig("O1MGID", FieldType.FIELD_STRING, 7));
		
		response.getPackageMode().unpack(buffer, response, datahead, buffer.readableBytes());
		System.out.println(datahead);
		System.out.println("*****************************");
		
//		InbankTranConfig.tran8808RespConfig(response, responseMode);
		StructConfig body = new StructConfig(responseMode, true);
		InbankTranConfig.tran8813RespConfig(body, responseMode);
		//拆报文
		PackageMode response_mode = body.getPackageMode();
		response_mode.unpack(buffer, body, data, buffer.readableBytes());
		System.out.println("after unpack body:\n" + data);
		return data;
	}

	@Override
	public ServiceData unpackError(PacketChannelBuffer buffer) {
		ServiceData data = new ServiceData();
		//失败报文
		StructConfig error = new StructConfig(errorMode, true);
		error.putChild(new FieldConfig("O1INFO", FieldType.FIELD_STRING, 60));
		InbankTranConfig.tran8813ErrConfig(error, errorMode);
		//拆报文
		PackageMode error_mode = error.getPackageMode();
		error_mode.unpack(buffer, error, data, buffer.readableBytes());
		System.out.println("after unpack body:\n" + data);
		return data;
	}

	@Override
	public PackageMode getReqPackageMode() {
//		return Modes.getPackageMode("outsys_mode");
		return Modes.getPackageMode(Msg.reqModeName);
	}

	@Override
	public PackageMode getRespPackageMode() {
		return Modes.getPackageMode(Msg.respModeName);
	}

	@Override
	public PackageMode getErrPackageMode() {
		return Modes.getPackageMode(Msg.errModeName);
	}
	
}
