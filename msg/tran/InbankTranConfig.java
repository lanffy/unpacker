package unpacker.msg.tran;

import com.wk.conv.config.ArrayConfig;
import com.wk.conv.config.FieldConfig;
import com.wk.conv.config.StructConfig;
import com.wk.conv.mode.PackageMode;
import com.wk.sdo.FieldType;

/**
 * @description
 * @author raoliang
 * @version 2015��2��2�� ����10:55:52
 */
public class InbankTranConfig {
	public static void tran8808Config(StructConfig request, StructConfig response, StructConfig error) {
		//��������
		request.putChild(new FieldConfig("I1TRCD", FieldType.FIELD_STRING, 4));
		request.putChild(new FieldConfig("I1SBNO", FieldType.FIELD_STRING, 10));
		request.putChild(new FieldConfig("I1USID", FieldType.FIELD_STRING, 6));
		request.putChild(new FieldConfig("I1AUUS", FieldType.FIELD_STRING, 6));
		request.putChild(new FieldConfig("I1AUPS", FieldType.FIELD_STRING, 6));
		request.putChild(new FieldConfig("I1WSNO", FieldType.FIELD_STRING, 40));
		request.putChild(new FieldConfig("I1PYNO", FieldType.FIELD_STRING, 4));
		//��Ӧ������
		response.putChild(new FieldConfig("O1ACUR", FieldType.FIELD_INT, 2));
		response.putChild(new FieldConfig("O1TRDT", FieldType.FIELD_DOUBLE, 8));
		response.putChild(new FieldConfig("O1TRTM", FieldType.FIELD_DOUBLE, 6));
		response.putChild(new FieldConfig("O1TLSQ", FieldType.FIELD_STRING, 10));
		response.putChild(new FieldConfig("O1DATE", FieldType.FIELD_DOUBLE, 8));
	}
	
	public static void tran8813RespConfig(StructConfig response, PackageMode respMode) {
		response.putChild(new FieldConfig("O1ACUR", FieldType.FIELD_DOUBLE, 2));
		response.putChild(new FieldConfig("O1TRDT", FieldType.FIELD_DOUBLE, 8));
		response.putChild(new FieldConfig("O1TRTM", FieldType.FIELD_DOUBLE, 6));
		response.putChild(new FieldConfig("O1TLSQ", FieldType.FIELD_STRING, 10));
		
		//1:�ṹ���ֶβ��ģʽ��2.�ṹ���ֶβ���Ƿ��ϸ�
		StructConfig struct = new StructConfig(respMode, true);
		struct.putChild(new FieldConfig("O2NBBH", FieldType.FIELD_STRING, 8));
		struct.putChild(new FieldConfig("O2FEDT", FieldType.FIELD_DOUBLE, 8));
		struct.putChild(new FieldConfig("O2RBSQ", FieldType.FIELD_STRING, 12));
		struct.putChild(new FieldConfig("O2TRDT", FieldType.FIELD_DOUBLE, 8));
		struct.putChild(new FieldConfig("O2RGSQ", FieldType.FIELD_STRING, 12));
		struct.putChild(new FieldConfig("O2TRSQ", FieldType.FIELD_DOUBLE, 8));
		struct.putChild(new FieldConfig("O2TINO", FieldType.FIELD_DOUBLE, 2));
		struct.putChild(new FieldConfig("O2SBAC", FieldType.FIELD_STRING, 25));
		struct.putChild(new FieldConfig("O2ACNM", FieldType.FIELD_STRING, 62));
		struct.putChild(new FieldConfig("O2ANTY", FieldType.FIELD_STRING, 3));
		struct.putChild(new FieldConfig("O2ACBL", FieldType.FIELD_DOUBLE, 15, 2));
		struct.putChild(new FieldConfig("O2OPNT", FieldType.FIELD_STRING, 10));
		struct.putChild(new FieldConfig("O2DASQ", FieldType.FIELD_STRING, 9));
		struct.putChild(new FieldConfig("O2RBAC", FieldType.FIELD_STRING, 25));
		struct.putChild(new FieldConfig("O2OTNM", FieldType.FIELD_STRING, 62));
		struct.putChild(new FieldConfig("O2CNTY", FieldType.FIELD_STRING, 3));
		struct.putChild(new FieldConfig("O2OTSB", FieldType.FIELD_STRING, 10));
		struct.putChild(new FieldConfig("O2AMFG", FieldType.FIELD_STRING, 1));
		struct.putChild(new FieldConfig("O2REAC", FieldType.FIELD_STRING, 25));
		struct.putChild(new FieldConfig("O2TRSB", FieldType.FIELD_STRING, 10));
		struct.putChild(new FieldConfig("O2WLBZ", FieldType.FIELD_STRING, 1));
		struct.putChild(new FieldConfig("O2TRAM", FieldType.FIELD_DOUBLE, 15, 2));
		struct.putChild(new FieldConfig("O2CYNO", FieldType.FIELD_STRING, 2));
		struct.putChild(new FieldConfig("O2CTFG", FieldType.FIELD_STRING, 1));
		struct.putChild(new FieldConfig("O2CATR", FieldType.FIELD_STRING, 1));
		
		//1. �������ƣ�2. �����С���ʽ�� 3. ���������ʽ�� 4. ������Ԫ�أ� 5. δ֪��6. �Ƿ����
		response.putChild(new ArrayConfig("FACTLYO1", "Double.valueOf(data.getDouble(\"O1ACUR\")).intValue();",
				null, struct, null, false));
	}
	
	public static void tran8813ReqConfig(StructConfig request, PackageMode reqMode) {
		
	}
	
	public static void tran8813ErrConfig(StructConfig err, PackageMode errMode) {
		
	}
}
