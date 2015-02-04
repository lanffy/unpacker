package unpacker.msg;

import unpacker.conf.LoadMode;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.mode.PackageMode;
import com.wk.logging.Log;
import com.wk.logging.LogFactory;
import com.wk.sdo.ServiceData;

/**
 * @description
 * @author raoliang
 * @version 2015��2��3�� ����5:40:31
 */
public abstract class Msg {
	static {
		LoadMode.loadMode();
	}
	protected final Log logger = LogFactory.getLog("unpacker");
	public static String reqModeName;
	public static String respModeName;
	public static String errModeName;
	public Msg(String reqModeName, String respModeName, String errModeName){
		Msg.reqModeName = reqModeName;
		Msg.respModeName = respModeName;
		Msg.errModeName = errModeName;
	}
	/**
	 * �����ģʽ
	 */
//	public PackageMode requestMode = getReqPackageMode(reqModeName);
	public PackageMode requestMode;
	/**
	 * ��Ӧ��ģʽ
	 */
//	public PackageMode responseMode = getRespPackageMode(respModeName);
	public PackageMode responseMode;
	/**
	 * �����ģʽ
	 */
//	public PackageMode errorMode = getErrPackageMode(errModeName);
	public PackageMode errorMode;
	
	/**
	* @description ��ȡ�����ģʽ
	* @return
	*/
	public abstract PackageMode getReqPackageMode();
	/**
	* @description ��ȡ��Ӧ��ģʽ
	* @return
	*/
	public abstract PackageMode getRespPackageMode();
	/**
	* @description ��ȡ�����ģʽ
	* @return
	*/
	public abstract PackageMode getErrPackageMode();
	
	/**
	* @description ��������
	* @param buffer
	* @return
	*/
	public abstract ServiceData unpackRequest(PacketChannelBuffer buffer);
	
	/**
	* @description ����Ӧ����
	* @param buffer
	* @return
	*/
	public abstract ServiceData unpackResponse(PacketChannelBuffer buffer);
	
	/**
	* @description �������
	* @param buffer
	* @return
	*/
	public abstract ServiceData unpackError(PacketChannelBuffer buffer);
}
