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
 * @version 2015年2月3日 下午5:40:31
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
	 * 请求包模式
	 */
//	public PackageMode requestMode = getReqPackageMode(reqModeName);
	public PackageMode requestMode;
	/**
	 * 响应包模式
	 */
//	public PackageMode responseMode = getRespPackageMode(respModeName);
	public PackageMode responseMode;
	/**
	 * 错误包模式
	 */
//	public PackageMode errorMode = getErrPackageMode(errModeName);
	public PackageMode errorMode;
	
	/**
	* @description 获取请求包模式
	* @return
	*/
	public abstract PackageMode getReqPackageMode();
	/**
	* @description 获取响应包模式
	* @return
	*/
	public abstract PackageMode getRespPackageMode();
	/**
	* @description 获取错误包模式
	* @return
	*/
	public abstract PackageMode getErrPackageMode();
	
	/**
	* @description 拆请求报文
	* @param buffer
	* @return
	*/
	public abstract ServiceData unpackRequest(PacketChannelBuffer buffer);
	
	/**
	* @description 拆响应报文
	* @param buffer
	* @return
	*/
	public abstract ServiceData unpackResponse(PacketChannelBuffer buffer);
	
	/**
	* @description 拆错误报文
	* @param buffer
	* @return
	*/
	public abstract ServiceData unpackError(PacketChannelBuffer buffer);
}
