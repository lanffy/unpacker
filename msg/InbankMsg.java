package unpacker.msg;

import unpacker.mode.InbankMode;
import unpacker.msg.tran.InbankTranConfig;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.config.FieldConfig;
import com.wk.conv.config.StructConfig;
import com.wk.conv.mode.PackageMode;
import com.wk.nio.ChannelBuffer;
import com.wk.sdo.FieldType;
import com.wk.sdo.ServiceData;

/**
 * @description
 * @author raoliang
 * @version 2015��1��20�� ����10:51:29
 */
public class InbankMsg {
	/**
	* @description ����ı���
	* @param buffer
	* @return
	* @version 2015��1��20�� ����10:58:49
	*/
	public static ServiceData unpack(ChannelBuffer buffer) {
		ServiceData data = new ServiceData();
		//��ģʽ
		PackageMode outsys = InbankMode.initInankMode();
		
		//������ͷ
		StructConfig request = new StructConfig(outsys, true);
		//��Ӧ����ͷ
		StructConfig response = new StructConfig(outsys, true);
		response.putChild(new FieldConfig("O1MGID", FieldType.FIELD_STRING, 7));
		//ʧ�ܱ���
		StructConfig error = new StructConfig(outsys, true);
		error.putChild(new FieldConfig("O1INFO", FieldType.FIELD_STRING, 60));
		
//		InbankTranConfig.tran8808Config(request, response, error);
		InbankTranConfig.tran8813Config(request, response, error, outsys);
		
		//����
		PackageMode response_mode = response.getPackageMode();
		response_mode.unpack(new PacketChannelBuffer(buffer), response, data, buffer.readableBytes());
		System.out.println("after unpack body:\n" + data);
		return data;
	}
	
}
