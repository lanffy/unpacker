package unpacker.msg;

import unpacker.msg.tran.InbankTranConfig;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.FieldConfig;
import com.wk.conv.config.StructConfig;
import com.wk.conv.mode.Modes;
import com.wk.conv.mode.PackageMode;
import com.wk.sdo.FieldType;
import com.wk.sdo.ServiceData;

/**
 * @description �𰲻պ��ı���
 * @author raoliang
 * @version 2015��1��20�� ����10:51:29
 */
public class InbankMsg extends Msg{

	@Override
	public ServiceData unpackRequest(PacketChannelBuffer buffer) {
		ServiceData data = new ServiceData();
		//������ͷ
		StructConfig request = new StructConfig(requestMode, true);
		
//		InbankTranConfig.tran8808Config(request, response, error);
		InbankTranConfig.tran8813ReqConfig(request, requestMode);
		
		//����
		PackageMode request_mode = request.getPackageMode();
		request_mode.unpack(buffer, request, data, buffer.readableBytes());
		System.out.println("after unpack body:\n" + data);
		return data;
	}

	@Override
	public ServiceData unpackResponse(PacketChannelBuffer buffer) {
		ServiceData data = new ServiceData();
		//��Ӧ����ͷ
		StructConfig response = new StructConfig(responseMode, true);
		response.putChild(new FieldConfig("O1MGID", FieldType.FIELD_STRING, 7));
		InbankTranConfig.tran8813RespConfig(response, responseMode);
		//����
		PackageMode response_mode = response.getPackageMode();
		response_mode.unpack(buffer, response, data, buffer.readableBytes());
		System.out.println("after unpack body:\n" + data);
		return data;
	}

	@Override
	public ServiceData unpackError(PacketChannelBuffer buffer) {
		ServiceData data = new ServiceData();
		//ʧ�ܱ���
		StructConfig error = new StructConfig(errorMode, true);
		error.putChild(new FieldConfig("O1INFO", FieldType.FIELD_STRING, 60));
		InbankTranConfig.tran8813ErrConfig(error, errorMode);
		//����
		PackageMode error_mode = error.getPackageMode();
		error_mode.unpack(buffer, error, data, buffer.readableBytes());
		System.out.println("after unpack body:\n" + data);
		return data;
	}

	@Override
	public PackageMode getReqPackageMode(String reqModeName) {
//		return Modes.getPackageMode("outsys_mode");
		return Modes.getPackageMode(reqModeName);
	}

	@Override
	public PackageMode getRespPackageMode(String respModeName) {
//		return Modes.getPackageMode("outsys_mode");
		return Modes.getPackageMode(respModeName);
	}

	@Override
	public PackageMode getErrPackageMode(String errModeName) {
		return Modes.getPackageMode(errModeName);
	}
	
}
