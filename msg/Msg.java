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
	public String reqModeName;
	public String respModeName;
	public String errModeName;
	/**
	 * �����ģʽ
	 */
	public PackageMode requestMode = getReqPackageMode(reqModeName);
	/**
	 * ��Ӧ��ģʽ
	 */
	public PackageMode responseMode = getRespPackageMode(respModeName);
	/**
	 * �����ģʽ
	 */
	public PackageMode errorMode = getErrPackageMode(errModeName);
	
	/**
	* @description ��ȡ�����ģʽ
	* @return
	*/
	public abstract PackageMode getReqPackageMode(String reqModeName);
	/**
	* @description ��ȡ��Ӧ��ģʽ
	* @return
	*/
	public abstract PackageMode getRespPackageMode(String respModeName);
	/**
	* @description ��ȡ�����ģʽ
	* @return
	*/
	public abstract PackageMode getErrPackageMode(String errModeName);
	
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
